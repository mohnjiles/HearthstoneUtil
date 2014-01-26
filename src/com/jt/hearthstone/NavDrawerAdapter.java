package com.jt.hearthstone;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NavDrawerAdapter extends ArrayAdapter<String> {
	private Context mContext;
	private Typeface font;

	public NavDrawerAdapter(Context c, int resId, String[] objects) {
		super(c, resId, objects);
		mContext = c;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

		TextView v = (TextView) view.findViewById(R.id.tvDrawer);
		font = TypefaceCache.get(mContext.getAssets(),
				"fonts/belwebd.ttf");
		v.setTypeface(font);

		return view;
	}

}