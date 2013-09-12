package com.jt.hearthstone;

import java.util.ArrayList;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DecksListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> decksList = CardListActivity.deckList;
	public static String url;

    public DecksListAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
    	return decksList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
    
    // Custom ViewHolder class to make scrolling smoother
    static class ViewHolder {
    	ImageView ivClassIcon = null;
    	TextView tvCardName = null;
    	TextView tvManaCost = null;
    	TextView tvAttack = null;
    	TextView tvHealth = null;
    }
    

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder vh;

    	// If our view (in this case, one item from the gridview) is null, then inflate it.
    	// We do this because re-using views makes memory happy :)
    	if (convertView == null) {
    		convertView = View.inflate(mContext, R.layout.custom_list_view, null);
    		vh = new ViewHolder();

    		vh.tvCardName = (TextView) convertView.findViewById(R.id.tvCardTitle);
    		vh.ivClassIcon = (ImageView) convertView.findViewById(R.id.ivClassImage);
    		vh.tvAttack = (TextView) convertView.findViewById(R.id.tvAttack);
    		vh.tvHealth = (TextView) convertView.findViewById(R.id.tvHealth);
    		vh.tvManaCost = (TextView) convertView.findViewById(R.id.tvMana);
    		convertView.setTag(vh);	
    		vh.ivClassIcon.setImageResource(R.drawable.ic_launcher);
    		
    	} else {
    		vh = (ViewHolder)convertView.getTag();
    	}
    	
    	return convertView;
    	
    }

}
