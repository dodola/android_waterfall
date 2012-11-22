package com.dodola.views;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dodola.R;
import com.dodola.activity.ContentActivity;
import com.dodola.model.DuitangInfo;
import com.dodola.tools.AsyncImageLoader;
import com.dodola.tools.FileCache;
import com.dodola.tools.AsyncImageLoader.ImageCallBack;

/**
 * 
 * @author dell
 * 
 */
public class InfosListAdapter extends BaseAdapter {

	public List<DuitangInfo>		list;
	public ContentActivity	context;
	public AsyncImageLoader		asyncImageLoader;
	public Bitmap				bitmap;

	// -----------------------
	public InfosSmallBmp			smallBmp;

	public InfosListAdapter(Context context, List<DuitangInfo> list, InfosSmallBmp smallBmp) {
		this.list = list;
		this.context = (ContentActivity) context;
		asyncImageLoader = new AsyncImageLoader();

		// -----------------------------
		this.smallBmp = smallBmp;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Map<String, Object> getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void setList(List<DuitangInfo> list) {
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(R.layout.infos_list, null);
		}
		final View view = convertView;
		final DuitangInfo newsInfo = list.get(position);
		final String imgUrl;
		imgUrl = newsInfo.getIsrc();
		view.setTag(imgUrl);

		final InfoImageView imageView = (InfoImageView) convertView.findViewById(R.id.news_pic);
		imageView.setTag(imgUrl);
		TextView newsTitle = (TextView) convertView.findViewById(R.id.news_title);
		TextView newsTime = (TextView) convertView.findViewById(R.id.news_time);
		newsTitle.setText(newsInfo.getMsg());
		newsTime.setText(newsInfo.getAlbid());
		final ImageView newsCover = (ImageView) convertView.findViewById(R.id.news_cover);
		LinearLayout news_list = (LinearLayout) convertView.findViewById(R.id.news_list);
		final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);

		final int height = context.getWindowManager().getDefaultDisplay().getHeight();
		
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
			R.drawable.news_picture);
		
		// 如果联网
		if (context.checkConnection()) {
			Bitmap bmpFromSD = FileCache.getInstance().getBmp(imgUrl);
			if (null != bmpFromSD) {
	
				smallBmp.addSmalBmp(bmp);
				imageView.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, bmpFromSD.getHeight()));
				imageView.setMyImageBitmap(bmpFromSD);
				progressBar.setVisibility(View.INVISIBLE);
			}
			else {
				Drawable cachedImage = asyncImageLoader.loaDrawable(imgUrl, new ImageCallBack() {
					@Override
					public void imageLoaded(Drawable imageDrawable) {
						Bitmap bitmap = InfosListAdapter.this.drawToBmp(imageDrawable);
						FileCache.getInstance().savaBmpData(view.getTag().toString(), bitmap);// 先缓存起来
						InfoImageView imageViewByTag = null;
						if (null != bitmap) {
							imageViewByTag = (InfoImageView) imageView.findViewWithTag(imgUrl);
							imageViewByTag.setLayoutParams(new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT, bitmap.getHeight()));
						}
						if (imageViewByTag != null) {
							if (context.isWifi(context)) {
								imageViewByTag.setMyImageBitmap(bitmap);
								progressBar.setVisibility(View.INVISIBLE);
							}
							else {
								if (bitmap != null) {
									imageViewByTag.setLayoutParams(new LinearLayout.LayoutParams(
										LinearLayout.LayoutParams.FILL_PARENT, bitmap.getHeight()
												* 2 * height / 854));
									imageViewByTag.setMyImageBitmap(bitmap);
									imageViewByTag.setScaleType(ImageView.ScaleType.MATRIX);
									progressBar.setVisibility(View.INVISIBLE);
								}
							}
						}
					}
				});
				if (cachedImage == null) {
			
					imageView.setMyImageBitmap(bmp);
					smallBmp.addSmalBmp(bmp);
				}
				else {
			
					smallBmp.addSmalBmp(bmp);
					if (context.isWifi(context)) {
						Bitmap bitmap = InfosListAdapter.this.drawToBmp(cachedImage);
						imageView.setLayoutParams(new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT, bitmap.getHeight()));
						imageView.setMyImageBitmap(bitmap);
					}
					else {
						imageView.setLayoutParams(new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.FILL_PARENT, cachedImage.getIntrinsicHeight()
									* 2 * height / 854));
						Bitmap bitmap = InfosListAdapter.this.drawToBmp(cachedImage);
						imageView.setMyImageBitmap(bitmap);
					}
					progressBar.setVisibility(View.INVISIBLE);
				}
			}
		}
		else {
			Bitmap bmpFromSD = FileCache.getInstance().getBmp(imgUrl);
			if (null != bmpFromSD) {
				InfoImageView imageViewByTag = (InfoImageView) imageView.findViewWithTag(imgUrl);
				imageViewByTag.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, bmpFromSD.getHeight()));
				imageViewByTag.setMyImageBitmap(bmpFromSD);
				progressBar.setVisibility(View.INVISIBLE);
			
				smallBmp.addSmalBmp(bmp);
			}
			else {
				
				imageView.setMyImageBitmap(bmp);
				progressBar.setVisibility(View.GONE);
				smallBmp.addSmalBmp(bmp);
			}

		}
		news_list.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					newsCover.setVisibility(android.view.View.VISIBLE);
				}
				else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
					newsCover.setVisibility(android.view.View.INVISIBLE);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP) {
					newsCover.setVisibility(android.view.View.INVISIBLE);
					context.selectedView = view;
					context.isDetailBcak = 1;

				}
				return true;
			}
		});

		return convertView;
	}

	public void removeObject() {
		this.list.clear();
		this.list.size();
	}

	/**
	 * Drawable转换成Bitmap
	 * 
	 * @param d
	 * @return
	 */
	public Bitmap drawToBmp(Drawable d) {
		if (null != d) {
			BitmapDrawable bd = (BitmapDrawable) d;
			return bd.getBitmap();
		}
		return null;
	}

}
