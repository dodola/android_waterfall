package com.dodola.activity;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dodola.R;
import com.dodola.base.ActivityBase;
import com.dodola.model.Infos;
import com.dodola.task.ContentFootTask;
import com.dodola.task.ContentTask;
import com.dodola.tools.FileCache;
import com.dodola.views.InfoImageView;
import com.dodola.views.InfosListAdapter;
import com.dodola.views.InfosListLayout;
import com.dodola.views.InfosListLayoutInterface;
import com.dodola.views.InfosSmallBmp;

public class ContentActivity extends ActivityBase implements
		InfosListLayoutInterface {
	public InfosListAdapter listAdapter;
	public InfosListLayout newsListLayout; // 列表控件
	public LinearLayout newsButtonPro;
	public ScrollView scrollView; // 界面中的ScrollView控件
	public TextView newsLoadMore; // 界面中的提示“上拉或点击加载更多...”的TextView
	public ProgressBar progressBar; // 刚进入界面加载内容时 显示的进度圈

	public int flag = 0; // 用来判断此界面数据是否正在加载：0代表已经加载完成，1代表正在加载
	public String newsLast = "";
	public Infos newsLeft; // 控件数据模型

	public View selectedView;
	private int isFirstCreate = 1;
	public int isDetailBcak = 0;

	public InfosSmallBmp smallBmp;
	private int iIndex;
	private List<List<View>> all_screen_view; // 封装每屏View集合的集合
	boolean thread_once_flag = true; // 确保线程只被开启一次
	private ManagerBmp managerBmp;
	private boolean initFlag = true; // 只有当本Activity中新增加了View时，其为true

	public ContentActivity() {
		newsLeft = new Infos();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.infos_content);

		newsListLayout = (InfosListLayout) findViewById(R.id.newsLeftLayout);
		scrollView = (ScrollView) findViewById(R.id.newsScrollview);
		newsLoadMore = (TextView) findViewById(R.id.newsLoadMore);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		newsButtonPro = (LinearLayout) findViewById(R.id.newsButtonPro);

		smallBmp = new InfosSmallBmp();
		newsListLayout.setScrollView(scrollView);
		newsListLayout.setEvent(this);

		upDateList();

		setListener();
	}

	/**
	 * 
	 * 给相应的控件注册事件
	 * 
	 * */
	public void setListener() {
		// 上拉刷新
		scrollView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP && flag == 0
						&& progressBar.getVisibility() == View.INVISIBLE) { // 当手指离开ScrollView且数据已经加载完成
					View view = ((ScrollView) v).getChildAt(0);
					if (view.getMeasuredHeight() <= v.getScrollY()
							+ v.getHeight()) { // 当ScrollView中包含的View的高度小于已经滚动了的值+SrollView本身的高度时
						newsLoadMore.setVisibility(android.view.View.VISIBLE);
						if (newsListLayout != null) {
							new ContentFootTask(ContentActivity.this)
									.execute("http://www.duitang.com/album/369270/masn/p/4/24/");
						} else {
							return false;
						}
					}
				}
				return false;
			}
		});

		// 点击刷新
		newsLoadMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new ContentFootTask(ContentActivity.this)
						.execute("http://www.duitang.com/album/369270/masn/p/4/24/");
			}
		});
	}

	public String getNewsLast() {
		return newsLast;
	}

	public void setNewsLast(String newsLast) {
		this.newsLast = newsLast;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	/**
	 * 
	 * 加载数据资源
	 * */
	public void upDateList() {
		new ContentTask(this)
				.execute("http://www.duitang.com/album/369270/masn/p/4/24/"); // 请求数据
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isFirstCreate == 1) {
			isFirstCreate = 0;
		} else if (isDetailBcak == 1) {
			isDetailBcak = 0;
			ImageView imgCover = (ImageView) selectedView
					.findViewById(R.id.news_cover);
			AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
			alphaAnimation.setDuration(1500);
			alphaAnimation.setFillAfter(true);
			imgCover.startAnimation(alphaAnimation);
		}
		if (null != managerBmp) {
			managerBmp.manaBmp_flag = true;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (0 == msg.what) {
				((ProgressBar) msg.obj).setVisibility(View.INVISIBLE);
			} else if (1 == msg.what) {
				((ProgressBar) msg.obj).setVisibility(View.VISIBLE);
			}
		}
	};

	@Override
	protected void onStop() {
		super.onStop();
		if (null != managerBmp) {
			managerBmp.manaBmp_flag = false;
		}

		if (null != this.all_screen_view) {
			int iBeforPresentBmps = 0;
			for (int i = 0; i < all_screen_view.size(); i++) {
				List<View> one_screen_view = all_screen_view.get(i);
				if (iIndex - 1 == i || iIndex + 1 == i) {
					for (int j = 0; j < one_screen_view.size(); j++) {
						RelativeLayout rootView = (RelativeLayout) one_screen_view
								.get(j);
						ProgressBar proBar = (ProgressBar) rootView
								.findViewById(R.id.progressBar);
						InfoImageView imageView = (InfoImageView) rootView
								.findViewById(R.id.news_pic);
						if (proBar.getVisibility() == View.INVISIBLE) {
							Bitmap bmp = imageView.getBmp();
							imageView.setMyImageBitmap(smallBmp
									.getSmalBmpList()
									.get(iBeforPresentBmps + j));
							proBar.setVisibility(View.VISIBLE);
							this.freeBmp(bmp);
						}
					}
				}
				iBeforPresentBmps += one_screen_view.size();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != managerBmp) {
			managerBmp.query_flag = false;
			managerBmp.manaBmp_flag = false;
		}

		for (int i = 0; i < smallBmp.getSmalBmpList().size(); i++) {
			this.freeBmp(smallBmp.getSmalBmpList().get(i));
		}
		smallBmp.getSmalBmpList().clear();

		// 释放掉MyImageView中的所有Bitmap
		if (null != this.all_screen_view) {
			int iBeforPresentBmps = 0;// 当前屏以前共有多少张图片
			for (int i = 0; i < all_screen_view.size(); i++) {
				List<View> one_screen_view = all_screen_view.get(i);

				for (int j = 0; j < one_screen_view.size(); j++) {
					RelativeLayout rootView = (RelativeLayout) one_screen_view
							.get(j);
					InfoImageView imageView = (InfoImageView) rootView
							.findViewById(R.id.news_pic);
					Bitmap bmp = imageView.getBmp();
					imageView.setMyImageBitmap(null);
					this.freeBmp(bmp);
				}
				// }
				// 删除SD卡上的多余的缓存（只保留第一次加载的图片缓存,不保留下拉或点击获得更多的图片）
				if (iBeforPresentBmps >= 10) {
					for (int j = 0; j < one_screen_view.size(); j++) {
						RelativeLayout rootView = (RelativeLayout) one_screen_view
								.get(j);
						FileCache.getInstance().clearImgByImgUrl(
								rootView.getTag().toString());
					}
				}
				iBeforPresentBmps += one_screen_view.size();
			}
		}
	}

	// 释放Bitmap的方法
	public void freeBmp(Bitmap bmp) {
		if (null != bmp && !bmp.isRecycled()) {
			bmp.recycle();
			bmp = null;
		}
	}

	@Override
	public void onCurChileCtrlScreen(int index, int direct, boolean flag) {
		// TODO Auto-generated method stub
		int preIndex = this.iIndex;
		this.iIndex = index;
		this.initFlag = flag;
		if (null == managerBmp || !managerBmp.isAlive()) {
			managerBmp = new ManagerBmp(handler); // 注意这里必须在重新赋值;如果不重新赋值的话线程结束后，这个引用指向的是已经结束了的线程对象（已经在内存中不存在咯）
			managerBmp.query_flag = true;
			managerBmp.manaBmp_flag = false;
			all_screen_view = newsListLayout.initScreenView();
			// System.out.println("总共有多少屏---------------->"
			// + all_screen_view.size());
			// 取得封装每屏View集合的集合
			managerBmp.start(); // 守护线程只开启一次
		}

		if (preIndex != index) { // 当屏幕变化时使守护线程内部循环开始执行
			managerBmp.manaBmp_flag = true;
		}
	}

	class ManagerBmp extends Thread {
		public boolean manaBmp_flag = false;
		public boolean query_flag = true;
		public Handler handler;

		public ManagerBmp(Handler handler) {
			this.handler = handler;
		}

		public void run() {
			while (query_flag) {
				if (initFlag) {
					all_screen_view = newsListLayout.initScreenView();// 重新取得封装每屏View集合的集合
					manaBmp_flag = true;
				}
				int i = 0;
				int iBeforPresentBmps = 0;// 当前屏以前共有多少张图片
				while (manaBmp_flag && i < all_screen_view.size()) {
					// 属于当前屏幕中心的上下三个屏幕，需要保留内存，因为随时会被上或下滑动
					List<View> one_screen_view = all_screen_view.get(i);
					if (iIndex - 1 == i || iIndex == i || iIndex + 1 == i) {
						for (int j = 0; j < one_screen_view.size(); j++) {
							RelativeLayout rootView = (RelativeLayout) one_screen_view
									.get(j);
							ProgressBar proBar = (ProgressBar) rootView
									.findViewById(R.id.progressBar);
							InfoImageView imageView = (InfoImageView) rootView
									.findViewById(R.id.news_pic);

							if (proBar.getVisibility() == View.VISIBLE) {
								// 获得SD卡上的图片
								Bitmap bmp = FileCache.getInstance().getBmp(
										rootView.getTag().toString());
								// System.out
								// .println("从SD卡上读取图片-------------------------");
								// System.out.println(rootView.getTag()
								// + "------------>图片的URL地址");
								// System.out.println("守护线程开始加载图片...");
								if (null != bmp) {
									imageView.setMyImageBitmap(bmp);
									// System.out.println("开始加载第"
									// + (iBeforPresentBmps + j) + "个View的图片");
									Message msg3 = handler.obtainMessage();
									msg3.what = 0; // 让ProgressBar消失
									msg3.obj = proBar;
									handler.sendMessage(msg3);
								}
							}
						}
					} else {
						for (int j = 0; j < one_screen_view.size(); j++) {
							RelativeLayout rootView = (RelativeLayout) one_screen_view
									.get(j);
							ProgressBar proBar = (ProgressBar) rootView
									.findViewById(R.id.progressBar);
							InfoImageView imageView = (InfoImageView) rootView
									.findViewById(R.id.news_pic);
							Bitmap bitmap = null;
							if (proBar.getVisibility() == View.INVISIBLE) {
								bitmap = imageView.getBmp();
								imageView.setMyImageBitmap(smallBmp
										.getSmalBmpList().get(
												iBeforPresentBmps + j));// 先给它设置小内存的默认图片
								ContentActivity.this.freeBmp(bitmap);// 释放掉
								Message msg = handler.obtainMessage();
								msg.what = 1; // ProgressBar出现
								msg.obj = proBar;
								handler.sendMessage(msg);
							}
						}
					}
					iBeforPresentBmps += one_screen_view.size();
					i++;
				}
				this.manaBmp_flag = false; // 内层循环执行完后停止
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
