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
public class InterfaceInfoBean implements Updatable {
    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("successRate")
    private Float success_rate;

    @JsonProperty("createTime")
    private String  create_time;

    @JsonProperty("updateTime")
    private Date update_time;

    @JsonProperty("flag")
    private int flag;

    @Override
    public SetClause genSetClause() {
        SetClause clause = new SetClause();
        clause.addColumn("name", name);
        clause.addColumn("flag", flag);
        clause.addColumn("success_rate", success_rate);
        clause.addColumn("create_time", create_time);
        clause.addColumn("update_time", update_time);
        return clause;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }


    public enum FlagState{
        OVERSEA(1), HOME(0);

        private int val;
        private FlagState(int val){
            this.val = val;
        }

        public int val(){
            return this.val;
        }
    }
}
