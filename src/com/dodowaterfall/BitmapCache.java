package com.dodowaterfall;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/**
 * 防止溢出
 *
 */
public class BitmapCache {
	static private BitmapCache cache;
	/** 用于Chche内容的存储 */
	private Hashtable<String, BtimapRef> bitmapRefs;
	/** 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中） */
	private ReferenceQueue<Bitmap> q;

	/**
	 * 继承SoftReference，使得每一个实例都具有可识别的标识。
	 */
	private class BtimapRef extends SoftReference<Bitmap> {
		private String _key = "";

		public BtimapRef(Bitmap bmp, ReferenceQueue<Bitmap> q, String key) {
			super(bmp, q);
			_key = key;
		}
	}

	private BitmapCache() {
		bitmapRefs = new Hashtable<String, BtimapRef>();
		q = new ReferenceQueue<Bitmap>();

	}

	/**
	 * 取得缓存器实例
	 */
	public static BitmapCache getInstance() {
		if (cache == null) {
			cache = new BitmapCache();
		}
		return cache;

	}

	/**
	 * 以软引用的方式对一个Bitmap对象的实例进行引用并保存该引用
	 */
	private void addCacheBitmap(Bitmap bmp, String key) {
		cleanCache();// 清除垃圾引用
		BtimapRef ref = new BtimapRef(bmp, q, key);
		bitmapRefs.put(key, ref);
	}

	/**
	 * 依据所指定的文件名获取图片
	 */
	public Bitmap getBitmap(String filename, AssetManager assetManager) {

		Bitmap bitmapImage = null;
		// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
		if (bitmapRefs.containsKey(filename)) {
			BtimapRef ref = (BtimapRef) bitmapRefs.get(filename);
			bitmapImage = (Bitmap) ref.get();
		}
		// 如果没有软引用，或者从软引用中得到的实例是null，重新构建一个实例，
		// 并保存对这个新建实例的软引用
		if (bitmapImage == null) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inTempStorage = new byte[16 * 1024];

			// bitmapImage = BitmapFactory.decodeFile(filename, options);
			BufferedInputStream buf;
			try {
				buf = new BufferedInputStream(assetManager.open(filename));
				bitmapImage = BitmapFactory.decodeStream(buf);
				this.addCacheBitmap(bitmapImage, filename);
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		return bitmapImage;
	}

	private void cleanCache() {
		BtimapRef ref = null;
		while ((ref = (BtimapRef) q.poll()) != null) {
			bitmapRefs.remove(ref._key);
		}
	}

	// 清除Cache内的全部内容
	public void clearCache() {
		cleanCache();
		bitmapRefs.clear();
		System.gc();
		System.runFinalization();
	}

}
