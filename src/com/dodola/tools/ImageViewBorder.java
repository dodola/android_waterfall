package com.dodola.tools;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewBorder extends ImageView {
	
	 
	public ImageViewBorder(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	  
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);    
        Rect rec = canvas.getClipBounds();
        rec.bottom--;
        rec.right--;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rec, paint);
    }
    
}
