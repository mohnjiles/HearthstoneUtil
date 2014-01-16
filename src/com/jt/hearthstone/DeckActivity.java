package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DeckActivity extends Fragment {

	ListView lvDeck;
	GridView gvDeck;
	TextView tvNumCards;
	ImageView ivSwipe;

	ImageAdapter adapter;
	DeckListAdapter adapter2;

	List<Cards> cardList;
	List<Integer> deckClasses;

	List<Cards> cardListUnique;
	private List<String> listDecks = DeckSelector.listDecks;
	private ImageLoader loader = ImageLoader.getInstance();
	private static BarGraph manaChart;
	private static PieGraph pieGraph;

	private int position;
	private int pos;
	private boolean isGrid = true;
	private Typeface font;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate view
		View V = inflater.inflate(R.layout.activity_deck, container, false);

		manaChart = findById(V, R.id.manaChart);
		pieGraph = findById(V, R.id.pieGraph);

		ImageLoader.getInstance().init(Utils.config(getActivity()));

		ImageLoader.getInstance().handleSlowNetwork(true);

		// Get font from Cache
		font = TypefaceCache
				.get(getActivity().getAssets(), "fonts/belwebd.ttf");

		// Get the position of item selected (determines class)
		Intent intent = getActivity().getIntent();
		position = intent.getIntExtra("position", 0);

		// Find views w/ ButterKnife
		lvDeck = findById(V, R.id.lvDeck);
		gvDeck = findById(V, R.id.gvDeck);
		tvNumCards = findById(V, R.id.tvNumCards);
		ivSwipe = findById(V, R.id.imageView1);

		// Set ListView and GridView to listen to long-press on an item
		registerForContextMenu(lvDeck);
		registerForContextMenu(gvDeck);

		// ImageLoader init
		if (!loader.isInited()) {
			loader.init(Utils.config(getActivity()));
		}
		setHasOptionsMenu(true);
		
		// Get corresponding deck
		cardList = (ArrayList<Cards>) DeckUtils.getCardsList(getActivity(),
				listDecks.get(position));
		if (cardList != null) {
			cardListUnique = new ArrayList<Cards>(new LinkedHashSet<Cards>(
					cardList));
		} else {
			cardListUnique = new ArrayList<Cards>();
		}

		// Hide graphic to make room for List
		if (cardList != null && cardList.size() != 0) {
			ivSwipe.setVisibility(View.GONE);
		}

		// Set typeface
		tvNumCards.setTypeface(font);

		deckClasses = (List<Integer>) DeckUtils.getIntegerDeck(getActivity(),
				"deckclasses");

		return V;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Get text sizes in sp
		int sp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				10, getActivity().getResources().getDisplayMetrics());
		int bigSp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				14, getActivity().getResources().getDisplayMetrics());

		// Get screen size
		int screenSize = getActivity().getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;

		if (cardList != null) {
			if (cardList.size() == 0
					&& screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL) {
				tvNumCards.setTextSize(bigSp);
				tvNumCards
						.setText("Looks like there's nothing here. Swipe right to get started!");
				ivSwipe.setVisibility(View.VISIBLE);
			} else if (cardList.size() == 0
					&& screenSize > Configuration.SCREENLAYOUT_SIZE_NORMAL) {
				tvNumCards.setTextSize(bigSp);
				tvNumCards
						.setText("Looks like there's nothing here. Add cards from the left to get started!");
				ivSwipe.setVisibility(View.VISIBLE);
			} else {
				tvNumCards.setTextSize(sp);
				tvNumCards.setText("" + cardList.size() + " / 30");
				ivSwipe.setVisibility(View.GONE);
			}
		}

		if (cardList != null) {
			Collections.sort(cardList, new CardComparator(2, false));
			Collections.sort(cardListUnique, new CardComparator(2, false));
		}
		
		adapter = new ImageAdapter(getActivity(), cardList);
		adapter2 = new DeckListAdapter(getActivity(), cardList);

		gvDeck.setAdapter(adapter);
		lvDeck.setAdapter(adapter2);

		// Change GridView / ListView visibility

		if (savedInstanceState != null) {
			this.isGrid = savedInstanceState.getBoolean("isGrid");
		}

		if (isGrid) {
			lvDeck.setVisibility(View.INVISIBLE);
			gvDeck.setVisibility(View.VISIBLE);
		} else {
			gvDeck.setVisibility(View.INVISIBLE);
			lvDeck.setVisibility(View.VISIBLE);
		}

		MyWindow.setContext(getActivity());

		// Set GridView and ListView to show PopupWindow when clicked
		gvDeck.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				refreshDecks();
				MyWindow.initiatePopupWindow(cardList, position, parent);
			}
		});
		lvDeck.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				refreshDecks();
				MyWindow.initiatePopupWindow(cardListUnique, position, parent);
			}
		});
		setManaChart(cardList);
		setPieGraph(cardList);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isGrid", isGrid);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		refreshDecks();
		
		if (v.getId() == R.id.lvDeck) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(cardListUnique.get(info.position).getName());
			pos = info.position;
			String menuItems = "Remove card \""
					+ cardListUnique.get(info.position).getName() + "\"";
			menu.add(Menu.NONE, 0, 0, menuItems);
		} else {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(cardList.get(info.position).getName());
			pos = info.position;
			String menuItems = "Remove card \""
					+ cardList.get(info.position).getName() + "\"";
			menu.add(Menu.FIRST, 0, 0, menuItems);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		refreshDecks();
		
		final int bigSp = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 14, getResources()
						.getDisplayMetrics());
		int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;

		// Remove selected card from the deck
		if (item.getGroupId() == Menu.FIRST) {
			cardList.remove(pos);
			cardListUnique = new ArrayList<Cards>(new LinkedHashSet<Cards>(
					cardList));
			Log.i("Card Removed", "Card removed, pos: " + pos);

		} else if (item.getGroupId() == Menu.NONE){
			for (Iterator<Cards> it = cardList.iterator(); it.hasNext();) {
				Cards card = it.next();
				if (card.getName().equals(cardListUnique.get(pos).getName())) {
					it.remove();
					break;
				}
			}
			cardListUnique = new ArrayList<Cards>(new LinkedHashSet<Cards>(
					cardList));
			Log.i("Card Removed", "Card removed, pos: " + pos);

		}
		
		// Save the updated deck
		DeckUtils.saveDeck(getActivity(), listDecks.get(position), cardList);
		Log.i("Deck Saved", "Deck name: " + listDecks.get(position));

		// Refresh Views with updated data
		adapter.update(cardList);
		adapter2.update(cardList);
		//lvDeck.setAdapter(new DeckListAdapter(getActivity(), cardList));
		
		// Refresh charts
		setManaChart(cardList);
		setPieGraph(cardList);

		
		// Set current card count
		tvNumCards.setText("" + cardList.size() + " / 30");

		if (cardList.size() == 0
				&& screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			tvNumCards.setTextSize(bigSp);
			tvNumCards
					.setText("Looks like there's nothing here. Swipe right to get started!");
			ivSwipe.setVisibility(View.VISIBLE);
		} else if (cardList.size() == 0
				&& screenSize > Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			tvNumCards.setTextSize(bigSp);
			tvNumCards
					.setText("Looks like there's nothing here. Add cards from the left to get started!");
			ivSwipe.setVisibility(View.VISIBLE);
		}
		
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.deck, menu);

		MenuItem listSwitcher = menu.findItem(R.id.action_switch);

		if (isGrid) {
			listSwitcher.setTitle("Switch to list view");
			listSwitcher.setIcon(R.drawable.collections_view_as_list);
		} else {
			listSwitcher.setTitle("Switch to grid view");
			listSwitcher.setIcon(R.drawable.collections_view_as_grid);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		// Switch between GridView and ListView
		case R.id.action_switch:
			if (isGrid) {
				gvDeck.setVisibility(View.GONE);
				lvDeck.setVisibility(View.VISIBLE);
				isGrid = false;
				item.setIcon(R.drawable.collections_view_as_grid);
				item.setTitle("Switch to grid view");
			} else {
				gvDeck.setVisibility(View.VISIBLE);
				lvDeck.setVisibility(View.GONE);
				isGrid = true;
				item.setIcon(R.drawable.collections_view_as_list);
				item.setTitle("Switch to list view");
			}
			break;

		case R.id.action_rename:
			DeckUtils.renameDeck(getActivity(), position, getActivity(), cardList);
			break;

		// Remove call cards from current deck
		case R.id.action_clear:
			AlertDialog dialog;
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Remove all cards from this deck?");
			builder.setPositiveButton("Remove All",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							cardListUnique.clear();
							cardList.clear();
							DeckUtils.saveDeck(getActivity(),
									listDecks.get(position), cardList);
							adapter.update(cardList);
							lvDeck.setAdapter(new DeckListAdapter(getActivity(), cardList));
							tvNumCards
									.setText("Looks like there's nothing here. Swipe right to get started!");
						}
					});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			dialog = builder.create();
			dialog.show();
		}
		return super.onOptionsItemSelected(item);
	}

	public static void setManaChart(List<Cards> cardList) {
		ArrayList<Bar> points = new ArrayList<Bar>();

		int costs[] = new int[20];

		for (Cards card : cardList) {
			if (card.getCost() != null) {
				if (card.getCost().intValue() > 7) {
					costs[7]++;
				} else {
					costs[card.getCost().intValue()]++;
				}
			}
		}

		for (int i = 0; i < 8; i++) {
			Bar dBar = new Bar();

			if (i != 7) {
				dBar.setName("" + i);
			} else {
				dBar.setName("7+");
			}
			dBar.setColor(Color.rgb(255, 68, 68));
			dBar.setValue(costs[i]);
			dBar.setShowAsFloat(false);
			points.add(dBar);
		}
		manaChart.setShowBarText(false);
		manaChart.setTextSize(15);
		manaChart.setBars(points);
	}

	public static void setPieGraph(List<Cards> cardList) {

		int minions = 0;
		int abilities = 0;
		int weapons = 0;

		pieGraph.removeSlices();

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
			PieSlice slice = new PieSlice();
			slice.setColor(Color.parseColor("#AA66CC"));
			slice.setValue(abilities);
			pieGraph.addSlice(slice);
		}
		if (minions != 0) {
			PieSlice slice = new PieSlice();
			slice.setColor(Color.rgb(0, 171, 249));
			slice.setValue(minions);
			pieGraph.addSlice(slice);
		}
		if (weapons != 0) {
			PieSlice slice = new PieSlice();
			slice.setColor(Color.parseColor("#99CC00"));
			slice.setValue(weapons);
			pieGraph.addSlice(slice);
		}
	}
	
	private void refreshDecks() {
		cardList = (List<Cards>) DeckUtils.getCardsList(getActivity(),
				listDecks.get(position));
		if (cardList != null) {
			cardListUnique = new ArrayList<Cards>(new LinkedHashSet<Cards>(
					cardList));
		} else {
			cardListUnique = new ArrayList<Cards>();
		}
		
		Collections.sort(cardList, new CardComparator(2, false));
		Collections.sort(cardListUnique, new CardComparator(2, false));
	}
}
