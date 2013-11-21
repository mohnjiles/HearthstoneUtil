package com.jt.hearthstone;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomDeckAdapter extends BaseAdapter {
	private Context mContext;
	private List<String> deckList;
	private List<Integer> cardClasses;

	public CustomDeckAdapter(Context c, ArrayList<String> list,
			List<Integer> classesList) {
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
		ImageView ivDeckBg;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderTwo vh;
		Typeface font = TypefaceCache.get(mContext.getAssets(), "fonts/belwebd.ttf");
		// If our view is null, then inflate it.
		// We do this because re-using views makes memory happy :)
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.deck_list_view, null);
			vh = new ViewHolderTwo();

			vh.tvDeckName = (TextView) convertView
					.findViewById(R.id.tvCardTitle);
			vh.ivClassImage = (ImageView) convertView
					.findViewById(R.id.ivClassImage);
			vh.ivDeckBg = (ImageView) convertView
					.findViewById(R.id.ivDeckBg);
			convertView.setTag(vh);

		} else {
			vh = (ViewHolderTwo) convertView.getTag();
		}

		vh.tvDeckName.setText(deckList.get(position));
		vh.tvDeckName.setTypeface(font);
		vh.ivDeckBg.setBackgroundResource(R.drawable.warrior_bg);
		
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

}
