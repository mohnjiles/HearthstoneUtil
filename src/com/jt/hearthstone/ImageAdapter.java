package com.jt.hearthstone;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
    
    static class ViewHolder {
    	ImageView iv = null;
    	TextView tv = null;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder vh;
    	View v;
    	Cards cards = (Cards) getItem(position);
    	//String[] champNames = mContext.getResources().getStringArray(R.array.ChampNames);
    	
    	if (convertView == null) {
    		vh = new ViewHolder();
    		@SuppressWarnings("static-access")
			LayoutInflater li = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
    		
    		v = li.inflate(R.layout.grid_layout, null);
    		vh.tv = (TextView)v.findViewById(R.id.grid_item_text);
    		vh.iv = (ImageView)v.findViewById(R.id.grid_item_image);
    		v.setTag(vh);		
    	} else {
    		vh = (ViewHolder)convertView.getTag();
    		v = convertView;
    	}
    	vh.iv.setImageResource(mThumbIds[position]);
    	vh.tv.setTextColor(Color.WHITE);
    	vh.tv.setText(cards.getName());
    	return v;
    	
    	
    }

    // references to our images
    public static Integer[] mThumbIds = {
    		R.drawable.cs1_042, R.drawable.cs1_069,
    		R.drawable.cs1_112
           
    };
}