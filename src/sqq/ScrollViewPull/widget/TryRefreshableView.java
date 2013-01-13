package sqq.ScrollViewPull.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.dodowaterfall.R;

/**
 * @author wangning
 * 
 */
public class TryRefreshableView extends LinearLayout {
	public static final int READYTOREFRESH = 0; // 初始状态
	public static final int TAP_TO_REFRESH = 1; // 初始状态
	public static final int PULL_TO_REFRESH = 2; // 拉动刷新
	public static final int RELEASE_TO_REFRESH = 3; // 释放刷新
	public static final int REFRESHING = 4; // 正在刷新
	public int mRefreshState;// 记录头当前状态
	public int mfooterRefreshState;// 记录尾当前状态
	public Scroller scroller;
	public TryPullToRefreshScrollView sv;
	private View refreshView;// 头部视图
	public TextView mfooterViewText;
	private ImageView refreshIndicatorView;
	private int refreshTargetTop = -60;
	public int refreshFooter;
	private ProgressBar bar;
	private TextView downTextView;
	private TextView timeTextView;

	private RefreshListener refreshListener;

	private int lastY;
	// 动画效果
	// 变为向下的箭头
	private RotateAnimation mFlipAnimation;
	// 变为逆向的箭头
	private RotateAnimation mReverseFlipAnimation;
	public int nowpull = -1;// 0为头部下拉，1为尾部上拉

	private boolean isRecord;
	private Context mContext;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// process incoming messages here
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				Log.d("TryPullToRefreshScrollView", "view.getMeasureHeight:"
						+ getMeasuredHeight());
				if (getMeasuredHeight() - 20 <= getScrollY() + getHeight()) {
					if (onBottomListener != null) {
						onBottomListener.onBottom();
					}
				}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 定义接口
	 * 
	 */
	public interface OnBottomListener {
		void onBottom();
	}

	private OnBottomListener onBottomListener;

	public void setOnBottomListener(OnBottomListener onScrollListener) {
		this.onBottomListener = onScrollListener;
	}

	public TryRefreshableView(Context context) {
		super(context);
		mContext = context;

	}

