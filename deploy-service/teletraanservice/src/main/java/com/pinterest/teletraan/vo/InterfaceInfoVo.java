package com.pinterest.teletraan.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;


/**
 * 16/4/3 下午8:47
 * aiguoxin
 * 说明:
 */
@Data
public class InterfaceInfoVo {
    @JsonProperty("createTime")
    private Long create_time;

    @NotEmpty
    @JsonProperty("proxyJson")
    private String proxy_json;
}
