package com.jt.hearthstone;

import java.util.ArrayList;
import java.util.Collections;

import static butterknife.Views.findById;

import android.annotation.SuppressLint;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;

@SuppressLint("DefaultLocale")
public class SearchListener2 implements SearchView.OnQueryTextListener {

	private ArrayList<Cards> cardList;
	private Cards[] cards;
	private GridView grid;
	private ImageAdapter adapter;
	private MenuItem searchItem;
	private Spinner spinner;
	private Spinner spinnerSort;
	private Spinner spinnerMechanic;
	private CustomListAdapter adapter2;
	private ListView listCards;
	private CheckBox cbGenerics;
	private CheckBox cbReverse;
	static String currentText;

	public SearchListener2(ActionBarActivity activity, ArrayList<Cards> cardList, Cards[] cards,
			GridView grid, ListView listCards, ImageAdapter adapter,
			CustomListAdapter adapter2, MenuItem searchItem) {
		this.cardList = cardList;
		this.cards = cards;
		this.grid = grid;
		this.adapter = adapter;
		this.searchItem = searchItem;
		this.listCards = listCards;
		this.adapter2 = adapter2;
		
		spinner = findById(activity, R.id.spinClass);
		spinnerSort = findById(activity, R.id.spinSort);
		spinnerMechanic = findById(activity, R.id.spinnerMechanic);
		cbGenerics = findById(activity, R.id.cbGenerics);
		cbReverse = findById(activity, R.id.cbReverse);
	}

	public boolean onQueryTextChange(String newText) {
		if (!cardList.isEmpty()) {
			cardList.clear();
		}
		currentText = newText;
		switch (spinner.getSelectedItemPosition()) {
		case 0:
			setCardList(newText);
			return false;
		case 1:
			setCardList(newText, Classes.DRUID);
			return false;
		case 2:
			setCardList(newText, Classes.HUNTER);
			return false;
		case 3:
			setCardList(newText, Classes.MAGE);
			return false;
		case 4:
			setCardList(newText, Classes.PALADIN);
			return false;
		case 5:
			setCardList(newText, Classes.PRIEST);
			return false;
		case 6:
			setCardList(newText, Classes.ROGUE);
			return false;
		case 7:
			setCardList(newText, Classes.SHAMAN);
			return false;
		case 8:
			setCardList(newText, Classes.WARLOCK);
			return false;
		case 9:
			setCardList(newText, Classes.WARRIOR);
			return false;
		default:
			setCardList(newText);
			return false;
		}

	}

	public boolean onQueryTextSubmit(String query) {
		MenuItemCompat.collapseActionView(searchItem);
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
		String mechanic = spinnerMechanic.getSelectedItem().toString();
		for (Cards card : cards) {
			if (card.getName().toLowerCase().contains(searchText)
					&& !mechanic.equals("Any") && card.getDescription() != null
					&& card.getDescription().contains(mechanic)) {
				cardList.add(card);
			} else if (mechanic.equals("Any")
					&& card.getName().toLowerCase().contains(searchText)) {
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

	private void setCardList(String newText, Classes clazz) {
		String searchText = null;
		String mechanic = spinnerMechanic.getSelectedItem().toString();
		if (newText != null) {
			searchText = newText.toLowerCase();
		}
		for (Cards card : cards) {
			if (card.getClasss() != null
					&& card.getClasss().intValue() == clazz.getValue()
					&& card.getName().toLowerCase().contains(searchText)
					&& !mechanic.equals("Any") && card.getDescription() != null
					&& card.getDescription().contains(mechanic)) {
				cardList.add(card);
			} else if (card.getClasss() != null
					&& card.getClasss().intValue() == clazz.getValue()
					&& card.getName().toLowerCase().contains(searchText)
					&& mechanic.equals("Any")) {
				cardList.add(card);
			}
			if (cbGenerics.isChecked()) {
				if (card.getClasss() == null) {
					if (card.getName().toLowerCase().contains(searchText)
							&& !mechanic.equals("Any")
							&& card.getDescription() != null
							&& card.getDescription().contains(mechanic)) {
						cardList.add(card);
					} else if (card.getName().toLowerCase().contains(searchText) && mechanic.equals("Any")) {
						cardList.add(card);
					}
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
}
