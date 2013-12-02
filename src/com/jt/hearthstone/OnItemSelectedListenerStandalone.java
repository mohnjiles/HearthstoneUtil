package com.jt.hearthstone;

import java.util.ArrayList;
import static butterknife.Views.findById;
import java.util.Collections;

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;

public class OnItemSelectedListenerStandalone implements OnItemSelectedListener {

	static int position = 0;

	private ArrayList<Cards> cardList;
	private Cards[] cards;
	private ImageAdapter adapter;
	private CustomListAdapter adapter2;

	private CheckBox cbReverse;
	private CheckBox includeNeutralCards;
	private Spinner spinnerSort;
	private Spinner spinnerClass;
	private Spinner spinnerMechanic;
	private SearchView mSearchView;
	private ListView listCards;
	private GridView grid;
	private String query;
	private boolean reverse;

	public OnItemSelectedListenerStandalone(ActionBarActivity activity,
			SearchView mSearchView, ArrayList<Cards> cardList, Cards[] cards,
			ImageAdapter adapter, CustomListAdapter adapter2) {
		this.mSearchView = mSearchView;
		this.cardList = cardList;
		this.cards = cards;
		this.adapter = adapter;
		this.adapter2 = adapter2;

		spinnerClass = findById(activity, R.id.spinClass);
		spinnerMechanic = findById(activity, R.id.spinnerMechanic);
		spinnerSort = findById(activity, R.id.spinSort);
		includeNeutralCards = findById(activity, R.id.cbGenerics);
		cbReverse = findById(activity, R.id.cbReverse);
		listCards = findById(activity, R.id.cardsList);
		grid = findById(activity, R.id.gvDeck);
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {

		Spinner spinner = (Spinner) parent;

		if (spinner.getId() == R.id.spinClass) {
			switch (pos) {
			// All Class Card List
			case 0:
				if (spinnerMechanic.getSelectedItem().toString().equals("Any")) {
					setCardList();
				} else {
					setCardList(spinnerMechanic.getSelectedItem().toString());
				}
				break;

			// Druid Class Card List
			case 1:
				if (spinnerMechanic.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.DRUID);
				} else {
					setCardList(Classes.DRUID, spinnerMechanic
							.getSelectedItem().toString());
				}
				break;

			// Hunter Class Card List
			case 2:
				if (spinnerMechanic.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.HUNTER);
				} else {
					setCardList(Classes.HUNTER, spinnerMechanic
							.getSelectedItem().toString());
				}
				break;

			// Mage Class Card List
			case 3:
				if (spinnerMechanic.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.MAGE);
				} else {
					setCardList(Classes.MAGE, spinnerMechanic.getSelectedItem()
							.toString());
				}
				break;

			// Paladin Class Card List
			case 4:
				if (spinnerMechanic.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.PALADIN);
				} else {
					setCardList(Classes.PALADIN, spinnerMechanic
							.getSelectedItem().toString());
				}
				break;

