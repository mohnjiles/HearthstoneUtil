package com.jt.hearthstone;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class Utils {
	private static Context cxt = HearthstoneUtil.getAppContext();
	static Locale curLocale = cxt.getResources().getConfiguration().locale;
	static Cards[] cards;

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
	
	static Cards[] setupCardList() {
		Gson gson = new Gson();

		FileInputStream fis = null;
		try {
			fis = cxt.openFileInput("cards.json");
		} catch (FileNotFoundException e1) {
			copyFile("cards.json");
			try {
				fis = cxt.openFileInput("cards.json");
			} catch (FileNotFoundException e) {
				Log.wtf("How is this possible?", "cards.json broke");
				e.printStackTrace();
			}
			e1.printStackTrace();
		}
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(fis,
					"UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// The json String from the file
		String jsonString = writer.toString();

		// Set our pojo from the GSON data
		cards = gson.fromJson(jsonString, Cards[].class);
		return cards;
	}
	
	private static void copyFile(String filename) {
		AssetManager assetManager = cxt.getAssets();

		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(filename);
			String newFileName = cxt.getFilesDir().getPath() + "/"
					+ filename;
			out = new FileOutputStream(newFileName);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
		}

	}

}
