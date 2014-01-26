package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.IOException;
import java.util.ArrayList;
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

public class DeckGuideDetail extends ActionBarActivity {

	private ListView lvDeck;
	private TextView tvDust;
	private Cards[] cards;
	private String dustCost;
	private List<Cards> deckCards = new ArrayList<Cards>();
	private Intent intent;
	private Spinner spinSort;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deck_guide_detail);

		lvDeck = findById(this, R.id.lvDeck);
		spinSort = findById(this, R.id.spinSort);
		tvDust = findById(this, R.id.tvSomeText);

		intent = getIntent();

		// Get an array of every card
		cards = Utils.setupCardList();

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
		spinSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch (arg2) {
				case 0:
					new FetchDeckCards(DeckGuideDetail.this).execute(url);
					break;
				case 1:
					new FetchDeckCards(DeckGuideDetail.this).execute(url
							+ "?sort=cost");
					break;
				case 2:
					new FetchDeckCards(DeckGuideDetail.this).execute(url
							+ "?sort=atk");
					break;
				case 3:
					new FetchDeckCards(DeckGuideDetail.this).execute(url
							+ "?sort=hp");
					break;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		
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
			SparseArray<String> soSparse = new SparseArray<String>();
			if (dialog != null) {
				dialog.cancel();
			}
			if (result != null) {
				deckCards.clear();
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

				lvDeck.setAdapter(new DeckGuideAdapter(DeckGuideDetail.this,
						deckCards.size(), deckCards, soSparse));

				MyWindow.setContext(DeckGuideDetail.this);

				lvDeck.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						MyWindow.initiatePopupWindow(deckCards, arg2, arg0);

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

}
