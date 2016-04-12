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

import com.pinterest.deployservice.bean.InterfaceInfoBean;
import com.pinterest.deployservice.bean.SetClause;
import com.pinterest.deployservice.dao.InterfaceInfoDAO;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DBInterfaceInfoDAOImpl implements InterfaceInfoDAO {
    private static final Logger LOG = LoggerFactory.getLogger(DBInterfaceInfoDAOImpl.class);

    private static final String INSERT_INTERFACE_INFO_TEMPLATE =
        "INSERT INTO interface_info SET %s";


    private static final String GET_INFO_BY_NAME_FLAG_DATE =
            "SELECT * FROM interface_info WHERE name=? AND flag=? AND " +
                    "create_time<=? AND create_time>=? ORDER BY create_time";


    private static final String GET_INTERFACE_BY_FLAG =
            "SELECT distinct(name) FROM interface_info WHERE flag=?";


    private BasicDataSource dataSource;

    public DBInterfaceInfoDAOImpl(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void insert(InterfaceInfoBean interfaceInfoBean) throws Exception {
        SetClause setClause = interfaceInfoBean.genSetClause();
        String clause = String.format(INSERT_INTERFACE_INFO_TEMPLATE, setClause.getClause());
        new QueryRunner(dataSource).update(clause, setClause.getValueArray());
    }

    @Override
    public List<InterfaceInfoBean> getByNameFlagDate(String name, int flag, String before, String after) throws Exception {
        QueryRunner run = new QueryRunner(this.dataSource);
        ResultSetHandler<List<InterfaceInfoBean>> h = new BeanListHandler<>(InterfaceInfoBean.class);
        return run.query(GET_INFO_BY_NAME_FLAG_DATE, h, name, flag, before, after);
    }

    @Override
    public List<String> getNameListByFlag(int flag) throws Exception {
        return new QueryRunner(this.dataSource).query(GET_INTERFACE_BY_FLAG,SingleResultSetHandlerFactory.<String>newListObjectHandler(),flag);
    }
}
