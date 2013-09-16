package com.jt.hearthstone;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomDeckAdapter extends BaseAdapter {
    private Context mContext;
	public static String url;
	private List<String> deckList;
	private List<Integer> cardClasses;

    public CustomDeckAdapter(Context c, ArrayList<String> list, List<Integer> classesList) {
        mContext = c;
        this.deckList = list;
        this.cardClasses = classesList;
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
    	ImageView ivClassImage;
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
    		vh.ivClassImage = (ImageView) convertView.findViewById(R.id.ivClassImage);
    		convertView.setTag(vh);	
    		
    	} else {
    		vh = (ViewHolderTwo)convertView.getTag();
    	}
    	
    	vh.tvDeckName.setText(deckList.get(position));
    	switch (cardClasses.get(position)) {
    	case 0:
    		vh.ivClassImage.setImageResource(R.drawable.druid);
    		break;
    	case 1:
    		vh.ivClassImage.setImageResource(R.drawable.hunter);
    		break;
    	case 2:
    		vh.ivClassImage.setImageResource(R.drawable.mage);
    		break;
    	case 3:
    		vh.ivClassImage.setImageResource(R.drawable.paladin);
    		break;
    	case 4:
    		vh.ivClassImage.setImageResource(R.drawable.priest);
    		break;
    	case 5:
    		vh.ivClassImage.setImageResource(R.drawable.rogue);
    		break;
    	case 6:
    		vh.ivClassImage.setImageResource(R.drawable.shaman);
    		break;
    	case 7:
    		vh.ivClassImage.setImageResource(R.drawable.warlock);
    		break;
    	case 8:
    		vh.ivClassImage.setImageResource(R.drawable.warrior);
    		break;
    		
    	}
    	
    	return convertView;
    	
    }
    
	private List<?> getDeck(String deckName) {
		InputStream instream = null;
		List<?> list = null;
		try {
			instream = mContext.openFileInput(deckName);
		} catch (FileNotFoundException e) {
			list = new ArrayList<Cards>();
			e.printStackTrace();
		}

		try {
			if (instream != null) {
				ObjectInputStream objStream = new ObjectInputStream(instream);
				try {
					list = (List<?>) objStream.readObject();
					if (instream != null) {
						instream.close();
					}
					if (objStream != null) {
						objStream.close();
					}

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

}
