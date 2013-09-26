package com.jt.hearthstone;

import java.util.Collections;
import java.util.List;

import android.support.v7.widget.SearchView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CustomOnCheckedChangeListener implements OnCheckedChangeListener {

	private Spinner spinnerSort;
	private Spinner spinnerMechanic;
	private Cards[] cards;
	private List<Cards> cardList;

	public CustomOnCheckedChangeListener(Spinner spinnerSort,
			Spinner spinnerMechanic, Cards[] cards, SearchView mSearchView,
			List<Cards> cardList) {
		this.spinnerSort = spinnerSort;
		this.spinnerMechanic = spinnerMechanic;
		this.cards = cards;
		this.cardList = cardList;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.cbGenerics:
			if (isChecked) {
				String mechanic = spinnerMechanic.getSelectedItem().toString();
				for (Cards card : cards) {
					if (card.getClasss() == null
							&& card.getName().contains(CardListFragment.mSearchView.getQuery())
							&& !mechanic.equals("Any")
							&& card.getDescription() != null
							&& card.getDescription().contains(mechanic)) {

						cardList.add(card);
					} else {
						if (card.getClasss() == null
								&& mechanic.equals("Any")
								&& card.getName().contains(
										CardListFragment.mSearchView.getQuery())) {
							cardList.add(card);
						}
					}
				}

				Collections.sort(
						cardList,
						new CardComparator(spinnerSort
								.getSelectedItemPosition(),
								CardListFragment.cbReverse.isChecked()));
				CardListFragment.adapter.notifyDataSetChanged();
				CardListFragment.adapter2.notifyDataSetChanged();
				CardListFragment.grid.setAdapter(CardListFragment.adapter);
				CardListFragment.listCards
						.setAdapter(CardListFragment.adapter2);

				// Otherwise, user is unchecking the box, so remove
				// all generic cards.
				// Why haven't I been using more ArrayLists in my
				// other app?????
			} else {
				for (Cards card : cards) {
					if (card.getClasss() == null) {
						cardList.remove(card);
					}
				}
				Collections.sort(
						cardList,
						new CardComparator(spinnerSort
								.getSelectedItemPosition(),
								CardListFragment.cbReverse.isChecked()));
				CardListFragment.adapter.notifyDataSetChanged();
				CardListFragment.adapter2.notifyDataSetChanged();
				CardListFragment.grid.setAdapter(CardListFragment.adapter);
				CardListFragment.listCards
						.setAdapter(CardListFragment.adapter2);
			}
			break;
		case R.id.cbReverse:
			CardListFragment.reverse = isChecked;
			Collections.sort(cardList, new CardComparator(
					spinnerSort.getSelectedItemPosition(),
					isChecked));
			CardListFragment.adapter.notifyDataSetChanged();
			CardListFragment.adapter2.notifyDataSetChanged();
			CardListFragment.grid.setAdapter(CardListFragment.adapter);
			CardListFragment.listCards.setAdapter(CardListFragment.adapter2);
			break;
		}

	}

}
