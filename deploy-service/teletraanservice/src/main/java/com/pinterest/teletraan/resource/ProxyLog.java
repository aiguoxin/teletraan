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
import com.pinterest.deployservice.bean.ProxyLogBean;
import com.pinterest.deployservice.common.CommonUtils;
import com.pinterest.deployservice.dao.ProxyLogDAO;
import com.pinterest.deployservice.scm.SourceControlManager;
import com.pinterest.teletraan.TeletraanServiceContext;
import com.pinterest.teletraan.exception.TeletaanInternalException;
import com.pinterest.teletraan.security.Authorizer;
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

@Path("/v1/proxylog")
@Api(tags = "ProxyLog")
@SwaggerDefinition(
        tags = {
                @Tag(name = "ProxyLog", description = "ProxyLog APIs"),
        }
)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProxyLog {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyLog.class);
    private ProxyLogDAO proxyLogDAO;
    private SourceControlManager sourceControlManager;
    private final Authorizer authorizer;

    @Context
    UriInfo uriInfo;

    public ProxyLog(TeletraanServiceContext context) throws Exception {
        proxyLogDAO = context.getProxyLogDAO();
        sourceControlManager = context.getSourceControlManager();
        authorizer = context.getAuthorizer();
    }

    @POST
    @ApiOperation(
            value = "save log",
            notes = "save proxy log")
    public void upload(
            @Context SecurityContext sc,
            @ApiParam(value = "Build object", required = true)@Valid ProxyLogVo proxyLogVo) throws Exception {

        LOG.info("-------------param={}",ReflectionToStringBuilder.toString(proxyLogVo));

        Long createTime = proxyLogVo.getCreate_time();
        String ip = proxyLogVo.getIp();
        String proxyJson = proxyLogVo.getProxy_json();
        proxyJson = new String(CommonUtils.decode(proxyJson));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        Map<String, ProxyLogBean> proxyMap = objectMapper.readValue(proxyJson, Map.class);
        for(Map.Entry<String, ProxyLogBean> entry  : proxyMap.entrySet()){
            ProxyLogBean proxyLogBean = objectMapper.readValue(JSON.toJSONString(entry.getValue()), ProxyLogBean.class);
            proxyLogBean.setCreate_time(createTime);
            proxyLogBean.setIp(ip);
            proxyLogBean.setProxy_name(entry.getKey());
            proxyLogDAO.insert(proxyLogBean);
        }
        LOG.info("Successfully upload ip={},time={}", ip, new DateTime(createTime).toString("dd-MM-yyyy HH:mm:ss"));
    }

    @POST
    @Path("/graph")
    public List<ProxyLogBean> get(@QueryParam("ip") String ip,
                               @QueryParam("name") String proxyName,
                               @QueryParam("before") Long before,
                               @QueryParam("after") Long after) throws Exception {

        LOG.info("ip={},name={},before={},after={}",ip,proxyName,before,after);
        if (!StringUtils.isEmpty(ip) && !StringUtils.isEmpty(proxyName)) {
            if (before == null || after == null) {
                after = System.currentTimeMillis()/1000;
                before = CommonUtils.getTimesmorning();
            }
            LOG.info("ip={},name={},before={},after={}",ip,proxyName,before,after);
            List<ProxyLogBean> list =  proxyLogDAO.getByNameDateIp(proxyName, ip, before, after);
            LOG.info("result = {}",ReflectionToStringBuilder.toString(list));
            return list;
        }
        throw new TeletaanInternalException(Response.Status.BAD_REQUEST, "Require  ip and proxy name in the request.");
    }


    @POST
    @Path("/ip")
    public Map<String,List<ProxyLogBean>> getByIp(@QueryParam("ip") String ip,
                                  @QueryParam("before") Long before,
                                  @QueryParam("after") Long after) throws Exception {

        Map<String,List<ProxyLogBean>> logMap = new HashMap();
        LOG.info("ip={},name={},before={},after={}",ip,before,after);
        if (!StringUtils.isEmpty(ip)) {
            if (before == null || after == null) {
                after = System.currentTimeMillis()/1000;
                before = CommonUtils.getTimesmorning();
            }
            List<String> list =  proxyLogDAO.getProxyByIp(ip);
            for(String proxyName : list){
                List<ProxyLogBean> logList =  proxyLogDAO.getByNameDateIp(proxyName, ip, before, after);
                if(CollectionUtils.isNotEmpty(logList)){
                    logMap.put(proxyName,logList);
                }
            }
            return logMap;
        }
        throw new TeletaanInternalException(Response.Status.BAD_REQUEST, "Require  ip and proxy name in the request.");
    }


    @GET
    @Path("/proxy")
    public List<String> get(@QueryParam("ip") String ip) throws Exception {

        LOG.info("ip={}",ip);
        if (!StringUtils.isEmpty(ip)) {
            List<String> list =  proxyLogDAO.getProxyByIp(ip);
            LOG.info("result = {}", JSON.toJSONString(list));
            return list;
        }
        throw new TeletaanInternalException(Response.Status.BAD_REQUEST, "Require  ip  in the request.");
    }

}

