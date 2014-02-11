package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DeckChanceFragment extends CustomCardFragment implements
		AdapterView.OnItemClickListener {

	private ListView lvDeck;
	List<Cards> deckList;
	private List<String> listOfDecks;
	private DeckChanceAdapter adapter;
	private List<Double> listPercents = new ArrayList<Double>();
	private Map<Cards, Integer> cardCounts = new HashMap<Cards, Integer>();
	List<Cards> deckListUnique;
	private Intent intent;
	private List<Cards> prevCards = new ArrayList<Cards>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.setClassName("DeckChanceFragment");

		View v = inflater.inflate(R.layout.deck_chance_fragment, container,
				false);

		lvDeck = findById(v, R.id.lvChanceDeck);

		intent = getActivity().getIntent();

		setHasOptionsMenu(true);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		Intent intent = getActivity().getIntent();
		listOfDecks = intent.getStringArrayListExtra("listDecks");
		new DeckUtils.GetCardsList(getActivity(), this, 1337)
				.execute(listOfDecks.get(intent.getIntExtra("position", 0)));

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.deck_chance, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			Utils.navigateUp(getActivity());
			break;
		case R.id.action_undo:
			if (prevCards.size() > 0) {
				Log.w("prevCard",
						"previous card: "
								+ prevCards.get(prevCards.size() - 1).getName());
				deckList.add(prevCards.get(prevCards.size() - 1));
				prevCards.remove(prevCards.size() - 1);
				updatePercents(deckList, true);
			} else {
				Crouton.makeText(getActivity(), "Cannot undo any further",
						Style.ALERT).show();
			}
			break;
		case R.id.action_restart:

			prevCards.clear();

			new DeckUtils.GetCardsList(getActivity(), this, R.id.action_restart)
					.execute(listOfDecks.get(intent.getIntExtra("position", 0)));

			updatePercents(deckList, true);

			break;
		case R.id.action_rename:
			DeckUtils.renameDeck(getActivity(),
					intent.getIntExtra("position", 0), getActivity(), deckList);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public void updatePercents(List<Cards> deckList, boolean updateAdapter) {

		listPercents.clear();
		cardCounts.clear();

		for (Cards card : deckList) {
			Integer current = cardCounts.get(card);
			if (current == null) {
				current = 1;
			} else {
				current++;
			}
			cardCounts.put(card, current);
		}

		deckListUnique = new ArrayList<Cards>(
				new LinkedHashSet<Cards>(deckList));

		Collections.sort(deckList, new CardComparator(2, false));
		Collections.sort(deckListUnique, new CardComparator(2, false));

		for (Cards card : deckListUnique) {
			listPercents
					.add(calcPercents(cardCounts.get(card), deckList.size()));
		}

		if (updateAdapter) {
			adapter.update(deckList, listPercents);
		}

	}

	private Double calcPercents(double timesInDeck, double deckSize) {
		return ((timesInDeck / deckSize) * 100);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		for (Iterator<Cards> it = deckList.iterator(); it.hasNext();) {
			Cards card = it.next();
			if (card.getName().equals(deckListUnique.get(arg2).getName())) {
				it.remove();
				prevCards.add(card);
				Log.w("card",
						"Card removed: "
								+ prevCards.get(prevCards.size() - 1).getName());

				break;
			}
		}
		updatePercents(deckList, true);

	}

	@Override
	protected void setCardList(List<Cards> cardList, int tag) {
		this.deckList = cardList;
		deckListUnique = new ArrayList<Cards>(
				new LinkedHashSet<Cards>(deckList));

		adapter = new DeckChanceAdapter(getActivity(), deckList, listPercents);
		lvDeck.setAdapter(adapter);
		lvDeck.setOnItemClickListener(this);

		updatePercents(cardList, true);
	}
}
