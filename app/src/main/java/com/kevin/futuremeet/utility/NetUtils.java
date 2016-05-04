package com.kevin.futuremeet.utility;

import android.util.Log;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;


public class NetUtils {

    private static final String TAG = NetUtils.class.getSimpleName();

    private static HttpClient httpClient = new DefaultHttpClient();
    private static final String BASE_URL = "https://api.cn.ronghub.com/user/getToken.json";

    private static final String REQUEST_PARAM_USER_ID = "userId";
    private static final String REQUEST_PARAM_MAME = "name";
    private static final String REQUEST_PARAM_PORTRAIT_URI = "portraitUri";

    private static final String REQUEST_HEADER_APPKEY = "App-Key";
    private static final String REQUEST_HEADER_NONCE = "Nonce";
    private static final String REQUEST_HEADER_TIMESTAMP = "Timestamp";
    private static final String REQUEST_HEADER_SIGNATURE = "Signature";

    private static final String APP_KEY = "pwe86ga5emc46";
    private static final String APP_SECRET = "65EzKkvEfBPj";


    /**
     * 发送post请求
     */
    public static String getToken(String userId, String name, String portraitUri) {
        HttpPost httpPost = new HttpPost(BASE_URL);

        String nonce = String.valueOf(Math.random() * 1000000);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);


        httpPost.addHeader(REQUEST_HEADER_APPKEY, APP_KEY);
        httpPost.addHeader(REQUEST_HEADER_NONCE, nonce);
        httpPost.addHeader(REQUEST_HEADER_TIMESTAMP, timestamp);
        httpPost.addHeader(REQUEST_HEADER_SIGNATURE, getSign(nonce, timestamp));

        try {
            List<NameValuePair> paramLists = new ArrayList<>();

            paramLists.add(new BasicNameValuePair(REQUEST_PARAM_MAME, name));
            paramLists.add(new BasicNameValuePair(REQUEST_PARAM_USER_ID, userId));
            paramLists.add(new BasicNameValuePair(REQUEST_PARAM_PORTRAIT_URI, portraitUri));

            httpPost.setEntity(new UrlEncodedFormEntity(paramLists, "UTF-8"));

            HttpResponse response = httpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            } else {
                Log.i(TAG, "getToken: " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getSign(String nonce, String timeStamp) {
        StringBuilder toSign = new StringBuilder(APP_SECRET).append(nonce)
                .append(timeStamp);
        return hexSHA1(toSign.toString());
    }

    public static String hexSHA1(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(value.getBytes("utf-8"));
            byte[] digest = md.digest();
            return byteToHexString(digest);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String byteToHexString(byte[] bytes) {
        return String.valueOf(Hex.encodeHex(bytes));
    }

}
