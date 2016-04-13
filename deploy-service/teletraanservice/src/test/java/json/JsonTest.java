package json;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pinterest.deployservice.bean.ProxyLogBean;
import com.pinterest.deployservice.common.CommonUtils;
import com.pinterest.teletraan.vo.CodeJsonVo;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 16/4/3 下午9:01 aiguoxin 说明:
 */
public class JsonTest {

    public static void main(String[] args) throws IOException {
        String json =
                "eyJhbGwiOiB7InJlcXVlc3Rfc3VjY2VzcyI6IDExLjI2NjY2NjY2NjY2NjY2NywgImNvZGUiOiB7IjIwMCI6IDExLjI2NjY2NjY2NjY2NjY2NywgIjQ5OSI6IDAuMTE2NjY2NjY2NjY2NjY2Njd9LCAicmVxdWVzdF9mYWlsdXJlIjogMC4xMTY2NjY2NjY2NjY2NjY2NywgInJlcXVlc3Rfc3VjY2Vzc19yYXRlIjogOTguOTc1MTA5ODA5NjYzMjUsICJyZXF1ZXN0X2NvdW50IjogMTEuMzgzMzMzMzMzMzMzMzMzLCAidXBzdHJlYW1fcmVzcG9uc2VfdGltZSI6IDI1NS45MTcxNTk3NjMzMTM4MywgInJlc3BvbnNlX3RpbWUiOiAyNjUuMDkwMjM2Njg2MzkwN30sICJ2aWV3c19mZWVkXzMuMCI6IHsicmVxdWVzdF9zdWNjZXNzIjogMC4wMzMzMzMzMzMzMzMzMzMzMywgImNvZGUiOiB7IjIwMCI6IDAuMDMzMzMzMzMzMzMzMzMzMzN9LCAicmVxdWVzdF9mYWlsdXJlIjogMC4wLCAicmVxdWVzdF9zdWNjZXNzX3JhdGUiOiAxMDAuMCwgInJlcXVlc3RfY291bnQiOiAwLjAzMzMzMzMzMzMzMzMzMzMzLCAidXBzdHJlYW1fcmVzcG9uc2VfdGltZSI6IDQ0Ni41LCAicmVzcG9uc2VfdGltZSI6IDQ0Ni41fSwgInZpZXdzX3BvcF8zLjAiOiB7InJlcXVlc3Rfc3VjY2VzcyI6IDAuNTMzMzMzMzMzMzMzMzMzMywgImNvZGUiOiB7IjIwMCI6IDAuNTMzMzMzMzMzMzMzMzMzM30sICJyZXF1ZXN0X2ZhaWx1cmUiOiAwLjAsICJyZXF1ZXN0X3N1Y2Nlc3NfcmF0ZSI6IDEwMC4wLCAicmVxdWVzdF9jb3VudCI6IDAuNTMzMzMzMzMzMzMzMzMzMywgInVwc3RyZWFtX3Jlc3BvbnNlX3RpbWUiOiAyMzkuNDM3NTAwMDAwMDAwMSwgInJlc3BvbnNlX3RpbWUiOiAyNDAuNzE4NzUwMDAwMDAwMDl9LCAidmlld3NfMy4wIjogeyJyZXF1ZXN0X3N1Y2Nlc3MiOiA0LjQ1LCAiY29kZSI6IHsiMjAwIjogNC40NSwgIjQ5OSI6IDAuMDMzMzMzMzMzMzMzMzMzMzN9LCAicmVxdWVzdF9mYWlsdXJlIjogMC4wMzMzMzMzMzMzMzMzMzMzMywgInJlcXVlc3Rfc3VjY2Vzc19yYXRlIjogOTkuMjU2NTA1NTc2MjA4MTgsICJyZXF1ZXN0X2NvdW50IjogNC40ODMzMzMzMzMzMzMzMzMsICJ1cHN0cmVhbV9yZXNwb25zZV90aW1lIjogMjczLjcxNTM1NTgwNTI0MzQsICJyZXNwb25zZV90aW1lIjogMjgwLjU1ODA1MjQzNDQ1Njh9LCAidmlld3Nfc2VhcmNoXzMuMCI6IHsicmVxdWVzdF9zdWNjZXNzIjogMC4yLCAiY29kZSI6IHsiMjAwIjogMC4yfSwgInJlcXVlc3RfZmFpbHVyZSI6IDAuMCwgInJlcXVlc3Rfc3VjY2Vzc19yYXRlIjogMTAwLjAsICJyZXF1ZXN0X2NvdW50IjogMC4yLCAidXBzdHJlYW1fcmVzcG9uc2VfdGltZSI6IDI5NS4zMzMzMzMzMzMzMzMzNywgInJlc3BvbnNlX3RpbWUiOiAyOTYuNX0sICJ2aWV3c19idXNfMy4wIjogeyJyZXF1ZXN0X3N1Y2Nlc3MiOiAwLjAxNjY2NjY2NjY2NjY2NjY2NiwgImNvZGUiOiB7IjIwMCI6IDAuMDE2NjY2NjY2NjY2NjY2NjY2fSwgInJlcXVlc3RfZmFpbHVyZSI6IDAuMCwgInJlcXVlc3Rfc3VjY2Vzc19yYXRlIjogMTAwLjAsICJyZXF1ZXN0X2NvdW50IjogMC4wMTY2NjY2NjY2NjY2NjY2NjYsICJ1cHN0cmVhbV9yZXNwb25zZV90aW1lIjogNDA1LjAsICJyZXNwb25zZV90aW1lIjogNDA1LjB9LCAidmlld3NfMi4wIjogeyJyZXF1ZXN0X3N1Y2Nlc3MiOiAwLjM4MzMzMzMzMzMzMzMzMzM2LCAiY29kZSI6IHsiMjAwIjogMC4zODMzMzMzMzMzMzMzMzMzNn0sICJyZXF1ZXN0X2ZhaWx1cmUiOiAwLjAsICJyZXF1ZXN0X3N1Y2Nlc3NfcmF0ZSI6IDEwMC4wLCAicmVxdWVzdF9jb3VudCI6IDAuMzgzMzMzMzMzMzMzMzMzMzYsICJ1cHN0cmVhbV9yZXNwb25zZV90aW1lIjogMzYzLjkxMzA0MzQ3ODI2MDksICJyZXNwb25zZV90aW1lIjogNDE3LjUyMTczOTEzMDQzNDg3fSwgInBocF94eXoiOiB7InJlcXVlc3Rfc3VjY2VzcyI6IDAuODgzMzMzMzMzMzMzMzMzMywgImNvZGUiOiB7IjIwMCI6IDAuODgzMzMzMzMzMzMzMzMzMywgIjQ5OSI6IDAuMDE2NjY2NjY2NjY2NjY2NjY2fSwgInJlcXVlc3RfZmFpbHVyZSI6IDAuMDE2NjY2NjY2NjY2NjY2NjY2LCAicmVxdWVzdF9zdWNjZXNzX3JhdGUiOiA5OC4xNDgxNDgxNDgxNDgxNSwgInJlcXVlc3RfY291bnQiOiAwLjksICJ1cHN0cmVhbV9yZXNwb25zZV90aW1lIjogMzQwLjczNTg0OTA1NjYwMzgsICJyZXNwb25zZV90aW1lIjogMzc3LjkyNDUyODMwMTg4Njl9LCAidmlkZW9fMy4wIjogeyJyZXF1ZXN0X3N1Y2Nlc3MiOiAzLjk1LCAiY29kZSI6IHsiMjAwIjogMy45NSwgIjQ5OSI6IDAuMDY2NjY2NjY2NjY2NjY2Njd9LCAicmVxdWVzdF9mYWlsdXJlIjogMC4wNjY2NjY2NjY2NjY2NjY2NywgInJlcXVlc3Rfc3VjY2Vzc19yYXRlIjogOTguMzQwMjQ4OTYyNjU1NjEsICJyZXF1ZXN0X2NvdW50IjogNC4wMTY2NjY2NjY2NjY2NjcsICJ1cHN0cmVhbV9yZXNwb25zZV90aW1lIjogMjEwLjU2MTE4MTQzNDU5OTU1LCAicmVzcG9uc2VfdGltZSI6IDIxMC43OTMyNDg5NDUxNDh9LCAiZnVzaW9uXzMuMCI6IHsicmVxdWVzdF9zdWNjZXNzIjogMC44MTY2NjY2NjY2NjY2NjY3LCAiY29kZSI6IHsiMjAwIjogMC44MTY2NjY2NjY2NjY2NjY3fSwgInJlcXVlc3RfZmFpbHVyZSI6IDAuMCwgInJlcXVlc3Rfc3VjY2Vzc19yYXRlIjogMTAwLjAsICJyZXF1ZXN0X2NvdW50IjogMC44MTY2NjY2NjY2NjY2NjY3LCAidXBzdHJlYW1fcmVzcG9uc2VfdGltZSI6IDIyNi4xNjMyNjUzMDYxMjI1LCAicmVzcG9uc2VfdGltZSI6IDI0Ny43OTU5MTgzNjczNDY5OX19";
        json = new String(CommonUtils.decode(json));
        System.out.println(json);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        Map<String, CodeJsonVo> proxyMap = objectMapper.readValue(json, Map.class);
        for(Map.Entry<String, CodeJsonVo> entry  : proxyMap.entrySet()){
            System.out.println(entry.getKey());
            CodeJsonVo codeJsonVo = objectMapper.readValue(JSON.toJSONString(entry.getValue()), CodeJsonVo.class);
            for(Map.Entry<String,Float> codeEntry : codeJsonVo.getCode().entrySet()){
                System.out.println(codeEntry.getKey());
                System.out.println(codeEntry.getValue());
            }
        }
    }
}
