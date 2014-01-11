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

public class DeckGuideAdapter extends BaseAdapter {
	private Context mContext;
	private int count;
	private List<String> deckNames;

	public DeckGuideAdapter(Context c, int count, List<String> deckNames) {
		mContext = c;
		this.count = count;
		this.deckNames = deckNames;
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

		return convertView;

	}
}
