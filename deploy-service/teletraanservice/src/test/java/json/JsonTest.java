package json;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pinterest.deployservice.bean.ProxyLogBean;
import com.pinterest.deployservice.common.CommonUtils;
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
                "MjQvMDMvMjAxNiA5OS40NSA5OS4zNCA5OC4zOCA5OS44MSA5OS42NiA5OS44NiA5OS4yMSA5OS45NCA5OS44IDk5LjU5IDk3LjgyIDk4LjU3IDk1Ljk5IDk5LjQ1IDk3LjgzIDk5LjU2IDkyLjkzIDEwMC4wIDk5LjMyIDk3Ljg=";
        json = new String(CommonUtils.decode(json));
        System.out.println(json);
//        Gson gson = new Gson();
//        Map<String, ProxyLogBean> proxyMap =
//                gson.fromJson(json, new TypeToken<Map<String, ProxyLogBean>>() {}.getType());
//        for (Map.Entry<String, ProxyLogBean> entry : proxyMap.entrySet()) {
//            System.out.println(entry.getKey());
//            System.out.println(entry.getValue());
//        }
//        System.out.println(proxyMap.keySet());
        List<String> list = Arrays.asList(json.split(" "));
        System.out.println(list.get(1));
        System.out.println(CommonUtils.getDayBeforeToday(7));
    }
}
