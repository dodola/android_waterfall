package com.dodola.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class ActivityBase extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	//检测网络连接
	public boolean checkConnection() {
		ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {  
            return networkInfo.isAvailable();  
        }  
        return false;  
	}

	//通过URL来获得输入流，这个是通过GET方式联网
	public InputStream getStreamByGetURL(String path) throws IOException {

		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.setReadTimeout(2000*10);
		
		if (connection.getResponseCode() == 200) {
			InputStream inputStream = connection.getInputStream();
			return inputStream;
		}	
			
		return null;
	}

	//通过URL来获得输入流，这个是通过POST方式联网
	public InputStream getStreamByPostURL(String path, Map<String, String> params) throws IOException {

		 StringBuilder sb = new StringBuilder();  
         if(params!=null && !params.isEmpty()){  
             for(Map.Entry<String, String> entry : params.entrySet()){  
                 sb.append(entry.getKey()).append('=')  
                     .append(URLEncoder.encode(entry.getValue())).append('&');   
             }  
             sb.deleteCharAt(sb.length()-1);  
         } 
         byte[] entitydata = sb.toString().getBytes();  
         URL url = new URL(path);  
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();  
         connection.setRequestMethod("POST");  
         connection.setConnectTimeout(2000*10);  
         connection.setDoOutput(true);
         
         connection.setRequestProperty("Charset", "UTF-8");
         connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
         connection.setRequestProperty("Content-Length", String.valueOf(entitydata.length));
         OutputStream outStream = connection.getOutputStream();  
         outStream.write(entitydata);  

         outStream.flush();  
         outStream.close();  
         
		if (connection.getResponseCode() == 200) {
			InputStream inputStream = connection.getInputStream();
			return inputStream;
		}	
			
		return null;
	}
	
	public InputStream getStreamByURLAndCookie(String path, Map<String, String> params, String cookie) throws IOException {
		 StringBuilder sb = new StringBuilder();  
         if(params!=null && !params.isEmpty()){  
             for(Map.Entry<String, String> entry : params.entrySet()){  
                 sb.append(entry.getKey()).append('=')  
                     .append(URLEncoder.encode(entry.getValue())).append('&');   
             }  
             sb.deleteCharAt(sb.length()-1);  
         } 
         byte[] entitydata = sb.toString().getBytes();  
         URL url = new URL(path);  
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();  
         connection.setRequestProperty("Cookie", cookie);
         connection.setRequestMethod("POST");  
         connection.setConnectTimeout(2000*10);  
         connection.setDoOutput(true);

         connection.setRequestProperty("Charset", "UTF-8");
         connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
         connection.setRequestProperty("Content-Length", String.valueOf(entitydata.length));
         OutputStream outStream = connection.getOutputStream();  
         outStream.write(entitydata);  

         outStream.flush();  
         outStream.close();  
         
		if (connection.getResponseCode() == 200) {
			InputStream inputStream = connection.getInputStream();
			return inputStream;
		}	
			
		return null;
	}
	
	public byte[] readInputStream(InputStream inputStream) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		
		while ((len=inputStream.read(buffer)) != -1 ) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inputStream.close();	
		
		return outStream.toByteArray();
	}

	public boolean isWifi(Context mContext) {  
	    ConnectivityManager connectivityManager = (ConnectivityManager) mContext  
	            .getSystemService(Context.CONNECTIVITY_SERVICE);  
	    NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo(); 
	    if(activeNetInfo != null  && activeNetInfo.getTypeName().equals("WIFI")){
	    	return true;  
	    }
	    return false;  
	}  
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


}
