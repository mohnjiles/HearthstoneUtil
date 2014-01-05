package com.jt.hearthstone;

import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Cards> cardList;
	private String url;
	private ImageLoader imageLoader = ImageLoader.getInstance();

    public ImageAdapter(Context c, List<Cards> list) {
        mContext = c;
        cardList = list;
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
    	Typeface font = TypefaceCache.get(mContext.getAssets(),
				"fonts/belwebd.ttf");
    	url = "http://54.224.222.135/Square/" + cardList.get(position).getImage() + ".png";
    	
    	// ImageLoader options to save images in Memory so we don't have to re-draw them. 
    	// We may eventually need to disable this based on further testing
    	// Not sure if real phones will have enough RAM to hold 500+ images, granted they're 
    	// only a few KB each.
    	
    	if (!imageLoader.isInited()) {
    		imageLoader.init(Utils.config(mContext));
    	}
    	 
    	// If our view (in this case, one item from the gridview) is null, then inflate it.
    	// We do this because re-using views makes memory happy :)
    	if (convertView == null) {
    		convertView = View.inflate(mContext, R.layout.grid_layout, null);
    		vh = new ViewHolder();

    		vh.tv = (TextView) convertView.findViewById(R.id.grid_item_text);
    		vh.iv = (ImageView) convertView.findViewById(R.id.grid_item_image);
    		convertView.setTag(vh);	
    		vh.iv.setImageResource(R.drawable.cards);
    		
    	} else {
    		vh = (ViewHolder)convertView.getTag();
    	}
		// Set the Text of the TextView
    	vh.tv.setTypeface(font);
    	vh.tv.setText(cardList.get(position).getName());
    	vh.tv.setShadowLayer(1.0f, 1, 1, Color.BLACK);
    	// Load the image for the ImageView
    	imageLoader.displayImage(url, vh.iv, Utils.defaultOptions);
    	return convertView;
    	
    }

}