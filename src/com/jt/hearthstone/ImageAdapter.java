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
    private ArrayList<Cards> cardList = MainActivity.cardList;
    private ArrayList<Cards> druidCards = MainActivity.druidCards;
	public static String url;
	boolean any = MainActivity.any;
	boolean druid = MainActivity.druid;
	boolean hunter = MainActivity.hunter;
	boolean mage = MainActivity.mage;
	boolean paladin = MainActivity.paladin;
	boolean priest = MainActivity.priest;
	boolean rogue = MainActivity.rogue;
	boolean shaman = MainActivity.shaman;
	boolean warlock = MainActivity.warlock;
	boolean warrior = MainActivity.warrior;
	ImageLoader imageLoader = MainActivity.loader;

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
    	if (druid) {
    		url = "http://jt.comyr.com/images/" + cardList.get(position).getName().replace(" ", "%20").replace(":", "") + ".png";
    	} else {
    		url = "http://jt.comyr.com/images/" + cardList.get(position).getName().replace(" ", "%20").replace(":", "") + ".png";
    	}

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

		vh.tv.setTextColor(Color.BLACK);
    	vh.tv.setText(cardList.get(position).getName());
    	imageLoader.displayImage(url, vh.iv, options);
    	return convertView;
    	
    }

}