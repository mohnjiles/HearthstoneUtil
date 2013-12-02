package com.jt.hearthstone;

import java.util.Collections;
import java.util.List;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SearchView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

public class CustomOnCheckedChangeListener implements OnCheckedChangeListener {

	private FragmentActivity activity;
	private String query;

	public CustomOnCheckedChangeListener(FragmentActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		CardListFragment cardListFrag = (CardListFragment) activity
				.getSupportFragmentManager().findFragmentByTag(
						Utils.makeFragmentName(R.id.pager, 0));

		if (cardListFrag.mSearchView != null) {
			query = cardListFrag.mSearchView.getQuery().toString()
					.toLowerCase();
		} else {
			query = "";
		}

		switch (buttonView.getId()) {
		case R.id.cbGenerics:
			if (isChecked) {
				String mechanic = cardListFrag.spinnerMechanic
						.getSelectedItem().toString();
				for (Cards card : cardListFrag.cards) {
					if (card.getClasss() == null
							&& card.getName().toLowerCase().contains(query)
							&& !mechanic.equals("Any")
							&& card.getDescription() != null
							&& card.getDescription().contains(mechanic)) {

						cardListFrag.cardList.add(card);
					} else {
						if (card.getClasss() == null && mechanic.equals("Any")
								&& card.getName().toLowerCase().contains(query)) {
							cardListFrag.cardList.add(card);
						}
					}
				}

				Collections.sort(cardListFrag.cardList, new CardComparator(
						cardListFrag.spinnerSort.getSelectedItemPosition(),
						cardListFrag.cbReverse.isChecked()));
				cardListFrag.adapter.notifyDataSetChanged();
				cardListFrag.adapter2.notifyDataSetChanged();
				cardListFrag.grid.setAdapter(cardListFrag.adapter);
				cardListFrag.listCards.setAdapter(cardListFrag.adapter2);

				// Otherwise, user is unchecking the box, so remove
				// all generic cards.
				// Why haven't I been using more ArrayLists in my
				// other app?????
			} else {
				for (Cards card : cardListFrag.cards) {
					if (card.getClasss() == null) {
						cardListFrag.cardList.remove(card);
					}
				}
				Collections.sort(cardListFrag.cardList, new CardComparator(
						cardListFrag.spinnerSort.getSelectedItemPosition(),
						cardListFrag.cbReverse.isChecked()));
				cardListFrag.adapter.notifyDataSetChanged();
				cardListFrag.adapter2.notifyDataSetChanged();
				cardListFrag.grid.setAdapter(cardListFrag.adapter);
				cardListFrag.listCards.setAdapter(cardListFrag.adapter2);
			}
			break;
		case R.id.cbReverse:
			cardListFrag.reverse = isChecked;
			Collections.sort(cardListFrag.cardList, new CardComparator(
					cardListFrag.spinnerSort.getSelectedItemPosition(),
					isChecked));
			cardListFrag.adapter.notifyDataSetChanged();
			cardListFrag.adapter2.notifyDataSetChanged();
			cardListFrag.grid.setAdapter(cardListFrag.adapter);
			cardListFrag.listCards.setAdapter(cardListFrag.adapter2);
			break;
		}

	}

}
