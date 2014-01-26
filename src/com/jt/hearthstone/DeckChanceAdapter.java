package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DeckChanceAdapter extends BaseAdapter {
	private Context mContext;
	private List<Double> listPercents;
	private Map<Cards, Integer> cardCounts = new HashMap<Cards, Integer>();
	private List<Cards> cardListUnique;

	public DeckChanceAdapter(Context c, List<Cards> cardList,
			List<Double> listPercents) {
		mContext = c;
		this.listPercents = listPercents;

		cardListUnique = new ArrayList<Cards>(
				new LinkedHashSet<Cards>(cardList));

		cardCounts.clear();
		for (Cards card : cardList) {
			Integer current = cardCounts.get(card);
			if (current == null) {
				current = 1;
			} else {
				current++;
			}
			cardCounts.put(card, current);
		}

		Collections.sort(cardList, new CardComparator(2, false));
	}

	public int getCount() {
		return cardListUnique.size();
	}

	public void update(List<Cards> cardList, List<Double> percents) {

		cardListUnique = new ArrayList<Cards>(
				new LinkedHashSet<Cards>(cardList));

		cardCounts.clear();
		for (Cards card : cardList) {
			Integer current = cardCounts.get(card);
			if (current == null) {
				current = 1;
			} else {
				current++;
			}
			cardCounts.put(card, current);
		}
		
		listPercents = new ArrayList<Double>();
		listPercents.addAll(percents);

		Collections.sort(cardListUnique, new CardComparator(2, false));
		this.notifyDataSetChanged();
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
		TextView tvPercent = null;
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
					R.layout.deck_chance_list_view, null);
			vh = new ViewHolder();
			vh.tvDeckName = findById(convertView, R.id.tvDeckName);
			vh.tvNumCards = findById(convertView, R.id.tvNumCards);
			vh.tvPercent = findById(convertView, R.id.tvPercent);
			vh.ivClass = findById(convertView, R.id.ivClassImage);

			convertView.setTag(vh);

		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		String numTimes = cardCounts.get(cardListUnique.get(position)) + "x";

		vh.tvDeckName.setTypeface(font);
		vh.tvNumCards.setTypeface(font);
		vh.tvPercent.setTypeface(font);

		vh.tvDeckName.setText(cardListUnique.get(position).getName());
		vh.tvPercent.setText(String.format("%.2f", listPercents.get(position)) + "%");
		vh.tvNumCards.setText(numTimes);
		
		double percent = listPercents.get(position);
		
		if (percent < 4.00f) {
			vh.tvPercent.setTextColor(Color.RED);
		} else if (percent >= 4.00f && percent < 15.0f) {
			vh.tvPercent.setTextColor(Color.YELLOW);
		} else {
			vh.tvPercent.setTextColor(Color.GREEN);
		}

		switch (cardListUnique.get(position).getQuality().intValue()) {
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

		if (cardListUnique.get(position).getClasss() != null) {
			switch (cardListUnique.get(position).getClasss().intValue()) {
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
