package com.dodowaterfall;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class LazyScrollView extends ScrollView {
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (view != null) {
			Log.d("LazyScrollView", String.format(
					"onScrollChanged:%d %d %d %d", l, t, oldl, oldt));
			if (view.getMeasuredHeight() <= getScrollY() + getHeight()) {
				if (onScrollListener != null) {
					onScrollListener.onBottom();
				}

			} else if (getScrollY() == 0) {
				if (onScrollListener != null) {
					onScrollListener.onTop();
				}
			} else {
				if (onScrollListener != null) {
					onScrollListener.onScroll();
				}
			}
		}

	}

	private static final String tag = "LazyScrollView";
	private Handler handler;
	private View view;
	private boolean isOnTouch = false;

	public LazyScrollView(Context context) {
		super(context);

	}

	public LazyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public LazyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	// 这个获得总的高度
	public int computeVerticalScrollRange() {
		return super.computeHorizontalScrollRange();
	}

	public int computeVerticalScrollOffset() {
		return super.computeVerticalScrollOffset();
	}

	/**
	 * 获得参考的View，主要是为了获得它的MeasuredHeight，然后和滚动条的ScrollY+getHeight作比较。
	 */
	public void getView() {
		this.view = getChildAt(0);

	}

	/**
	 * 定义接口
	 * 
	 * @author admin
	 * 
	 */
	public interface OnScrollListener {
		void onBottom();

		void onTop();

		void onScroll();
	}

	private OnScrollListener onScrollListener;

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}
}
