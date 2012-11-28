package com.dodola.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.View;

import com.dodola.activity.ContentActivity;
import com.dodola.base.TaskBase;
import com.dodola.model.DuitangInfo;
import com.dodola.views.InfosListAdapter;

public class ContentTask extends TaskBase {

	public ContentActivity contentActivity;

	public ContentTask(ContentActivity newsContentActivity) {
		super();
		this.contentActivity = newsContentActivity;
	}

	@Override
	protected List<Map<String, Object>> doInBackground(String... params) {
		try {
			parseNewsJSON(params[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(List<Map<String, Object>> result) {
		if (this.contentActivity.newsLeft != null && this.contentActivity.newsLeft.getNewsInfos() != null) {
			this.contentActivity.listAdapter = new InfosListAdapter(this.contentActivity, this.contentActivity.newsLeft.getNewsInfos()/*
																																	 * ,
																																	 * this
																																	 * .
																																	 * contentActivity
																																	 * .
																																	 * smallBmp
																																	 */);
			this.contentActivity.newsListLayout.clearData();
			this.contentActivity.newsListLayout.setAdapter(this.contentActivity.listAdapter);
		}

		this.contentActivity.progressBar.setVisibility(android.view.View.INVISIBLE);
		this.contentActivity.newsLoadMore.setVisibility(android.view.View.VISIBLE);
	}

	@Override
	protected void onPreExecute() {
		this.contentActivity.progressBar.setVisibility(View.VISIBLE);
	}

	public void parseNewsJSON(String url) throws IOException {
		String json = "";
		if (this.contentActivity.checkConnection()) {
			try {
				json = this.getStringFromUrl(url);

			} catch (IOException e) {
				Log.e("IOException is : ", e.toString());
				e.printStackTrace();
				return;
			}
		}

		try {
			if (null != json) {
				JSONObject newsObject = new JSONObject(json);
				JSONObject jsonObject = newsObject.getJSONObject("data");
				JSONArray blogsJson = jsonObject.getJSONArray("blogs");

				List<DuitangInfo> newsLeftInfos = new ArrayList<DuitangInfo>();

				for (int i = 0; i < blogsJson.length(); i++) {
					JSONObject newsInfoLeftObject = blogsJson.getJSONObject(i);
					DuitangInfo newsInfo1 = new DuitangInfo();
					newsInfo1.setAlbid(newsInfoLeftObject.isNull("albid") ? "" : newsInfoLeftObject.getString("albid"));
					newsInfo1.setIsrc(newsInfoLeftObject.isNull("isrc") ? "" : newsInfoLeftObject.getString("isrc"));
					newsInfo1.setMsg(newsInfoLeftObject.isNull("msg") ? "" : newsInfoLeftObject.getString("msg"));
					newsLeftInfos.add(newsInfo1);
				}
				this.contentActivity.newsLeft.setNewsInfos(newsLeftInfos);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
