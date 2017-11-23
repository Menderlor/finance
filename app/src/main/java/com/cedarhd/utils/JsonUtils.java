package com.cedarhd.utils;

import android.util.Log;

import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.models.FieldInfo;
import com.cedarhd.models.ReturnModel;
import com.cedarhd.models.crm.VmBase;
import com.cedarhd.models.日志;
import com.cedarhd.models.评论列表;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtils {

    public final static String KEY_STATUS = "Status";
    public final static String KEY_MESSAGE = "Message";
    public final static String KEY_DATA = "Data";

    /****
     * 根据键值获取json中对应的value
     *
     * @param json
     * @param key
     * @return
     * @throws JSONException
     */
    public static String getStringValue(String json, String key)
            throws JSONException {
        JSONObject jo = new JSONObject(json);
        return jo.getString(key);
    }

    /****
     * 根据键值获取json中对应的value
     *
     * @param json
     * @param key
     * @return
     * @throws JSONException
     */
    public static String putStringValue(String json, String data, String key)
            throws JSONException {
        JSONObject jo = new JSONObject(json);
        String str = jo.getString(key);
        return json.replace("\"" + str + "\"", data);
    }

    /**
     * 解析表单中字典的json数据
     *
     * @param jsonString 数据
     * @return
     */
    public static HashMap<String, HashMap<String, String>> ConvertJsonToMap(
            String jsonString) {
        HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
        JSONObject jsonObjectsStart;
        try {
            // 解析外面一层
            jsonObjectsStart = new JSONObject(jsonString);
            Iterator iteratorStart = jsonObjectsStart.keys();
            while (iteratorStart.hasNext()) {
                String objectStart = (String) iteratorStart.next();
                // 解析里面
                JSONObject object = jsonObjectsStart.getJSONObject(objectStart);
                Iterator<String> iterator = object.keys();
                HashMap<String, String> arrayMap = new HashMap<String, String>();
                while (iterator.hasNext()) {
                    String object2 = (String) iterator.next();
                    arrayMap.put(object2, object.getString(object2));
                }
                map.put(objectStart, arrayMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 解析表单中Fields的json数据,转为FieldInfo对象的字段名和字段值的map集合，必须是FieldInfo类型
     *
     * @param jsonStr json字符串
     * @return
     */
    public static HashMap<String, String> ConvertFieldsJson2Map(String jsonStr) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<FieldInfo>>() {
        }.getType(); // 指定集合对象属性
        List<FieldInfo> list = gson.fromJson(jsonStr, type);
        HashMap<String, String> map = new HashMap<String, String>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String name = list.get(i).fieldName;
                String value = list.get(i).fieldValue;
                map.put(name, value);
            }
        }
        return map;
    }

    /**
     * 解析json字符串（通用泛型方法）
     *
     * @param json 波尔云服务器返回的json字符串
     * @param type 实体类型
     * @return
     */
    public static <T> List<T> ConvertJsonToList(String json, Class<T> type) {
        json = json + "";
        json = json.trim();
        try {
            JSONObject jsonObjectsStart = new JSONObject(json);
            json = jsonObjectsStart.get("Data").toString();
            LogUtils.i("2keno22", json);
            /**
             * 日志内容换行
             */
            LogUtils.d("3keno3", json);
            if (type.getClass().equals(日志.class.getName())) {
                if (json.contains("\\r")) {
                    json = json.replace("\\r", "\\n\\r");
                    LogUtils.d("3keno3", json);
                }
            }

            GsonBuilder gsonb = new GsonBuilder();

            String regEx = "[0-9]{2}T[0-9]{2}"; // 表示11T11这样的数据
            Pattern pat = Pattern.compile(regEx);

            Gson gson = gsonb.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JSONArray jsonArray = new JSONArray(json);
            ArrayList<T> list = new ArrayList<T>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String str = jsonObject.toString();
                Matcher mat = pat.matcher(jsonObject.toString());
                while (mat.find()) {
                    String temp = str.substring(mat.start(), mat.end());
                    str = str.replaceAll(temp, temp.replace("T", " "));// temp.substring(0,temp.lastIndexOf("T"))+" ");
                }
                list.add(gson.fromJson(str, type));
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("JsonUtils", ex.getMessage() + "\r\n" + ex.getStackTrace());
            return null;
        }
    }

    /***
     * 把新版Crm接口中带字典集合 的数据源转为通用VmBase实体
     *
     * @param json
     *            数据源
     * @param type
     *            泛型类型
     * @return
     * @throws JSONException
     */
    public static <T> VmBase<T> convertJsonToVmBase(String json, Class<T> type)
            throws JSONException {
        VmBase<T> vmBase = new VmBase<T>();
        List<T> list = JsonUtils.ConvertJsonToList(
                StrUtils.removeRex(JsonUtils.getStringValue(json, "Data")),
                type);
        vmBase.Data = list;
        List<? extends VmBase> baseList = JsonUtils.ConvertJsonToList(json,
                vmBase.getClass());
        if (baseList != null && baseList.size() > 0) {
            vmBase.Dict = baseList.get(0).Dict;
        }
        return vmBase;
    }

    /**
     * 解析json字符串（通用泛型方法）
     *
     * @param json 正常的包括一个集合形式的json字符串，
     * @param type 实体类型
     * @return
     */
    public static <T> List<T> pareseJsonToList(String jsonString, Class<T> type) {
        jsonString = jsonString + "";
        jsonString = jsonString.trim();
        try {
            GsonBuilder gsonb = new GsonBuilder();
            String regEx = "[0-9]{2}T[0-9]{2}"; // 表示11T11这样的数据
            Pattern pat = Pattern.compile(regEx);

            Gson gson = gsonb.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList<T> list = new ArrayList<T>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String str = jsonObject.toString();
                Matcher mat = pat.matcher(jsonObject.toString());
                while (mat.find()) {
                    String temp = str.substring(mat.start(), mat.end());
                    str = str.replaceAll(temp, temp.replace("T", " "));// temp.substring(0,temp.lastIndexOf("T"))+" ");
                }
                list.add(gson.fromJson(str, type));
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("JsonUtils", ex.getMessage() + "\r\n" + ex.getStackTrace());
            return null;
        }
    }

    /**
     * （通用方法）解析Json字符串为指定对象
     *
     * @param json
     * @param type
     * @return
     */
    public static <T> T ConvertJsonObject(String json, Class<T> type) {
        json = json + "";
        json = json.trim();
        try {
            JSONObject jsonObjectsStart = new JSONObject(json);
            json = jsonObjectsStart.get("Data").toString();

            json = StrUtils.removeRex(json);
            /**
             * 日志内容换行
             */
            if (type.getClass().equals(日志.class.getName())) {
                if (json.contains("\\r")) {
                    json = json.replace("\\r", "\\n\\r");
                    LogUtils.d("3keno3", json);
                }
            }

            GsonBuilder gsonb = new GsonBuilder();

            String regEx = "[0-9]{2}T[0-9]{2}"; // 表示11T11这样的数据
            Pattern pat = Pattern.compile(regEx);
            Matcher mat = pat.matcher(json);
            while (mat.find()) {
                String temp = json.substring(mat.start(), mat.end());
                json = json.replaceAll(temp, temp.replace("T", " "));// temp.substring(0,temp.lastIndexOf("T"))+" ");
            }
            Gson gson = gsonb.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            return gson.fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("JsonUtils", ex.getMessage() + "\r\n" + ex.getStackTrace());
            return null;
        }
    }

    /**
     * （通用方法）解析Json字符串为指定对象
     *
     * @param json
     * @param type
     * @return
     */
    public static <T> T ConvertJsonObject(String json, Type type) {
        json = json + "";
        json = json.trim();
        try {
            JSONObject jsonObjectsStart = new JSONObject(json);
            json = jsonObjectsStart.get("Data").toString();

            json = StrUtils.removeRex(json);

            GsonBuilder gsonb = new GsonBuilder();

            String regEx = "[0-9]{2}T[0-9]{2}"; // 表示11T11这样的数据
            Pattern pat = Pattern.compile(regEx);
            Matcher mat = pat.matcher(json);
            while (mat.find()) {
                String temp = json.substring(mat.start(), mat.end());
                json = json.replaceAll(temp, temp.replace("T", " "));// temp.substring(0,temp.lastIndexOf("T"))+" ");
            }
            Gson gson = gsonb.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            return gson.fromJson(json, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("JsonUtils", ex.getMessage() + "\r\n" + ex.getStackTrace());
            return null;
        }
    }

    // TODO 待移除
    public static 评论列表 parseContentFromJson(String data) {
        Type listType = new TypeToken<评论列表>() {
        }.getType();

        GsonBuilder gsonb = new GsonBuilder();
        DateDeserializer ds = new DateDeserializer();
        gsonb.registerTypeAdapter(Date.class, ds);
        Gson gson = gsonb.create();
        评论列表 item = gson.fromJson(data, listType);
        return item;
    }

    /**
     * 针对Gson对泛型的支持不足
     *
     * @param raw
     * @param args
     * @return
     */
    static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }

    /**
     * 解析结果状态
     * <p>
     * 获得Status:0为失败，1代表成功
     * <p>
     * Message：服务器返回信息
     *
     * @param json
     * @return
     */
    public static ReturnModel<String> pareseResult(String json) {
        ReturnModel<String> returnModel = new ReturnModel<String>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            returnModel.Status = jsonObject.getInt("Status");
            returnModel.Message = jsonObject.getString("Message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnModel;
    }

    @Deprecated
    // 使用getStringValue(json)方法替代即可
    public static String parseLoginMessage(String data) {
        String dataString = "";
        try {
            // 将服务器的json解析，拿取Data
            JSONObject jsonObject = new JSONObject(data);
            dataString = jsonObject.get("Message").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataString == null ? "" : dataString;
    }

    /**
     * 解析状态码 ReturenModel中的状态码
     *
     * @param data
     * @return
     */
    @Deprecated
    // 使用getStringValue(json)方法替代即可
    public static String parseStatus(String data) {
        String dataString = "";
        try {
            // 将服务器的json解析，拿取Data
            JSONObject jsonObject = new JSONObject(data);
            dataString = jsonObject.get("Status").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataString == null ? "" : dataString;
    }

    /**
     * 解析结果状态
     * <p>
     * 获得Data中的字符串
     *
     * @param json
     * @return
     */
    public static String pareseData(String json) {
        String result = "";
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            result = jsonObject.getString("Data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解析结果状态
     * <p>
     * 获得Message中的字符串
     *
     * @param json
     * @return
     */
    public static String pareseMessage(String json) {
        String result = "";
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            result = jsonObject.getString("Message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 保存实体对象转化为JSONObject对象, 需要转化的实体对象必须所有属性public修饰
     *
     * @param info 实体对象（）
     * @param c    实体对象类型
     * @return Json对象
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws JSONException
     */
    public static JSONObject initJsonObj(Object info, Class c)
            throws IllegalArgumentException, IllegalAccessException,
            JSONException {
        JSONObject jo = new JSONObject();
        Field[] fields = c.getFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object obj = field.get(info);
            jo.put(field.getName(), obj);
            LogUtils.i("JsonUtils", field.getName() + "---" + obj);
        }
        return jo;
    }

    /**
     * 保存实体对象转化为JSONObject对象[Google库转换]
     *
     * @param info 实体对象（）
     * @param c
     * @return Json对象
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws JSONException
     */
    public static JSONObject initJsonObj(Object info)
            throws IllegalArgumentException, IllegalAccessException,
            JSONException {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(info);
        JSONObject jo = new JSONObject(jsonStr);
        return jo;
    }

    /**
     * 保存实体对象转化为JSONObject对象[Google库转换]
     *
     * @param info 实体对象（）
     * @param c
     * @return Json对象
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws JSONException
     */
    public static String initJsonString(Object info)
            throws IllegalArgumentException, IllegalAccessException,
            JSONException {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(info);
        return jsonStr;
    }

}
