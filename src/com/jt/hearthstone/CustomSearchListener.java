package com.jt.hearthstone;

import java.util.ArrayList;
import java.util.Collections;

import android.R.integer;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Spinner;

public class CustomSearchListener implements SearchView.OnQueryTextListener {
	
	private ArrayList<Cards> cardList;
	private Cards[] cards;
	private GridView grid;
	private ImageAdapter adapter;
	private MenuItem searchItem;
	private Spinner spinner;
	
	public CustomSearchListener(ArrayList<Cards> cardList, Cards[] cards, GridView grid, 
			ImageAdapter adapter, MenuItem searchItem, Spinner spinner) {
		this.cardList = cardList;
		this.cards = cards;
		this.grid = grid;
		this.adapter = adapter;
		this.searchItem = searchItem;
		this.spinner = spinner;
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
    		
        default:
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
    	Collections.sort(cardList, new CardComparator());
		grid.setAdapter(adapter);
    }
    private void setCardList(String newText, Classes clazz){
		String searchText = newText.toLowerCase();
    	for (Cards card : cards) {
    		if (card.getName().toLowerCase().contains(searchText)) {
    			if (card.getClasss() != null && card.getClasss().equals(clazz.getValue())) {
    				cardList.add(card);
    			}
    			
    		}
    	}
    	Collections.sort(cardList, new CardComparator());
		grid.setAdapter(adapter);
    }
}
