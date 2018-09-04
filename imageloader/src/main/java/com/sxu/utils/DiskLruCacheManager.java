package com.sxu.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*******************************************************************************
 * Description: 用于缓存经过高斯模糊的图片
 *
 * Author: Freeman
 *
 * Date: 2018/9/4
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
public class DiskLruCacheManager {

	private DiskLruCache diskLruCache;
	private static DiskLruCacheManager instance;

	private final int MAX_CACHE_SIZE = 16 * 1024 * 1024;

	private DiskLruCacheManager(Context context) {
		try {
			diskLruCache = DiskLruCache.open(context.getCacheDir(), 0, 0,
					MAX_CACHE_SIZE, Integer.MAX_VALUE);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public static DiskLruCacheManager getInstance(Context context) {
		if (instance == null) {
			synchronized (instance) {
				if (instance == null) {
					instance = new DiskLruCacheManager(context.getApplicationContext());
				}
			}
		}

		return instance;
	}

	public void put(String url, Bitmap bitmap) {
		if (bitmap == null || bitmap.isRecycled()) {
			return;
		}

		try {
			DiskLruCache.Editor editor = diskLruCache.edit(url);
			OutputStream outputStream = editor.newOutputStream(0);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
				editor.commit();
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public Bitmap get(String url) {
		try {
			DiskLruCache.Editor editor = diskLruCache.edit(url);
			InputStream inputStream = editor.newInputStream(0);
			return BitmapFactory.decodeStream(inputStream);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		return null;
	}

	public void close() {
		try {
			diskLruCache.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
