package com.jt.hearthstone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;

@SuppressLint("DefaultLocale")
public class CustomOnItemSelectedListener implements OnItemSelectedListener {

	private ArrayList<Cards> cardList;
	private Cards[] cards;
	private GridView grid;
	private ImageAdapter adapter;
	private CustomListAdapter adapter2;
	private CheckBox cbReverse = CardListFragment.cbReverse;
	private Spinner spinnerSort = CardListFragment.spinnerSort;
	private ListView listCards;
	private String query;
	private SearchView mSearchView;
	private List<Integer> deckClasses;
	static int position = 0;
	private boolean reverse = CardListActivity.reverse;

	public CustomOnItemSelectedListener(ArrayList<Cards> cardList,
			Cards[] cards, GridView grid, ListView listCards,
			ImageAdapter adapter, CustomListAdapter adapter2,
			List<Integer> classes, SearchView mSearchView) {
		this.cardList = cardList;
		this.cards = cards;
		this.grid = grid;
		this.adapter = adapter;
		this.listCards = listCards;
		this.adapter2 = adapter2;
		this.deckClasses = classes;
		this.mSearchView = mSearchView;

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		
		Spinner spinner = (Spinner) parent;
		if (spinner.getId() == R.id.spinClass) {
			switch (pos) {
			// All Class Card List
			case 0:
				setCardList();
				break;
			// Druid Class Card List
			case 1:
				setCardList(Classes.DRUID);
				break;

			// Hunter Class Card List
			case 2:
				setCardList(Classes.HUNTER);
				break;

			// Mage Class Card List
			case 3:
				setCardList(Classes.MAGE);
				break;

			// Paladin Class Card List
			case 4:
				setCardList(Classes.PALADIN);
				break;
			case 5:
				setCardList(Classes.PRIEST);
				break;
			case 6:
				setCardList(Classes.ROGUE);
				break;
			case 7:
				setCardList(Classes.SHAMAN);
				break;
			case 8:
				setCardList(Classes.WARLOCK);
				break;
			case 9:
				setCardList(Classes.WARRIOR);
				break;
			}

		} else if (spinner.getId() == R.id.spinnerSort) {
			position = pos;
			Collections.sort(cardList,
					new CardComparator(pos, cbReverse.isChecked()));
			adapter.notifyDataSetChanged();
			adapter2.notifyDataSetChanged();
			grid.setAdapter(adapter);
			listCards.setAdapter(adapter2);

		} else if (spinner.getId() == R.id.spinnerMechanic) {
			switch (deckClasses.get(CardListFragment.deckListPos)) {
			case 0:
				if (mSearchView != null) {
					query = mSearchView.getQuery().toString().toLowerCase();
				} else {
					query = "";
				}
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.DRUID, "");
				} else {
					cardList.clear();
					for (Cards card : cards) {
						if (card.getDescription() != null
								&& card.getClasss() != null
								&& card.getClasss().intValue() == Classes.DRUID
										.getValue()
								&& card.getName().toLowerCase().contains(query)
								&& card.getDescription().contains(
										spinner.getSelectedItem().toString())) {
							cardList.add(card);
						}
					}
					Collections.sort(
							cardList,
							new CardComparator(spinnerSort
									.getSelectedItemPosition(), cbReverse
									.isChecked()));
					adapter.notifyDataSetChanged();
					adapter2.notifyDataSetChanged();
					grid.setAdapter(adapter);
					listCards.setAdapter(adapter2);
				}
				break;
			case 1:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.HUNTER, "");
				} else {
					setCardList(Classes.HUNTER, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 2:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.MAGE, "");
				} else {
					setCardList(Classes.MAGE, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 3:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.PALADIN, "");
				} else {
					setCardList(Classes.PALADIN, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 4:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.PRIEST, "");
				} else {
					setCardList(Classes.PRIEST, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 5:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.ROGUE, "");
				} else {
					setCardList(Classes.ROGUE, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 6:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.SHAMAN, "");
				} else {
					setCardList(Classes.SHAMAN, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 7:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.WARLOCK, "");
				} else {
					setCardList(Classes.WARLOCK, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 8:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.WARRIOR, "");
				} else {
					setCardList(Classes.WARRIOR, spinner.getSelectedItem()
							.toString());
				}
				break;
			}
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO
	}

	/*
	 * Handy little function to set the card list This is called when you select
	 * any class other than "Any" from the Spinner
	 */
	private void setCardList(Classes classes, String selectedItem) {
		if (mSearchView != null) {
			query = mSearchView.getQuery().toString().toLowerCase();
		} else {
			query = "";
		}
		cardList.clear();
		for (Cards card : cards) {
			if (card.getDescription() != null && card.getClasss() != null
					&& card.getClasss().intValue() == classes.getValue()
					&& card.getDescription().contains(selectedItem)
					&& card.getName().toLowerCase().contains(query)
					&& !card.getName().equals(classes.getHeroName())) {
				cardList.add(card);
			}
			if (CardListFragment.includeNeutralCards.isChecked()) {
				if (card.getClasss() == null && card.getDescription() != null
						&& card.getDescription().contains(selectedItem)
						&& card.getName().toLowerCase().contains(query)
						&& !card.getName().equals(classes.getHeroName())) {
					cardList.add(card);
				}
			}
		}
		Collections.sort(cardList,
				new CardComparator(spinnerSort.getSelectedItemPosition(),
						cbReverse.isChecked()));

		adapter.notifyDataSetChanged();
		adapter2.notifyDataSetChanged();
		grid.setAdapter(adapter);
		listCards.setAdapter(adapter2);
	}

	private void setCardList(Classes classes) {
		if (mSearchView != null) {
			query = mSearchView.getQuery().toString().toLowerCase();
		} else {
			query = "";
		}
		// Clear the current ArrayList so we can repopulate it
		cardList.clear();
		// Repopulate the card list with class cards
		for (Cards card : cards) {
			if (card.getClasss() != null
					&& card.getClasss().intValue() == classes.getValue()
					&& card.getName().toLowerCase().contains(query)
					&& !card.getName().equals(classes.getHeroName())) {
				cardList.add(card);
			}

		}

		Collections.sort(cardList,
				new CardComparator(spinnerSort.getSelectedItemPosition(),
						cbReverse.isChecked()));
		adapter.notifyDataSetChanged();
		adapter2.notifyDataSetChanged();
		grid.setAdapter(adapter);
		listCards.setAdapter(adapter2);
	}

	/*
	 * Handy little function to set the default card list This is called when
	 * you select "Any" from the Spinner Overloaded version of previous function
	 */
	private void setCardList() {
		if (mSearchView != null) {
			query = mSearchView.getQuery().toString().toLowerCase();
		} else {
			query = "";
		}
		// Clear the current ArrayList so we can repopulate it
		Classes[] classArray = new Classes[] { Classes.DRUID, Classes.HUNTER,
				Classes.MAGE, Classes.PALADIN, Classes.PRIEST, Classes.ROGUE,
				Classes.SHAMAN, Classes.WARLOCK, Classes.WARRIOR };

		if (!cardList.isEmpty()) {
			cardList.clear();
		}

		// Repopulate the card list with class cards
		for (Cards card : cards) {
			String name = card.getName();
			if (!name.equals(classArray[0].getHeroName())
					&& !name.equals(classArray[1].getHeroName())
					&& !name.equals(classArray[2].getHeroName())
					&& !name.equals(classArray[3].getHeroName())
					&& !name.equals(classArray[4].getHeroName())
					&& !name.equals(classArray[5].getHeroName())
					&& !name.equals(classArray[6].getHeroName())
					&& !name.equals(classArray[7].getHeroName())
					&& !name.equals(classArray[8].getHeroName())
					&& card.getName().toLowerCase().contains(query)
					&& card.getClasss() != null
					&& card.getClasss().intValue() == Classes.DRUID.getValue()) {
				cardList.add(card);
			} else if (CardListFragment.includeNeutralCards.isChecked()
					&& card.getName().toLowerCase().contains(query)
					&& card.getClasss() == null) {
				cardList.add(card);
			}
		}
		Collections.sort(cardList,
				new CardComparator(spinnerSort.getSelectedItemPosition(),
						reverse));
		adapter.notifyDataSetChanged();
		adapter2.notifyDataSetChanged();
		grid.setAdapter(adapter);
		listCards.setAdapter(adapter2);
	}
}
