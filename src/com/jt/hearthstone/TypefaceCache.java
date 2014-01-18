package com.jt.hearthstone;

import java.util.Hashtable;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

public class TypefaceCache {

    private static final Hashtable<String, Typeface> CACHE = new Hashtable<String, Typeface>();

    public static Typeface get(AssetManager manager, String name) {
        synchronized (CACHE) {

            if (!CACHE.containsKey(name)) {
                try {
                    Typeface t = Typeface.createFromAsset(manager,
                            name);
                    CACHE.put(name, t);
                } catch (Exception e) {
                    Log.e("TypefaceCache", "Could not get typeface '" + name
                            + "' because " + e.getMessage());
                    return null;
                }
            }
            return CACHE.get(name);
        }
    }

}
