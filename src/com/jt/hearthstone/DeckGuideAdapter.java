package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DeckGuideAdapter extends BaseAdapter {
	private Context mContext;
	private int count;
	private List<Cards> cardList;
	private SparseArray<String> soSparse;

	public DeckGuideAdapter(Context c, int count, List<Cards> cardList,
			SparseArray<String> soSparse) {
		mContext = c;
		this.count = count;
		this.cardList = cardList;
		this.soSparse = soSparse;
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
		TextView tvNumCards = null;
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
			convertView = View.inflate(mContext,
					R.layout.guide_detail_list_view, null);
			vh = new ViewHolder();
			vh.tvDeckName = findById(convertView, R.id.tvDeckName);
			vh.tvNumCards = findById(convertView, R.id.tvNumCards);
			vh.ivClass = findById(convertView, R.id.ivClassImage);
			
			convertView.setTag(vh);

		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		String numTimes = new StringBuilder(soSparse.get(position)).reverse()
				.toString();
		numTimes = numTimes.replace(" ", "");

		vh.tvDeckName.setTypeface(font);
		vh.tvNumCards.setTypeface(font);

		vh.tvDeckName.setText(cardList.get(position).getName());
		vh.tvNumCards.setText(numTimes);

		switch (cardList.get(position).getQuality().intValue()) {
		case 0:
			int free = mContext.getResources().getColor(R.color.free);
			vh.tvDeckName.setTextColor(free);
			break;
		case 1:
			vh.tvDeckName.setTextColor(Color.WHITE);
			break;
		case 3:
			int rare = mContext.getResources().getColor(R.color.rare);
			vh.tvDeckName.setTextColor(rare);
			break;
		case 4:
			int epic = mContext.getResources().getColor(R.color.epic);
			vh.tvDeckName.setTextColor(epic);
			break;
		case 5:
			int legendary = mContext.getResources().getColor(R.color.legendary);
			vh.tvDeckName.setTextColor(legendary);
			break;
		default: // No rarity? This should only happen for some abilities.
			vh.tvDeckName.setTextColor(Color.GREEN);
			break;
		}

		if (cardList.get(position).getClasss() != null) {
			switch (cardList.get(position).getClasss().intValue()) {
			case 1:
				vh.ivClass.setImageResource(R.drawable.warrior);
				break;
			case 2:
				vh.ivClass.setImageResource(R.drawable.paladin);
				break;
			case 3:
				vh.ivClass.setImageResource(R.drawable.hunter);
				break;
			case 4:
				vh.ivClass.setImageResource(R.drawable.rogue);
				break;
			case 5:
				vh.ivClass.setImageResource(R.drawable.priest);
				break;
			case 7:
				vh.ivClass.setImageResource(R.drawable.shaman);
				break;
			case 8:
				vh.ivClass.setImageResource(R.drawable.mage);
				break;
			case 9:
				vh.ivClass.setImageResource(R.drawable.warlock);
				break;
			case 11:
				vh.ivClass.setImageResource(R.drawable.druid);
				break;
			default:
				vh.ivClass.setImageResource(R.drawable.ic_launcher);
				break;
			}
		} else {
			vh.ivClass.setImageResource(R.drawable.ic_launcher);
		}

		return convertView;

	}
}
