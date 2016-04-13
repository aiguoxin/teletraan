package com.pinterest.teletraan.resource;

/**
 * 16/4/1 下午5:50
 * aiguoxin
 * 说明:
 */
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinterest.deployservice.bean.InterfaceCodeBean;
import com.pinterest.deployservice.bean.InterfaceInfoBean;
import com.pinterest.deployservice.bean.ProxyLogBean;
import com.pinterest.deployservice.common.CommonUtils;
import com.pinterest.deployservice.dao.InterfaceCodeDAO;
import com.pinterest.deployservice.dao.ProxyLogDAO;
import com.pinterest.deployservice.scm.SourceControlManager;
import com.pinterest.teletraan.TeletraanServiceContext;
import com.pinterest.teletraan.exception.TeletaanInternalException;
import com.pinterest.teletraan.security.Authorizer;
import com.pinterest.teletraan.vo.CodeJsonVo;
import com.pinterest.teletraan.vo.ProxyLogVo;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/v1/code")
@Api(tags = "InterfaceCode")
@SwaggerDefinition(
        tags = {
                @Tag(name = "InterfaceCode", description = "InterfaceCode APIs"),
        }
)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InterfaceCode {
    private static final Logger LOG = LoggerFactory.getLogger(InterfaceCode.class);
    private InterfaceCodeDAO interfaceCodeDAO;
    private SourceControlManager sourceControlManager;
    private final Authorizer authorizer;

    @Context
    UriInfo uriInfo;

    public InterfaceCode(TeletraanServiceContext context) throws Exception {
        interfaceCodeDAO = context.getInterfaceCodeDAO();
        sourceControlManager = context.getSourceControlManager();
        authorizer = context.getAuthorizer();
    }

    @POST
    @ApiOperation(
            value = "save interface code",
            notes = "save interface code")
    public void upload(
            @Context SecurityContext sc,
            @ApiParam(value = "Build object", required = true)@Valid ProxyLogVo proxyLogVo) throws Exception {

        LOG.info("-------------param={}",ReflectionToStringBuilder.toString(proxyLogVo));

        Long createTime = proxyLogVo.getCreate_time();
        String interfaceName;
        Float upstreamResponseTime,responseTime,requestCount;
        String ip = proxyLogVo.getIp();
        String json = proxyLogVo.getProxy_json();
        json = new String(CommonUtils.decode(json));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        Map<String, CodeJsonVo> proxyMap = objectMapper.readValue(json, Map.class);
        for(Map.Entry<String, CodeJsonVo> entry  : proxyMap.entrySet()){
            interfaceName = entry.getKey();
            CodeJsonVo codeJsonVo = objectMapper.readValue(JSON.toJSONString(entry.getValue()), CodeJsonVo.class);
            upstreamResponseTime = codeJsonVo.getUpstream_response_time();
            responseTime = codeJsonVo.getResponse_time();
            requestCount = codeJsonVo.getRequest_count();
            //循环处理状态码
            int count = 0;
            for(Map.Entry<String,Float> codeEntry : codeJsonVo.getCode().entrySet()){
                InterfaceCodeBean interfaceCodeBean = new InterfaceCodeBean();
                interfaceCodeBean.setCreate_time(createTime);
                interfaceCodeBean.setInterface_name(interfaceName);
                interfaceCodeBean.setCode_tag(codeEntry.getKey());
                interfaceCodeBean.setCode_count(codeEntry.getValue());
                interfaceCodeBean.setIp(ip);
                interfaceCodeBean.setUpstream_response_time(upstreamResponseTime);
                interfaceCodeBean.setResponse_time(responseTime);
                interfaceCodeDAO.insert(interfaceCodeBean);
                if(count == 0){
                    //加入总状态码
                    interfaceCodeBean.setCode_tag("all");
                    interfaceCodeBean.setCode_count(requestCount);
                    interfaceCodeDAO.insert(interfaceCodeBean);
                }
                count ++;
            }
        }
        LOG.info("Successfully upload ip={},time={}", ip, new DateTime(createTime).toString("dd-MM-yyyy HH:mm:ss"));
    }

    @POST
    @Path("/line")
    public Map<String,List<InterfaceCodeBean>> getLineData(@QueryParam("ip") String ip,
                               @QueryParam("name") String interfaceName,
                               @QueryParam("before") Long before,
                               @QueryParam("after") Long after) throws Exception {

        LOG.info("ip={},name={},before={},after={}",ip,interfaceName,before,after);
        if (!StringUtils.isEmpty(ip) && !StringUtils.isEmpty(interfaceName)) {
            Map<String,List<InterfaceCodeBean>> logMap = new HashMap();
            if (before == null || after == null) {
                after = System.currentTimeMillis()/1000;
                before = CommonUtils.getTimesmorning();
            }
            LOG.info("ip={},name={},before={},after={}",ip,interfaceName,before,after);
            List<String> codeList = interfaceCodeDAO.getCodeListByIpInterface(ip, interfaceName);
            for (String codeName : codeList){
                List<InterfaceCodeBean> list =  interfaceCodeDAO.getByNameIpTagDate(interfaceName, ip, codeName, before, after);
                logMap.put(codeName, list);
            }
            return logMap;
        }
        throw new TeletaanInternalException(Response.Status.BAD_REQUEST, "Require  ip and proxy name in the request.");
    }


    @POST
    @Path("/pie")
    public Map<String,Double> getPieData(@QueryParam("ip") String ip,
                                                   @QueryParam("name") String interfaceName,
                                                   @QueryParam("before") Long before,
                                                   @QueryParam("after") Long after) throws Exception {

        LOG.info("ip={},name={},before={},after={}",ip,interfaceName,before,after);
        if (!StringUtils.isEmpty(ip) && !StringUtils.isEmpty(interfaceName)) {
            Map<String,Double> logMap = new HashMap();
            if (before == null || after == null) {
                after = System.currentTimeMillis()/1000;
                before = CommonUtils.getTimesmorning();
            }
            LOG.info("ip={},name={},before={},after={}",ip,interfaceName,before,after);
            List<String> codeList = interfaceCodeDAO.getCodeListByIpInterface(ip, interfaceName);
            for (String codeName : codeList){
                Double total = interfaceCodeDAO.countByNameIpTagDate(interfaceName,ip,codeName,before,after);
                logMap.put(codeName, total);
            }
            return logMap;
        }
        throw new TeletaanInternalException(Response.Status.BAD_REQUEST, "Require  ip and proxy name in the request.");
    }




    @GET
    @Path("/interface")
    public List<String> get(@QueryParam("ip") String ip) throws Exception {

        LOG.info("ip={}",ip);
        if (!StringUtils.isEmpty(ip)) {
            List<String> list =  interfaceCodeDAO.getNameListByIp(ip);
            LOG.info("result = {}", JSON.toJSONString(list));
            return list;
        }
        throw new TeletaanInternalException(Response.Status.BAD_REQUEST, "Require  ip  in the request.");
    }

}

