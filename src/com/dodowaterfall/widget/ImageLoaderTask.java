package com.dodowaterfall.widget;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

//已不使用此类：AsyncTask加载过多会造成 java.util.concurrent.RejectedExecutionException
public class ImageLoaderTask extends AsyncTask<FlowTag, Void, Bitmap> {

	private FlowTag param;
	private final WeakReference<FlowView> imageViewReference;

	public ImageLoaderTask(FlowView imageView) {
		imageViewReference = new WeakReference<FlowView>(imageView);

	}

	@Override
	protected Bitmap doInBackground(FlowTag... params) {

		param = params[0];
		return loadImageFile(param.getFileName(), param.getAssetManager());
	}

	private Bitmap loadImageFile(final String filename,
			final AssetManager manager) {
		InputStream is = null;
		try {
			Bitmap bmp = null;
			// Bitmap bmp = BitmapCache.getInstance().getBitmap(filename,
			// param.getAssetManager());
			BufferedInputStream buf;
			try {
				buf = new BufferedInputStream(param.getAssetManager().open(
						filename));
				bmp = BitmapFactory.decodeStream(buf);

			} catch (IOException e) {

				e.printStackTrace();
			}

			return bmp;
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}

		if (imageViewReference != null) {
			final FlowView imageView = imageViewReference.get();
			if (imageView != null) {
				if (bitmap != null) {
					int width = bitmap.getWidth();// 获取真实宽高
					int height = bitmap.getHeight();

					LayoutParams lp = imageView.getLayoutParams();
					lp.height = (height * param.getItemWidth()) / width;// 调整高度
					imageView.setLayoutParams(lp);
					imageView.bitmap = bitmap;
					imageView.setImageBitmap(imageView.bitmap);// 将引用指定到同一个对象，方便销毁

					Handler h = imageView.getViewHandler();
					Message m = h.obtainMessage(this.param.what,
							this.param.getFlowId(), lp.height, imageView);
					h.sendMessage(m);
					
				}
			}
		}
	}
}