package com.dodola.views;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

/**
 * 
 * 这个类用来存储在资讯界面用到的默认小图
 * 
 * */

public class InfosSmallBmp {
	private List<Bitmap> list_save_smal_bmp;

	public InfosSmallBmp() {
		list_save_smal_bmp = new ArrayList<Bitmap>();
	}

	public List<Bitmap> getSmalBmpList() {
		if (null != list_save_smal_bmp)
			return list_save_smal_bmp;
		return null;
	}

	public void addSmalBmp(Bitmap bmp) {
		if (null != list_save_smal_bmp) {
			this.list_save_smal_bmp.add(bmp);
		}
	}
}
