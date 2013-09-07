package com.jt.hearthstone;

import java.util.ArrayList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ClassAdapter extends BaseAdapter {
    private Context mContext;

    public ClassAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
    	return thumbIds.length;
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
    	String[] classes = mContext.getResources().getStringArray(R.array.ClassesWithoutAny);
    	 
    	// If our view (in this case, one item from the gridview) is null, then inflate it.
    	// We do this because re-using views makes memory happy :)
    	if (convertView == null) {
    		convertView = View.inflate(mContext, R.layout.class_grid_layout, null);
    		vh = new ViewHolder();

    		vh.tv = (TextView) convertView.findViewById(R.id.grid_item_text);
    		vh.iv = (ImageView) convertView.findViewById(R.id.grid_item_image);
    		convertView.setTag(vh);	
    		vh.iv.setImageResource(R.drawable.ic_launcher);
    		
    	} else {
    		vh = (ViewHolder)convertView.getTag();
    	}
    	vh.tv.setText(classes[position]);
    	vh.iv.setImageResource(thumbIds[position]);

    	return convertView;
    	
    }
    
    private static Integer thumbIds[] = {
    	R.drawable.malfurion_stormrage, R.drawable.rexxar,
    	R.drawable.jaina_proudmoore, R.drawable.uther_lightbringer,
    	R.drawable.anduin_wrynn, R.drawable.valeera_sanguinar,
    	R.drawable.thrall, R.drawable.gul_dan,
    	R.drawable.garrosh_hellscream
    };

}