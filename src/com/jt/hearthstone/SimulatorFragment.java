package com.jt.hearthstone;

import static butterknife.Views.findById;

import icepick.Icepick;
import icepick.Icicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.simonvt.messagebar.MessageBar;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SimulatorFragment extends CustomCardFragment {

	@Icicle private GridView gvCards;
	@Icicle private Button btnRedraw;
	@Icicle private Button btnDrawAnother;
	@Icicle private Spinner spinnerNumCards;
	@Icicle private TextView tvStartingSize;

	@Icicle ImageAdapter adapter;

	private List<String> listDecks;
	@Icicle
	private List<Cards> cardList;
	@Icicle
	private List<Cards> cardsToShow = new ArrayList<Cards>();

	@Icicle private int numCards;
	@Icicle private int position;

	@Icicle private Typeface font;
	@Icicle private DeckActivity deckActivity;
	
	private MessageBar mBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.setClassName("SimulatorFragment");

		View V = inflater
				.inflate(R.layout.simulator_fragment, container, false);

		gvCards = findById(V, R.id.gvCard);
		btnRedraw = findById(V, R.id.btnRedraw);
		btnDrawAnother = findById(V, R.id.btnDrawAnother);
		spinnerNumCards = findById(V, R.id.spinnerNumCards);
		tvStartingSize = findById(V, R.id.tvSomeText);
		mBar = ((DeckFragmentHolder)getActivity()).getMessageBar();

		position = getActivity().getIntent().getIntExtra("position", 0);
		listDecks = getActivity().getIntent().getStringArrayListExtra(
				"listDecks");

		setHasOptionsMenu(true);

		deckActivity = (DeckActivity) getActivity().getSupportFragmentManager()
				.findFragmentByTag(Utils.makeFragmentName(R.id.pager, 1));

		return V;
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.simulator, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_rename:
			DeckUtils.renameDeck(getActivity(), position, getActivity(),
					cardList);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Icepick.restoreInstanceState(getActivity(), savedInstanceState);
		
		font = TypefaceCache
				.get(getActivity().getAssets(), "fonts/belwebd.ttf");

		MyWindow.setContext(getActivity());

		btnRedraw.setTypeface(font);
		btnDrawAnother.setTypeface(font);
		tvStartingSize.setTypeface(font);

		if (spinnerNumCards.getSelectedItem() != null) {
			numCards = Integer.parseInt(spinnerNumCards.getSelectedItem()
					.toString());
		} else {
			numCards = 4;
		}
		if (cardsToShow.size() > 0) {
			cardsToShow.clear();
		}

		btnRedraw.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				cardsToShow.clear();
				cardList.clear();
				cardList.addAll(deckActivity.cardList);
				Collections.shuffle(cardList);
				int numToShow;

				if (spinnerNumCards.getSelectedItem() != null) {
					numToShow = Integer.parseInt(spinnerNumCards
							.getSelectedItem().toString());
				} else {
					numToShow = 4;
				}

				if (cardList.size() >= numToShow) {
					for (int i = 0; i < numToShow; i++) {
						cardsToShow.add(cardList.get(i));
					}
					gvCards.setAdapter(adapter);
				} else {
					mBar.clear();
					mBar.show("Not enough cards in the deck");
				}

			}
		});
		btnDrawAnother.setOnClickListener(new View.OnClickListener() {

			
			@Override
			public void onClick(View v) {
				if (cardsToShow.size() < cardList.size()) {
					cardsToShow.add(cardList.get(cardsToShow.size()));
					int index = gvCards.getLastVisiblePosition();
					gvCards.setAdapter(adapter);
					gvCards.setSelection(index + 1);
				} else {
					mBar.clear();
					mBar.show("No more cards");
				}
			}
		});

		new DeckUtils.GetCardsList(getActivity(), this, 0).execute(listDecks
				.get(position));

		String[] cardsToDraw = getResources().getStringArray(
				R.array.CardsToDraw);
		CustomArrayAdapter spinAdapter = new CustomArrayAdapter(getActivity(),
				R.layout.spinner_row, R.id.name, cardsToDraw);
		spinAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row);

		spinnerNumCards.setAdapter(spinAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Icepick.saveInstanceState(getActivity(), outState);

	}

	@Override
	protected void setCardList(final List<Cards> cardList, int tag) {
		this.cardList = cardList;

		if (this.cardList != null) {
			this.cardList.clear();
		} else {
			this.cardList = new ArrayList<Cards>();
		}

		this.cardList.addAll(cardList);

		Collections.shuffle(cardList);
		if (cardList.size() > numCards - 1) {
			for (int i = 0; i < numCards; i++) {
				cardsToShow.add(cardList.get(i));
			}
		}

		adapter = new ImageAdapter(getActivity(), cardsToShow);
		gvCards.setAdapter(adapter);

		gvCards.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				MyWindow.setCardList(cardsToShow);
				MyWindow.initiatePopupWindow(position, parent);
			}
		});
	}
}
