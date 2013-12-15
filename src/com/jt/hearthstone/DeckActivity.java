package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.achartengine.renderer.SimpleSeriesRenderer;

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

import com.nostra13.universalimageloader.core.ImageLoader;

public class DeckActivity extends Fragment {

	ListView lvDeck;
	GridView gvDeck;
	TextView tvNumCards;
	ImageView ivSwipe;

	DeckListAdapter adapter;
	ImageAdapter adapter2;

	List<Cards> cardList;

	private ArrayList<Cards> cardListUnique;
	private ArrayList<String> listDecks = DeckSelector.listDecks;
	private ImageLoader loader = ImageLoader.getInstance();

	private int position;
	private int pos;
	private boolean isGrid = false;
	private Typeface font;

	private ChartActivity chartFrag;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate view
		View V = inflater.inflate(R.layout.activity_deck, container, false);

		ImageLoader.getInstance().init(Utils.config(getActivity()));

		ImageLoader.getInstance().handleSlowNetwork(true);

		// Get font from Cache
		font = TypefaceCache
				.get(getActivity().getAssets(), "fonts/belwebd.ttf");

		// Get the position of item selected (determines class)
		Intent intent = getActivity().getIntent();
		position = intent.getIntExtra("position", 0);

		// Get corresponding deck
		cardList = (List<Cards>) Utils.getDeck(getActivity(),
				listDecks.get(position));
		cardListUnique = new ArrayList<Cards>(
				new LinkedHashSet<Cards>(cardList));

		// Find views w/ ButterKnife
		lvDeck = findById(V, R.id.lvDeck);
		gvDeck = findById(V, R.id.gvDeck);
		tvNumCards = findById(V, R.id.tvNumCards);
		ivSwipe = findById(V, R.id.imageView1);

		// Set ListView and GridView to listen to long-press on an item
		registerForContextMenu(lvDeck);
		registerForContextMenu(gvDeck);

		// Get reference to Chart Fragment
		// (Will need this later to update charts)
		chartFrag = (ChartActivity) getActivity().getSupportFragmentManager()
				.findFragmentByTag(Utils.makeFragmentName(R.id.pager, 2));

		// ImageLoader init
		if (!loader.isInited()) {
			loader.init(Utils.config(getActivity()));
		}

		// If not on a tablet, use per-page menu/actionbar
		int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		if (screenSize < Configuration.SCREENLAYOUT_SIZE_LARGE
				|| getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setHasOptionsMenu(true);
		}

		// Hide graphic to make room for List
		if (cardListUnique.size() != 0) {
			ivSwipe.setVisibility(View.GONE);
		}

		// Set typeface
		tvNumCards.setTypeface(font);

		return V;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Set up initial card list if available
		doSomeStuff(
				(List<Cards>) Utils.getDeck(getActivity(),
						listDecks.get(position)), listDecks.get(position));

		// Change GridView / ListView visibility
		if (isGrid) {
			lvDeck.setVisibility(View.INVISIBLE);
			gvDeck.setVisibility(View.VISIBLE);
		} else {
			gvDeck.setVisibility(View.INVISIBLE);
			lvDeck.setVisibility(View.VISIBLE);
		}

		Utils.setContext(getActivity());
		
