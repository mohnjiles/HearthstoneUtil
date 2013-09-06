package com.jt.hearthstone;

import java.util.ArrayList;

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
    private ArrayList<Cards> cardNames = MainActivity.cardNames;
	public static String url;
	ImageLoader imageLoader = ImageLoader.getInstance();

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return cardNames.size();
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
    
    	
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		url = "http://jt.comyr.com/images/" + cardNames.get(position).getName().replace(" ", "%20") + ".png";
//		final ImageView imageView;
//		LayoutInflater li = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
//		
//		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
//		
//		if (convertView == null) {
//			imageView = (ImageView) li.inflate(R.layout.item_grid_image, parent, false);
//		} else {
//			imageView = (ImageView) convertView;
//		}
//
//		imageLoader.displayImage(url, imageView);
//
//		return imageView;
//	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder vh;

    	
    	url = "http://jt.comyr.com/images/" + cardNames.get(position).getName().replace(" ", "%20").replace(":", "") + ".png";
    	imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
    	 DisplayImageOptions options = new DisplayImageOptions.Builder()
         .showStubImage(R.drawable.ic_launcher)
         .cacheInMemory()
         .cacheOnDisc()
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
    	vh.tv.setText(cardNames.get(position).getName());
    	imageLoader.displayImage(url, vh.iv, options);
    	
    	return convertView;
    	
    }

}