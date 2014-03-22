package com.jt.hearthstone;

import java.util.Collections;

import android.R.integer;
import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

@SuppressLint("DefaultLocale")
public class CustomOnItemSelectedListener implements OnItemSelectedListener {

	private CardListFragment cardListFrag;
	static int position = 0;
	private String query;
	private FragmentActivity activity;

	public CustomOnItemSelectedListener(FragmentActivity activity) {

		this.activity = activity;

		cardListFrag = (CardListFragment) activity.getSupportFragmentManager()
				.findFragmentByTag(Utils.makeFragmentName(R.id.pager, 0));
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
			cardListFrag = (CardListFragment) activity
					.getSupportFragmentManager().findFragmentByTag(
							Utils.makeFragmentName(R.id.pager, 0));

			Collections.sort(cardListFrag.cardList, new CardComparator(pos,
					cardListFrag.cbReverse.isChecked()));
			cardListFrag.adapter.notifyDataSetChanged();
			cardListFrag.adapter2.notifyDataSetChanged();
			// cardListFrag.grid.setAdapter(cardListFrag.adapter);
			// cardListFrag.listCards.setAdapter(cardListFrag.adapter2);

		} else if (spinner.getId() == R.id.spinnerMechanic) {
			switch (cardListFrag.deckClasses.get(cardListFrag.deckListPos)) {
			case 0:
				if (cardListFrag.mSearchView != null) {
					query = cardListFrag.mSearchView.getQuery().toString()
							.toLowerCase();
				} else {
					query = "";
				}
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.DRUID, "", cardListFrag.spinnerMana.getSelectedItem().toString());
				} else {
					setCardList(Classes.DRUID, spinner.getSelectedItem()
							.toString(), cardListFrag.spinnerMana.getSelectedItem().toString());
				}
				break;
			case 1:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.HUNTER, "", cardListFrag.spinnerMana.getSelectedItem().toString());
				} else {
					setCardList(Classes.HUNTER, spinner.getSelectedItem()
							.toString(), cardListFrag.spinnerMana.getSelectedItem().toString());
				}
				break;
			case 2:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.MAGE, "", cardListFrag.spinnerMana.getSelectedItem().toString());
				} else {
					setCardList(Classes.MAGE, spinner.getSelectedItem()
							.toString(), cardListFrag.spinnerMana.getSelectedItem().toString());
				}
				break;
			case 3:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.PALADIN, "", cardListFrag.spinnerMana.getSelectedItem().toString());
				} else {
					setCardList(Classes.PALADIN, spinner.getSelectedItem()
							.toString(), cardListFrag.spinnerMana.getSelectedItem().toString());
				}
				break;
			case 4:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.PRIEST, "", cardListFrag.spinnerMana.getSelectedItem().toString());
				} else {
					setCardList(Classes.PRIEST, spinner.getSelectedItem()
							.toString(), cardListFrag.spinnerMana.getSelectedItem().toString());
				}
				break;
			case 5:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.ROGUE, "", cardListFrag.spinnerMana.getSelectedItem().toString());
				} else {
					setCardList(Classes.ROGUE, spinner.getSelectedItem()
							.toString(), cardListFrag.spinnerMana.getSelectedItem().toString());
				}
				break;
			case 6:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.SHAMAN, "", cardListFrag.spinnerMana.getSelectedItem().toString());
				} else {
					setCardList(Classes.SHAMAN, spinner.getSelectedItem()
							.toString(), cardListFrag.spinnerMana.getSelectedItem().toString());
				}
				break;
			case 7:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.WARLOCK, "", cardListFrag.spinnerMana.getSelectedItem().toString());
				} else {
					setCardList(Classes.WARLOCK, spinner.getSelectedItem()
							.toString(), cardListFrag.spinnerMana.getSelectedItem().toString());
				}
				break;
			case 8:
				if (spinner.getSelectedItem().toString().equals("Any")) {
					setCardList(Classes.WARRIOR, "", cardListFrag.spinnerMana.getSelectedItem().toString());
				} else {
					setCardList(Classes.WARRIOR, spinner.getSelectedItem()
							.toString(), cardListFrag.spinnerMana.getSelectedItem().toString());
				}
				break;
			}

		} else if (spinner.getId() == R.id.spinnerMana) {
			switch (cardListFrag.deckClasses.get(cardListFrag.deckListPos)) {
			case 0:
				if (cardListFrag.mSearchView != null) {
					query = cardListFrag.mSearchView.getQuery().toString()
							.toLowerCase();
				} else {
					query = "";
				}
				if (cardListFrag.spinnerMechanic.getSelectedItem().toString()
						.equals("Any")) {
					setCardList(Classes.DRUID, "", spinner.getSelectedItem()
							.toString());
				} else {
					setCardList(Classes.DRUID, cardListFrag.spinnerMechanic
							.getSelectedItem().toString(), spinner
							.getSelectedItem().toString());
				}
				break;
			case 1:
				if (cardListFrag.spinnerMechanic.getSelectedItem().toString()
						.equals("Any")) {
					setCardList(Classes.HUNTER, "", spinner.getSelectedItem()
							.toString());
				} else {
					setCardList(Classes.HUNTER, cardListFrag.spinnerMechanic
							.getSelectedItem().toString(), spinner
							.getSelectedItem().toString());
				}
				break;
			case 2:
				if (cardListFrag.spinnerMechanic.getSelectedItem().toString()
						.equals("Any")) {
					setCardList(Classes.MAGE, "", spinner.getSelectedItem()
							.toString());
				} else {
					setCardList(Classes.MAGE, cardListFrag.spinnerMechanic
							.getSelectedItem().toString(), spinner
							.getSelectedItem().toString());
				}
				break;
			case 3:
				if (cardListFrag.spinnerMechanic.getSelectedItem().toString()
						.equals("Any")) {
					setCardList(Classes.PALADIN, "", spinner.getSelectedItem()
							.toString());
				} else {
					setCardList(Classes.PALADIN, cardListFrag.spinnerMechanic
							.getSelectedItem().toString(), spinner
							.getSelectedItem().toString());
				}
				break;
			case 4:
				if (cardListFrag.spinnerMechanic.getSelectedItem().toString()
						.equals("Any")) {
					setCardList(Classes.PRIEST, "", spinner.getSelectedItem()
							.toString());
				} else {
					setCardList(Classes.PRIEST, cardListFrag.spinnerMechanic
							.getSelectedItem().toString(), spinner
							.getSelectedItem().toString());
				}
				break;
			case 5:
				if (cardListFrag.spinnerMechanic.getSelectedItem().toString()
						.equals("Any")) {
					setCardList(Classes.ROGUE, "", spinner.getSelectedItem()
							.toString());
				} else {
					setCardList(Classes.ROGUE, cardListFrag.spinnerMechanic
							.getSelectedItem().toString(), spinner
							.getSelectedItem().toString());
				}
				break;
			case 6:
				if (cardListFrag.spinnerMechanic.getSelectedItem().toString()
						.equals("Any")) {
					setCardList(Classes.SHAMAN, "", spinner.getSelectedItem()
							.toString());
				} else {
					setCardList(Classes.SHAMAN, cardListFrag.spinnerMechanic
							.getSelectedItem().toString(), spinner
							.getSelectedItem().toString());
				}
				break;
			case 7:
				if (cardListFrag.spinnerMechanic.getSelectedItem().toString()
						.equals("Any")) {
					setCardList(Classes.WARLOCK, "", spinner.getSelectedItem()
							.toString());
				} else {
					setCardList(Classes.WARLOCK, cardListFrag.spinnerMechanic
							.getSelectedItem().toString(), spinner
							.getSelectedItem().toString());
				}
				break;
			case 8:
				if (cardListFrag.spinnerMechanic.getSelectedItem().toString()
						.equals("Any")) {
					setCardList(Classes.WARRIOR, "", spinner.getSelectedItem()
							.toString());
				} else {
					setCardList(Classes.WARRIOR, cardListFrag.spinnerMechanic
							.getSelectedItem().toString(), spinner
							.getSelectedItem().toString());
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
	 * Handy little function to set the card list. This is called when you select
	 * any class other than "Any" from the Spinner
	 */
	private void setCardList(Classes classes, String selectedItem,
			String manaText) {

		int manaCost = -1;

		if (cardListFrag.mSearchView != null) {
			query = cardListFrag.mSearchView.getQuery().toString()
					.toLowerCase();
		} else {
			query = "";
		}

		if (!manaText.equals("All") && !manaText.equals("7+")) {
			manaCost = Integer.parseInt(manaText);
		}

		cardListFrag.cardList.clear();
		for (Cards card : cardListFrag.cards) {

			if (manaText.equals("All") && card.getDescription() != null
					&& card.getClasss() != null
					&& card.getClasss().intValue() == classes.getValue()
					&& card.getDescription().contains(selectedItem)
					&& card.getName().toLowerCase().contains(query)
					&& !card.getName().equals(classes.getHeroName())) {
				cardListFrag.cardList.add(card);

			} else if (manaCost >= 0 && manaCost < 7
					&& card.getDescription() != null
					&& card.getClasss() != null
					&& card.getClasss().intValue() == classes.getValue()
					&& card.getDescription().contains(selectedItem)
					&& card.getName().toLowerCase().contains(query)
					&& !card.getName().equals(classes.getHeroName())
					&& card.getCost().intValue() == manaCost) {
				cardListFrag.cardList.add(card);

			} else if (manaText.equals("7+") && card.getDescription() != null
					&& card.getClasss() != null
					&& card.getClasss().intValue() == classes.getValue()
					&& card.getDescription().contains(selectedItem)
					&& card.getName().toLowerCase().contains(query)
					&& !card.getName().equals(classes.getHeroName())
					&& card.getCost().intValue() >= 7) {
				cardListFrag.cardList.add(card);
			}
			
			if (manaText.equals("All")
					&& cardListFrag.includeNeutralCards.isChecked()
					&& card.getClasss() == null
					&& card.getDescription() != null
					&& card.getDescription().contains(selectedItem)
					&& card.getName().toLowerCase().contains(query)
					&& !card.getName().equals(classes.getHeroName())) {
				cardListFrag.cardList.add(card);
			} else if (manaCost >= 0 && manaCost < 7
					&& cardListFrag.includeNeutralCards.isChecked()
					&& card.getClasss() == null
					&& card.getDescription() != null
					&& card.getDescription().contains(selectedItem)
					&& card.getName().toLowerCase().contains(query)
					&& !card.getName().equals(classes.getHeroName())
					&& card.getCost().intValue() == manaCost) {
				cardListFrag.cardList.add(card);
			} else if (manaText.equals("7+")
					&& cardListFrag.includeNeutralCards.isChecked()
					&& card.getClasss() == null
					&& card.getDescription() != null
					&& card.getDescription().contains(selectedItem)
					&& card.getName().toLowerCase().contains(query)
					&& !card.getName().equals(classes.getHeroName())
					&& card.getCost().intValue() >= 7) {
				cardListFrag.cardList.add(card);
			}
		}
		Collections.sort(cardListFrag.cardList, new CardComparator(
				cardListFrag.spinnerSort.getSelectedItemPosition(),
				cardListFrag.cbReverse.isChecked()));

		cardListFrag.adapter.notifyDataSetChanged();
		cardListFrag.adapter2.notifyDataSetChanged();
	}

	private void setCardList(Classes classes) {
		if (cardListFrag.mSearchView != null) {
			query = cardListFrag.mSearchView.getQuery().toString()
					.toLowerCase();
		} else {
			query = "";
		}
		// Clear the current ArrayList so we can repopulate it
		cardListFrag.cardList.clear();
		// Repopulate the card list with class cardListFrag.cards
		for (Cards card : cardListFrag.cards) {
			if (card.getClasss() != null
					&& card.getClasss().intValue() == classes.getValue()
					&& card.getName().toLowerCase().contains(query)
					&& !card.getName().equals(classes.getHeroName())) {
				cardListFrag.cardList.add(card);
			}
			if (cardListFrag.includeNeutralCards.isChecked()
					&& card.getName().toLowerCase().contains(query)
					&& card.getClasss() == null) {
				cardListFrag.cardList.add(card);
			}

		}

		Collections.sort(cardListFrag.cardList, new CardComparator(
				cardListFrag.spinnerSort.getSelectedItemPosition(),
				cardListFrag.cbReverse.isChecked()));
		cardListFrag.adapter.notifyDataSetChanged();
		cardListFrag.adapter2.notifyDataSetChanged();
	}

	/*
	 * Handy little function to set the default card list This is called when
	 * you select "Any" from the Spinner Overloaded version of previous function
	 */
	private void setCardList() {
		if (cardListFrag.mSearchView != null) {
			query = cardListFrag.mSearchView.getQuery().toString()
					.toLowerCase();
		} else {
			query = "";
		}
		// Clear the current ArrayList so we can repopulate it
		Classes[] classArray = new Classes[] { Classes.DRUID, Classes.HUNTER,
				Classes.MAGE, Classes.PALADIN, Classes.PRIEST, Classes.ROGUE,
				Classes.SHAMAN, Classes.WARLOCK, Classes.WARRIOR };

		if (!cardListFrag.cardList.isEmpty()) {
			cardListFrag.cardList.clear();
		}

		// Repopulate the card list with class cardListFrag.cards
		for (Cards card : cardListFrag.cards) {
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
				cardListFrag.cardList.add(card);
			}
			if (cardListFrag.includeNeutralCards.isChecked()
					&& card.getName().toLowerCase().contains(query)
					&& card.getClasss() == null) {
				cardListFrag.cardList.add(card);
			}
		}
		Collections.sort(cardListFrag.cardList, new CardComparator(
				cardListFrag.spinnerSort.getSelectedItemPosition(),
				cardListFrag.reverse));
		cardListFrag.adapter.notifyDataSetChanged();
		cardListFrag.adapter2.notifyDataSetChanged();
	}
}
