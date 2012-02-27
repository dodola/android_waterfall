package com.dodowaterfall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.dodowaterfall.LazyScrollView.OnScrollListener;
import com.dodowaterfall.widget.FlowTag;
import com.dodowaterfall.widget.FlowView;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	private LazyScrollView waterfall_scroll;
	private LinearLayout waterfall_container;
	private ArrayList<LinearLayout> waterfall_items;
	private Display display;
	private AssetManager asset_manager;
	private List<String> image_filenames;
	private final String image_path = "images";
	private Handler handler;
	private int item_width;

	private int column_count = 4;// 显示列数
	private int page_count = 15;// 每次加载15张图片

	private int current_page = 0;

	private int[] topIndex;
	private int[] bottomIndex;

	private int[] column_height;

	private HashMap<Integer, String> pins;
	private int loaded_count = 0;
	private HashMap<Integer, Integer>[] pin_mark = null;
	private Context context;

	private HashMap<Integer, FlowView> iviews;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		display = this.getWindowManager().getDefaultDisplay();
		item_width = display.getWidth() / column_count;// 根据屏幕大小计算每列大小
		asset_manager = this.getAssets();

		topIndex = new int[column_count];
		bottomIndex = new int[column_count];
		column_height = new int[column_count];
		context = this.getApplicationContext();
		iviews = new HashMap<Integer, FlowView>();
		pins = new HashMap<Integer, String>();
		InitLayout();

	}

	private void InitLayout() {
		waterfall_scroll = (LazyScrollView) findViewById(R.id.waterfall_scroll);

		waterfall_scroll.getView();
		waterfall_scroll.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onTop() {
				// 滚动到最顶端
				Log.d("LazyScroll", "Scroll to top");
			}

			@Override
			public void onScroll() {

				// 暂时解决,需重写
				// 滚动
				Rect bounds = new Rect();

				Rect scrollBounds = new Rect(waterfall_scroll.getScrollX(),
						waterfall_scroll.getScrollY(), waterfall_scroll
								.getScrollX() + waterfall_scroll.getWidth(),
						waterfall_scroll.getScrollY()
								+ waterfall_scroll.getHeight());
				for (int i = 1; i < loaded_count; i++) {
					FlowView v = iviews.get(i);
					if (v != null) {
						v.getHitRect(bounds);
						if (Rect.intersects(scrollBounds, bounds)) {
							if (v.bitmap == null) {
								v.Reload();
							}
						} else {
							v.recycle();
						}
					}
				}

			}

			@Override
			public void onBottom() {
				// 滚动到最低端
				AddItemToContainer(++current_page, page_count);
			}
		});

		waterfall_container = (LinearLayout) this
				.findViewById(R.id.waterfall_container);
		handler = new Handler() {

			@Override
			public void dispatchMessage(Message msg) {

				super.dispatchMessage(msg);
			}

			@Override
			public void handleMessage(Message msg) {

				// super.handleMessage(msg);

				switch (msg.what) {
				case 1:
					// Toast.makeText(context, ((FlowView) msg.obj).getHeight(),
					// Toast.LENGTH_SHORT).show();

					FlowView v = (FlowView) msg.obj;
					Log.d("MainActivity",
							String.format(
									"获取实际View高度:%d,ID：%d,columnIndex:%d,rowIndex:%d,filename:%s",
									v.getHeight(), v.getId(), v
											.getColumnIndex(), v.getRowIndex(),
									v.getFlowTag().getFileName()));
					String f = v.getFlowTag().getFileName();
					column_height[v.getColumnIndex()] += v.getHeight();

					pins.put(v.getId(), f);
					iviews.put(v.getId(), v);
					break;
				}

			}

			@Override
			public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
				return super.sendMessageAtTime(msg, uptimeMillis);
			}
		};

		waterfall_items = new ArrayList<LinearLayout>();

		for (int i = 0; i < column_count; i++) {
			LinearLayout itemLayout = new LinearLayout(this);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					item_width, LayoutParams.WRAP_CONTENT);

			itemLayout.setPadding(2, 2, 2, 2);
			itemLayout.setOrientation(LinearLayout.VERTICAL);

			itemLayout.setLayoutParams(itemParam);
			waterfall_items.add(itemLayout);
			waterfall_container.addView(itemLayout);
		}

		// 加载所有图片路径

		try {
			image_filenames = Arrays.asList(asset_manager.list(image_path));

		} catch (IOException e) {
			e.printStackTrace();
		}
		// 第一次加载
		AddItemToContainer(current_page, page_count);
	}

	private void AddItemToContainer(int pageindex, int pagecount) {
		int currentIndex = pageindex * pagecount;
		int j = currentIndex % column_count;
		int imagecount = image_filenames.size();
		for (int i = currentIndex; i < pagecount * (pageindex + 1)
				&& i < imagecount; i++) {
			loaded_count++;
			j = j >= column_count ? j = 0 : j;
			AddImage(image_filenames.get(i), j++,
					(int) Math.ceil(loaded_count / (double) column_count),
					loaded_count);

		}

	}

	private void AddImage(String filename, int columnIndex, int rowIndex, int id) {
		FlowView item = (FlowView) LayoutInflater.from(this).inflate(
				R.layout.waterfallitem, null);

		item.setColumnIndex(columnIndex);
		item.setRowIndex(rowIndex);
		item.setId(id);
		item.setViewHandler(this.handler);
		// 多线程参数
		FlowTag param = new FlowTag();
		param.setFlowId(id);
		param.setAssetManager(asset_manager);
		param.setFileName(image_path + "/" + filename);
		param.setItemWidth(item_width);

		item.setFlowTag(param);
		item.LoadImage();
		waterfall_items.get(columnIndex).addView(item);

	}
}
