package com.jt.hearthstone;

import java.util.Hashtable;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

public class ImageCache {

	private static final Hashtable<Integer, Bitmap> CACHE = new Hashtable<Integer, Bitmap>();

	public static Bitmap get(Context context, Integer id) {
		synchronized (CACHE) {

			if (!CACHE.containsKey(id)) {
				Bitmap icon = BitmapFactory.decodeResource(
						context.getResources(), id);
				CACHE.put(id, icon);
			}
			return CACHE.get(id);
		}
	}
}
