package com.jt.hearthstone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;

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
	public List<Cards> listChoices = new ArrayList<Cards>();
	List<Cards> listDeck = new ArrayList<Cards>();
	ImageLoader loader = ImageLoader.getInstance();
	private Classes selectedClass;
	DeckListAdapter adapter;
	ArenaDeckFragment deckFrag;
	ActionBar aBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		/*
		 * Inflate layout from arena_fragment XML
		 */
		View v = inflater.inflate(R.layout.arena_fragment, container, false);

		ivItem1 = (ImageView) v.findViewById(R.id.ivItem1);
		ivItem2 = (ImageView) v.findViewById(R.id.ivItem2);
		ivItem3 = (ImageView) v.findViewById(R.id.ivItem3);
		textView = (TextView) v.findViewById(R.id.tvSomeText);

		/*
		 * Tells the parent activity to let this fragment create it's own
		 * options menu for the ActionBar (if present)
		 */
		setHasOptionsMenu(true);

		return v;

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		if (listDeck == null) {
			listDeck = new ArrayList<Cards>();
		}

		if (ivItem1.getTag() != null && ivItem2.getTag() != null
				&& ivItem3.getTag() != null) {
			outState.putSerializable("tag_0", (Classes) ivItem1.getTag());
			outState.putSerializable("tag_1", (Classes) ivItem2.getTag());
			outState.putSerializable("tag_2", (Classes) ivItem3.getTag());
		}

		if (selectedClass != null) {
			outState.putSerializable("selectedClass", selectedClass);
		}

		if (listChoices.size() > 0) {
			outState.putSerializable("card_0", listChoices.get(0));
			outState.putSerializable("card_1", listChoices.get(1));
			outState.putSerializable("card_2", listChoices.get(2));
		}

		if (listDeck.size() > 0) {
			outState.putSerializable("listDeck", (ArrayList<Cards>) listDeck);
		}

		getActivity().getSupportFragmentManager().putFragment(outState,
				ArenaDeckFragment.class.getName(), deckFrag);

		super.onSaveInstanceState(outState);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		deckFrag = (ArenaDeckFragment) getActivity()
				.getSupportFragmentManager().findFragmentByTag(
						Utils.makeFragmentName(R.id.pager, 1));

		// Initiate ImageLoader if it's not already initiated.
		if (loader.isInited() == false) {

			loader.init(Utils.config(getActivity()));
		}
		ImageLoader.getInstance().handleSlowNetwork(true);

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
		cards = Utils.setupCardList();

		if (savedInstanceState != null) {

			Classes tag0 = (Classes) savedInstanceState
					.getSerializable("tag_0");
			Classes tag1 = (Classes) savedInstanceState
					.getSerializable("tag_1");
			Classes tag2 = (Classes) savedInstanceState
					.getSerializable("tag_2");

			Classes selectedClass = (Classes) savedInstanceState
					.getSerializable("selectedClass");

			Cards card0 = (Cards) savedInstanceState.getSerializable("card_0");
			Cards card1 = (Cards) savedInstanceState.getSerializable("card_1");
			Cards card2 = (Cards) savedInstanceState.getSerializable("card_2");

			listDeck = (List<Cards>) savedInstanceState
					.getSerializable("listDeck");

			if (listDeck != null && listDeck.size() >= 30) {
				ivItem1.setVisibility(View.GONE);
				ivItem2.setVisibility(View.GONE);
				ivItem3.setVisibility(View.GONE);
				textView.setText("Swipe left to see your finished deck!");
				return;
			}

			if (tag0 == null) {
				tag0 = selectedClass;
			}
			if (tag1 == null) {
				tag1 = selectedClass;
			}
			if (tag2 == null) {
				tag2 = selectedClass;
			}

			ivItem1.setTag(tag0);
			ivItem2.setTag(tag1);
			ivItem3.setTag(tag2);

			if (tag0.getValue() != tag1.getValue()) {
				restoreHeroState(ivItem1, tag0);
				restoreHeroState(ivItem2, tag1);
				restoreHeroState(ivItem3, tag2);
			} else {
				int resId_0 = Utils.getResIdByName(getActivity(), card0
						.getImage().toLowerCase(Utils.curLocale));
				int resId_1 = Utils.getResIdByName(getActivity(), card1
						.getImage().toLowerCase(Utils.curLocale));
				int resId_2 = Utils.getResIdByName(getActivity(), card2
						.getImage().toLowerCase(Utils.curLocale));

				loader.displayImage("drawable://" + resId_0, ivItem1,
						Utils.defaultOptions);
				loader.displayImage("drawable://" + resId_1, ivItem2,
						Utils.defaultOptions);
				loader.displayImage("drawable://" + resId_2, ivItem3,
						Utils.defaultOptions);

				listChoices.add(card0);
				listChoices.add(card1);
				listChoices.add(card2);

				if (listDeck == null) {
					textView.setText("Choose a card (0 / 30)");
				} else {
					textView.setText("Choose a card (" + listDeck.size()
							+ " / 30)");
				}
			}

			if (ivItem1.getTag() == null) {
				ivItem1.setTag(selectedClass);
			}
			if (ivItem2.getTag() == null) {
				ivItem2.setTag(selectedClass);
			}
			if (ivItem1.getTag() == null) {
				ivItem3.setTag(selectedClass);
			}

			if (selectedClass != null) {

				aBar = ((ActionBarActivity) getActivity())
						.getSupportActionBar();
				switch (selectedClass) {
				case DRUID:
					aBar.setIcon(R.drawable.druid);
					break;
				case HUNTER:
					aBar.setIcon(R.drawable.hunter);
					break;
				case MAGE:
					aBar.setIcon(R.drawable.mage);
					break;
				case PALADIN:
					aBar.setIcon(R.drawable.paladin);
					break;
				case PRIEST:
					aBar.setIcon(R.drawable.priest);
					break;
				case ROGUE:
					aBar.setIcon(R.drawable.rogue);
					break;
				case SHAMAN:
					aBar.setIcon(R.drawable.shaman);
					break;
				case WARLOCK:
					aBar.setIcon(R.drawable.warlock);
					break;
				case WARRIOR:
					aBar.setIcon(R.drawable.warrior);
					break;
				default:
					break;
				}
			}

		} else {
			// Start the arena!
			pickRandomHero();
		}

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
						Utils.makeFragmentName(R.id.pager, 1));

		@Override
		public void onClick(View v) {

			if (listDeck == null) {
				listDeck = new ArrayList<Cards>();

			}

			// If the heroes never get shown? (for some reason?) try again.
			lvArena = deckFrag.lvArena;
			if (lvArena == null) {
				lvArena = (ListView) getView().findViewById(R.id.lvDeck);
			}
			if (v.getTag() != null) {
				// Save the class the user has chosen
				selectedClass = (Classes) v.getTag();
				ivItem1.setTag(selectedClass);
				ivItem2.setTag(selectedClass);
				ivItem3.setTag(selectedClass);
				// Set class icon and text
				ActionBar aBar = ((ActionBarActivity) getActivity())
						.getSupportActionBar();
				switch (selectedClass) {
				case DRUID:
					aBar.setIcon(R.drawable.druid);
					break;
				case HUNTER:
					aBar.setIcon(R.drawable.hunter);
					break;
				case MAGE:
					aBar.setIcon(R.drawable.mage);
					break;
				case PALADIN:
					aBar.setIcon(R.drawable.paladin);
					break;
				case PRIEST:
					aBar.setIcon(R.drawable.priest);
					break;
				case ROGUE:
					aBar.setIcon(R.drawable.rogue);
					break;
				case SHAMAN:
					aBar.setIcon(R.drawable.shaman);
					break;
				case WARLOCK:
					aBar.setIcon(R.drawable.warlock);
					break;
				case WARRIOR:
					aBar.setIcon(R.drawable.warrior);
					break;
				default:
					break;
				}

				// Update text with current deck size
				textView.setText("Choose a card (" + listDeck.size() + " / 30)");
			}

			// Pick the corresponding card based on ImageView selected
			if (listChoices.size() > 0 && v.getId() == R.id.ivItem1) {
				listDeck.add(listChoices.get(0));
			} else if (listChoices.size() > 0 && v.getId() == R.id.ivItem2) {
				listDeck.add(listChoices.get(1));
			} else if (listChoices.size() > 0 && v.getId() == R.id.ivItem3) {
				listDeck.add(listChoices.get(2));
			}

			DeckUtils.saveDeck(getActivity(), "arenaDeck", listDeck);

			// Update TextView with current deck size
			deckFrag.tvDeckSize.setText(listDeck.size() + " / 30");
			// Update ListView adapter with new info
			Collections.sort(listDeck, new CardComparator(2, false));
			adapter = new DeckListAdapter(HearthstoneUtil.getAppContext(),
					listDeck);
			lvArena.setAdapter(adapter);
			deckFrag.gvDeck.setAdapter(new ImageAdapter(HearthstoneUtil
					.getAppContext(), listDeck));

			// Up until we get to 30 cards, get the next 3 random cards
			if (listDeck.size() < 30) {
				pickRandomCards(selectedClass);
				textView.setText("Choose a card (" + listDeck.size() + " / 30)");
			} else {
				// We hit 30/30 cards in the deck, hide the views!
				ivItem1.setVisibility(View.GONE);
				ivItem2.setVisibility(View.GONE);
				ivItem3.setVisibility(View.GONE);

				// Inform them the deck is complete
				textView.setText("Swipe left to see your finished deck!");
			}

			deckFrag.setManaChart(listDeck);
			deckFrag.setPieGraph(listDeck);

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
	 * <li>Common: ~92%</li>
	 * <li>Rare: 8%</li>
	 * <li>Epic: 3.5%</li>
	 * <li>Legendary: ~0.9%</li>
	 * </ul>
	 * 
	 * @param className
	 *            The class of the class-specific cards to include in the random
	 *            card selection. Takes an <b>element</b> of <code>enum</code>
	 *            <i>Classes</i> (Example: Classes.WARRIOR)
	 */
	private void pickRandomCards(Classes className) {
		listChoices.clear();
		loader.cancelDisplayTask(ivItem1);
		loader.cancelDisplayTask(ivItem2);
		loader.cancelDisplayTask(ivItem3);
		double random = Math.random();

		/*
		 * ************ LEGENDARY ************ ~3% chance if either first or
		 * last choice of 3. Otherwise, ~1% chance
		 */
		if (random >= 0.991
				|| ((listDeck.size() == 0 || listDeck.size() == 29) && random >= 0.97)) {
			for (Cards card : cards) {
				if (((card.getClasss() != null && card.getClasss().intValue() == className
						.getValue()) || card.getClasss() == null)
						&& card.getQuality().intValue() == 5) {
					listChoices.add(card);
				}
			}
		}
		/*
		 * ************ EPIC ************ ~10% chance if either first or last
		 * choice of 3.
		 * 
		 * Otherwise, ~4% chance
		 */
		else if (random >= 0.965
				|| ((listDeck.size() == 0 || listDeck.size() == 29) && random >= 0.90)) {
			for (Cards card : cards) {
				if (((card.getClasss() != null && card.getClasss().intValue() == className
						.getValue()) || card.getClasss() == null)
						&& card.getQuality().intValue() == 4) {
					listChoices.add(card);
				}
			}
		}
		/*
		 * ************ RARE ************ ~100% chance if either first or last
		 * choice of 3 (unless a higher rarity is chosen).
		 * 
		 * Otherwise, ~10% chance
		 */
		else if (random >= 0.92
				|| ((listDeck.size() == 0 || listDeck.size() == 29) && random >= 0)) {
			for (Cards card : cards) {
				if (((card.getClasss() != null && card.getClasss().intValue() == className
						.getValue()) || card.getClasss() == null)
						&& card.getQuality().intValue() == 3) {
					listChoices.add(card);
				}
			}
		} else {
			for (Cards card : cards) {
				if (((card.getClasss() != null && card.getClasss().intValue() == className
						.getValue()) || card.getClasss() == null)
						&& (card.getQuality().intValue() == 1 || card
								.getQuality().intValue() == 0)) {
					listChoices.add(card);
				}
			}
		}

		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(ivItem1, "scaleX", 1, 0.5f),
				ObjectAnimator.ofFloat(ivItem1, "scaleY", 1, 0.5f),
				ObjectAnimator.ofFloat(ivItem2, "scaleX", 1, 0.5f),
				ObjectAnimator.ofFloat(ivItem2, "scaleY", 1, 0.5f),
				ObjectAnimator.ofFloat(ivItem3, "scaleX", 1, 0.5f),
				ObjectAnimator.ofFloat(ivItem3, "scaleY", 1, 0.5f));

		set.setDuration(125).start();
		
		AnimatorSet otherSet = new AnimatorSet();
		otherSet.playTogether(ObjectAnimator.ofFloat(ivItem1, "scaleX", 0.5f, 1),
				ObjectAnimator.ofFloat(ivItem1, "scaleY", 0.5f, 1),
				ObjectAnimator.ofFloat(ivItem2, "scaleX", 0.5f, 1),
				ObjectAnimator.ofFloat(ivItem2, "scaleY", 0.5f, 1),
				ObjectAnimator.ofFloat(ivItem3, "scaleX", 0.5f, 1),
				ObjectAnimator.ofFloat(ivItem3, "scaleY", 0.5f, 1));
		otherSet.setDuration(125).start();

		Collections.shuffle(listChoices);

		ivItem1.setImageResource(getResId(0));
		ivItem2.setImageResource(getResId(1));
		ivItem3.setImageResource(getResId(2));

	}

	private void restoreHeroState(ImageView iv, Classes tag) {
		switch (tag) {
		case DRUID:
			iv.setImageResource(R.drawable.malfurion_stormrage);
			iv.setTag(Classes.DRUID);
			break;
		case HUNTER:
			iv.setImageResource(R.drawable.rexxar);
			iv.setTag(Classes.HUNTER);
			break;
		case MAGE:
			iv.setImageResource(R.drawable.jaina_proudmoore);
			iv.setTag(Classes.MAGE);
			break;
		case PALADIN:
			iv.setImageResource(R.drawable.uther_lightbringer);
			iv.setTag(Classes.PALADIN);
			break;
		case PRIEST:
			iv.setImageResource(R.drawable.anduin_wrynn);
			iv.setTag(Classes.PRIEST);
			break;
		case ROGUE:
			iv.setImageResource(R.drawable.valeera_sanguinar);
			iv.setTag(Classes.ROGUE);
			break;
		case SHAMAN:
			iv.setImageResource(R.drawable.thrall);
			iv.setTag(Classes.SHAMAN);
			break;
		case WARLOCK:
			iv.setImageResource(R.drawable.gul_dan);
			iv.setTag(Classes.WARLOCK);
			break;
		case WARRIOR:
			iv.setImageResource(R.drawable.garrosh_hellscream);
			iv.setTag(Classes.WARRIOR);
			break;
		default:
			break;
		}
	}

	private int getResId(int position) {
		return Utils.getResIdByName(getActivity(), listChoices.get(position)
				.getImage().toLowerCase(Utils.curLocale));
	}

}
