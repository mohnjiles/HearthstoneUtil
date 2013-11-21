package com.jt.hearthstone;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageCache {

    private static final Hashtable<Integer, Bitmap> CACHE = new Hashtable<Integer, Bitmap>();

    public static Bitmap get(Context context, Integer id) {
        synchronized (CACHE) {

            if (!CACHE.containsKey(id)) {
                Bitmap icon = BitmapFactory.decodeResource(context.getResources(), id);
                CACHE.put(id, icon);
            }
            return CACHE.get(id);
        }
    }

}
