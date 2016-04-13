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
package com.pinterest.deployservice.dao;

import com.pinterest.deployservice.bean.InterfaceCodeBean;
import com.pinterest.deployservice.bean.InterfaceInfoBean;

import java.util.List;

/**
 * A collection of methods to help interact with table BUILDS
 */
public interface InterfaceCodeDAO {
    void insert(InterfaceCodeBean interfaceCodeBean) throws Exception;

    List<InterfaceCodeBean> getByNameIpTagDate(String name, String ip, String codeTag, Long before, Long after) throws Exception;

    Double countByNameIpTagDate(String name, String ip, String codeTag, Long before, Long after) throws Exception;

    List<String> getNameListByIp(String ip) throws Exception;

    List<String> getCodeListByIpInterface(String ip, String interfaceName) throws Exception;
}
