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

import com.pinterest.deployservice.bean.InterfaceCodeBean;
import com.pinterest.deployservice.bean.SetClause;
import com.pinterest.deployservice.dao.InterfaceCodeDAO;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DBInterfaceCodeDAOImpl implements InterfaceCodeDAO {
    private static final Logger LOG = LoggerFactory.getLogger(DBInterfaceCodeDAOImpl.class);

    private static final String INSERT_INTERFACE_CODE_TEMPLATE =
        "INSERT INTO interface_code SET %s";


    private static final String GET_INTERFACE_CODE_BY_NAME_IP_DATE =
            "SELECT * FROM interface_code WHERE ip=? AND interface_name=? AND code_tag=? AND " +
                    "create_time<=? AND create_time>=? ORDER BY create_time";

    private static final String COUNT_INTERFACE_CODE_BY_NAME_IP_DATE =
            "SELECT sum(code_count) FROM interface_code WHERE ip=? AND interface_name=? AND code_tag=? AND " +
                    "create_time<=? AND create_time>=? ORDER BY create_time";


    private static final String GET_INTERFACE_BY_IP =
            "SELECT distinct(interface_name) FROM interface_code WHERE ip=?";

    private static final String GET_TAG_BY_IP_INTERFACE =
            "SELECT distinct(code_tag) FROM interface_code WHERE ip=? and interface_name=?";


    private BasicDataSource dataSource;

    public DBInterfaceCodeDAOImpl(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void insert(InterfaceCodeBean interfaceCodeBean) throws Exception {
        SetClause setClause = interfaceCodeBean.genSetClause();
        String clause = String.format(INSERT_INTERFACE_CODE_TEMPLATE, setClause.getClause());
        new QueryRunner(dataSource).update(clause, setClause.getValueArray());
    }

    @Override
    public List<InterfaceCodeBean> getByNameIpTagDate(String name, String ip, String codeTag, Long before, Long after) throws Exception {
        QueryRunner run = new QueryRunner(this.dataSource);
        ResultSetHandler<List<InterfaceCodeBean>> h = new BeanListHandler<>(InterfaceCodeBean.class);
        return run.query(GET_INTERFACE_CODE_BY_NAME_IP_DATE, h, ip, name,codeTag, before, after);
    }

    @Override
    public Double countByNameIpTagDate(String name, String ip, String codeTag, Long before, Long after) throws Exception {
        Double n = new QueryRunner(dataSource).query(COUNT_INTERFACE_CODE_BY_NAME_IP_DATE,
                SingleResultSetHandlerFactory.<Double>newObjectHandler(), ip,name,codeTag,before,after);
        return n == null ? 0 : n;
    }

    @Override
    public List<String> getNameListByIp(String ip) throws Exception {
        return new QueryRunner(this.dataSource).query(GET_INTERFACE_BY_IP,SingleResultSetHandlerFactory.<String>newListObjectHandler(),ip);
    }

    @Override
    public List<String> getCodeListByIpInterface(String ip, String interfaceName) throws Exception {
        return new QueryRunner(this.dataSource).query(GET_TAG_BY_IP_INTERFACE,SingleResultSetHandlerFactory.<String>newListObjectHandler(),ip,interfaceName);
    }
}
