package com.dodola.base;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

public class TaskBase extends AsyncTask<String, Integer, List<Map<String, Object>>> {

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected List<Map<String, Object>> doInBackground(String... params) {
		return null;
	}

	@Override
	protected void onPostExecute(List<Map<String, Object>> result) {
		super.onPostExecute(result);
	}

	public InputStream getStreamByLocalURL(Context context, String fileUrl) throws IOException {
		AssetManager assetManager = context.getAssets();

		return assetManager.open(fileUrl);
	}

	public InputStream getStreamByGetURL(String path) throws IOException {

		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setReadTimeout(2000 * 10);
		connection.setRequestProperty("Charset", "UTF-8");

		if (connection.getResponseCode() == 200) {
			InputStream inputStream = connection.getInputStream();
			return inputStream;
		}

		return null;
	}

	public InputStream getStreamByPostURL(String path, Map<String, String> params)
		throws IOException {

		StringBuilder sb = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(entry.getKey())
					.append('=')
					.append(URLEncoder.encode(entry.getValue()))
					.append('&');
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		byte[] entitydata = sb.toString().getBytes();
		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setConnectTimeout(2000 * 10);
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

	public InputStream getStreamByURLAndCookie(String path, Map<String, String> params,
			String cookie) throws IOException {

		StringBuilder sb = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(entry.getKey())
					.append('=')
					.append(URLEncoder.encode(entry.getValue()))
					.append('&');
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		byte[] entitydata = sb.toString().getBytes();
		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Cookie", cookie);
		connection.setRequestMethod("POST");
		connection.setConnectTimeout(2000 * 10);
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

		while ((len = inputStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inputStream.close();

		return outStream.toByteArray();
	}

	
}
