package com.jt.hearthstone;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomDeckAdapter extends BaseAdapter {
    private Context mContext;
	public static String url;
	private List<String> deckList;

    public CustomDeckAdapter(Context c, ArrayList<String> list) {
        mContext = c;
        this.deckList = list;
    }
    
    
    public int getCount() {
    	return deckList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
    
    // Custom ViewHolder class to make scrolling smoother
    static class ViewHolderTwo {
    	TextView tvDeckName;
    }
    

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolderTwo vh;
    	// If our view is null, then inflate it.
    	// We do this because re-using views makes memory happy :)
    	if (convertView == null) {
    		convertView = View.inflate(mContext, R.layout.deck_list_view, null);
    		vh = new ViewHolderTwo();

    		vh.tvDeckName = (TextView) convertView.findViewById(R.id.tvCardTitle);
    		convertView.setTag(vh);	
    		
    	} else {
    		vh = (ViewHolderTwo)convertView.getTag();
    	}
    	
    	vh.tvDeckName.setText(deckList.get(position));
    	
    	return convertView;
    	
    }

}
