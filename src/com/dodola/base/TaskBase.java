package com.dodola.base;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

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

	/**
	 * 从网上获取内容get方式
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public String getStringFromUrl(String url) throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		return EntityUtils.toString(entity, "UTF-8");
	}
	
}
