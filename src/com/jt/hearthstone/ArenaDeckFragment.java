package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

/**
 * Fragment that holds a ListView that contains the chosen cards for the current
 * arena run.
 * 
 * @author JT
 * 
 */
public class ArenaDeckFragment extends CustomCardFragment {

	GridView gvDeck;
	ListView lvArena;
	TextView tvDeckSize;
	
	private PieGraph pieGraph;
	private BarGraph manaChart;
	
	ImageAdapter gridAdapter;
	DeckListAdapter listAdapter;

	private List<Cards> cardListUnique;
	
	private boolean isGrid = true;

	/**
	 * Overriding onCreateView to inflate our XML layout and to find the views.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.setClassName("ArenaDeckFragment");

		View v = inflater.inflate(R.layout.arena_deck_fragment, container,
				false);

		lvArena = findById(v, R.id.lvArena);
		tvDeckSize = findById(v, R.id.tvDeckSize);
		pieGraph = findById(v, R.id.pieGraph);
		manaChart = findById(v, R.id.manaChart);
		gvDeck = findById(v, R.id.gvDeck);

		setHasOptionsMenu(true);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		refreshList();
		
		tvDeckSize.setTypeface(TypefaceCache.get(getActivity().getAssets(),
				"fonts/belwebd.ttf"));

		/*
		 * ********** START PopupWindow stuff **********
		 */
		MyWindow.setContext(getActivity());
		
		lvArena.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				MyWindow.initiatePopupWindow(position, parent);
			}
		});
		gvDeck.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				MyWindow.initiatePopupWindow(position, parent);
			}
		});
		
		/*
		 * ********** END PopupWindow stuff **********
		 */

		if (savedInstanceState != null) {
			this.isGrid = savedInstanceState.getBoolean("isGrid");
		}
		
		if (isGrid) {
			lvArena.setVisibility(View.GONE);
			gvDeck.setVisibility(View.VISIBLE);
		} else {
			gvDeck.setVisibility(View.GONE);
			lvArena.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		new DeckUtils.GetCardsList(getActivity(), this, 1337).execute("arenaDeck");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isGrid", isGrid);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.arena_deck, menu);
		MenuItem listSwitcher = menu.findItem(R.id.action_switch);

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
				gvDeck.setVisibility(View.GONE);
				lvArena.setVisibility(View.VISIBLE);
				item.setTitle("Switch to grid view");
				item.setIcon(R.drawable.collections_view_as_grid);
				isGrid = false;
				return super.onOptionsItemSelected(item);
			} else {
				gvDeck.setVisibility(View.VISIBLE);
				lvArena.setVisibility(View.GONE);
				item.setTitle("Switch to list view");
				item.setIcon(R.drawable.collections_view_as_list);
				isGrid = true;
				return super.onOptionsItemSelected(item);
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void update(List<Cards> cardList) {
		gridAdapter.update(cardList);
		listAdapter.update(cardList);
		tvDeckSize.setText(cardList.size() + " / 30");
		setManaChart(cardList);
		setPieGraph(cardList);
		MyWindow.setCardList(cardList);

	}

	public void setManaChart(List<Cards> cardList) {
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
	
	private void refreshList() {
		new DeckUtils.GetCardsList(getActivity(), this, 1337).execute("arenaDeck");
	}


	@Override
	protected void setCardList(final List<Cards> cardList, int tag) {
		cardListUnique = new ArrayList<Cards>(new LinkedHashSet<Cards>(cardList));
		Collections.sort(cardList, new CardComparator(2, false));
		Collections.sort(cardListUnique, new CardComparator(2, false));
		
		// Set up adapters for ListView & GridView
		if (gridAdapter == null) {
			gridAdapter = new ImageAdapter(getActivity(), cardList);
			gvDeck.setAdapter(gridAdapter);
		} else {
			gridAdapter.update(cardList);
		}
		if (listAdapter == null) {
			listAdapter = new DeckListAdapter(getActivity(), cardList);
			lvArena.setAdapter(listAdapter);
		} else {
			listAdapter.update(cardList);
		}
		
		if (tag != 0 && tag == 123) {
			// TODO: PopupWindow after async
		} else if (tag != 0 && tag == 42) {
			if (cardList != null) {
				update(cardList);
			}
		} else if (tag != 0 && tag == 1337) {
			
		}
		
	}


}
