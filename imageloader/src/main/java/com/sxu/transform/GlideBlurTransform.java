package com.sxu.transform;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.sxu.utils.DiskLruCacheManager;
import com.sxu.utils.FastBlurUtil;

import java.security.MessageDigest;

/**

 * 类或接口的描述信息
 *
 * @author Freeman
 * @date 2017/12/19
 */


public class GlideBlurTransform extends BitmapTransformation {

	private String url;
	private Context context;
	private int blurRadius;

	private final int MAX_CACHE_SIZE = 16 * 1024 * 1024;

	public GlideBlurTransform(Context context, String url, int blurRadius) {
		this.context = context;
		this.url = url;
		this.blurRadius = blurRadius;
	}

	@Override
	protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
		Bitmap bitmap =  FastBlurUtil.doBlur(toTransform, 8, blurRadius);
		// 缓存高斯模糊图片
		// DiskLruCacheManager.getInstance(context).put(url, bitmap);
		return bitmap;
	}

	@Override
	public void updateDiskCacheKey(MessageDigest messageDigest) {

	}
}
