package com.dodola.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class InfoImageView extends ImageView {

	public boolean	bSwitch	= false;
	private Bitmap	bitmap;

	public InfoImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setMyImageBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		if (null != bitmap) {

			this.setMinimumWidth(bitmap.getWidth());
			this.setMinimumHeight(bitmap.getHeight());
			this.postInvalidate();
		}
	}

	public Bitmap getBmp() {
		return this.bitmap;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		if (null != bitmap) {
			synchronized (bitmap) {// ......
				if (null != bitmap && !bitmap.isRecycled()) {
					canvas.drawBitmap(bitmap,
						new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
						new Rect(0, 0, this.getWidth(), this.getHeight()), null);
				}
			}
		}
	}
}
