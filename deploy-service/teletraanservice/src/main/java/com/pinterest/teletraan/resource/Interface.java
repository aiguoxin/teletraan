package com.pinterest.teletraan.resource;

/**
 * 16/4/1 下午5:50
 * aiguoxin
 * 说明: 接收api接口成功率数据
 */
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.pinterest.deployservice.bean.InterfaceInfoBean;
import com.pinterest.deployservice.common.CommonUtils;
import com.pinterest.deployservice.dao.InterfaceInfoDAO;
import com.pinterest.deployservice.scm.SourceControlManager;
import com.pinterest.teletraan.TeletraanServiceContext;
import com.pinterest.teletraan.exception.TeletaanInternalException;
import com.pinterest.teletraan.security.Authorizer;
import com.pinterest.teletraan.vo.InterfaceInfoVo;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/v1/interface")
@Api(tags = "InterfaceInfo")
@SwaggerDefinition(
        tags = {
                @Tag(name = "InterfaceInfo", description = "InterfaceInfo APIs"),
        }
)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Interface {
    private static final Logger LOG = LoggerFactory.getLogger(Interface.class);
    private InterfaceInfoDAO interfaceInfoDAO;
    private SourceControlManager sourceControlManager;
    private final Authorizer authorizer;

//  日期	国内all views3.0 views2.0 search video fusion xyz st pop bus  国外all views3.0 views2.0 search video fusion xyz pop st bus

    private static final ImmutableMap<Integer, String> interfaceMap = new ImmutableMap.Builder<Integer, String>()
            .put(0, "day")
            .put(1, "all")
            .put(2, "views_3.0")
            .put(3, "php_views")
            .put(4, "views_search")
            .put(5, "video")
            .put(6, "fusion")
            .put(7, "php_xyz")
            .put(8, "php_st")
            .put(9, "views_pop")
            .put(10, "php_bus")
            .put(11, "all")
            .put(12, "views_3.0")
            .put(13, "php_views")
            .put(14, "views_search")
            .put(15, "video")
            .put(16, "fusion")
            .put(17, "php_xyz")
            .put(18, "php_st")
            .put(19, "views_pop")
            .put(20, "php_bus")
            .build();



    @Context
    UriInfo uriInfo;

    public Interface(TeletraanServiceContext context) throws Exception {
        interfaceInfoDAO = context.getInterfaceInfoDAO();
        sourceControlManager = context.getSourceControlManager();
        authorizer = context.getAuthorizer();
    }

    @POST
    @ApiOperation(
            value = "save interface info",
            notes = "save interface info")
    public void upload(
            @Context SecurityContext sc,
            @ApiParam(value = "Build object", required = true)@Valid InterfaceInfoVo interfaceInfoVo) throws Exception {

        String proxyJson = interfaceInfoVo.getProxy_json();
        proxyJson = new String(CommonUtils.decode(proxyJson));
        List<String> list = Arrays.asList(proxyJson.split(" "));
        int length = list.size();
        if (length != interfaceMap.size()){
            throw new TeletaanInternalException(Response.Status.BAD_REQUEST, "Require json in the request is wrong format.");
        }
        String day = CommonUtils.changeDateFromat(list.get(0));
        if(StringUtils.isNotBlank(day)){
            for(int i=1; i< length; i++){
                InterfaceInfoBean interfaceInfoBean = new InterfaceInfoBean();
                interfaceInfoBean.setCreate_time(day);
                interfaceInfoBean.setName(interfaceMap.get(i));
                if(i > 10){
                    interfaceInfoBean.setFlag(InterfaceInfoBean.FlagState.OVERSEA.val()); //国外接口,根据日志格式约定
                }
                interfaceInfoBean.setSuccess_rate(Float.valueOf(list.get(i)));
                interfaceInfoDAO.insert(interfaceInfoBean);
            }
        }else {
            throw new TeletaanInternalException(Response.Status.BAD_REQUEST, "Require  json data error in the request.");
        }
        LOG.info("Successfully upload json={},time={}", proxyJson, day);
    }

    @POST
    @Path("/graph")
    public List<InterfaceInfoBean> get(@QueryParam("flag") int flag,
                               @QueryParam("name") String name,
                               @QueryParam("before") String before,
                               @QueryParam("after") String after) throws Exception {

        LOG.info("flag={},name={},before={},after={}",flag,name,before,after);
        if (flag > -1 && !StringUtils.isEmpty(name)) {
            if (before == null || after == null) {
                after = CommonUtils.getDayBeforeToday(0);
                before = CommonUtils.getDayBeforeToday(7);
            }
            LOG.info("ip={},name={},before={},after={}",name,flag,before,after);
            List<InterfaceInfoBean> list =  interfaceInfoDAO.getByNameFlagDate(name, flag, before, after);
            LOG.info("result = {}",ReflectionToStringBuilder.toString(list));
            return list;
        }
        throw new TeletaanInternalException(Response.Status.BAD_REQUEST, "Require  flag and  name in the request.");
    }


    @POST
    @Path("/host")
    public Map<String,List<InterfaceInfoBean>> getByIp(@QueryParam("flag") int flag,
                                  @QueryParam("before") String before,
                                  @QueryParam("after") String after) throws Exception {

        Map<String,List<InterfaceInfoBean>> logMap = new HashMap();
        LOG.info("flag={},name={},before={},after={}",flag,before,after);
        if (flag > -1) {
            if (before == null || after == null) {
                after = CommonUtils.getDayBeforeToday(0);
                before = CommonUtils.getDayBeforeToday(7);
            }
            List<String> list =  interfaceInfoDAO.getNameListByFlag(flag);
            for(String interfaceName : list){
                List<InterfaceInfoBean> logList =  interfaceInfoDAO.getByNameFlagDate(interfaceName, flag, before, after);
                if(CollectionUtils.isNotEmpty(logList)){
                    logMap.put(interfaceName,logList);
                }
            }
            return logMap;
        }
        throw new TeletaanInternalException(Response.Status.BAD_REQUEST, "Require  ip and proxy name in the request.");
    }


    @GET
    @Path("/list")
    public List<String> get(@QueryParam("flag") int flag) throws Exception {

        LOG.info("flag={}",flag);
        if (flag > -1) {
            List<String> list =  interfaceInfoDAO.getNameListByFlag(flag);
            LOG.info("result = {}", JSON.toJSONString(list));
            return list;
        }
        throw new TeletaanInternalException(Response.Status.BAD_REQUEST, "Require flag in the request.");
    }

}

