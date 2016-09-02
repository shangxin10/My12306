package com.neu.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2016/8/30.
 */
public class URLConnManager {

    public static HttpURLConnection getHttpURLConnection(String url){
        HttpURLConnection conn = null;
        try {
            URL murl = new URL(url);
            conn = (HttpURLConnection) murl.openConnection();
            conn.setConnectTimeout(Constant.CONNECTTIME);
            conn.setReadTimeout(Constant.CONNECTTIME);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection","Keep-alive");
            conn.setDoInput(true);
            conn.setDoOutput(true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }


    public static void postParams(OutputStream outputStream, Map<String,String> paramMap) throws IOException{
        StringBuilder builder = new StringBuilder();
        if(paramMap!=null){
            for(Map.Entry<String,String> entry:paramMap.entrySet()){
                if(entry.getValue()!=null&&entry.getKey()!=null){
                    if(!TextUtils.isEmpty(builder)){
                        builder.append("&");
                    }
                    builder.append(URLEncoder.encode(entry.getKey(),"UTF-8"));
                    builder.append("=");
                    builder.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
                }
            }
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
        bufferedWriter.write(builder.toString());
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public static String converStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer stringBuffer = new StringBuffer();
        String line = null;
        while((line= bufferedReader.readLine())!=null){
            stringBuffer.append(line+"\n");
        }
        String response = stringBuffer.toString();
        return response;
    }

}
