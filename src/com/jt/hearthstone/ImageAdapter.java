package com.jt.hearthstone;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Cards> cardList;

    public ImageAdapter(Context c, List<Cards> list) {
        mContext = c;
        cardList = list;
    }

    public int getCount() {
    	return cardList.size();
    }
    
    public void update(List<Cards> list){
    	cardList.clear();
    	cardList.addAll(list);
    	Collections.sort(cardList, new CardComparator(2, false));
    	this.notifyDataSetChanged();
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
    	
    	 
    	// If our view (in this case, one item from the gridview) is null, then inflate it.
    	// We do this because re-using views makes memory happy :)
    	if (convertView == null) {
    		convertView = View.inflate(mContext, R.layout.grid_layout, null);
    		vh = new ViewHolder();

    		vh.tv = (TextView) convertView.findViewById(R.id.grid_item_text);
    		vh.iv = (ImageView) convertView.findViewById(R.id.grid_item_image);
    		convertView.setTag(vh);	
    		vh.iv.setImageBitmap(ImageCache.get(mContext, R.drawable.cards));
    		
    	} else {
    		vh = (ViewHolder)convertView.getTag();
    	}
		// Set the Text of the TextView
    	vh.tv.setTypeface(font);
    	vh.tv.setText(cardList.get(position).getName());
    	
    	// Load the image for the ImageView
    	int resId = Utils.getResIdByName(mContext, cardList.get(position).getImage() + "_square");
    	vh.iv.setImageResource(resId);
    	
    	return convertView;
    	
    }

}
