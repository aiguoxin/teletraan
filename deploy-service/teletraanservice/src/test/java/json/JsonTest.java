package json;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pinterest.deployservice.bean.ProxyLogBean;
import com.pinterest.deployservice.common.CommonUtils;

import java.io.IOException;
import java.util.Map;

/**
 * 16/4/3 下午9:01 aiguoxin 说明:
 */
public class JsonTest {

    public static void main(String[] args) throws IOException {
        String json =
                "eydQbGF5SURNb2RlbEJhdGNoUHJveHknOiB7J3N1Y2Nlc3NSYXRlJzogMTAwLjAsICdzdWNjZXNzQ291bnQnOiAyMiwgJ2Vycm9yUmF0ZSc6IDAuMCwgJ3Fwcyc6IDAsICdlcnJvckNvdW50JzogMCwgJ3NhZmVNb2RlVGltZVNsb3RMZWZ0JzogMH19=";
        json = new String(CommonUtils.decode(json));
        System.out.println(json);
        Gson gson = new Gson();
        Map<String, ProxyLogBean> proxyMap =
                gson.fromJson(json, new TypeToken<Map<String, ProxyLogBean>>() {}.getType());
        for (Map.Entry<String, ProxyLogBean> entry : proxyMap.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        System.out.println(proxyMap.keySet());

    }
}
