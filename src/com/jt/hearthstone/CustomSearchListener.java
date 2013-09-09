package com.jt.hearthstone;

import java.util.ArrayList;
import java.util.Collections;

import android.R.integer;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;

public class CustomSearchListener implements SearchView.OnQueryTextListener {
	
	private ArrayList<Cards> cardList;
	private Cards[] cards;
	private GridView grid;
	private ImageAdapter adapter;
	private MenuItem searchItem;
	private Spinner spinner = CardListActivity.spinner;
	private Spinner spinnerSort = CardListActivity.spinnerSort;
	private CustomListAdapter adapter2;
	private ListView listCards;
	private CheckBox cbGenerics = CardListActivity.includeNeutralCards;
	private CheckBox cbReverse = CardListActivity.cbReverse;
	int position = CustomOnItemSelectedListener.position;
	boolean reverse = CardListActivity.reverse;
	
	public CustomSearchListener(ArrayList<Cards> cardList, Cards[] cards, GridView grid, 
			ListView listCards, ImageAdapter adapter, CustomListAdapter adapter2, 
			MenuItem searchItem, Spinner spinner) {
		this.cardList = cardList;
		this.cards = cards;
		this.grid = grid;
		this.adapter = adapter;
		this.searchItem = searchItem;
		this.spinner = spinner;
		this.listCards = listCards;
		this.adapter2 = adapter2;
	}
	
    public boolean onQueryTextChange(String newText) {
    	if (!cardList.isEmpty()) {
			cardList.clear();
		}
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
    
    private void setCardList(String newText){
		String searchText = newText.toLowerCase();
    	for (Cards card : cards) {
    		if (card.getName().toLowerCase().contains(searchText)) {
    			cardList.add(card);
    		}
    	}
    	Collections.sort(cardList, new CardComparator(spinnerSort.getSelectedItemPosition(), cbReverse.isChecked()));
    	adapter.notifyDataSetChanged();
		adapter2.notifyDataSetChanged();
		grid.setAdapter(adapter);
		listCards.setAdapter(adapter2);
    }
    private void setCardList(String newText, Classes clazz){
    	String searchText = null;
		if (newText != null) {
			searchText = newText.toLowerCase();
		} 
    	for (Cards card : cards) {
    		if (card.getClasss() != null && card.getClasss().intValue() == clazz.getValue()) {
    			if (card.getName().toLowerCase().contains(searchText)) {
        			cardList.add(card);
        		}
    		}
    		if (cbGenerics.isChecked()) {
    			if (card.getClasss() == null) {
    				if (card.getName().toLowerCase().contains(searchText)) {
            			cardList.add(card);
            		}
    			}
    		}
    		
    	}
    	Collections.sort(cardList, new CardComparator(spinnerSort.getSelectedItemPosition(), cbReverse.isChecked()));
    	adapter.notifyDataSetChanged();
		adapter2.notifyDataSetChanged();
		grid.setAdapter(adapter);
		listCards.setAdapter(adapter2);
    }
}
