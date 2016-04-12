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
package com.pinterest.deployservice.db;

import com.pinterest.deployservice.bean.ProxyLogBean;
import com.pinterest.deployservice.bean.SetClause;
import com.pinterest.deployservice.dao.ProxyLogDAO;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DBProxyLogDAOImpl implements ProxyLogDAO {
    private static final Logger LOG = LoggerFactory.getLogger(DBProxyLogDAOImpl.class);

    private static final String INSERT_PROXY_LOG_TEMPLATE =
        "INSERT INTO proxy_log SET %s";


    private static final String GET_PROXY_LOG_BY_NAME_IP_DATE =
            "SELECT * FROM proxy_log WHERE ip=? AND proxy_name=? AND " +
                    "create_time<=? AND create_time>=? ORDER BY create_time";

    private static final String GET_PROXY_LOG_BY_IP_DATE =
            "SELECT * FROM proxy_log WHERE ip=? AND " +
                    "create_time<=? AND create_time>=? ORDER BY create_time";


    private static final String GET_PROXY_BY_IP =
            "SELECT distinct(proxy_name) FROM proxy_log WHERE ip=?";


    private BasicDataSource dataSource;

    public DBProxyLogDAOImpl(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(ProxyLogBean proxyLogBean) throws Exception {
        SetClause setClause = proxyLogBean.genSetClause();
        String clause = String.format(INSERT_PROXY_LOG_TEMPLATE, setClause.getClause());
        LOG.info("-------------------clause={}, proxyLogBean={}",clause, proxyLogBean);
        new QueryRunner(dataSource).update(clause, setClause.getValueArray());
    }

    @Override
    public List<ProxyLogBean> getByNameDateIp(String proxyName, String ip, long before, long after) throws Exception {
        QueryRunner run = new QueryRunner(this.dataSource);
        ResultSetHandler<List<ProxyLogBean>> h = new BeanListHandler<>(ProxyLogBean.class);
            return run.query(GET_PROXY_LOG_BY_NAME_IP_DATE, h, ip, proxyName, before, after);
    }

    @Override
    public List<ProxyLogBean> getByDateIp(String ip, long before, long after) throws Exception {
        QueryRunner run = new QueryRunner(this.dataSource);
        ResultSetHandler<List<ProxyLogBean>> h = new BeanListHandler<>(ProxyLogBean.class);
        return run.query(GET_PROXY_LOG_BY_IP_DATE, h, ip, before, after);
    }


    @Override
    public List<String> getProxyByIp(String ip) throws Exception {
        return new QueryRunner(this.dataSource).query(GET_PROXY_BY_IP,SingleResultSetHandlerFactory.<String>newListObjectHandler(),ip);
    }

}
