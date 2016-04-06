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
    private String proxy_name;

    @JsonProperty("successCount")
    private int success_count;

    @JsonProperty("errorCount")
    private int error_count;

    @JsonProperty("qps")
    private int qps;

    @JsonProperty("errorRate")
    private Float error_rate;

    @JsonProperty("successRate")
    private Float success_rate;

    @JsonProperty("safeModeTimeSlotLeft")
    private int slot_left;

    @JsonProperty("createTime")
    private Long create_time;

    @JsonProperty("updateTime")
    private Date update_time;

    @NotEmpty
    @JsonProperty("ip")
    private String ip;

    @Override
    public SetClause genSetClause() {
        SetClause clause = new SetClause();
        clause.addColumn("proxy_name", proxy_name);
        clause.addColumn("success_count", success_count);
        clause.addColumn("error_count", error_count);
        clause.addColumn("qps", qps);
        clause.addColumn("error_rate", error_count);
        clause.addColumn("success_rate", success_rate);
        clause.addColumn("slot_left", slot_left);
        clause.addColumn("create_time", create_time);
        clause.addColumn("update_time", update_time);
        clause.addColumn("ip", ip);
        return clause;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
