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
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.nineoldandroids.animation.ObjectAnimator;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DeckActivity extends CustomCardFragment {

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
	private BarGraph manaChart;
	private PieGraph pieGraph;

	private View gridOrListView;

	private int position;
	private boolean isGrid = true;
	private Typeface font;

	private int cardPos;
	private View parentAdapterView;

	private boolean isQuickEditMode;
	private SharedPreferences prefs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.setClassName("DeckActivity");

		// Inflate view
		View V = inflater.inflate(R.layout.activity_deck, container, false);

		manaChart = findById(V, R.id.manaChart);
		pieGraph = findById(V, R.id.pieGraph);

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

		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		setHasOptionsMenu(true);

		// Get corresponding deck
		new DeckUtils.GetCardsList(getActivity(), this, 999).execute(listDecks
				.get(position));

		// Set typeface
		tvNumCards.setTypeface(font);

		deckClasses = (List<Integer>) DeckUtils.getIntegerDeck(getActivity(),
				"deckclasses");

		isQuickEditMode = prefs.getBoolean("isQuickEditMode", false);

		return V;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (cardList != null) {
			Collections.sort(cardList, new CardComparator(2, false));
			Collections.sort(cardListUnique, new CardComparator(2, false));
		}

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

		// Set GridView and ListView to remove card when clicked
		gvDeck.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent, final View v,
					final int position, long id) {

				isQuickEditMode = prefs.getBoolean("isQuickEditMode", false);

				if (isQuickEditMode) {

					ObjectAnimator anim = ObjectAnimator.ofFloat(v, "alpha",
							1.0f, 0.0f);
					anim.setDuration(100).start();

					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							removeCard(parent, position);
							refreshDecks();
							ObjectAnimator animTwo = ObjectAnimator.ofFloat(v,
									"alpha", 0.0f, 1.0f);
							animTwo.setDuration(100).start();
						}
					}, 100);

				} else {
					MyWindow.setCardList(cardList);
					MyWindow.initiatePopupWindow(position, parent);
				}
			}
		});
		lvDeck.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parent, final View v,
					final int position, long id) {

				isQuickEditMode = prefs.getBoolean("isQuickEditMode", false);

				if (isQuickEditMode) {
					ObjectAnimator anim = ObjectAnimator.ofFloat(v, "alpha",
							1.0f, 0.0f);
					anim.setDuration(100).start();

					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							removeCard(parent, position);
							refreshDecks();
							ObjectAnimator animTwo = ObjectAnimator.ofFloat(v,
									"alpha", 0.0f, 1.0f);
							animTwo.setDuration(100).start();
						}
					}, 100);
				} else {
					MyWindow.setCardList(cardList);
					MyWindow.initiatePopupWindow(position, parent);
				}
			}
		});
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
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		gridOrListView = v;

		cardListUnique = new ArrayList<Cards>(
				new LinkedHashSet<Cards>(cardList));
		isQuickEditMode = prefs.getBoolean("isQuickEditMode", false);

		if (v.getId() == R.id.lvDeck) {
			if (isQuickEditMode) {
				menu.setHeaderTitle(cardListUnique.get(info.position).getName());
				menu.add(Menu.NONE, 0, 0, "Show details");
			} else {
				menu.setHeaderTitle(cardListUnique.get(info.position).getName());
				menu.add(Menu.NONE, 0, 0, "Remove card \""
						+ cardListUnique.get(info.position).getName() + "\"");
			}
		} else {
			if (isQuickEditMode) {
				menu.setHeaderTitle(cardList.get(info.position).getName());
				menu.add(Menu.FIRST, 0, 0, "Show details");
			} else {
				menu.setHeaderTitle(cardList.get(info.position).getName());
				menu.add(Menu.FIRST, 0, 0,
						"Remove card \""
								+ cardList.get(info.position).getName() + "\"");
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		isQuickEditMode = prefs.getBoolean("isQuickEditMode", false);
		if (isQuickEditMode) {
			if (item.getGroupId() == Menu.FIRST
					|| item.getGroupId() == Menu.NONE) {
				MyWindow.setCardList(cardList);
				MyWindow.initiatePopupWindow(info.position, info.targetView);
			}
		} else {
			if (item.getGroupId() == Menu.FIRST
					|| item.getGroupId() == Menu.NONE) {
				removeCard(gridOrListView, info.position);
				refreshDecks();
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
			DeckUtils.renameDeck(getActivity(), position, getActivity(),
					cardList);
			break;

		// Remove call cards from current deck
		case R.id.action_clear:
			AlertDialog dialog;
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			final CardListFragment cardListFrag = (CardListFragment) getActivity()
					.getSupportFragmentManager().findFragmentByTag(
							Utils.makeFragmentName(R.id.pager, 0));

			builder.setTitle("Remove all cards from this deck?");
			builder.setPositiveButton("Remove All",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							cardListUnique.clear();
							cardList = new ArrayList<Cards>();
							new DeckUtils.SaveDeck(getActivity(), listDecks
									.get(position), cardList).execute();
							cardListFrag.cardsList.clear();
							cardListFrag.cardsList.addAll(cardList);
							adapter.update(cardList);
							adapter2.update(cardList);
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

	public void setManaChart(List<Cards> cardList) {
		if (cardList.size() > 0) {
			ArrayList<Bar> points = new ArrayList<Bar>();

			int costs[] = new int[8];

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
	}

	public void setPieGraph(List<Cards> cardList) {

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

	public void refreshDecks() {
		new DeckUtils.GetCardsList(getActivity(), this, 1337).execute(listDecks
				.get(position));

	}

	private void removeCard(View v, int pos) {
		DeckChanceFragment deckChanceFragment = (DeckChanceFragment) getActivity()
				.getSupportFragmentManager().findFragmentByTag(
						Utils.makeFragmentName(R.id.pager, 3));

		CardListFragment cardListFragment = (CardListFragment) getActivity()
				.getSupportFragmentManager().findFragmentByTag(
						Utils.makeFragmentName(R.id.pager, 0));

		final int bigSp = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 14, getResources()
						.getDisplayMetrics());
		int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;

		// Remove selected card from the deck
		if (v.getId() == R.id.gvDeck) {
			Crouton.cancelAllCroutons();
			Crouton.makeText(getActivity(),
					"Card removed: " + cardList.get(pos).getName(),
					Style.INFO).show();

			cardList.remove(pos);
			cardListUnique = new ArrayList<Cards>(new LinkedHashSet<Cards>(
					cardList));
			Log.i("Card Removed", "Card removed, pos: " + pos);

		} else {
			Crouton.cancelAllCroutons();
			Crouton.makeText(getActivity(),
					"Card removed: " + cardListUnique.get(pos).getName(),
					Style.INFO).show();

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
		new DeckUtils.SaveDeck(getActivity(), listDecks.get(position), cardList)
				.execute();
		Log.i("Deck Saved", "Deck name: " + listDecks.get(position));

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

		deckChanceFragment.deckList.clear();
		deckChanceFragment.deckList.addAll(cardList);
		cardListFragment.cardsList.clear();
		cardListFragment.cardsList.addAll(cardList);

		// Refresh Views with updated data
		adapter.update(cardList);
		adapter2.update(cardList);
		// lvDeck.setAdapter(new DeckListAdapter(getActivity(), cardList));

		// Refresh charts
		setManaChart(cardList);
		setPieGraph(cardList);

		deckChanceFragment.updatePercents(cardList, true);
	}

	@Override
	protected void setCardList(List<Cards> cardList, int tag) {
		if (this.cardList != null) {
			this.cardList.clear();
		} else {
			this.cardList = new ArrayList<Cards>();
		}
		this.cardList.addAll(cardList);
		if (cardList != null) {
			cardListUnique = new ArrayList<Cards>(new LinkedHashSet<Cards>(
					cardList));
		} else {
			cardListUnique = new ArrayList<Cards>();
		}

		Collections.sort(cardList, new CardComparator(2, false));
		Collections.sort(cardListUnique, new CardComparator(2, false));

		switch (tag) {
		case 999:
			// Hide graphic to make room for List
			if (cardList != null && cardList.size() != 0) {
				ivSwipe.setVisibility(View.GONE);
			}

			int sp = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_SP, 10, getActivity()
							.getResources().getDisplayMetrics());
			int bigSp = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_SP, 14, getActivity()
							.getResources().getDisplayMetrics());

			if (cardList != null) {
				// Get screen size
				int screenSize = getActivity().getResources()
						.getConfiguration().screenLayout
						& Configuration.SCREENLAYOUT_SIZE_MASK;

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

			adapter = new ImageAdapter(getActivity(), cardList);
			adapter2 = new DeckListAdapter(getActivity(), cardList);

			gvDeck.setAdapter(adapter);
			lvDeck.setAdapter(adapter2);

			setManaChart(cardList);
			setPieGraph(cardList);

			break;
		case 1337:
			adapter.update(cardList);
			adapter2.update(cardList);
		}
	}
}