		// Set GridView and ListView to show PopupWindow when clicked
		gvDeck.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Utils.initiatePopupWindow(cardList, position, parent);
			}
		});
		lvDeck.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Utils.initiatePopupWindow(cardList, position, parent);
			}
		});

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.lvDeck || v.getId() == R.id.gvDeck) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(cardListUnique.get(info.position).getName());
			pos = info.position;
			String menuItems = "Remove card \""
					+ cardListUnique.get(info.position).getName() + "\"";
			menu.add(Menu.FIRST, 0, 0, menuItems);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		final int bigSp = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 14, getResources()
						.getDisplayMetrics());
		int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;

		// Remove selected card from the deck
		if (item.getGroupId() == Menu.FIRST) {
			cardList.remove(pos);
			cardListUnique.remove(pos);
			Log.i("Card Removed", "Card removed, pos: " + pos);

			// Save the updated deck
			Utils.saveDeck(getActivity(), listDecks.get(position), cardList);

			// Tell adapters data has changed!
			adapter.notifyDataSetChanged();
			adapter2.notifyDataSetChanged();

			// Refresh Views with updated data
			doSomeStuff(
					(List<Cards>) Utils.getDeck(getActivity(),
							listDecks.get(position)), listDecks.get(position));
			Log.i("Deck Saved", "Deck name: " + listDecks.get(position));

			// Set current card count
			tvNumCards.setText("" + cardListUnique.size() + " / 30");

			if (cardListUnique.size() == 0
					&& screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL) {
				tvNumCards.setTextSize(bigSp);
				tvNumCards
						.setText("Looks like there's nothing here. Swipe right to get started!");
				ivSwipe.setVisibility(View.VISIBLE);
			} else if (cardListUnique.size() == 0
					&& screenSize > Configuration.SCREENLAYOUT_SIZE_NORMAL) {
				tvNumCards.setTextSize(bigSp);
				tvNumCards
						.setText("Looks like there's nothing here. Add cards from the left to get started!");
				ivSwipe.setVisibility(View.VISIBLE);
			}

			// Refresh Mana Chart if possible
			if (chartFrag.mChart != null) {
				((ViewGroup) chartFrag.mChart.getParent())
						.removeView(chartFrag.mChart);
				chartFrag.mCurrentSeries.clear();
				addSampleData();
				chartFrag.layout2.addView(chartFrag.mChart);
			}

			// Refresh pie chart if possible
			if (chartFrag.mPieChart != null) {
				((ViewGroup) chartFrag.mPieChart.getParent())
						.removeView(chartFrag.mPieChart);
				chartFrag.mSeries.clear();
				chartFrag.mRenderer2.removeAllRenderers();
				addPieData(cardList);
				chartFrag.layout.addView(chartFrag.mPieChart);
			}

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
				gvDeck.setVisibility(View.INVISIBLE);
				lvDeck.setVisibility(View.VISIBLE);
				isGrid = false;
				item.setIcon(R.drawable.collections_view_as_grid);
				item.setTitle("Switch to grid view");
			} else {
				gvDeck.setVisibility(View.VISIBLE);
				lvDeck.setVisibility(View.INVISIBLE);
				isGrid = true;
				item.setIcon(R.drawable.collections_view_as_list);
				item.setTitle("Switch to list view");
			}
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
							Utils.saveDeck(getActivity(),
									listDecks.get(position), cardList);
							doSomeStuff((List<Cards>) Utils.getDeck(
									getActivity(), listDecks.get(position)),
									listDecks.get(position));
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

	/*
	 * Set up the PopupWindow
	 * 
	 * Takes int based on position in list, used to determine card to show
	 */

	private void addSampleData() {
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
		final int[] colors = { Color.rgb(0, 171, 249), Color.rgb(245, 84, 0),
				Color.rgb(60, 242, 0) };
		final int[] colors2 = { Color.rgb(0, 108, 229), Color.rgb(225, 23, 3),
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

	public void doSomeStuff(List<Cards> result, String deckName) {

		ArrayList<Cards> unique = new ArrayList<Cards>(
				new LinkedHashSet<Cards>(result));

		// Get text sizes in sp
		int sp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				10, getActivity().getResources().getDisplayMetrics());
		int bigSp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				14, getActivity().getResources().getDisplayMetrics());

		// Get screen size
		int screenSize = getActivity().getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;

		// If a null list was passed, try to manually lookup deck
		if (result == null) {
			result = (List<Cards>) Utils.getDeck(getActivity(), deckName);
			unique = new ArrayList<Cards>(new LinkedHashSet<Cards>(result));
		}
		cardListUnique = unique;
		cardList = result;
		if (result.size() == 0
				&& screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			tvNumCards.setTextSize(bigSp);
			tvNumCards
					.setText("Looks like there's nothing here. Swipe right to get started!");
			ivSwipe.setVisibility(View.VISIBLE);
		} else if (result.size() == 0
				&& screenSize > Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			tvNumCards.setTextSize(bigSp);
			tvNumCards
					.setText("Looks like there's nothing here. Add cards from the left to get started!");
			ivSwipe.setVisibility(View.VISIBLE);
		} else {
			tvNumCards.setTextSize(sp);
			tvNumCards.setText("" + result.size() + " / 30");
			ivSwipe.setVisibility(View.GONE);
		}

		if (adapter2 == null) {
			adapter2 = new ImageAdapter(getActivity(), result);
		}
		adapter = new DeckListAdapter(getActivity(), result);
		gvDeck.setAdapter(adapter2);
		lvDeck.setAdapter(adapter);
	}
}
