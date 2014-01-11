package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StreamCorruptedException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
	List<Cards> cardList;
	private ArrayList<String> deckList;
	Cards[] cards;

	private boolean isGrid = true;
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
		deckClasses = (List<Integer>) DeckUtils.getDeck(getActivity(),
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
		
		

		CustomOnItemSelectedListener listener = new CustomOnItemSelectedListener(
				getActivity());

		spinnerSort.setOnItemSelectedListener(listener);
		spinnerMechanic.setOnItemSelectedListener(listener);

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
			if (deckOne == null) {
				deckOne = (List<Cards>) DeckUtils.getDeck(getActivity(), deckList.get(deckListPos));
			}
			DeckUtils.renameDeck(getActivity(), deckListPos, getActivity(),
					deckOne);
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

		if (DeckUtils.getDeck(getActivity(), deckList.get(menuItemIndex)) != null) {
			list = (List<Cards>) DeckUtils.getDeck(getActivity(),
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

		DeckActivity.setManaChart(list);
		DeckActivity.setPieGraph(list);

		DeckUtils.saveDeck(getActivity(), deckList.get(menuItemIndex), list);
		doSomeStuff(
				(List<Cards>) DeckUtils.getDeck(getActivity(),
						deckList.get(menuItemIndex)),
				deckList.get(menuItemIndex));
		deckFrag.adapter = new DeckListAdapter(getActivity(), list);
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

		FileInputStream fis = null;
		try {
			fis = getActivity().openFileInput("cards.json");
		} catch (FileNotFoundException e1) {
			copyFile("cards.json");
			try {
				fis = getActivity().openFileInput("cards.json");
			} catch (FileNotFoundException e) {
				Log.wtf("How is this possible?", "cards.json broke");
				e.printStackTrace();
			}
			e1.printStackTrace();
		}
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(fis,
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
				fis.close();
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

	private void copyFile(String filename) {
		AssetManager assetManager = getActivity().getAssets();

		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(filename);
			String newFileName = getActivity().getFilesDir().getPath() + "/"
					+ filename;
			out = new FileOutputStream(newFileName);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
		}

	}

	public void doSomeStuff(List<Cards> result, String deckName) {
		int sp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				10, getActivity().getResources().getDisplayMetrics());
		int bigSp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				14, getActivity().getResources().getDisplayMetrics());

		int screenSize = getActivity().getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		if (result == null) {
			result = (List<Cards>) DeckUtils.getDeck(getActivity(), deckName);
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
		Collections.sort(result, new CardComparator(2, false));
		deckFrag.adapter2 = new ImageAdapter(getActivity(), result);
		deckFrag.gvDeck.setAdapter(deckFrag.adapter2);
		deckFrag.adapter = new DeckListAdapter(getActivity(), result);
		deckFrag.lvDeck.setAdapter(deckFrag.adapter);
	}
}
