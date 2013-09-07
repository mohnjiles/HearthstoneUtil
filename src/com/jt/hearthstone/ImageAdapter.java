package com.jt.hearthstone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Cards> cardList = MainActivity.cardList; // Get card list from MainActivity
	public static String url;
	ImageLoader imageLoader = MainActivity.loader; // Get instance of ImageLoader from main activity

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
    	return cardList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
    
    // Custom ViewHolder class to make scrolling smoother
    static class ViewHolder {
    	ImageView iv = null;
    	TextView tv = null;
    }
    

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder vh;
    	url = "http://jt.comyr.com/images/" + cardList.get(position).getName().replace(" ", "%20").replace(":", "") + ".png";
    	
    	// ImageLoader options to save images in Memory so we don't have to re-draw them. 
    	// We may eventually need to disable this based on further testing
    	// Not sure if real phones will have enough RAM to hold 500+ images, granted they're 
    	// only a few KB each.
    	DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showStubImage(R.drawable.ic_launcher)
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .build();
    	 
    	// If our view (in this case, one item from the gridview) is null, then inflate it.
    	// We do this because re-using views makes memory happy :)
    	if (convertView == null) {
    		convertView = View.inflate(mContext, R.layout.grid_layout, null);
    		vh = new ViewHolder();

    		vh.tv = (TextView) convertView.findViewById(R.id.grid_item_text);
    		vh.iv = (ImageView) convertView.findViewById(R.id.grid_item_image);
    		convertView.setTag(vh);	
    		vh.iv.setImageResource(R.drawable.ic_launcher);
    		
    	} else {
    		vh = (ViewHolder)convertView.getTag();
    	}
    	// Set the TextView color to black, even though we can do this in XML.
    	// On second thought, do we even need the TextView?
		vh.tv.setTextColor(Color.BLACK);
		// Set the Text of the TextView
    	vh.tv.setText(cardList.get(position).getName());
    	// Load the image for the ImageView
    	imageLoader.displayImage(url, vh.iv, options);
    	return convertView;
    	
    }

}