package com.jt.hearthstone;

import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class Utils {
	private static Context cxt = HearthstoneUtil.getAppContext();
	private static Locale curLocale = cxt.getResources().getConfiguration().locale;

	static final DisplayImageOptions noStubOptions = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(false).cacheOnDisc(true)
			.cacheInMemory(false).bitmapConfig(Bitmap.Config.ARGB_8888)
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

	static final DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(false).cacheInMemory(false)
			.cacheOnDisc(true).showStubImage(R.drawable.cards)
			.bitmapConfig(Bitmap.Config.ARGB_8888)
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

	static final ImageLoaderConfiguration config(Context c) {
		return new ImageLoaderConfiguration.Builder(c)
				.discCacheExtraOptions(480, 800, CompressFormat.PNG, 75, null)
				.threadPoolSize(5).defaultDisplayImageOptions(defaultOptions)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheSize(100 * 1024 * 1024).writeDebugLogs().build();
	}
	
	static final ImageLoaderConfiguration squareConfig(Context c) {
		return new ImageLoaderConfiguration.Builder(c)
				.discCacheExtraOptions(120, 120, CompressFormat.PNG, 75, null)
				.threadPoolSize(5).defaultDisplayImageOptions(defaultOptions)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheSize(100 * 1024 * 1024).writeDebugLogs().build();
	}

	static int getResIdByName(Context context, String drawableName) {
		return context.getResources().getIdentifier(
				drawableName.toLowerCase(curLocale), "drawable",
				context.getPackageName());
	}

	static String makeFragmentName(int viewId, int index) {
		return "android:switcher:" + viewId + ":" + index;
	}

}
