package com.jt.hearthstone;

import java.util.Collections;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;

@SuppressLint("DefaultLocale")
public class CustomSearchListener implements SearchView.OnQueryTextListener {

	private CardListFragment cardListFrag;

	public CustomSearchListener(FragmentActivity activity) {

		cardListFrag = (CardListFragment) activity.getSupportFragmentManager()
				.findFragmentByTag(Utils.makeFragmentName(R.id.pager, 0));

	}

	public boolean onQueryTextChange(String newText) {
		if (!cardListFrag.cardList.isEmpty()) {
			cardListFrag.cardList.clear();
		}
		switch (cardListFrag.deckClasses.get(cardListFrag.deckListPos)) {
		case 0:
			setCardList(newText, Classes.DRUID);
			return false;
		case 1:
			setCardList(newText, Classes.HUNTER);
			return false;
		case 2:
			setCardList(newText, Classes.MAGE);
			return false;
		case 3:
			setCardList(newText, Classes.PALADIN);
			return false;
		case 4:
			setCardList(newText, Classes.PRIEST);
			return false;
		case 5:
			setCardList(newText, Classes.ROGUE);
			return false;
		case 6:
			setCardList(newText, Classes.SHAMAN);
			return false;
		case 7:
			setCardList(newText, Classes.WARLOCK);
			return false;
		case 8:
			setCardList(newText, Classes.WARRIOR);
			return false;
		default:
			setCardList(newText);
			return false;
		}

	}

	public boolean onQueryTextSubmit(String query) {
		MenuItemCompat.collapseActionView(cardListFrag.searchItem);
		return true;
	}

	public boolean onClose() {
		return false;
	}

	protected boolean isAlwaysExpanded() {
		return false;
	}

	private void setCardList(String newText) {
		String searchText = newText.toLowerCase();
		String mechanic = cardListFrag.spinnerMechanic.getSelectedItem().toString();
		for (Cards card : cardListFrag.cards) {
			if (card.getName().toLowerCase().contains(searchText)
					&& !mechanic.equals("Any") && card.getDescription() != null
					&& card.getDescription().equals(mechanic)) {
				cardListFrag.cardList.add(card);
			} else if (card.getName().toLowerCase().contains(searchText)
					&& mechanic.equals("Any")) {
				cardListFrag.cardList.add(card);
			}
		}
		Collections.sort(cardListFrag.cardList,
				new CardComparator(cardListFrag.spinnerSort.getSelectedItemPosition(),
						cardListFrag.cbReverse.isChecked()));
		cardListFrag.adapter.notifyDataSetChanged();
		cardListFrag.adapter2.notifyDataSetChanged();
		cardListFrag.grid.setAdapter(cardListFrag.adapter);
		cardListFrag.listCards.setAdapter(cardListFrag.adapter2);
	}

	private void setCardList(String newText, Classes clazz) {
		String searchText = null;
		String mechanic = cardListFrag.spinnerMechanic.getSelectedItem().toString();
		if (newText != null) {
			searchText = newText.toLowerCase();
		}
		for (Cards card : cardListFrag.cards) {
			if (card.getClasss() != null
					&& card.getClasss().intValue() == clazz.getValue()
					&& !mechanic.equals("Any") && card.getDescription() != null
					&& card.getDescription().contains(mechanic)
					&& card.getName().toLowerCase().contains(searchText)) {
				cardListFrag.cardList.add(card);
			} else if (card.getClasss() != null
					&& card.getClasss().intValue() == clazz.getValue()
					&& mechanic.equals("Any")
					&& card.getName().toLowerCase().contains(searchText)) {
				cardListFrag.cardList.add(card);
			}
			if (cardListFrag.includeNeutralCards.isChecked()) {
				if (card.getClasss() == null
						&& card.getName().toLowerCase().contains(searchText)
						&& !mechanic.equals("Any")
						&& card.getDescription() != null
						&& card.getDescription().contains(mechanic)) {
					cardListFrag.cardList.add(card);
				} else if (card.getClasss() == null
						&& card.getName().toLowerCase().contains(searchText)
						&& mechanic.equals("Any")) {
					cardListFrag.cardList.add(card);
				}
			}

		}
		Collections.sort(cardListFrag.cardList,
				new CardComparator(cardListFrag.spinnerSort.getSelectedItemPosition(),
						cardListFrag.cbReverse.isChecked()));
		cardListFrag.adapter.notifyDataSetChanged();
		cardListFrag.adapter2.notifyDataSetChanged();
		cardListFrag.grid.setAdapter(cardListFrag.adapter);
		cardListFrag.listCards.setAdapter(cardListFrag.adapter2);
	}
}
