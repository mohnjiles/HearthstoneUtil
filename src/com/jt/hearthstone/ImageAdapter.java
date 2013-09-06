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
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Cards> cardList = MainActivity.cardList;
	public static String url;
	ImageLoader imageLoader = ImageLoader.getInstance();

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
    
    static class ViewHolder {
    	ImageView iv = null;
    	TextView tv = null;
    }
    

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder vh;

    	
    	url = "http://jt.comyr.com/images/" + cardList.get(position).getName().replace(" ", "%20").replace(":", "") + ".png";
    
    	ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
    	.denyCacheImageMultipleSizesInMemory().build();
    	imageLoader.init(config);
    	 DisplayImageOptions options = new DisplayImageOptions.Builder()
         .showStubImage(R.drawable.ic_launcher)
         .cacheInMemory(true)
         .cacheOnDisc(false)
         .build();
    	 
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
    	//vh.iv.setImageResource(mThumbIds[position]);
    	vh.tv.setTextColor(Color.BLACK);
    	vh.tv.setText(cardList.get(position).getName());
    	imageLoader.displayImage(url, vh.iv, options);
    	
    	
    	
    	
    	return convertView;
    	
    }

}