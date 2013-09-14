package com.jt.hearthstone;

import java.util.ArrayList;
import java.util.Collections;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;

public class CustomOnItemSelectedListener implements OnItemSelectedListener {

	ArrayList<Cards> cardList;
	private Cards[] cards;
	private GridView grid;
	private ImageAdapter adapter;
	private CustomListAdapter adapter2;
	private CheckBox cbReverse = CardListFragment.cbReverse;
	private Spinner spinnerSort = CardListFragment.spinnerSort;
	private Spinner spinnerClass = CardListFragment.spinner;
	private ListView listCards;
	public static int position = 0;
	private boolean reverse = CardListActivity.reverse;

	public CustomOnItemSelectedListener(ArrayList<Cards> cardList,
			Cards[] cards, GridView grid, ListView listCards,
			ImageAdapter adapter, CustomListAdapter adapter2) {
		this.cardList = cardList;
		this.cards = cards;
		this.grid = grid;
		this.adapter = adapter;
		this.listCards = listCards;
		this.adapter2 = adapter2;

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		Spinner spinner = (Spinner) parent;
		if (spinner.getId() == R.id.spinner1) {
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
			switch (spinnerClass.getSelectedItemPosition()) {
			case 0:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList();
				} else {
					cardList.clear();
					for (Cards card : cards) {
						if (card.getDescription() != null
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
					setCardList(Classes.DRUID, spinner.getSelectedItem().toString());
				}
				break;
			case 2:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.HUNTER);
				} else {
					setCardList(Classes.HUNTER, spinner.getSelectedItem().toString());
				}
				break;
			case 3:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.MAGE);
				} else {
					setCardList(Classes.MAGE, spinner.getSelectedItem().toString());
				}
				break;
			case 4:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.PALADIN);
				} else {
					setCardList(Classes.PALADIN, spinner.getSelectedItem().toString());
				}
				break;
			case 5:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.PRIEST);
				} else {
					setCardList(Classes.PRIEST, spinner.getSelectedItem().toString());
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
		cardList.clear();
		for (Cards card : cards) {
			if (card.getDescription() != null
					&& card.getClasss() != null
					&& card.getClasss().intValue() == classes.getValue()
					&& card.getDescription().contains(selectedItem)
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

	private void setCardList(Classes classes) {
		// Clear the current ArrayList so we can repopulate it
		cardList.clear();
		// Repopulate the card list with class cards
		for (Cards card : cards) {
			if (card.getClasss() != null
					&& card.getClasss().intValue() == classes.getValue()
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
					&& !name.equals(classArray[8].getHeroName())) {
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
