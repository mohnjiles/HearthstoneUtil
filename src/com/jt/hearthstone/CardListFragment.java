package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.StreamCorruptedException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CardListFragment extends Fragment {

	Spinner spinner;
	Spinner spinnerSort;
	Spinner spinnerMechanic;
	ListView listCards;
	GridView grid;
	CheckBox includeNeutralCards;
	CheckBox cbReverse;

	SearchView mSearchView;
	ImageAdapter adapter;
	CustomListAdapter adapter2;
	ChartActivity chartFrag;
	DeckActivity deckFrag;
	int pos = CustomOnItemSelectedListener.position;
	int deckListPos;
	boolean reverse = false;

	private TextView tvMechanic;
	private TextView tvSort;
	private RelativeLayout rlPopup;
	MenuItem searchItem;
	private MenuItem listSwitcher;
	private SharedPreferences prefs;
	List<Integer> deckClasses = DeckSelector.deckClasses;
	private List<Cards> deckOne;

	private ImageLoader loader = ImageLoader.getInstance();
	ArrayList<Cards> cardList;
	private ArrayList<String> deckList;
	Cards[] cards;

	private boolean isGrid = false;
	private int position;

	private Typeface font;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		font = TypefaceCache
				.get(getActivity().getAssets(), "fonts/belwebd.ttf");

		View V = inflater
				.inflate(R.layout.activity_card_list, container, false);

		// Get views with ButterKnife
		grid = findById(V, R.id.gvDeck);
		listCards = findById(V, R.id.cardsList);
		includeNeutralCards = findById(V, R.id.cbGenerics);
		cbReverse = findById(V, R.id.cbReverse);
		spinner = findById(V, R.id.spinClass);
		spinnerSort = findById(V, R.id.spinnerSort);
		spinnerMechanic = findById(V, R.id.spinnerMechanic);
		rlPopup = findById(V, R.id.rlPopup);
		tvMechanic = findById(V, R.id.tvCost);
		tvSort = findById(V, R.id.textView2);

		tvMechanic.setTypeface(font);
		tvSort.setTypeface(font);
		cbReverse.setTypeface(font);
		includeNeutralCards.setTypeface(font);

		registerForContextMenu(listCards);
		registerForContextMenu(grid);

		chartFrag = (ChartActivity) getActivity().getSupportFragmentManager()
				.findFragmentByTag(Utils.makeFragmentName(R.id.pager, 2));
		deckFrag = (DeckActivity) getActivity().getSupportFragmentManager()
				.findFragmentByTag(Utils.makeFragmentName(R.id.pager, 1));

		int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		if (screenSize < Configuration.SCREENLAYOUT_SIZE_LARGE
				|| getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setHasOptionsMenu(true);
		}

		return V;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Check to see if it's users first time
		// If so, we show the overlay layout
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

		if (isGrid) {
			listCards.setVisibility(View.INVISIBLE);
			grid.setVisibility(View.VISIBLE);
		} else {
			grid.setVisibility(View.INVISIBLE);
			listCards.setVisibility(View.VISIBLE);
		}

		Intent intent = getActivity().getIntent();
		deckListPos = intent.getIntExtra("position", 0);

		// ImageLoader config for the ImageLoader that gets our card images
		// denyCacheImage blah blah does what it says. We use this because
		// I don't know. Maybe to save memory(RAM).

		// Initialize the ImageLoader
		if (!loader.isInited()) {
			ImageLoader.getInstance().init(Utils.config(getActivity()));
		}

		ImageLoader.getInstance().handleSlowNetwork(true);

		// Get our JSON for GSON from the cards.json file in our "raw" directory
		// and use it to set up the list of cards
		setupCardList();

		// Get deck list from file
		getDeckList();
		
		MyWindow.setContext(getActivity());

		/************ Listeners for PopupWindow ***************/
		grid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				MyWindow.initiatePopupWindow(cardList, position, parent);
			}
		});
		listCards.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				MyWindow.initiatePopupWindow(cardList, position, parent);
			}
		});

		// Custom listener for CheckBoxes
		CustomOnCheckedChangeListener checkListener = new CustomOnCheckedChangeListener(
				getActivity());

		includeNeutralCards.setOnCheckedChangeListener(checkListener);
		cbReverse.setOnCheckedChangeListener(checkListener);

		// Spinner setup (set items/adapters/etc)
		deckClasses = (List<Integer>) Utils.getDeck(getActivity(),
				"deckclasses");
		String[] mechanicNames = getResources()
				.getStringArray(R.array.Mechanic);
		String[] sortNames = getResources().getStringArray(R.array.Sort);
		CustomArrayAdapter spinAdapter = new CustomArrayAdapter(getActivity(),
				R.layout.spinner_row, R.id.name, sortNames);
		spinAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row);

		CustomArrayAdapter spinSortAdapter = new CustomArrayAdapter(
				getActivity(), R.layout.spinner_row, R.id.name, mechanicNames);
		spinSortAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row);

		spinnerSort.setAdapter(spinAdapter);
		spinnerMechanic.setAdapter(spinSortAdapter);

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

		// Need to do the listener here (afaik) to get SearchView data
		CustomOnItemSelectedListener listener = new CustomOnItemSelectedListener(
				getActivity());

		spinnerSort.setOnItemSelectedListener(listener);
		spinnerMechanic.setOnItemSelectedListener(listener);

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
			return true;
		case R.id.action_switch:
			if (isGrid) {
				grid.setVisibility(View.INVISIBLE);
				listCards.setVisibility(View.VISIBLE);
				item.setTitle("Switch to grid view");
				item.setIcon(R.drawable.collections_view_as_grid);
				isGrid = false;
				return true;
			} else {
				grid.setVisibility(View.VISIBLE);
				listCards.setVisibility(View.INVISIBLE);
				item.setTitle("Switch to list view");
				item.setIcon(R.drawable.collections_view_as_list);
				isGrid = true;
				return true;
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.cardsList || v.getId() == R.id.gvDeck) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(cardList.get(info.position).getName());
			position = info.position;
			menu.add(1337, 0, 0, "Add to deck \"" + deckList.get(deckListPos)
					+ "\"");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getGroupId() == 1337) {
			addCards(deckOne, deckListPos);
			return super.onContextItemSelected(item);
		}
		return super.onContextItemSelected(item);
	}

	private void addCards(List<Cards> list, int menuItemIndex) {
		if (Utils.getDeck(getActivity(), deckList.get(menuItemIndex)) != null) {
			list = (List<Cards>) Utils.getDeck(getActivity(),
					deckList.get(menuItemIndex));
		} else {
			list = new ArrayList<Cards>();
		}
		if (list.size() < 30) {
			list.add(cardList.get(position));
		} else {
			Crouton.makeText(getActivity(),
					"Cannot have more than 30 cards in the deck", Style.ALERT)
					.show();
		}

		chartFrag.mCurrentSeries.add(cardList.get(position).getCost()
				.intValue(), 1);
		chartFrag.layout.invalidate();
		Utils.saveDeck(getActivity(), deckList.get(menuItemIndex), list);
		doSomeStuff(
				(List<Cards>) Utils.getDeck(getActivity(),
						deckList.get(menuItemIndex)),
				deckList.get(menuItemIndex));

		if (chartFrag.mChart == null) {

		} else {
			((ViewGroup) chartFrag.mChart.getParent())
					.removeView(chartFrag.mChart);
			chartFrag.mCurrentSeries.clear();
			addSampleData(list);
			chartFrag.layout2.addView(chartFrag.mChart);
		}

		if (chartFrag.mPieChart == null) {

		} else {
			((ViewGroup) chartFrag.mPieChart.getParent())
					.removeView(chartFrag.mPieChart);
			chartFrag.mSeries.clear();
			chartFrag.mRenderer2.removeAllRenderers();
			addPieData(list);
			chartFrag.layout.addView(chartFrag.mPieChart);
		}

		deckFrag.adapter = new DeckListAdapter(getActivity(), list);
	}

	private void addSampleData(List<Cards> cardList) {
		int[] costs = new int[50];
		for (Cards card : cardList) {
			if (card.getCost() != null) {
				costs[card.getCost().intValue()]++;
				Log.i("cost", "" + costs[card.getCost().intValue()]);
				chartFrag.mCurrentSeries.add(card.getCost().intValue(),
						costs[card.getCost().intValue()]);
			}
		}
	}

	private void addPieData(List<Cards> cardList) {
		int minions = 0;
		int abilities = 0;
		int weapons = 0;
		int[] colors = { Color.rgb(0, 171, 249), Color.rgb(245, 84, 0),
				Color.rgb(60, 242, 0) };
		int[] colors2 = { Color.rgb(0, 108, 229), Color.rgb(225, 23, 3),
				Color.rgb(8, 196, 0) };
		for (Cards card : cardList) {
			if (card.getType() != null && card.getType().intValue() == 4) {
				minions++;
			} else if (card.getType() != null && card.getType().intValue() == 5) {
				abilities++;
			} else if (card.getType() != null && card.getType().intValue() == 7) {
				weapons++;
			}
		}
		if (abilities != 0) {
			chartFrag.mSeries.add("Spells", abilities);
			SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
			seriesRenderer.setDisplayChartValues(true);
			seriesRenderer.setGradientEnabled(true);
			seriesRenderer.setGradientStart(0, colors[0]);
			seriesRenderer.setGradientStop(20, colors2[0]);
			chartFrag.mRenderer2.addSeriesRenderer(seriesRenderer);
		}
		if (minions != 0) {
			chartFrag.mSeries.add("Minions", minions);
			SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
			seriesRenderer.setDisplayChartValues(true);
			seriesRenderer.setGradientEnabled(true);
			seriesRenderer.setGradientStart(0, colors[1]);
			seriesRenderer.setGradientStop(20, colors2[1]);
			chartFrag.mRenderer2.addSeriesRenderer(seriesRenderer);
		}
		if (weapons != 0) {
			chartFrag.mSeries.add("Weapons", weapons);
			SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
			seriesRenderer.setDisplayChartValues(true);
			seriesRenderer.setGradientEnabled(true);
			seriesRenderer.setGradientStart(0, colors[2]);
			seriesRenderer.setGradientStop(20, colors2[2]);
			chartFrag.mRenderer2.addSeriesRenderer(seriesRenderer);
		}

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
		// Load default card list
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

	public void doSomeStuff(List<Cards> result, String deckName) {
		int sp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				10, getActivity().getResources().getDisplayMetrics());
		int bigSp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				14, getActivity().getResources().getDisplayMetrics());

		int screenSize = getActivity().getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		if (result == null) {
			result = (List<Cards>) Utils.getDeck(getActivity(), deckName);
		}
		deckFrag.cardList = result;
		if (result.size() == 0
				&& screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			deckFrag.tvNumCards.setTextSize(bigSp);
			deckFrag.tvNumCards
					.setText("Looks like there's nothing here. Swipe right to get started!");
			deckFrag.ivSwipe.setVisibility(View.VISIBLE);
		} else if (result.size() == 0
				&& screenSize > Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			deckFrag.tvNumCards.setTextSize(bigSp);
			deckFrag.tvNumCards
					.setText("Looks like there's nothing here. Add cards from the left to get started!");
			deckFrag.ivSwipe.setVisibility(View.VISIBLE);
		} else {
			deckFrag.tvNumCards.setTextSize(sp);
			deckFrag.tvNumCards.setText("" + result.size() + " / 30");
			deckFrag.ivSwipe.setVisibility(View.GONE);
		}
		deckFrag.adapter2 = new ImageAdapter(getActivity(), result);
		deckFrag.gvDeck.setAdapter(deckFrag.adapter2);
		deckFrag.adapter = new DeckListAdapter(getActivity(), result);
		deckFrag.lvDeck.setAdapter(deckFrag.adapter);
	}

}
