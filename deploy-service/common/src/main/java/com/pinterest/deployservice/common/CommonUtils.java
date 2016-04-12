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
package com.pinterest.deployservice.common;

import com.amazonaws.util.StringUtils;
import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.text.SimpleDateFormat;

public class CommonUtils {
    /**
     * TODO figure out how to use guava to achive this
     *
     * @return base64 encoded shorten UUID, e.g. 11YozyYYTvKmuUXpRDvoJA
     */
    public static String getBase64UUID() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        String base64 = BaseEncoding.base64Url().omitPadding().encode(buffer.array());
        if (base64.charAt(0) == '_') {
            return 'x' + base64.substring(1);
        }
        if (base64.charAt(0) == '-') {
            return 'X' + base64.substring(1);
        }
        return base64;
    }

    public static String getShaHex(byte[] bytes) {
        return DigestUtils.sha1Hex(bytes);
    }

    public static String encodeData(Map<String, String> dataMap) {
        return new Gson().toJson(dataMap);
    }

    public static Map<String, String> decodeData(String data) {
        return new Gson().fromJson(data, new TypeToken<Map<String, String>>() {}.getType());
    }

    public static String getValue(Map<String, String> map, String name, String defaultValue) {
        String value = map.get(name);
        return value == null ? defaultValue : value;
    }

    public static Map<String, String> encodeScript(Map<String, String> data) throws Exception {
        Map<String, String> encoded = new HashMap<String, String>(data.size());
        for (Map.Entry<String, String> entry : data.entrySet()) {
            encoded.put(entry.getKey(), Base64.encodeBase64URLSafeString(entry.getValue().getBytes("UTF8")));
        }
        return encoded;
    }

    public static Map<String, String> decodeScript(Map<String, String> data) throws Exception {
        Map<String, String> decoded = new HashMap<String, String>(data.size());
        for (Map.Entry<String, String> entry : data.entrySet()) {
            decoded.put(entry.getKey(), new String(Base64.decodeBase64(entry.getValue().getBytes("UTF8")), "UTF8"));
        }
        return decoded;
    }

    public static String determineScm(String repo) {
        if(repo.contains("/")) {
            return "Github";
        }
        else {
            return "Phabricator";
        }
    }

    public static Long convertDateStringToMilliseconds(String dateString) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS");
        Date date = formatter.parse(dateString);
        return date.getTime();
    }

    public static byte[] decode(String str) {
        byte[] bt = null;
        try {
            sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
            bt = decoder.decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bt;
    }

    public static long getTimesmorning(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) (cal.getTimeInMillis()/1000);
    }

    public static String getDayBeforeToday(int day){
        String dayStr = new DateTime().minusDays(day).toString("yyyy-mm-dd");
        return dayStr;
    }

    public static String changeDateFromat(String dateStr){
        if(StringUtils.isNullOrEmpty(dateStr)){
            return "";
        }
        DateTimeFormatter format = DateTimeFormat.forPattern("dd/mm/yyyy");
        DateTime dateTime = DateTime.parse(dateStr, format);
        return dateTime.toString("yyyy-mm-dd");
    }
}
