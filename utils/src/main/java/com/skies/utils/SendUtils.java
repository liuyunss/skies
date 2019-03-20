package com.skies.utils;

import com.alibaba.fastjson.JSONObject;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by Admin on 2017-7-27.
 */
@Slf4j
public class SendUtils {

	public static String sendGet(String url) {

		String result = "";
		BufferedReader in = null;
		URL realUrl = null;
		try {
			String urlNameString = url;
			realUrl = new URL(urlNameString);
			System.out.println(realUrl);
			URLConnection connection = realUrl.openConnection();
			connection.setRequestProperty("connection", "close");
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.connect();
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//        System.out.println(result);
		return result;
	}

	public static String sendPost(String url, Map<String, String> params) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		StringBuilder string = new StringBuilder();
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			for (String key : params.keySet()) {
                if(StringUtils.isBlank(params.get(key))){
                    continue;
                }
				if (string.length() != 0) {
					string.append("&");
				}
				string.append(key).append("=").append(URLEncoder.encode(params.get(key), "utf-8"));
			}

			log.info("SendUtils_sendPost_输入：url="+url+"---params="+string.toString());

			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(string.toString());
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
			// 使用finally块来关闭输出流、输入流
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
        log.info("SendUtils_sendPost_输出：result="+result);
		return result;
	}

	public static String postJSON(String url, Map<String, String> params) {

		AsyncHttpClient http = null;
		String body = "";
		try {
			http = new AsyncHttpClient();
			AsyncHttpClient.BoundRequestBuilder builder = http.preparePost(url);
			builder.setBodyEncoding(String.valueOf(StandardCharsets.UTF_8));

			JSONObject items = new JSONObject();
			items.putAll(params);
			byte[] bytes = items.toString().getBytes();
	       /* if (params != null && !params.isEmpty()) {
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    builder.addParameter(key, params.get(key));
                }
            }*/
			//builder.setHeader("connection", "Keep-Alive");
			builder.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.107 Safari/537.36");
			builder.setHeader("Charsert", "UTF-8");
			builder.setHeader("Content-Type", "application/json;charset=UTF-8");
			builder.setBody(bytes);
			Future<Response> f = builder.execute();
			body = f.get().getResponseBody("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("SendUtils_postJSON:Exception=" + e.getMessage());

		} finally {
			if (http != null) {
				http.close();
			}
		}
		return body;
	}
}
