package com.jt.hearthstone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Cards> cardList; // Get card list from Fragment 
	private String cardName;
	private ImageLoader loader = ImageLoader.getInstance();

	public CustomListAdapter(Context c, ArrayList<Cards> cardList) {
		mContext = c;
		this.cardList = cardList;
	
		if (!loader.isInited()) {
			loader.init(new ImageLoaderConfiguration.Builder(mContext).build());
		}
	}

	public int getCount() {
		return cardList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// Custom ViewHolder class to make scrolling smoother
	private static class ViewHolder {
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
		ViewHolder vh;
		String attack = null;
		String health = null;
		String mana = null;
		Typeface font = TypefaceCache.get(mContext.getAssets(),
				"fonts/belwebd.ttf");


		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.custom_list_view,
					null);
			vh = new ViewHolder();

			vh.tvCardName = (TextView) convertView
					.findViewById(R.id.tvCardTitle);
			vh.ivClassIcon = (ImageView) convertView
					.findViewById(R.id.ivClassImage);
			vh.tvAttack = (TextView) convertView.findViewById(R.id.tvAttack);
			vh.tvHealth = (TextView) convertView.findViewById(R.id.tvHealth);
			vh.tvManaCost = (TextView) convertView.findViewById(R.id.tvMana);
			vh.ivBackground = (ImageView) convertView
					.findViewById(R.id.ivBackground);
			vh.tvNum = (TextView) convertView.findViewById(R.id.tvNumOfCard);
			convertView.setTag(vh);

		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		int quality = cardList.get(position).getQuality().intValue();

		String mDrawablename = "files_"
				+ cardList.get(position).getImage().toLowerCase() + "_rect";
		int resID = mContext.getResources().getIdentifier(mDrawablename,
				"drawable", mContext.getPackageName());

		vh.ivBackground.setImageBitmap(ImageCache.get(mContext, resID));

		vh.ivClassIcon.setImageResource(R.drawable.ic_launcher);

		vh.tvCardName.setShadowLayer(0.01f, 1, 0, Color.BLACK);
		vh.tvAttack.setShadowLayer(0.01f, 1, 1, Color.BLACK);
		vh.tvHealth.setShadowLayer(0.01f, 1, 1, Color.BLACK);
		vh.tvManaCost.setShadowLayer(0.01f, 1, 1, Color.BLACK);
		vh.tvCardName.setTypeface(font);
		vh.tvAttack.setTypeface(font);
		vh.tvHealth.setTypeface(font);
		vh.tvManaCost.setTypeface(font);
		vh.tvNum.setVisibility(View.INVISIBLE);

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
		cardName = cardList.get(position).getName();
		if (cardList.get(position).getAttack() != null) {
			attack = cardList.get(position).getAttack().toString();
		}
		// Set mana cost
		mana = cardList.get(position).getCost().toString();
		if (cardList.get(position).getHealth() != null) {
			health = cardList.get(position).getHealth().toString();
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
		if (cardList.get(position).getClasss() != null) {
			switch (cardList.get(position).getClasss().intValue()) {
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


		return convertView;

	}

}
