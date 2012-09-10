package com.dodowaterfall.widget;

import java.io.BufferedInputStream;
import java.io.IOException;

import com.dodowaterfall.Constants;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class FlowView extends ImageView implements View.OnClickListener,
		View.OnLongClickListener {

	private Context context;
	public Bitmap bitmap;
	private int columnIndex;// 图片属于第几列
	private int rowIndex;// 图片属于第几行
	private String fileName;
	private int ItemWidth;
	private Handler viewHandler;

	public FlowView(Context c, AttributeSet attrs, int defStyle) {
		super(c, attrs, defStyle);
		this.context = c;
		Init();
	}

	public FlowView(Context c, AttributeSet attrs) {
		super(c, attrs);
		this.context = c;
		Init();
	}

	public FlowView(Context c) {
		super(c);
		this.context = c;
		Init();
	}

	private void Init() {

		setOnClickListener(this);
		setOnLongClickListener(this);
		setAdjustViewBounds(true);

	}

	@Override
	public boolean onLongClick(View v) {
		Log.d("FlowView", "LongClick");
		Toast.makeText(context, "长按：" + getId(),
				Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public void onClick(View v) {
		Log.d("FlowView", "Click");
		Toast.makeText(context, "单击：" + getId(),
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * 加载图片
	 */
	public void LoadImage() {
			new LoadImageThread().start();
	}

	/**
	 * 重新加载图片
	 */
	public void Reload() {
		if (this.bitmap == null) {
			new ReloadImageThread().start();
		}
	}

	/**
	 * 回收内存
	 */
	public void recycle() {
		setImageBitmap(null);
		if ((this.bitmap == null) || (this.bitmap.isRecycled()))
			return;
		this.bitmap.recycle();
		this.bitmap = null;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getItemWidth() {
		return ItemWidth;
	}

	public void setItemWidth(int itemWidth) {
		ItemWidth = itemWidth;
	}

	public Handler getViewHandler() {
		return viewHandler;
	}

	public FlowView setViewHandler(Handler viewHandler) {
		this.viewHandler = viewHandler;
		return this;
	}

	class ReloadImageThread extends Thread {

		@Override
		public void run() {

			BufferedInputStream buf;
			try {
				buf = new BufferedInputStream(context.getAssets()
						.open(getFileName()));
				bitmap = BitmapFactory.decodeStream(buf);

			} catch (IOException e) {

				e.printStackTrace();
			}

			((Activity) context).runOnUiThread(new Runnable() {
				public void run() {
					if (bitmap != null) {// 此处在线程过多时可能为null
						setImageBitmap(bitmap);
					}
				}
			});
		}
	}

	class LoadImageThread extends Thread {
		LoadImageThread() {
		}

		public void run() {

			BufferedInputStream buf;
			try {
				buf = new BufferedInputStream(context.getAssets()
						.open(getFileName()));
				bitmap = BitmapFactory.decodeStream(buf);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// if (bitmap != null) {

			// 此处不能直接更新UI，否则会发生异常：
			// CalledFromWrongThreadException: Only the original thread that
			// created a view hierarchy can touch its views.
			// 也可以使用Handler或者Looper发送Message解决这个问题

			((Activity) context).runOnUiThread(new Runnable() {
				public void run() {
					if (bitmap != null) {// 此处在线程过多时可能为null
						int width = bitmap.getWidth();// 获取真实宽高
						int height = bitmap.getHeight();

						LayoutParams lp = getLayoutParams();

						int layoutHeight = (height * getItemWidth())
									/ width;// 调整高度
						if (lp == null) {
							lp = new LayoutParams(getItemWidth(),
									layoutHeight);
						}
						setLayoutParams(lp);

						setImageBitmap(bitmap);
						Handler h = getViewHandler();
						Message m = h.obtainMessage(Constants.HANDLER_WHAT, width,
								layoutHeight, FlowView.this);
						h.sendMessage(m);
					}
				}
			});
		}
	}
}
