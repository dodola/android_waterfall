package sqq.ScrollViewPull.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * 
 * @author wangning
 * 
 */
public class TryPullToRefreshScrollView extends ScrollView {

	public TryPullToRefreshScrollView(Context context) {
		super(context);

	}

	public TryPullToRefreshScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public TryPullToRefreshScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		getOnScrollListener().onAutoScroll(l, t, oldl, oldt);
	}

	private View view;

	// 这个获得总的高度
	public int computeVerticalScrollRange() {
		return super.computeHorizontalScrollRange();
	}

	public int computeVerticalScrollOffset() {
		return super.computeVerticalScrollOffset();
	}

	public OnScrollListener getOnScrollListener() {
		return onScrollListener;
	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	private OnScrollListener onScrollListener;

	/**
	 * 定义接口
	 * 
	 */
	public interface OnScrollListener {
		void onAutoScroll(int l, int t, int oldl, int oldt);
	}

}