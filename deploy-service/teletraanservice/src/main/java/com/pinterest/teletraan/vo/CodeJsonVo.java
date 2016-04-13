package com.pinterest.teletraan.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * 16/4/13 下午5:44
 * aiguoxin
 * 说明:
 */
@Data
public class CodeJsonVo {

    @JsonProperty("request_count")
    private Float request_count;

    @JsonProperty("code")
    private Map<String,Float> code;

    @JsonProperty("request_success_rate")
    private Float request_success_rate;

    @JsonProperty("upstream_response_time")
    private Float upstream_response_time;

    @JsonProperty("response_time")
    private Float response_time;
}
