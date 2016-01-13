/**
 * Copyright 2016 Pinterest, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pinterest.teletraan.resource;

import com.pinterest.deployservice.bean.UserRolesBean;
import com.pinterest.deployservice.dao.UserRolesDAO;
import com.pinterest.teletraan.TeletraanServiceContext;
import com.pinterest.teletraan.security.Authorizer;
import com.pinterest.deployservice.bean.Resource;
import com.pinterest.deployservice.bean.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

public abstract class UserRoles {
    private static final Logger LOG = LoggerFactory.getLogger(UserRoles.class);
    private final UserRolesDAO userRolesDAO;
    private final Authorizer authorizer;

    @Context
    UriInfo uriInfo;

    public UserRoles(TeletraanServiceContext context) {
        userRolesDAO = context.getUserRolesDAO();
        authorizer = context.getAuthorizer();
    }

    public List<UserRolesBean> getByResource(String resourceId,
        Resource.Type resourceType) throws Exception {
        return userRolesDAO.getByResource(resourceId, resourceType);
    }

    public UserRolesBean getByNameAndResource(String userName, String resourceId,
        Resource.Type resourceType) throws Exception {
        return userRolesDAO.getByNameAndResource(userName, resourceId, resourceType);
    }

    public void update(SecurityContext sc, UserRolesBean bean, String userName,
        String resourceId, Resource.Type resourceType) throws Exception {
        authorizer.authorize(sc, new Resource(resourceId, resourceType), Role.ADMIN);
        userRolesDAO.update(bean, userName, resourceId, resourceType);
        LOG.info("Successfully updated user {} permission for resource {} with {}",
            userName, resourceId, bean);
    }

    public Response create(SecurityContext sc, UserRolesBean bean, String resourceId,
        Resource.Type resourceType) throws Exception {
        authorizer.authorize(sc, new Resource(resourceId, resourceType), Role.ADMIN);
        bean.setResource_id(resourceId);
        bean.setResource_type(resourceType);
        userRolesDAO.insert(bean);
        LOG.info("Successfully created new user permission for resource {} with {}",
            resourceId, bean);
        UserRolesBean newBean = userRolesDAO.getByNameAndResource(bean.getUser_name(), resourceId, resourceType);
        UriBuilder ub = uriInfo.getAbsolutePathBuilder();
        URI roleUri = ub.path(newBean.getUser_name()).build();
        return Response.created(roleUri).entity(newBean).build();
    }

    public void delete(SecurityContext sc, String userName, String resourceId,
        Resource.Type resourceType) throws Exception {
        authorizer.authorize(sc, new Resource(resourceId, resourceType), Role.ADMIN);
        userRolesDAO.delete(userName, resourceId, resourceType);
        LOG.info("Successfully deleted user {} permission for resource {}",
            userName, resourceId);
    }
}