package com.jt.hearthstone;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
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

public class DecksAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> cardList;
    private List<Integer> classList;
	private String url;
	private ImageLoader imageLoader = ImageLoader.getInstance();

    public DecksAdapter(Context c, ArrayList<String> list, List<Integer> classes) {
        mContext = c;
        cardList = list;
        classList = classes;
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
    	vh.tv.setText(cardList.get(position));
    	
    	// Load the image for the ImageView
		switch (classList.get(position)) {
		case 0:
			vh.iv.setImageResource(R.drawable.druid_square);
			break;
		case 1:
			vh.iv.setImageResource(R.drawable.hunter_square);
			break;
		case 2:
			vh.iv.setImageResource(R.drawable.mage_square);
			break;
		case 3:
			vh.iv.setImageResource(R.drawable.paladin_square);
			break;
		case 4:
			vh.iv.setImageResource(R.drawable.priest_square);
			break;
		case 5:
			vh.iv.setImageResource(R.drawable.rogue_square);
			break;
		case 6:
			vh.iv.setImageResource(R.drawable.shaman_square);
			break;
		case 7:
			vh.iv.setImageResource(R.drawable.warlock_square);
			break;
		case 8:
			vh.iv.setImageResource(R.drawable.warrior_square);
			break;

		}
    	return convertView;
    	
    }

}