package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DeckGuideDetail extends ActionBarActivity implements
		AdapterView.OnItemSelectedListener {

	private ListView lvDeck;
	private TextView tvDust;
	private Cards[] cards;
	private String dustCost;
	private List<Cards> deckCards = new ArrayList<Cards>();
	private Intent intent;
	private Spinner spinSort;
	private String url;
	SerializableSparseArray<String> soSparse = new SerializableSparseArray<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deck_guide_detail);

		lvDeck = findById(this, R.id.lvDeck);
		spinSort = findById(this, R.id.spinSort);
		tvDust = findById(this, R.id.tvSomeText);

		intent = getIntent();

		// Get an array of every card
		cards = Utils.cards;

		// Set back button on ActionBar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Get selected deck name
		getSupportActionBar().setTitle(intent.getStringExtra("deckName"));

		// Get url from last activity
		url = intent.getStringExtra("url");
		dustCost = intent.getStringExtra("dust");

		// Load the deck selected by user in last activity
		// new FetchDeckCards(this).execute(url);

		String[] sortNames = getResources().getStringArray(R.array.Sorts);
		CustomArrayAdapter spinAdapter = new CustomArrayAdapter(this,
				R.layout.spinner_row, R.id.name, sortNames);
		spinAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row);
		spinSort.setAdapter(spinAdapter);

		if (savedInstanceState == null) {
			new FetchDeckCards(DeckGuideDetail.this).execute(url);
			spinSort.setOnItemSelectedListener(this);
		} else {
			deckCards = (ArrayList<Cards>) savedInstanceState
					.getSerializable("deckCards");
			soSparse = (SerializableSparseArray<String>) savedInstanceState
					.getSerializable("soSparse");

			lvDeck.setAdapter(new DeckGuideAdapter(DeckGuideDetail.this,
					deckCards.size(), deckCards, soSparse));

			MyWindow.setContext(DeckGuideDetail.this);

			lvDeck.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					MyWindow.setCardList(deckCards);
					MyWindow.initiatePopupWindow(arg2, arg0);

				}
			});
			
			spinSort.setSelection(savedInstanceState.getInt("spinnerPos"), false);
			spinSort.setOnItemSelectedListener(this);
		}

		ListView mDrawerList = findById(this, R.id.left_drawer);
		String[] mActivityNames = getResources().getStringArray(R.array.Drawer);
		mDrawerList.setAdapter(new NavDrawerAdapter(this,
				R.layout.sliding_list, mActivityNames));
		mDrawerList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						switch (arg2) {
						case 0:
							startActivity(new Intent(DeckGuideDetail.this,
									CardListActivity.class));
							break;
						case 1:
							Utils.navigateUp(DeckGuideDetail.this);
							break;
						case 2:
							startActivity(new Intent(DeckGuideDetail.this,
									NewsActivity.class));
							break;
						case 3:
							startActivity(new Intent(DeckGuideDetail.this,
									ArenaSimulator.class));
							break;
						case 4:
							startActivity(new Intent(DeckGuideDetail.this,
									DeckSelector.class));
							break;
						}
					}
				});

		tvDust.setText("Deck Crafting Cost: " + dustCost);
		tvDust.setTypeface(TypefaceCache.get(getAssets(), "fonts/belwebd.ttf"));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("soSparse", soSparse);
		outState.putSerializable("deckCards", (ArrayList<Cards>) deckCards);
		outState.putInt("spinnerPos", spinSort.getSelectedItemPosition());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deck_guide_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case android.R.id.home:
			// When the back button on the ActionBar is pressed, go up one
			// Activity
			Utils.navigateUp(this);
			return true;
		case R.id.action_save:
			List<String> deckList = DeckUtils.getStringList(this, "decklist");
			List<Integer> classesList = DeckUtils.getIntegerDeck(this,
					"deckclasses");
			List<Cards> tempList = new ArrayList<Cards>();

			int i = 0;

			for (Cards card : deckCards) {

				String numTimes = new StringBuilder(soSparse.get(i)).reverse()
						.toString();
				numTimes = numTimes.replace(" ", "").replace("×", "");

				if (Integer.parseInt(numTimes) == 2) {
					tempList.add(card);
				}

				i++;
			}

			for (Cards card : deckCards) {
				if (card.getClasss() != null) {
					switch (card.getClasss().intValue()) {
					case 1:
						classesList.add(8);
						break;
					case 2:
						classesList.add(3);
						break;
					case 3:
						classesList.add(1);
						break;
					case 4:
						classesList.add(5);
						break;
					case 5:
						classesList.add(4);
						break;
					case 7:
						classesList.add(6);
						break;
					case 8:
						classesList.add(2);
						break;
					case 9:
						classesList.add(7);
						break;
					case 11:
						classesList.add(0);
						break;
					}
					break;
				}

			}

			deckCards.addAll(tempList);

			deckList.add(intent.getStringExtra("deckName"));

			new DeckUtils.SaveDeck(this, intent.getStringExtra("deckName"),
					deckCards).execute();
			new DeckUtils.SaveDeck(this, "decklist", deckList).execute();
			new DeckUtils.SaveDeck(this, "deckclasses", classesList).execute();

			Crouton.makeText(this, "Deck saved", Style.INFO).show();
		}
		return super.onOptionsItemSelected(item);
	}

	private class FetchDeckCards extends AsyncTask<String, Void, Document> {

		Document doc = null;
		Elements elementz = null;
		Elements someEles = null;
		private ProgressDialog dialog;
		private Context cxt;

		private FetchDeckCards(Context cxt) {
			this.cxt = cxt;
			dialog = new ProgressDialog(cxt);
		}

		@Override
		protected Document doInBackground(String... params) {

			try {
				doc = Jsoup
						.connect(params[0])
						.timeout(5000)
						.userAgent(
								"Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
						.get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return doc;
		}

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(cxt, "",
					"Loading " + intent.getStringExtra("deckName") + "...",
					true);
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Document result) {
			soSparse.clear();
			if (dialog != null) {
				dialog.cancel();
			}
			if (result != null) {
				deckCards.clear();
				result.select("div#related").remove();
				elementz = result.select("a[class]");
				someEles = result.select("td.col-name");

				int i = 0;
				for (Element e : someEles) {
					soSparse.put(i, e.ownText());
					i++;
				}

				for (Element e : elementz) {
					if (!e.hasAttr("rel") && e.attr("class").contains("rarity")) {
						for (Cards card : cards) {
							if (card.getName().equals(e.text())) {
								deckCards.add(card);
							}
						}
					}
				}
				
				Collections.sort(deckCards, new CardComparator(1, false));
				lvDeck.setAdapter(new DeckGuideAdapter(DeckGuideDetail.this,
						deckCards.size(), deckCards, soSparse));

				MyWindow.setContext(DeckGuideDetail.this);

				lvDeck.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						MyWindow.setCardList(deckCards);
						MyWindow.initiatePopupWindow(arg2, arg0);

					}
				});
			} else {
				Crouton.makeText(
						DeckGuideDetail.this,
						"Failed to load guides. Check your internet connectoin and try again later",
						Style.ALERT).show();
			}

			super.onPostExecute(result);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		switch (arg2) {
		case 0:
			Collections.sort(deckCards, new CardComparator(1, false));
			break;
		case 1:
			Collections.sort(deckCards, new CardComparator(2, false));
			break;
		case 2:
			Collections.sort(deckCards, new CardComparator(3, false));
			break;
		case 3:
			Collections.sort(deckCards, new CardComparator(4, false));
			break;
		}
		
		lvDeck.setAdapter(new DeckGuideAdapter(DeckGuideDetail.this,
				deckCards.size(), deckCards, soSparse));

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
