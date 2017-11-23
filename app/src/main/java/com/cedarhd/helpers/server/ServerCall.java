package com.cedarhd.helpers.server;

import android.text.TextUtils;

import com.cedarhd.helpers.Global;
import com.cedarhd.models.Demand;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServerCall {
    String URL = Global.IP;

    public JSONArray jArr;
    public JSONObject jobj;

    public ServerCall() {

    }

    /**
     * 向服务器提交请求
     *
     * @param demand
     * @return
     */
    public String makeServerCalll_Post(Demand demand) {
        String tableName = demand.表名;
        String URL = Global.BASE_URL + Global.EXTENSION + demand.方法名;
        LogUtils.i("ServerCallTestURLDemand", URL);
        try {
            JSONObject jo = new JSONObject();
            // jo.put("Id", notice.Id);
            jo.put("企业编号", Global.mUser.CorpId);
            jo.put("用户编号", demand.用户编号);
            jo.put("条件", demand.条件);
            jo.put("每页数量", demand.每页数量);
            jo.put("偏移量", demand.偏移量);
            jo.put("附加条件", demand.附加条件);
            LogUtils.i("demand", demand.toString());
            HttpUtils httpUtils = new HttpUtils();

            LogUtils.i("ServerCallTestURLDemand", "URL : " + URL);
            if (Global.DEBUG_MODE) {
                LogUtils.i("ServerCallTestURL", "URL : " + URL);
                LogUtils.i("ServerCallTestURL", "demand : " + demand.toString());
            }
            String result = httpUtils.postSubmit(URL, jo);
            LogUtils.i("ServerCallTestURL_result", result);
            if (result == null) {
                LogUtils.i("guojianwen", "网络错误!");
                return "";
            }
            if (Global.DEBUG_MODE) {
                LogUtils.i("ServerCallTestURL", result);// .getBody());
            }
            // handler.sendEmptyMessage(Constants.MSG_GET_DATA_FROM_NETWORK_SUCCESS);
            return result;// .getBody();
        } catch (Exception e) {
            e.printStackTrace();
            // LogUtils.i("ServerCallTestURL",
            // ((org.springframework.web.client.HttpClientErrorException) e)
            // .getResponseBodyAsString());
            LogUtils.i("ServerCallTestURL", "" + e);
            // handler.sendEmptyMessage(Constants.MSG_GET_DATA_FROM_NETWORK_FAILED);
            return null;
        }
    }

    public String makeServerCalll_Post(Demand demand, String userId) {
        String tableName = demand.表名;
        String URL;
        if (tableName.equals("流程")) {
            URL = Global.BASE_URL_PROCESS + Global.EXTENSION + demand.方法名;
        } else {
            URL = Global.BASE_URL + Global.EXTENSION + demand.方法名;
        }

        LogUtils.i("kjx-->makeServerCalll_Post,URL:", URL);
        try {
            JSONObject jo = new JSONObject();
            jo.put("企业编号", Global.mUser.CorpId);
            jo.put("用户编号", demand.用户编号);
            jo.put("表名", tableName);
            HttpUtils httpUtils = new HttpUtils();

            if (Global.DEBUG_MODE) {
                LogUtils.i("ServerCallTestURL", "URL : " + URL);
            }
            String result = httpUtils.postSubmit(URL, jo);
            if (result == null) {
                LogUtils.i("guojianwen", "网络错误!");
                return "网络错误!";
            }
            if (Global.DEBUG_MODE) {
                LogUtils.i("ServerCallTestURL", result);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(
                    "ServerCallTestURL",
                    ((org.springframework.web.client.HttpClientErrorException) e)
                            .getResponseBodyAsString());
            return "网络错误!";
        }
    }

    /**
     * 向服务器提交请求
     *
     * @param demand
     * @return
     * @author kjx
     * @since 2014-07-20
     */
    public String makeServer_Post(Demand demand, String filter) {
        String URL = Global.BASE_URL + Global.EXTENSION + demand.方法名;
        if (TextUtils.isEmpty(filter)) {
            filter = demand.条件;
        } else if (!TextUtils.isEmpty(demand.条件)) {
            filter += " AND " + demand.条件;
        }
        try {
            JSONObject jo = new JSONObject();
            jo.put("用户编号", demand.用户编号);
            jo.put("条件", filter);
            jo.put("每页数量", demand.每页数量);
            jo.put("偏移量", demand.偏移量);
            jo.put("附加条件", demand.附加条件);
            jo.put("Keyword", demand.Keyword);
            LogUtils.i("demand_server", "用户编号:" + demand.用户编号 + "\n条件:"
                    + filter + "\n偏移量:" + demand.偏移量 + "\n每页数量:" + demand.每页数量
                    + "\n附加条件" + demand.附加条件 + "\nKeyword" + demand.Keyword);
            HttpUtils httpUtils = new HttpUtils();
            if (Global.DEBUG_MODE) {
                LogUtils.i("ServerCallTestURL", "URL : " + URL);
            }
            String result = httpUtils.postSubmit(URL, jo);
            if (result == null) {
                LogUtils.i("guojianwen", "网络错误!");
                return null;
            } else {
                LogUtils.i("result", result);
            }
            if (Global.DEBUG_MODE) {
                LogUtils.i("ServerCallTestURL", result);// .getBody());
            }
            // handler.sendEmptyMessage(Constants.MSG_GET_DATA_FROM_NETWORK_SUCCESS);
            return result;// .getBody();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(
                    "ServerCallTestURL",
                    ((org.springframework.web.client.HttpClientErrorException) e)
                            .getResponseBodyAsString());
            LogUtils.i("ServerCallTestURL", "" + e);
            // handler.sendEmptyMessage(Constants.MSG_GET_DATA_FROM_NETWORK_FAILED);
            return null;
        }
    }
}
