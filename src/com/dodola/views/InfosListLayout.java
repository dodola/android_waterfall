package com.dodola.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.dodola.activity.ContentActivity;

/**
 * 
 * 资讯列表的自定义控件
 * 
 * @author dell
 * 
 */
public class InfosListLayout extends LinearLayout {
	public InfosListAdapter adapter;
	public int count;

	private ScrollView sv;
	private InfosListLayoutInterface mEventInterface;// 事件回调接口
	private int iDrection = 0;// 滚动方向,向上滚动值是-1，向下滚动值是1
	private int iAllViewHeight_px = 0;// 本布局中所有view高度的像素值和
	private List<List<View>> all_screen_View;// 存放每屏幕能放得下的View
	private List<View> list_one_screen_view;
	private List<Rect> childView_rect; // 存放所有子View的矩形坐标
	private int self_height = 0;// 这个布局本身的可见高度
	private int self_width = 0;// 这个布局本身的可见宽度

	private int iCureetScreen = 0; // 当前屏幕索引
	private boolean once_flag = true;// 保证注册本布局的的监听器只执行一次
	private boolean computeScroll_flag = false; // 保证computeScroll()方法在获得子控件和本布局的长和宽后才能被执行
	private boolean addNewView_flag = false;// 判断是否在本布局中添加了新的View;

	private Context context;

	private LinearLayout leftLayout;
	private LinearLayout rightLayout;

	private boolean addViewOnce = false;


	public InfosListLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		all_screen_View = new ArrayList<List<View>>();
		list_one_screen_view = new ArrayList<View>();
		childView_rect = new ArrayList<Rect>();
		this.setOrientation(LinearLayout.HORIZONTAL);
		addViewOnce = true;
	}

	// 在这个方法中将从适配器中得到的View给add到本LinearLayout布局中，并没有删除子View的操作，所以即使适配器中的View的内容值是20-30这段数据，也会显示0-30这段数据
	// 在这里实现了一个布局View的算法，第一个View位于左边第一个位置
	public void setAdapter(InfosListAdapter adapter) {

		if (addViewOnce) {
			leftLayout = new LinearLayout(context);
			LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
			leftLayout.setOrientation(LinearLayout.VERTICAL);
			leftLayout.setGravity(Gravity.CENTER_HORIZONTAL);
			leftLayout.setLayoutParams(leftParams);
			rightLayout = new LinearLayout(context);
			LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
			rightLayout.setOrientation(LinearLayout.VERTICAL);
			rightLayout.setLayoutParams(rightParams);

			this.addView(leftLayout);
			this.addView(rightLayout);

			addViewOnce = false;
		}

		this.adapter = adapter;

		for (int i = 0; i < this.adapter.getCount(); i++) {
			count++;
			final View view = adapter.getView(i, null, null);
			list_one_screen_view.add(view);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			if (0 == i % 2) {
				leftLayout.addView(view, params);
			} else {
				rightLayout.addView(view, params);
			}
			ViewTreeObserver vto = view.getViewTreeObserver();
			vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					view.getViewTreeObserver().removeOnPreDrawListener(this);
					// 每个子控件的矩形位置
					Rect retChildView = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
					childView_rect.add(retChildView);
					if (iAllViewHeight_px < retChildView.bottom) {
						iAllViewHeight_px = retChildView.bottom;
					}
					return false;
				}
			});
		}

		if (once_flag) {
			ViewTreeObserver vto = this.getViewTreeObserver();
			vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					Rect rect = new Rect();
					InfosListLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
					InfosListLayout.this.getGlobalVisibleRect(rect); //获取layout的矩形数据
					self_height = rect.height();
					self_width = rect.width();
					computeScroll_flag = true;
					InfosListLayout.this.computeScroll();
					return false;
				}
			});
			once_flag = false;
		}
		((ContentActivity) context).newsLeft.setNewsInfos(null);
		this.addNewView_flag = true;
	}

	public void clearData() {
		this.removeAllViews();
		this.all_screen_View.clear();
	}

	public int getCount() {
		return count;
	}

	// 这个方法必须调用,给本布局设置事件接口，用来释放和加载图片
	public void setEvent(InfosListLayoutInterface mEventInterface) {
		this.mEventInterface = mEventInterface;
	}

	// 这个方法必须调用，将本布局外层的ScrollView给传递进来
	public void setScrollView(ScrollView sv) {
		this.sv = sv;
	}

	// 初始化控件需要的数据(效率偏低..)
	public List<List<View>> initScreenView() {
		synchronized (childView_rect) {
			addNewView_flag = false;
			all_screen_View.clear();
			if (0 != childView_rect.size()) {
				int screen_count = (int) Math.ceil((double) this.iAllViewHeight_px / self_height);// 计算总共有多少屏
				List<Rect> self_rect = new ArrayList<Rect>();// 存放本布局的每屏矩形范围
				for (int i = 0; i < screen_count; i++) {
					
					self_rect.add(new Rect(0, i * self_height, self_width, i * self_height + self_height));
					List<View> one_screen = new ArrayList<View>();// 存放一屏所包含的View
					for (int j = 0; j < childView_rect.size(); j++) {// 判断出每屏包含的View
						if (childView_rect.get(j).intersect(self_rect.get(i)) || childView_rect.get(j).contains(self_rect.get(i))) {
							one_screen.add(list_one_screen_view.get(j));
						}
					}
					all_screen_View.add(one_screen);
				}
			}
		}
		return all_screen_View;
	}

	// private int preSVY = 0;

	// 在这个方法里应该再添加一个判断ScrollView向哪个方向滚动的代码
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (computeScroll_flag) {
			if (0 != this.self_height) {
				// int iSVY = sv.getScrollY();
				// if (Math.abs(iSVY - preSVY) > 10) {
				iCureetScreen = (int) sv.getScrollY() / (int) this.self_height;
				// System.out.println("当前滑动到了第几屏：" + iCureetScreen);
				this.mEventInterface.onCurChileCtrlScreen(iCureetScreen, iDrection, addNewView_flag);
				// preSVY = iSVY;
				// }
			}
		}
	}
}
