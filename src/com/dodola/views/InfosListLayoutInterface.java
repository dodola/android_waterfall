package com.dodola.views;

public interface InfosListLayoutInterface {

	/*
	 * 滑动处理完后的通知
	 * 
	 * @param index,序列组件所在的屏幕序号
	 * 
	 * @param direct,本次滑动的方向
	 */

	public void onCurChileCtrlScreen(int index, int direct, boolean flag);
}
