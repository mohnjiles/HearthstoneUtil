package com.jt.hearthstone;

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

public class DeckListAdapter extends BaseAdapter {
	private Context mContext;
	private String cardName;
	private List<Cards> cardListUnique;
	private Map<Cards, Integer> cardCounts = new HashMap<Cards, Integer>();

	public DeckListAdapter(Context c, List<Cards> cardList) {
		mContext = c;
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

			// Log.w("Test", "" + card.getName() + " / " +
			// cardCounts.get(card));

		}
		
		Collections.sort(cardListUnique, new CardComparator(2, false));

	}

	public int getCount() {
		return cardListUnique.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}
	
	public void update(List<Cards> cardList) {
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
		
		Collections.sort(cardListUnique, new CardComparator(2, false));
		this.notifyDataSetChanged();
	}

	// Custom ViewHolder class to make scrolling smoother
	static class ViewHolderTwo {
		ImageView ivClassIcon = null;
		ImageView ivBackground = null;
		TextView tvCardName = null;
		TextView tvManaCost = null;
		TextView tvAttack = null;
		TextView tvHealth = null;
		TextView tvNum = null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderTwo vh;
		String attack = null;
		String health = null;
		String mana = null;
		Typeface font = TypefaceCache.get(mContext.getAssets(),
				"fonts/belwebd.ttf");

		// If our view (in this case, one item from the gridview) is null, then
		// inflate it.
		// We do this because re-using views makes memory happy :)
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.custom_list_view,
					null);
			vh = new ViewHolderTwo();

			vh.tvCardName = (TextView) convertView
					.findViewById(R.id.tvCardTitle);
			vh.ivClassIcon = (ImageView) convertView
					.findViewById(R.id.ivClassImage);
			vh.tvAttack = (TextView) convertView.findViewById(R.id.tvAttack);
			vh.tvHealth = (TextView) convertView.findViewById(R.id.tvHealth);
			vh.tvManaCost = (TextView) convertView.findViewById(R.id.tvMana);
			vh.tvNum = (TextView) convertView.findViewById(R.id.tvNumOfCard);
			vh.ivBackground = (ImageView) convertView
					.findViewById(R.id.ivBackground);
			convertView.setTag(vh);
			vh.ivClassIcon.setImageResource(R.drawable.ic_launcher);

		} else {
			vh = (ViewHolderTwo) convertView.getTag();
		}
		if (cardListUnique.size() > 0) {
			String mDrawablename = "files_"
					+ cardListUnique.get(position).getImage().toLowerCase()
					+ "_rect";
			int resID = mContext.getResources().getIdentifier(mDrawablename,
					"drawable", mContext.getPackageName());
			vh.ivBackground.setImageResource(resID);
		}

		

		vh.tvAttack.setShadowLayer(0.01f, 1, 1, Color.BLACK);
		vh.tvHealth.setShadowLayer(0.01f, 1, 1, Color.BLACK);
		vh.tvManaCost.setShadowLayer(0.01f, 1, 1, Color.BLACK);
		vh.tvCardName.setShadowLayer(0.01f, 1, 1, Color.BLACK);
		vh.tvNum.setShadowLayer(0.01f, 1, 1, Color.BLACK);
		vh.tvCardName.setTypeface(font);
		vh.tvAttack.setTypeface(font);
		vh.tvHealth.setTypeface(font);
		vh.tvManaCost.setTypeface(font);
		vh.tvNum.setTypeface(font);

		int quality = cardListUnique.get(position).getQuality().intValue();

		// Set the color of the text based on quality
		switch (quality) {
		case 0:
			int free = mContext.getResources().getColor(R.color.free);
			vh.tvCardName.setTextColor(free);
			break;
		case 1:
			vh.tvCardName.setTextColor(Color.WHITE);
			break;
		case 3:
			int rare = mContext.getResources().getColor(R.color.rare);
			vh.tvCardName.setTextColor(rare);
			break;
		case 4:
			int epic = mContext.getResources().getColor(R.color.epic);
			vh.tvCardName.setTextColor(epic);
			break;
		case 5:
			int legendary = mContext.getResources().getColor(R.color.legendary);
			vh.tvCardName.setTextColor(legendary);
			break;
		default: // No rarity? This should only happen for some abilities.
			vh.tvCardName.setTextColor(Color.GREEN);
			break;
		}

		// Set card name
		cardName = cardListUnique.get(position).getName();
		if (cardListUnique.get(position).getAttack() != null) {
			attack = cardListUnique.get(position).getAttack().toString();
		}
		// Set mana cost
		mana = cardListUnique.get(position).getCost().toString();
		if (cardListUnique.get(position).getHealth() != null) {
			health = cardListUnique.get(position).getHealth().toString();
		}

		// Set the name of the card
		vh.tvCardName.setText(cardName);
		// Set the attack value
		if (attack != null) {
			vh.tvAttack.setText(attack);
		} else {
			vh.tvAttack.setText("0");
		}
		// Set the mana value
		if (mana != null) {
			vh.tvManaCost.setText(mana);
		} else {
			vh.tvManaCost.setText("0");
		}

		// Set the Health value
		if (health != null) {
			vh.tvHealth.setText(health);
		} else {
			vh.tvHealth.setText("0");
		}

		// Set the Class icon
		if (cardListUnique.get(position).getClasss() != null) {
			switch (cardListUnique.get(position).getClasss().intValue()) {
			case 1:
				vh.ivClassIcon.setImageResource(R.drawable.warrior);
				break;
			case 2:
				vh.ivClassIcon.setImageResource(R.drawable.paladin);
				break;
			case 3:
				vh.ivClassIcon.setImageResource(R.drawable.hunter);
				break;
			case 4:
				vh.ivClassIcon.setImageResource(R.drawable.rogue);
				break;
			case 5:
				vh.ivClassIcon.setImageResource(R.drawable.priest);
				break;
			case 7:
				vh.ivClassIcon.setImageResource(R.drawable.shaman);
				break;
			case 8:
				vh.ivClassIcon.setImageResource(R.drawable.mage);
				break;
			case 9:
				vh.ivClassIcon.setImageResource(R.drawable.warlock);
				break;
			case 11:
				vh.ivClassIcon.setImageResource(R.drawable.druid);
				break;
			default:
				vh.ivClassIcon.setImageResource(R.drawable.ic_launcher);
				break;
			}
		} else {
			vh.ivClassIcon.setImageResource(R.drawable.ic_launcher);
		}

		if (cardCounts.get(cardListUnique.get(position)) > 1) {
			vh.tvNum.setVisibility(View.VISIBLE);
			vh.tvNum.setText("" + cardCounts.get(cardListUnique.get(position))
					+ "x");
		} else {
			vh.tvNum.setVisibility(View.INVISIBLE);
		}

		return convertView;

	}
}
