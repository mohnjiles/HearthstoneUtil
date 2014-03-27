package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GuideListAdapter extends BaseAdapter implements Serializable {

	private static final long serialVersionUID = 1608733535153958604L;

	private Context mContext;
	private List<String> mDeckNames;
	private List<Classes> classes;
	private List<String> sparseRatings;

	public GuideListAdapter(Context c, List<String> deckNames,
			List<Classes> classes, List<String> sparseRatings) {
		mContext = c;
		mDeckNames = new ArrayList<String>(deckNames);
		this.classes = new ArrayList<Classes>(classes);
		this.sparseRatings = new ArrayList<String>(sparseRatings);

	}

	public int getCount() {
		return mDeckNames.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}
	
	public void update(List<String> deckNames,
			List<Classes> classes, List<String> sparseRatings) {
		Log.w("deckNames.size() before clear", (mDeckNames == deckNames) + "   mDeckNames.size() = " + mDeckNames.size() + "   deckNames.size() = " + deckNames.size() + "   getCount() = " + getCount());
		mDeckNames.clear();
		mDeckNames.addAll(deckNames);
		Log.w("deckNames.size() after clear", "mDeckNames.size() = " + mDeckNames.size() + "   deckNames.size() = " + deckNames.size() + "   getCount() = " + getCount());
		this.classes.clear();
		this.classes.addAll(classes);
		this.sparseRatings.clear();
		this.sparseRatings.addAll(sparseRatings);

		this.notifyDataSetChanged();
	}

	// Custom ViewHolder class to make scrolling smoother
	static class ViewHolder {
		TextView tvDeckName = null;
		TextView tvRating = null;
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
			vh.tvRating = findById(convertView, R.id.tvRating);

			convertView.setTag(vh);

		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		vh.tvDeckName.setTypeface(font);
		vh.tvRating.setTypeface(font);

		vh.tvDeckName.setText(mDeckNames.get(position));
		vh.tvRating.setText(sparseRatings.get(position));

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
