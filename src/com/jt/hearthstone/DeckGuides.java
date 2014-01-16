package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

public class DeckGuides extends ActionBarActivity {

	private ListView lvDecks;
	private Spinner spinClass;
	List<Classes> classes = new ArrayList<Classes>();
	List<String> deckNames = new ArrayList<String>();
	List<String> deckLinks = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deck_guides);

		lvDecks = findById(this, R.id.lvDecks);
		spinClass = findById(this, R.id.spinClass);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Deck Guides");

		String[] classNames = getResources().getStringArray(R.array.Classes);
		CustomArrayAdapter spinAdapter = new CustomArrayAdapter(this,
				R.layout.spinner_row, R.id.name, classNames);
		spinAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row);

		spinClass.setAdapter(spinAdapter);

		spinClass
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						deckNames.clear();
						deckLinks.clear();
						classes.clear();

						switch (arg2) {
						case 0:
							new FetchDecks(DeckGuides.this)
									.execute("http://www.hearthpwn.com/decks?filter-class=0");
							break;
						case 1:
							new FetchDecks(DeckGuides.this)
									.execute("http://www.hearthpwn.com/decks?filter-class=2");
							break;
						case 2:
							new FetchDecks(DeckGuides.this)
									.execute("http://www.hearthpwn.com/decks?filter-class=3");
							break;
						case 3:
							new FetchDecks(DeckGuides.this)
									.execute("http://www.hearthpwn.com/decks?filter-class=4");
							break;
						case 4:
							new FetchDecks(DeckGuides.this)
									.execute("http://www.hearthpwn.com/decks?filter-class=5");
							break;
						case 5:
							new FetchDecks(DeckGuides.this)
									.execute("http://www.hearthpwn.com/decks?filter-class=6");
							break;
						case 6:
							new FetchDecks(DeckGuides.this)
									.execute("http://www.hearthpwn.com/decks?filter-class=7");
							break;
						case 7:
							new FetchDecks(DeckGuides.this)
									.execute("http://www.hearthpwn.com/decks?filter-class=8");
							break;
						case 8:
							new FetchDecks(DeckGuides.this)
									.execute("http://www.hearthpwn.com/decks?filter-class=9");
							break;
						case 9:
							new FetchDecks(DeckGuides.this)
									.execute("http://www.hearthpwn.com/decks?filter-class=10");
							break;

						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

		lvDecks.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Intent intent = new Intent(DeckGuides.this,
						DeckGuideDetail.class);
				intent.putExtra("url",
						"http://www.hearthpwn.com" + deckLinks.get(arg2));
				intent.putExtra("deckName", deckNames.get(arg2));
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deck_guides, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case android.R.id.home:
			// When the back button on the ActionBar is pressed, go up one
			// Activity
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class FetchDecks extends AsyncTask<String, Void, Document> {

		Document doc = null;
		Elements elements = null;
		Elements moreElements = null;
		private Context cxt;
		private ProgressDialog dialog;

		private FetchDecks(Context cxt) {
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
			dialog = ProgressDialog.show(cxt, "", "Loading guides...", true);
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Document result) {
			dialog.cancel();
			SparseArray<String> sparseRatings = new SparseArray<String>();

			if (result == null) {
				Crouton.makeText(
						DeckGuides.this,
						"Failed to load guides. Check your internet connectoin and try again later",
						Style.ALERT).show();
				return;
			}

			elements = result.select("a[class]");
			moreElements = result.select("tr[class~=(even|odd)] > td");

			int i = 0;
			for (Element e : moreElements) {
				if (e.className().equals("")) {
					sparseRatings.put(i, e.text());
					i++;
				}
			}

			Log.w("elements", "wat  " + elements.text());

			if (elements != null) {
				for (Element e : elements) {
					String classs = e.attr("class");
					String link = e.attr("href");
					String deckName = e.text();
					if (classs.contains("hunter")) {
						classes.add(Classes.HUNTER);
						deckNames.add(deckName);
						deckLinks.add(link);
					} else if (classs.contains("druid")) {
						classes.add(Classes.DRUID);
						deckNames.add(deckName);
						deckLinks.add(link);
					} else if (classs.contains("mage")) {
						classes.add(Classes.MAGE);
						deckNames.add(deckName);
						deckLinks.add(link);
					} else if (classs.contains("paladin")) {
						classes.add(Classes.PALADIN);
						deckNames.add(deckName);
						deckLinks.add(link);
					} else if (classs.contains("priest")) {
						classes.add(Classes.PRIEST);
						deckNames.add(deckName);
						deckLinks.add(link);
					} else if (classs.contains("rogue")) {
						classes.add(Classes.ROGUE);
						deckNames.add(deckName);
						deckLinks.add(link);
					} else if (classs.contains("shaman")) {
						classes.add(Classes.SHAMAN);
						deckNames.add(deckName);
						deckLinks.add(link);
					} else if (classs.contains("warlock")) {
						classes.add(Classes.WARLOCK);
						deckNames.add(deckName);
						deckLinks.add(link);
					} else if (classs.contains("warrior")) {
						classes.add(Classes.WARRIOR);
						deckNames.add(deckName);
						deckLinks.add(link);
					}
				}

				lvDecks.setAdapter(new GuideListAdapter(DeckGuides.this,
						deckNames.size(), deckNames, classes, sparseRatings));
				Log.w("Fetch Guides", "Done fetching guides");
			} else {
				Log.w("Load failed!", "Loading guides failed!");
			}
			super.onPostExecute(result);
		}

	}

}