	public TryRefreshableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();

	}

	private void init() {
		// 初始化动画
		//
		mFlipAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(250);
		mFlipAnimation.setFillAfter(true);

		mReverseFlipAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);
		// 滑动对象，
		scroller = new Scroller(mContext);

		// 刷新视图顶端的的view
		refreshView = LayoutInflater.from(mContext).inflate(
				R.layout.pull_to_refresh_header, null);
		// 箭头图标
		refreshIndicatorView = (ImageView) refreshView
				.findViewById(R.id.pull_to_refresh_image);
		// 刷新bar
		bar = (ProgressBar) refreshView
				.findViewById(R.id.pull_to_refresh_progress);
		// 下拉显示text
		downTextView = (TextView) refreshView
				.findViewById(R.id.pull_to_refresh_text);
		// 下来显示时间
		timeTextView = (TextView) refreshView
				.findViewById(R.id.pull_to_refresh_updated_at);

		// 添加头部view
		refreshView.setMinimumHeight(50);
		LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, -refreshTargetTop);
		lp.topMargin = refreshTargetTop;
		lp.gravity = Gravity.CENTER;
		addView(refreshView, lp);

		isRecord = false;

		mRefreshState = TAP_TO_REFRESH;
		mfooterRefreshState = TAP_TO_REFRESH;

	}

	public boolean onTouchEvent(MotionEvent event) {

		int y = (int) event.getRawY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 记录下y坐标
			if (isRecord == false) {
				Log.i("moveY", "lastY:" + y);
				lastY = y;
				isRecord = true;
			}
			break;

		case MotionEvent.ACTION_MOVE:

			Log.i("TAG", "ACTION_MOVE");
			// y移动坐标
			Log.i("moveY", "lastY:" + lastY);
			Log.i("moveY", "y:" + y);
			int m = y - lastY;

			doMovement(m);
			// 记录下此刻y坐标
			lastY = y;

			break;

		case MotionEvent.ACTION_UP:
			Log.i("TAG", "ACTION_UP");
			handler.sendEmptyMessage(1);
			fling();

			isRecord = false;
			break;
		}
		return true;
	}

	/**
	 * up事件处理
	 */
	private void fling() {
		if (nowpull == 0 && mRefreshState != REFRESHING) {
			LinearLayout.LayoutParams lp = (LayoutParams) refreshView
					.getLayoutParams();
			Log.i("TAG", "fling()" + lp.topMargin);
			if (lp.topMargin > 0) {// 拉到了触发可刷新事件
				refresh();
			} else {
				returnInitState();
			}
		}
	}

	// 刷新
	public void onDownRefresh() {
		Log.d("TAG", "执行刷新");

		if (refreshListener != null) {

			refreshListener.onDownRefresh();
		}
	}

	private void returnInitState() {
		mRefreshState = TAP_TO_REFRESH;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		scroller.startScroll(0, i, 0, refreshTargetTop);
		invalidate();
	}

	private void refresh() {
		mRefreshState = READYTOREFRESH;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		refreshIndicatorView.setVisibility(View.GONE);
		refreshIndicatorView.setImageDrawable(null);
		bar.setVisibility(View.VISIBLE);
		timeTextView.setVisibility(View.GONE);
		downTextView.setText(R.string.pull_to_refresh_refreshing_label);
		scroller.startScroll(0, i, 0, 0 - i);
		invalidate();
		onDownRefresh();
		mRefreshState = REFRESHING;

	}

	private void resetFooterPadding() {
		LayoutParams svlp = (LayoutParams) sv.getLayoutParams();
		svlp.bottomMargin = 0;

		sv.setLayoutParams(svlp);
		// TryPullToRefreshScrollView.ScrollToPoint(sv, sv.getChildAt(0), 0);

	}

	public void FooterPrepareForRefresh() {
		resetFooterPadding();
		mfooterViewText
				.setText(R.string.pull_to_refresh_footer_refreshing_label);
		mfooterRefreshState = REFRESHING;
	}

	/**
	 * 
	 */
	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			int i = this.scroller.getCurrY();
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
					.getLayoutParams();
			int k = Math.max(i, refreshTargetTop);
			lp.topMargin = k;
			this.refreshView.setLayoutParams(lp);
			this.refreshView.invalidate();
			invalidate();
		}
	}

	/**
	 * 下拉move事件处理
	 * 
	 * @param moveY
	 */
	public void doMovement(int moveY) {
		LinearLayout.LayoutParams lp = (LayoutParams) refreshView
				.getLayoutParams();

		if (sv.getScrollY() == 0 && moveY > 0 && refreshFooter <= 0) {
			nowpull = 0;
		}
		if (sv.getChildAt(0).getMeasuredHeight() <= sv.getScrollY()
				+ getHeight()
				&& moveY < 0 && lp.topMargin <= refreshTargetTop) {
			nowpull = 1;
		}

		if (nowpull == 0 && mRefreshState != REFRESHING) {

			// 获取view的上边距
			float f1 = lp.topMargin;
			float f2 = f1 + moveY * 0.3F;
			int i = (int) f2;
			// 修改上边距
			lp.topMargin = i;
			// 修改后刷新
			refreshView.setLayoutParams(lp);
			refreshView.invalidate();
			invalidate();

			downTextView.setVisibility(View.VISIBLE);

			refreshIndicatorView.setVisibility(View.VISIBLE);
			bar.setVisibility(View.GONE);
			if (lp.topMargin > 0 && mRefreshState != RELEASE_TO_REFRESH) {
				downTextView.setText(R.string.refresh_release_text);
				// refreshIndicatorView.setImageResource(R.drawable.goicon);
				refreshIndicatorView.clearAnimation();
				refreshIndicatorView.startAnimation(mFlipAnimation);
				mRefreshState = RELEASE_TO_REFRESH;

				Log.i("TAG", "现在处于下拉状态");
			} else if (lp.topMargin <= 0 && mRefreshState != PULL_TO_REFRESH) {
				downTextView.setText(R.string.refresh_down_text);
				// refreshIndicatorView.setImageResource(R.drawable.goicon);
				if (mRefreshState != TAP_TO_REFRESH) {
					refreshIndicatorView.clearAnimation();
					refreshIndicatorView.startAnimation(mReverseFlipAnimation);

					Log.i("TAG", "现在处于回弹状态");

				}
				mRefreshState = PULL_TO_REFRESH;

			}
		}
	}

	public void setRefreshListener(RefreshListener listener) {
		this.refreshListener = listener;
	}

	/**
	 * 结束刷新事件
	 */
	public void finishRefresh() {
		Log.i("TAG", "执行了=====finishRefresh");

		if (mRefreshState != TAP_TO_REFRESH) {
			mRefreshState = TAP_TO_REFRESH; // 初始刷新状态
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
					.getLayoutParams();
			int i = lp.topMargin;
			refreshIndicatorView.setImageResource(R.drawable.xlistview_arrow);
			refreshIndicatorView.clearAnimation();
			bar.setVisibility(View.GONE);
			refreshIndicatorView.setVisibility(View.GONE);
			downTextView.setText(R.string.pull_to_refresh_tap_label);
			scroller.startScroll(0, i, 0, refreshTargetTop);
			invalidate();
		}

	}

	/*
	 * 该方法一般和ontouchEvent 一起用
	 * 
	 * @see
	 * android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		int action = e.getAction();
		int y = (int) e.getRawY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// lastY = y;
			if (isRecord == false) {
				Log.i("moveY", "lastY:" + y);
				lastY = y;
				isRecord = true;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			// y移动坐标
			int m = y - lastY;

			if (canScroll(m)) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			isRecord = false;
			break;

		case MotionEvent.ACTION_CANCEL:

			break;
		}
		return false;
	}

	private boolean canScroll(int diff) {
		View childView;
		Log.i("other", "mRefreshState:" + mRefreshState);
		// if (mRefreshState == REFRESHING || mfooterRefreshState == REFRESHING)
		// {
		// return true;
		// }
		if (getChildCount() > 1) {
			childView = this.getChildAt(1);
			if (childView instanceof ListView) {
				int top = ((ListView) childView).getChildAt(0).getTop();
				int pad = ((ListView) childView).getListPaddingTop();
				if ((Math.abs(top - pad)) < 3
						&& ((ListView) childView).getFirstVisiblePosition() == 0) {
					return true;
				} else {
					return false;
				}
			} else if (childView instanceof ScrollView) {

				// 头部下拉
				if (((ScrollView) childView).getScrollY() == 0 && diff > 0) {
					nowpull = 0;
					Log.i("other", "外框处理");
					return true;
				} else if ((((ScrollView) childView).getChildAt(0)
						.getMeasuredHeight() <= ((ScrollView) childView)
						.getScrollY() + getHeight() && diff < 0)) {// 底部上拉
					Log.i("other", "外框处理2");
					nowpull = 1;

					return true;
				} else {
					Log.i("other", "ScrollView处理");
					return false;
				}
			}

		}
		return false;
	}

	/**
	 * 刷新监听接口
	 * 
	 * @author Nono
	 * 
	 */
	public interface RefreshListener {
		public void onDownRefresh();

	}
}
