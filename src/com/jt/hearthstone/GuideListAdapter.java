package com.jt.hearthstone;

import java.util.List;

import android.R.integer;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static butterknife.Views.findById;

public class GuideListAdapter extends BaseAdapter {
	private Context mContext;
	private int count;
	private List<String> deckNames;
	private List<Classes> classes;

	public GuideListAdapter(Context c, int count, List<String> deckNames, List<Classes> classes) {
		mContext = c;
		this.count = count;
		this.deckNames = deckNames;
		this.classes = classes;
		
	}

	public int getCount() {
		return count;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// Custom ViewHolder class to make scrolling smoother
	static class ViewHolder {
		TextView tvDeckName = null;
		ImageView ivClass = null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		Typeface font = TypefaceCache.get(mContext.getAssets(),
				"fonts/belwebd.ttf");

		// If our view (in this case, one item from the gridview) is null, then
		// inflate it.
		// We do this because re-using views makes memory happy :)
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.deck_guide_list_view,
					null);
			vh = new ViewHolder();
			vh.tvDeckName = findById(convertView, R.id.tvDeckName);
			vh.ivClass = findById(convertView, R.id.ivClassImage);
			
			convertView.setTag(vh);

		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		vh.tvDeckName.setTypeface(font);
		
		vh.tvDeckName.setText(deckNames.get(position));
		
		switch (classes.get(position)) {
		case DRUID:
			vh.ivClass.setImageResource(R.drawable.druid);
			break;
		case HUNTER:
			vh.ivClass.setImageResource(R.drawable.hunter);
			break;
		case MAGE:
			vh.ivClass.setImageResource(R.drawable.mage);
			break;
		case PALADIN:
			vh.ivClass.setImageResource(R.drawable.paladin);
			break;
		case PRIEST:
			vh.ivClass.setImageResource(R.drawable.priest);
			break;
		case ROGUE:
			vh.ivClass.setImageResource(R.drawable.rogue);
			break;
		case SHAMAN:
			vh.ivClass.setImageResource(R.drawable.shaman);
			break;
		case WARLOCK:
			vh.ivClass.setImageResource(R.drawable.warlock);
			break;
		case WARRIOR:
			vh.ivClass.setImageResource(R.drawable.warrior);
			break;
			
		}

		return convertView;

	}
}