			case 5:
				if (spinnerMechanic.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.PRIEST);
				} else {
					setCardList(Classes.PRIEST, spinnerMechanic
							.getSelectedItem().toString());
				}
				break;

			case 6:
				if (spinnerMechanic.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.ROGUE);
				} else {
					setCardList(Classes.ROGUE, spinnerMechanic
							.getSelectedItem().toString());
				}
				break;

			case 7:
				if (spinnerMechanic.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.SHAMAN);
				} else {
					setCardList(Classes.SHAMAN, spinnerMechanic
							.getSelectedItem().toString());
				}
				break;

			case 8:
				if (spinnerMechanic.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.WARLOCK);
				} else {
					setCardList(Classes.WARLOCK, spinnerMechanic
							.getSelectedItem().toString());
				}
				break;

			case 9:
				if (spinnerMechanic.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.WARRIOR);
				} else {
					setCardList(Classes.WARRIOR, spinnerMechanic
							.getSelectedItem().toString());
				}
				break;
			}

		} else if (spinner.getId() == R.id.spinSort) {
			position = pos;
			Collections.sort(cardList,
					new CardComparator(pos, cbReverse.isChecked()));
			adapter.notifyDataSetChanged();
			adapter2.notifyDataSetChanged();
			grid.setAdapter(adapter);
			listCards.setAdapter(adapter2);

		} else if (spinner.getId() == R.id.spinnerMechanic) {
			switch (spinnerClass.getSelectedItemPosition()) {
			case 0:
				if (mSearchView != null) {
					query = mSearchView.getQuery().toString().toLowerCase();
				} else {
					query = "";
				}
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList();
				} else {
					cardList.clear();
					for (Cards card : cards) {
						if (card.getDescription() != null
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
					setCardList(Classes.DRUID);
				} else {
					setCardList(Classes.DRUID, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 2:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.HUNTER);
				} else {
					setCardList(Classes.HUNTER, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 3:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.MAGE);
				} else {
					setCardList(Classes.MAGE, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 4:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.PALADIN);
				} else {
					setCardList(Classes.PALADIN, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 5:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.PRIEST);
				} else {
					setCardList(Classes.PRIEST, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 6:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.ROGUE);
				} else {
					setCardList(Classes.ROGUE, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 7:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.SHAMAN);
				} else {
					setCardList(Classes.SHAMAN, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 8:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.WARLOCK);
				} else {
					setCardList(Classes.WARLOCK, spinner.getSelectedItem()
							.toString());
				}
				break;
			case 9:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.WARRIOR);
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
					&& !card.getName().equals(classes.getHeroName())
					&& card.getName().toLowerCase().contains(query)) {
				Log.i("setCardList(classes, string)", "" + query + "   "
						+ card.getName().toLowerCase());
				cardList.add(card);
			}
			if (includeNeutralCards.isChecked()) {
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
		Log.i("setCardList(Classes)", "" + query);
		// Clear the current ArrayList so we can repopulate it
		cardList.clear();
		// Repopulate the card list with class cards
		for (Cards card : cards) {
			if (card.getClasss() != null
					&& card.getClasss().intValue() == classes.getValue()
					&& !card.getName().equals(classes.getHeroName())
					&& card.getName().toLowerCase().contains(query)) {
				cardList.add(card);
			} else if (includeNeutralCards.isChecked()
					&& card.getClasss() == null
					&& card.getName().toLowerCase().contains(query)) {
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

		Classes[] classArray = new Classes[] { Classes.DRUID, Classes.HUNTER,
				Classes.MAGE, Classes.PALADIN, Classes.PRIEST, Classes.ROGUE,
				Classes.SHAMAN, Classes.WARLOCK, Classes.WARRIOR };

		if (!cardList.isEmpty()) {
			cardList.clear();
		}
		Log.i("setCardList()", "" + query);

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
					&& name.toLowerCase().contains(query)) {
				cardList.add(card);
			} else if (includeNeutralCards.isChecked()
					&& card.getClasss() == null
					&& card.getName().toLowerCase().contains(query)) {
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

	private void setCardList(String selectedItem) {
		// Clear the current ArrayList so we can repopulate it
		Classes[] classArray = new Classes[] { Classes.DRUID, Classes.HUNTER,
				Classes.MAGE, Classes.PALADIN, Classes.PRIEST, Classes.ROGUE,
				Classes.SHAMAN, Classes.WARLOCK, Classes.WARRIOR };

		if (mSearchView != null) {
			query = mSearchView.getQuery().toString().toLowerCase();
		} else {
			query = "";
		}

		if (!cardList.isEmpty()) {
			cardList.clear();
		}
		Log.i("query", "" + query);
		// Repopulate the card list with class cards
		for (Cards card : cards) {
			String name = card.getName().toLowerCase();
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
					&& card.getDescription() != null
					&& card.getDescription().contains(selectedItem)) {
				cardList.add(card);
			} else if (includeNeutralCards.isChecked()
					&& card.getClasss() == null
					&& card.getName().toLowerCase().contains(query)) {
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