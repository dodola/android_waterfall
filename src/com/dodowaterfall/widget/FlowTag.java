package com.dodowaterfall.widget;

import com.dodowaterfall.Constants;

import android.content.res.AssetManager;

//这个类是一个java bean，可以不用这个类
public class FlowTag {
	
	public final int what = Constants.HANDLER_WHAT;
	
	private int flowId;
	private String fileName;
	private int ItemWidth;
	private AssetManager assetManager;

	public int getFlowId() {
		return flowId;
	}

	public void setFlowId(int flowId) {
		this.flowId = flowId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public void setAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	public int getItemWidth() {
		return ItemWidth;
	}

	public void setItemWidth(int itemWidth) {
		ItemWidth = itemWidth;
	}
}
