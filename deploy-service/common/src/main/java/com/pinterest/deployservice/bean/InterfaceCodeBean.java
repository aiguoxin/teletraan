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
public class InterfaceCodeBean implements Updatable {
    @JsonProperty("id")
    private int id;

    @JsonProperty("interfaceName")
    private String interface_name;

    @JsonProperty("codeTag")
    private String code_tag;

    @JsonProperty("codeCount")
    private Float code_count;

    @JsonProperty("createTime")
    private Long create_time;

    @JsonProperty("updateTime")
    private Date update_time;

    @NotEmpty
    @JsonProperty("ip")
    private String ip;

    @JsonProperty("upstreamResponseTime")
    private Float upstream_response_time;

    @JsonProperty("responseTime")
    private Float response_time;

    @Override
    public SetClause genSetClause() {
        SetClause clause = new SetClause();
        clause.addColumn("interface_name", interface_name);
        clause.addColumn("code_tag", code_tag);
        clause.addColumn("code_count", code_count);
        clause.addColumn("create_time", create_time);
        clause.addColumn("update_time", update_time);
        clause.addColumn("ip", ip);
        clause.addColumn("upstream_response_time", upstream_response_time);
        clause.addColumn("response_time", response_time);

        return clause;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
