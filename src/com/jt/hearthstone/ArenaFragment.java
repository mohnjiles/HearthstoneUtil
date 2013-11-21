package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Main Fragment responsible for choosing random hero and cards for the Arena
 * simulation.
 * 
 * @author JT
 * 
 */
public class ArenaFragment extends Fragment {

	ImageView ivItem1;
	ImageView ivItem2;
	ImageView ivItem3;
	private ListView lvArena;
	TextView textView;
	private Cards cards[];
	private ArrayList<Cards> listChoices = new ArrayList<Cards>();
	ArrayList<Cards> listDeck = new ArrayList<Cards>();
	ImageLoader loader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private Classes selectedClass;
	CustomListAdapter adapter;
	int doOnce = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		/*
		 * Inflate layout from arena_fragment XML
		 */
		View v = inflater.inflate(R.layout.arena_fragment, container, false);

		ivItem1 = findById(v, R.id.ivItem1);
		ivItem2 = findById(v, R.id.ivItem2);
		ivItem3 = findById(v, R.id.ivItem3);
		textView = findById(v, R.id.textView1);

		/*
		 * Tells the parent activity to let this fragment create it's own
		 * options menu for the ActionBar (if present)
		 */
		setHasOptionsMenu(true);

		return v;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Initiate ImageLoader if it's not already initiated.
		if (loader.isInited() == false) {
			ImageLoaderConfiguration config = ImageLoaderConfiguration
					.createDefault(getActivity());
			loader.init(config);
		}

		// Show a "Loading..." (stub) image
		options = new DisplayImageOptions.Builder().showStubImage(
				R.drawable.cards).build();

		/*
		 * Set the ImageViews to listen to our custom OnClickListener (Handles
		 * adding cards to deck, etc.)
		 */
		ArenaClickListener aClickListener = new ArenaClickListener();
		ivItem1.setOnClickListener(aClickListener);
		ivItem2.setOnClickListener(aClickListener);
		ivItem3.setOnClickListener(aClickListener);

		// Look in our cache for the font, and set it to the TextView
		Typeface tf = TypefaceCache.get(getActivity().getAssets(),
				"fonts/belwebd.ttf");
		textView.setTypeface(tf);

		// Populate cards array with GSON
		getCards();

		// Start the arena!
		pickRandomHero();

