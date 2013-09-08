package com.jt.hearthstone;

import java.util.ArrayList;
import java.util.Collections;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemSelectedListener;

public class CustomOnItemSelectedListener implements OnItemSelectedListener {
	
	ArrayList<Cards> cardList;
	private Cards[] cards;
	private GridView grid;
	private ImageAdapter adapter;
	public CustomOnItemSelectedListener(ArrayList<Cards> cardList, Cards[] cards, GridView grid, ImageAdapter adapter) {
		this.cardList = cardList;
		this.cards = cards;
		this.grid = grid;
		this.adapter = adapter;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {

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
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO
	}
	
	/*
	 * Handy little function to set the card list This is called when you select
	 * any class other than "Any" from the Spinner
	 */
	private void setCardList(Classes classes) {
		// Clear the current ArrayList so we can repopulate it
		cardList.clear();
		// Repopulate the card list with class cards
		for (Cards card : cards) {
			if (card.getClasss() != null) {
				if (card.getClasss().intValue() == classes.getValue()) {
					// Ignore hero cards
					if (!card.getName().equals(classes.getHeroName())) { 
						cardList.add(card);
					}
				}
			}

		}

		Collections.sort(cardList, new CardComparator());
		grid.setAdapter(adapter);
	}

	/*
	 * Handy little function to set the default card list This is called when
	 * you select "Any" from the Spinner Overloaded version of previous function
	 */
	private void setCardList() {
		// Clear the current ArrayList so we can repopulate it
		if (cardList.isEmpty()) {
			cardList.clear();
		}
		
		// Repopulate the card list with class cards
		for (Cards card : cards) {
			cardList.add(card);
		}
		Collections.sort(cardList, new CardComparator());
		grid.setAdapter(adapter);
	}
}
