package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

public class DeckGuides extends Activity {

	private ListView lvDecks;
	private Spinner spinClass;
	List<Classes> classes = new ArrayList<Classes>();
	List<String> deckNames = new ArrayList<String>();
	List<String> deckLinks = new ArrayList<String>();
	List<String> deckCards = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deck_guides);

		lvDecks = findById(this, R.id.lvDecks);
		spinClass = findById(this, R.id.spinClass);

		String[] classNames = getResources().getStringArray(R.array.Classes);
		CustomArrayAdapter spinAdapter = new CustomArrayAdapter(this,
				R.layout.spinner_row, R.id.name, classNames);
		spinAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row);

		spinClass.setAdapter(spinAdapter);

		// Load initial deck list
		new FetchDecks()
				.execute("http://www.hearthpwn.com/decks/307-miracle-rogue");

		spinClass
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						switch (arg2) {
						case 0:
							new FetchDecks()
									.execute("http://www.hearthpwn.com/decks?filter-class=0");
							break;
						case 1:
							new FetchDecks()
									.execute("http://www.hearthpwn.com/decks?filter-class=2");
							break;
						case 2:
							new FetchDecks()
									.execute("http://www.hearthpwn.com/decks?filter-class=3");
							break;
						case 3:
							new FetchDecks()
									.execute("http://www.hearthpwn.com/decks?filter-class=4");
							break;
						case 4:
							new FetchDecks()
									.execute("http://www.hearthpwn.com/decks?filter-class=5");
							break;
						case 5:
							new FetchDecks()
									.execute("http://www.hearthpwn.com/decks?filter-class=6");
							break;
						case 6:
							new FetchDecks()
									.execute("http://www.hearthpwn.com/decks?filter-class=7");
							break;
						case 7:
							new FetchDecks()
									.execute("http://www.hearthpwn.com/decks?filter-class=8");
							break;
						case 8:
							new FetchDecks()
									.execute("http://www.hearthpwn.com/decks?filter-class=9");
							break;
						case 9:
							new FetchDecks()
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
				new FetchDecks().execute("http://www.hearthpwn.com"
						+ deckLinks.get(arg2));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deck_guides, menu);
		return true;
	}

	private class FetchDecks extends AsyncTask<String, Void, Integer> {

		Document doc = null;
		Elements elements = null;

		@Override
		protected Integer doInBackground(String... params) {
			try {
				doc = Jsoup
						.connect(params[0])
						.userAgent(
								"Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
						.get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			elements = doc.select("a[class]");
			Log.w("elements", "wat  " + elements.text());

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

			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {

			lvDecks.setAdapter(new GuideListAdapter(DeckGuides.this, deckNames
					.size(), deckNames, classes));
			Log.w("Fetch Guides", "Done fetching guides");
			super.onPostExecute(result);
		}

	}

}