		// 
		adapter = new CustomListAdapter(getActivity(), listDeck);

	}

	/**
	 * Custom <code>OnClickListener</code> for the three <code>ImageView</code>
	 * s. Handles adding selected cards to deck and updating Deck ListView in
	 * ArenaDeckFragment.
	 * 
	 * @author JT
	 * 
	 */
	public class ArenaClickListener implements View.OnClickListener {

		ArenaDeckFragment deckFrag = (ArenaDeckFragment) getActivity()
				.getSupportFragmentManager().findFragmentByTag(
						makeFragmentName(R.id.pager, 1));

		@Override
		public void onClick(View v) {

			// If the heroes never get shown? (for some reason?) try again.
			if (v.getTag() == null) {
				pickRandomHero();
			} else {
				lvArena = deckFrag.lvArena;
				if (doOnce == 0) { // Do once
					// Save the class the user has chosen
					selectedClass = (Classes) v.getTag();

					// Update text with current deck size
					textView.setText("Choose a card (" + listDeck.size()
							+ " / 30)");

					// To make sure this only happens once per run
					doOnce++;

				} else {
					// Pick the corresponding card based on ImageView selected
					if (listChoices != null && v.getId() == R.id.ivItem1) {
						listDeck.add(listChoices.get(0));
					} else if (listChoices != null && v.getId() == R.id.ivItem2) {
						listDeck.add(listChoices.get(1));
					} else if (listChoices != null && v.getId() == R.id.ivItem3) {
						listDeck.add(listChoices.get(2));
					}

					// Update TextView with current deck size
					deckFrag.tvDeckSize.setText(listDeck.size() + " / 30");
					// Update ListView adapter with new info
					lvArena.setAdapter(adapter);
				}
				// Up until we get to 30 cards, get the next 3 random cards
				if (listDeck.size() < 30) {
					pickRandomCards(selectedClass);
					textView.setText("Choose a card (" + listDeck.size()
							+ " / 30)");
				} else {
					// We hit 30/30 cards in the deck, hide the views!
					ivItem1.setVisibility(View.GONE);
					ivItem2.setVisibility(View.GONE);
					ivItem3.setVisibility(View.GONE);

					// Inform them the deck is complete
					textView.setText("Swipe left to see your finished deck!");
				}
			}

		}

	}

	/**
	 * Function that chooses 3 random heroes for the user to choose from and
	 * sets the ImageViews accordingly.
	 * 
	 * @author JT
	 */
	public void pickRandomHero() {
		ImageView[] ivs = { ivItem1, ivItem2, ivItem3 };

		for (int i = 0; i < 3; i++) {
			if (ivs[i].getVisibility() == View.GONE) {
				ivs[i].setVisibility(View.VISIBLE);
			}

			if (loader != null) {
				loader.cancelDisplayTask(ivs[i]);
			}

			int random = (int) (Math.random() * 9); // Pick a number 0-8

			/*
			 * Set ImageView & ImageView's tag to the right class based on
			 * randomness!
			 */
			switch (random) {
			case 0:
				ivs[i].setImageResource(R.drawable.valeera_sanguinar);
				ivs[i].setTag(Classes.ROGUE);
				break;
			case 1:
				ivs[i].setImageResource(R.drawable.anduin_wrynn);
				ivs[i].setTag(Classes.PRIEST);
				break;
			case 2:
				ivs[i].setImageResource(R.drawable.gul_dan);
				ivs[i].setTag(Classes.WARLOCK);
				break;
			case 3:
				ivs[i].setImageResource(R.drawable.garrosh_hellscream);
				ivs[i].setTag(Classes.WARRIOR);
				break;
			case 4:
				ivs[i].setImageResource(R.drawable.jaina_proudmoore);
				ivs[i].setTag(Classes.MAGE);
				break;
			case 5:
				ivs[i].setImageResource(R.drawable.thrall);
				ivs[i].setTag(Classes.SHAMAN);
				break;
			case 6:
				ivs[i].setImageResource(R.drawable.malfurion_stormrage);
				ivs[i].setTag(Classes.DRUID);
				break;
			case 7:
				ivs[i].setImageResource(R.drawable.uther_lightbringer);
				ivs[i].setTag(Classes.PALADIN);
				break;
			case 8:
				ivs[i].setImageResource(R.drawable.rexxar);
				ivs[i].setTag(Classes.HUNTER);
				break;
			}
		}

		if (ivs[0].getTag() == ivs[1].getTag()
				|| ivs[1].getTag() == ivs[2].getTag()
				|| ivs[2].getTag() == ivs[0].getTag()) {

			// Recursive call in case of duplicate hero choices
			pickRandomHero();
		}

	}

	/**
	 * Function used to determine rarity of the cards to display, and displaying
	 * said cards. <br>
	 * The chances based on rarity are as follows: <br>
	 * <ul>
	 * <li>Common: ~90%</li>
	 * <li>Rare: 10%</li>
	 * <li>Epic: 4%</li>
	 * <li>Legendary: ~1%</li>
	 * </ul>
	 * 
	 * @param className
	 *            The class of the class-specific cards to include in the random
	 *            card selection. Takes an <b>element</b> of <code>enum</code>
	 *            <i>Classes</i> (Example: Classes.WARRIOR)
	 */
	private void pickRandomCards(Classes className) {
		listChoices.clear();
		double random = Math.random();

		/* ************ LEGENDARY ************
		 * ~5% chance if either first or last choice of 3
		 * Otherwise, ~1% chance
		 */
		if (random >= 0.99
				|| ((listDeck.size() == 0 || listDeck.size() == 29) && random >= 0.95)) {
			for (Cards card : cards) {
				if (((card.getClasss() != null && card.getClasss().intValue() == className
						.getValue()) || card.getClasss() == null)
						&& card.getQuality().intValue() == 5) {
					listChoices.add(card);
				}
			}
			
			// Shuffle the list for maximum randomness
			Collections.shuffle(listChoices);
			
			// Then pick the first 3 cards, and load their images.
			loader.displayImage("http://54.224.222.135/"
					+ listChoices.get(0).getImage() + ".png", ivItem1, options);
			loader.displayImage("http://54.224.222.135/"
					+ listChoices.get(1).getImage() + ".png", ivItem2, options);
			loader.displayImage("http://54.224.222.135/"
					+ listChoices.get(2).getImage() + ".png", ivItem3, options);

		} 
		/* ************ EPIC ************
		 * ~10% chance if either first or last choice of 3.
		 * 
		 * Otherwise, ~4% chance
		 */
		else if (random >= 0.96
				|| ((listDeck.size() == 0 || listDeck.size() == 29) && random >= 0.90)) {
			for (Cards card : cards) {
				if (((card.getClasss() != null && card.getClasss().intValue() == className
						.getValue()) || card.getClasss() == null)
						&& card.getQuality().intValue() == 4) {
					listChoices.add(card);
				}
			}
			Collections.shuffle(listChoices);
			loader.displayImage("http://54.224.222.135/"
					+ listChoices.get(0).getImage() + ".png", ivItem1, options);
			loader.displayImage("http://54.224.222.135/"
					+ listChoices.get(1).getImage() + ".png", ivItem2, options);
			loader.displayImage("http://54.224.222.135/"
					+ listChoices.get(2).getImage() + ".png", ivItem3, options);
		} 
		/* ************ RARE ************
		 * ~100% chance if either first or last choice of 3 (unless a higher
		 * rarity is chosen).
		 * 
		 * Otherwise, ~10% chance
		 */
		else if (random >= 0.90
				|| ((listDeck.size() == 0 || listDeck.size() == 29) && random >= 0)) {
			for (Cards card : cards) {
				if (((card.getClasss() != null && card.getClasss().intValue() == className
						.getValue()) || card.getClasss() == null)
						&& card.getQuality().intValue() == 3) {
					listChoices.add(card);
				}
			}
			Collections.shuffle(listChoices);
			loader.displayImage("http://54.224.222.135/"
					+ listChoices.get(0).getImage() + ".png", ivItem1, options);
			loader.displayImage("http://54.224.222.135/"
					+ listChoices.get(1).getImage() + ".png", ivItem2, options);
			loader.displayImage("http://54.224.222.135/"
					+ listChoices.get(2).getImage() + ".png", ivItem3, options);
		} else {
			for (Cards card : cards) {
				if (((card.getClasss() != null && card.getClasss().intValue() == className
						.getValue()) || card.getClasss() == null)
						&& (card.getQuality().intValue() == 1 || card
								.getQuality().intValue() == 0)) {
					listChoices.add(card);
				}
			}
			Collections.shuffle(listChoices);
			loader.displayImage("http://54.224.222.135/"
					+ listChoices.get(0).getImage() + ".png", ivItem1, options);
			loader.displayImage("http://54.224.222.135/"
					+ listChoices.get(1).getImage() + ".png", ivItem2, options);
			loader.displayImage("http://54.224.222.135/"
					+ listChoices.get(2).getImage() + ".png", ivItem3, options);
		}

	}

	/**
	 * Function that takes no arguments; it merely parses the JSON into our POJO
	 */
	private void getCards() {
		Gson gson = new Gson();
		InputStream is = getResources().openRawResource(R.raw.cards);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// The json String from the file
		String jsonString = writer.toString();

		// Set our pojo from the GSON data
		cards = gson.fromJson(jsonString, Cards[].class);
	}

	private static String makeFragmentName(int viewId, int index) {
		return "android:switcher:" + viewId + ":" + index;
	}

}
