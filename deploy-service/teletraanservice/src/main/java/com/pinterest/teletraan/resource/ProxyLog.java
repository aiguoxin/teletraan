package com.pinterest.teletraan.resource;

/**
 * 16/4/1 下午5:50
 * aiguoxin
 * 说明:
 */
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pinterest.deployservice.bean.BuildBean;
import com.pinterest.deployservice.bean.ProxyLogBean;
import com.pinterest.deployservice.common.CommonUtils;
import com.pinterest.deployservice.dao.BuildDAO;
import com.pinterest.deployservice.dao.ProxyLogDAO;
import com.pinterest.deployservice.scm.SourceControlManager;
import com.pinterest.teletraan.TeletraanServiceContext;
import com.pinterest.teletraan.security.Authorizer;
import com.pinterest.teletraan.vo.ProxyLogVo;
import io.swagger.annotations.*;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Date;
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
    private BuildDAO buildDAO;
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
        Gson gson = new Gson();
        Map<String, ProxyLogBean> proxyMap = gson.fromJson(proxyJson, new TypeToken<Map<String, ProxyLogBean>>() {}.getType());
        for(Map.Entry<String, ProxyLogBean> entry  : proxyMap.entrySet()){
            ProxyLogBean proxyLogBean = entry.getValue();
            proxyLogBean.setCreateTime(createTime);
            proxyLogBean.setIp(ip);
            proxyLogBean.setProxyName(entry.getKey());
            proxyLogDAO.insert(proxyLogBean);
        }
        LOG.info("Successfully upload ip={},time={}", ip, new DateTime(createTime).toString("dd-MM-yyyy HH:mm:ss"));
    }

}

