package com.sxu.imageloader.instance;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.sxu.imageloader.listener.ImageLoaderListener;
import com.sxu.imageloader.WrapImageView;
import com.sxu.utils.FastBlurUtil;

/**

 * 类或接口的描述信息
 *
 * @author Freeman
 * @date 2017/12/5
 */


public class UILInstance implements ImageLoaderInstance {

	private DisplayImageOptions options;

	private void initOptions(final WrapImageView imageView) {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
				.showImageOnLoading(imageView.getPlaceHolder())
				.showImageForEmptyUri(imageView.getPlaceHolder())
				.showImageOnFail(imageView.getFailureHolder())
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.considerExifParams(true)
				.preProcessor(!imageView.isBlur() ? null : new BitmapProcessor() {
					@Override
					public Bitmap process(Bitmap bitmap) {
						return FastBlurUtil.doBlur(bitmap, 2, imageView.getBlurRadius() * 4);
					}
				});
		int shape = imageView.getShape();
		if (shape == WrapImageView.SHAPE_CIRCLE) {
			builder.displayer(new CircleBitmapDisplayer(imageView.getBorderColor(), imageView.getBorderWidth()));
		} else if (shape == WrapImageView.SHAPE_ROUND) {
			builder.displayer(new RoundedBitmapDisplayer(imageView.getRadius()));
		}
		options = builder.build();
	}

	@Override
	public void init(Context context) {
		int memCacheSize = (int) Math.min(Runtime.getRuntime().maxMemory() / 8, 64 * 1024 * 1024);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context.getApplicationContext())
				.denyCacheImageMultipleSizesInMemory()
				.memoryCacheSize(memCacheSize)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.threadPriority(10)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.diskCacheFileCount(Integer.MAX_VALUE)
				.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 5 * 1000))
				.writeDebugLogs()
				.build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	public void displayImage(String url, WrapImageView imageView) {
		initOptions(imageView);
		ImageLoader.getInstance().displayImage(url, imageView, options);
	}

	@Override
	public void displayImage(String url, WrapImageView imageView, final ImageLoaderListener listener) {
		initOptions(imageView);
		ImageLoader.getInstance().displayImage(url, imageView, options, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				if (listener != null) {
					listener.onStart();
				}
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				if (listener != null) {
					listener.onFailure(new Exception(failReason.getCause()));
				}
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if (listener != null) {
					listener.onCompleted(loadedImage);
				}
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				if (listener != null) {
					listener.onFailure(new Exception("task is cancelled"));
				}
			}
		});
	}

	@Override
	public void downloadImage(Context context, String url, final ImageLoaderListener listener) {
		ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				if (listener != null) {
					listener.onStart();
				}
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				if (listener != null) {
					listener.onFailure(new Exception(failReason.getCause()));
				}
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if (listener != null) {
					listener.onCompleted(loadedImage);
				}
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				if (listener != null) {
					listener.onFailure(new Exception("task is cancelled"));
				}
			}
		});
	}

	@Override
	public void destroy() {
		if (ImageLoader.getInstance().isInited()) {
			ImageLoader.getInstance().destroy();
		}
	}
}
