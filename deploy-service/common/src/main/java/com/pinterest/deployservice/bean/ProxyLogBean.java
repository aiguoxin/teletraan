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
package com.pinterest.deployservice.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

@Data
public class ProxyLogBean implements Updatable {
    @JsonProperty("id")
    private int id;

    @JsonProperty("proxyName")
    private String proxyName;

    @JsonProperty("successCount")
    private int successCount;

    @JsonProperty("errorCount")
    private int errorCount;

    @JsonProperty("qps")
    private int qps;

    @JsonProperty("errorRate")
    private Float errorRate;

    @JsonProperty("successRate")
    private Float successRate;

    @JsonProperty("slotLeft")
    private int slotLeft;

    @JsonProperty("createTime")
    private Long createTime;

    @JsonProperty("updateTime")
    private Date updateTime;

    @NotEmpty
    @JsonProperty("ip")
    private String ip;

    @Override
    public SetClause genSetClause() {
        SetClause clause = new SetClause();
        clause.addColumn("proxy_name", proxyName);
        clause.addColumn("success_count", successCount);
        clause.addColumn("error_count", errorCount);
        clause.addColumn("qps", qps);
        clause.addColumn("error_rate", errorCount);
        clause.addColumn("success_rate", successRate);
        clause.addColumn("slot_left", slotLeft);
        clause.addColumn("create_time", createTime);
        clause.addColumn("update_time", updateTime);
        clause.addColumn("ip", ip);
        return clause;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
