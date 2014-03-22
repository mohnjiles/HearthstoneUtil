package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CardListFragment extends CustomCardFragment {

	Spinner spinnerMana;
	Spinner spinnerSort;
	Spinner spinnerMechanic;
	ListView listCards;
	GridView grid;
	CheckBox includeNeutralCards;
	CheckBox cbReverse;

	SearchView mSearchView;
	ImageAdapter adapter;
	CustomListAdapter adapter2;
	DeckActivity deckFrag;
	int pos = CustomOnItemSelectedListener.position;
	int deckListPos;
	boolean reverse = false;

	private TextView tvMechanic;
	private TextView tvSort;
	private TextView tvMana;
	private RelativeLayout rlPopup;
	MenuItem searchItem;
	private MenuItem listSwitcher;
	private SharedPreferences prefs;
	List<Integer> deckClasses = DeckSelector.deckClasses;
	private List<String> listDecks = DeckSelector.listDecks;
	List<Cards> cardsList;

	List<Cards> cardList;
	private ArrayList<String> deckList;
	Cards[] cards = Utils.cards;

	private boolean isGrid = true;
	private boolean isQuickEditMode = false;

	private Typeface font;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		super.setClassName("CardListFragment");

		font = TypefaceCache
				.get(getActivity().getAssets(), "fonts/belwebd.ttf");

		View V = inflater
				.inflate(R.layout.activity_card_list, container, false);

		// Get views with ButterKnife
		grid = findById(V, R.id.gvDeck);
		listCards = findById(V, R.id.cardsList);
		includeNeutralCards = findById(V, R.id.cbGenerics);
		cbReverse = findById(V, R.id.cbReverse);
		spinnerSort = findById(V, R.id.spinnerSort);
		spinnerMechanic = findById(V, R.id.spinnerMechanic);
		spinnerMana = findById(V, R.id.spinnerMana);
		rlPopup = findById(V, R.id.rlPopup);
		tvMechanic = findById(V, R.id.tvCost);
		tvSort = findById(V, R.id.textView2);
		tvMana = findById(V, R.id.tvMana);

		tvMechanic.setTypeface(font);
		tvSort.setTypeface(font);
		tvMana.setTypeface(font);
		cbReverse.setTypeface(font);
		includeNeutralCards.setTypeface(font);

		registerForContextMenu(listCards);
		registerForContextMenu(grid);

		deckFrag = (DeckActivity) getActivity().getSupportFragmentManager()
				.findFragmentByTag(Utils.makeFragmentName(R.id.pager, 1));

		setHasOptionsMenu(true);

		return V;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Check to see if it's users first time
		// If so, we show the overlay layout
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		isQuickEditMode = prefs.getBoolean("isQuickEditMode", false);

		boolean firstTime = prefs.getBoolean("first_time", true);
		if (firstTime) {
			rlPopup.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					prefs.edit().putBoolean("first_time", false).commit();
					rlPopup.setVisibility(View.GONE);
				}
			});

		} else {
			rlPopup.setVisibility(View.GONE);
		}

		if (savedInstanceState != null) {
			this.isGrid = savedInstanceState.getBoolean("isGrid");
		}
		if (isGrid) {
			listCards.setVisibility(View.INVISIBLE);
			grid.setVisibility(View.VISIBLE);
		} else {
			grid.setVisibility(View.INVISIBLE);
			listCards.setVisibility(View.VISIBLE);
		}

		Intent intent = getActivity().getIntent();
		deckListPos = intent.getIntExtra("position", 0);

		// Get our JSON for GSON from the cards.json file in our "raw" directory
		// and use it to set up the list of cards
		setupCardList();

		// Get deck list from file
		getDeckList();

		new DeckUtils.GetCardsList(getActivity(), this, 999).execute(listDecks
				.get(intent.getIntExtra("position", 0)));

		MyWindow.setContext(getActivity());

		/************ Listeners for PopupWindow ***************/
		grid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				isQuickEditMode = prefs.getBoolean("isQuickEditMode", false);
				if (isQuickEditMode) {
					addCards(position);
					ObjectAnimator anim = ObjectAnimator.ofFloat(v, "alpha",
							0.5f, 1.0f);
					anim.setDuration(500).start();
				} else {
					MyWindow.setCardList(cardList);
					MyWindow.initiatePopupWindow(position, parent);
				}
			}
		});
		listCards.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				isQuickEditMode = prefs.getBoolean("isQuickEditMode", false);
				if (isQuickEditMode) {
					addCards(position);
					ObjectAnimator anim = ObjectAnimator.ofFloat(v, "alpha",
							0.5f, 1.0f);
					anim.setDuration(500).start();
				} else {
					MyWindow.setCardList(cardList);
					MyWindow.initiatePopupWindow(position, parent);
				}
			}
		});

		// Custom listener for CheckBoxes
		CustomOnCheckedChangeListener checkListener = new CustomOnCheckedChangeListener(
				getActivity());

		includeNeutralCards.setOnCheckedChangeListener(checkListener);
		cbReverse.setOnCheckedChangeListener(checkListener);

		// Spinner setup (set items/adapters/etc)
		deckClasses = (List<Integer>) DeckUtils.getIntegerDeck(getActivity(),
				"deckclasses");
		String[] mechanicNames = getResources()
				.getStringArray(R.array.Mechanic);
		String[] sortNames = getResources().getStringArray(R.array.Sort);
		String[] sortMana = getResources().getStringArray(R.array.ManaCost);
		
		CustomArrayAdapter spinAdapter = new CustomArrayAdapter(getActivity(),
				R.layout.spinner_row, R.id.name, sortNames);
		spinAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row);

		CustomArrayAdapter spinSortAdapter = new CustomArrayAdapter(
				getActivity(), R.layout.spinner_row, R.id.name, mechanicNames);
		spinSortAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row);
		
		CustomArrayAdapter spinManaAdapter = new CustomArrayAdapter(
				getActivity(), R.layout.spinner_row, R.id.name, sortMana);
		spinSortAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row);

		spinnerSort.setAdapter(spinAdapter);
		spinnerMechanic.setAdapter(spinSortAdapter);
		spinnerMana.setAdapter(spinManaAdapter);

		CustomOnItemSelectedListener listener = new CustomOnItemSelectedListener(
				getActivity());

		spinnerSort.setOnItemSelectedListener(listener);
		spinnerMechanic.setOnItemSelectedListener(listener);
		spinnerMana.setOnItemSelectedListener(listener);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isGrid", isGrid);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.card_list, menu);
		searchItem = menu.findItem(R.id.action_search);
		listSwitcher = menu.findItem(R.id.action_switch);
		mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		mSearchView.setOnQueryTextListener(new CustomSearchListener(
				getActivity()));

		if (isGrid) {
			listSwitcher.setTitle("Switch to list view");
			listSwitcher.setIcon(R.drawable.collections_view_as_list);
		} else {
			listSwitcher.setTitle("Switch to grid view");
			listSwitcher.setIcon(R.drawable.collections_view_as_grid);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			// When Settings button is clicked, start Settings Activity
			startActivity(new Intent(getActivity(), SettingsActivity.class));
			break;

		case R.id.action_rename:
			new DeckUtils.GetCardsList(getActivity(), this, R.id.action_rename)
					.execute(deckList.get(deckListPos));
			break;
		case R.id.action_switch:
			if (isGrid) {
				grid.setVisibility(View.INVISIBLE);
				listCards.setVisibility(View.VISIBLE);
				item.setTitle("Switch to grid view");
				item.setIcon(R.drawable.collections_view_as_grid);
				isGrid = false;
				break;
			} else {
				grid.setVisibility(View.VISIBLE);
				listCards.setVisibility(View.INVISIBLE);
				item.setTitle("Switch to list view");
				item.setIcon(R.drawable.collections_view_as_list);
				isGrid = true;
				break;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		isQuickEditMode = prefs.getBoolean("isQuickEditMode", false);

		if (isQuickEditMode) {
			if (v.getId() == R.id.cardsList || v.getId() == R.id.gvDeck) {
				menu.setHeaderTitle(cardList.get(info.position).getName());
				menu.add(1337, 0, 0, "Show details");
			}
		} else {

			menu.setHeaderTitle(cardList.get(info.position).getName());
			menu.add(1337, 0, 0, "Add to deck");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		isQuickEditMode = prefs.getBoolean("isQuickEditMode", false);
		if (isQuickEditMode) {
			if (item.getGroupId() == 1337) {
				MyWindow.setCardList(cardList);
				MyWindow.initiatePopupWindow(info.position, info.targetView);
			}
		} else {
			if (item.getGroupId() == 1337) {
				addCards(info.position);
			}
		}
		return super.onContextItemSelected(item);
	}

	private void addCards(int position) {

		DeckChanceFragment deckChanceFragment = (DeckChanceFragment) getActivity()
				.getSupportFragmentManager().findFragmentByTag(
						Utils.makeFragmentName(R.id.pager, 3));
		DeckActivity deckActivity = (DeckActivity) getActivity()
				.getSupportFragmentManager().findFragmentByTag(
						Utils.makeFragmentName(R.id.pager, 1));

		Crouton.cancelAllCroutons();

		if (cardsList.size() < 30) {
			cardsList.add(cardList.get(position));

			Crouton.makeText(getActivity(),
					"Card added: " + cardList.get(position).getName(),
					Style.INFO).show();
		} else {
			Crouton.makeText(getActivity(),
					"Cannot have more than 30 cards in the deck", Style.ALERT)
					.show();
		}

		deckActivity.setManaChart(cardsList);
		deckActivity.setPieGraph(cardsList);

		new DeckUtils.SaveDeck(getActivity(), deckList.get(deckListPos),
				cardsList).execute();

		// Get text sizes in sp
		int sp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				12, getActivity().getResources().getDisplayMetrics());
		int bigSp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				16, getActivity().getResources().getDisplayMetrics());

		if (cardsList != null) {
			if (cardsList.size() == 0) {
				deckFrag.tvNumCards.setTextSize(bigSp);
				deckFrag.tvNumCards
						.setText("Looks like there's nothing here. Swipe right to get started!");
				deckFrag.ivSwipe.setVisibility(View.VISIBLE);
			} else {
				deckFrag.tvNumCards.setTextSize(sp);
				deckFrag.tvNumCards.setText("" + cardsList.size() + " / 30");
				deckFrag.ivSwipe.setVisibility(View.GONE);
			}
		}

		if (cardsList != null) {
			Collections.sort(cardsList, new CardComparator(2, false));
		}

		deckFrag.adapter.update(cardsList);
		deckFrag.adapter2.update(cardsList);
		deckFrag.cardList.clear();
		deckFrag.cardList.addAll(cardsList);
		deckFrag.refreshDecks();

		
		deckChanceFragment.deckList.clear();
		deckChanceFragment.deckList.addAll(cardsList);
		deckChanceFragment.updatePercents(cardsList, true);

	}

	private void getDeckList() {
		InputStream instream = null;

		try {
			instream = getActivity().openFileInput("decklist");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (instream != null) {
				ObjectInputStream objStream = new ObjectInputStream(instream);
				try {
					deckList = (ArrayList<String>) objStream.readObject();
					if (instream != null) {
						instream.close();
					}
					if (objStream != null) {
						objStream.close();
					}

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setupCardList() {
		if (cardList == null) {
			cardList = new ArrayList<Cards>();
			for (Cards card : cards) {
				switch (deckClasses.get(deckListPos)) {
				case 0:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.DRUID
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 1:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.HUNTER
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 2:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.MAGE
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 3:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.PALADIN
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 4:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.PRIEST
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 5:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.ROGUE
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 6:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.SHAMAN
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 7:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.WARLOCK
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 8:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.WARRIOR
									.getValue()) {
						cardList.add(card);
					}
					break;
				}
			}
		} else {
			cardList.clear();
			for (Cards card : cards) {
				switch (deckClasses.get(deckListPos)) {
				case 0:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.DRUID
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 1:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.HUNTER
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 2:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.MAGE
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 3:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.PALADIN
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 4:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.PRIEST
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 5:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.ROGUE
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 6:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.SHAMAN
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 7:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.WARLOCK
									.getValue()) {
						cardList.add(card);
					}
					break;
				case 8:
					if (card.getClasss() != null
							&& card.getClasss().intValue() == Classes.WARRIOR
									.getValue()) {
						cardList.add(card);
					}
					break;
				}
			}
		}
		adapter = new ImageAdapter(getActivity(), cardList);
		adapter2 = new CustomListAdapter(getActivity(), cardList);
		Collections.sort(cardList, new CardComparator(pos, reverse));

		// Set the gridview's adapter to our custom adapter
		grid.setAdapter(adapter);
		listCards.setAdapter(adapter2);
	}

	@Override
	protected void setCardList(List<Cards> cardList, int tag) {
		Log.w("setCardList", "setCardList");
		if (this.cardsList != null) {
			this.cardsList.clear();
		} else {
			this.cardsList = new ArrayList<Cards>();
		}
		this.cardsList.addAll(cardList);

		switch (tag) {
		case R.id.action_rename:
			DeckUtils.renameDeck(getActivity(), deckListPos, getActivity(),
					cardList);
			break;
		default:
			break;
		}

	}

}
