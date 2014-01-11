package com.jt.hearthstone;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<String> {
	private Context mContext;
	private Typeface font;

	public CustomArrayAdapter(Context context, int resource,
			int textViewResourceId, String[] objects) {
		super(context, R.layout.spinner_row, R.id.name, objects);
		mContext = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

		TextView v = (TextView) view.findViewById(R.id.name);
		font = TypefaceCache.get(mContext.getAssets(),
				"fonts/belwebd.ttf");
		v.setTypeface(font);

		return view;
	}

	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		view.setBackgroundColor(Color.rgb(184, 157, 117));
		view.setPadding(0, 16, 0, 16);
		TextView v = (TextView) view.findViewById(R.id.name);
		if (font == null) {
			font = TypefaceCache.get(mContext.getAssets(), "fonts/belwebd.ttf");
		}
		v.setTypeface(font);
		return view;
	}
	
	public void setDropDownViewResource(int resource) {
		super.setDropDownViewResource(resource);
	}

}