package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.simonvt.messagebar.MessageBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DeckGuides extends HearthstoneActivity implements
		AdapterView.OnItemSelectedListener {

	private ListView lvDecks;
	private Spinner spinClass;
	private List<Classes> classes = new ArrayList<Classes>();
	private List<String> deckNames = new ArrayList<String>();
	private List<String> deckLinks = new ArrayList<String>();
	private List<String> dustList = new ArrayList<String>();
	private List<String> listRatings = new ArrayList<String>();
	private GuideListAdapter adapter;

	private String[] mActivityNames;
	private ListView mDrawerList;
	private DrawerLayout drawerLayout;
	private MessageBar mBar;

	private int pageNum = 1;
	private int preLast;

	private View v;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deck_guides);

		lvDecks = findById(this, R.id.lvDecks);
		spinClass = findById(this, R.id.spinClass);
		drawerLayout = findById(this, R.id.drawerLayout);
		mBar = new MessageBar(this);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Deck Guides");

		mDrawerList = findById(this, R.id.left_drawer);
		mActivityNames = getResources().getStringArray(R.array.Drawer);
		mDrawerList.setAdapter(new NavDrawerAdapter(this,
				R.layout.sliding_list, mActivityNames));
		mDrawerList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						switch (arg2) {
						case 0:
							startActivity(new Intent(DeckGuides.this,
									CardListActivity.class));
							break;
						case 1:
							drawerLayout.closeDrawers();
							break;
						case 2:
							startActivity(new Intent(DeckGuides.this,
									NewsActivity.class));
							break;
						case 3:
							startActivity(new Intent(DeckGuides.this,
									ArenaSimulator.class));
							break;
						case 4:
							startActivity(new Intent(DeckGuides.this,
									DeckSelector.class));
							break;
						}
					}
				});

		String[] classNames = getResources().getStringArray(R.array.Classes);
		CustomArrayAdapter spinAdapter = new CustomArrayAdapter(this,
				R.layout.spinner_row, R.id.name, classNames);

		spinAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row);
		spinClass.setAdapter(spinAdapter);

		if (savedInstanceState == null) {
			spinClass.setOnItemSelectedListener(this);
		} else {

			deckNames = savedInstanceState.getStringArrayList("deckNames");
			listRatings = (List<String>) savedInstanceState
					.getSerializable("sparseRatings");
			classes = (List<Classes>) savedInstanceState
					.getSerializable("classes");
			deckLinks = savedInstanceState.getStringArrayList("deckLinks");
			dustList = savedInstanceState.getStringArrayList("dustList");

			adapter = new GuideListAdapter(DeckGuides.this, deckNames, classes,
					listRatings);
			lvDecks.setAdapter(adapter);

			spinClass.setSelection(savedInstanceState.getInt("spinnerPos"),
					false);
			spinClass.setOnItemSelectedListener(this);
		}

		lvDecks.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Intent intent = new Intent(DeckGuides.this,
						DeckGuideDetail.class);
				intent.putExtra("url",
						"http://www.hearthpwn.com" + deckLinks.get(arg2));
				intent.putExtra("deckName", deckNames.get(arg2));
				intent.putExtra("dust", dustList.get(arg2));
				startActivity(intent);
			}
		});

		v = getLayoutInflater().inflate(R.layout.list_footer, null);
		lvDecks.addFooterView(v);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deck_guides, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			// When the back button on the ActionBar is pressed, go up one
			// Activity
			Utils.navigateUp(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putStringArrayList("deckNames", (ArrayList<String>) deckNames);
		outState.putSerializable("sparseRatings",
				(ArrayList<String>) listRatings);
		outState.putSerializable("classes", (ArrayList<Classes>) classes);
		outState.putStringArrayList("deckLinks", (ArrayList<String>) deckLinks);
		outState.putStringArrayList("dustList", (ArrayList<String>) dustList);
		outState.putInt("spinnerPos", spinClass.getSelectedItemPosition());

		super.onSaveInstanceState(outState);
	}

	private class FetchDecks extends AsyncTask<String, Void, Document> {

		Document doc = null;
		Elements elements = null;
		Elements moreElements = null;
		Elements dustCost;
		private Context cxt;
		private ProgressDialog dialog;
		private String url;
		private boolean isNextPage;

		private FetchDecks(Context cxt, boolean isNextPage) {
			this.cxt = cxt;
			this.isNextPage = isNextPage;
			dialog = new ProgressDialog(cxt);
		}

		@Override
		protected Document doInBackground(String... params) {

			url = params[0].replace("&page=" + pageNum, "");
			Log.w("pageNum", "pageNum = " + pageNum);

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

			if (isNextPage) {
				v.setVisibility(View.VISIBLE);
			} else {
				dialog = ProgressDialog
						.show(cxt, "", "Loading guides...", true);
				dialog.show();
			}
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Document result) {
			dialog.cancel();
			if (result == null) {
				mBar.show("Failed to load guides. Check your internet connection and try again later");
				return;
			}

			dustCost = result.select("td.col-dust-cost");
			elements = result.select("a[class]");
			moreElements = result
					.select("div.rating-sum.rating-average.rating-average-ratingPositive");

			for (Element e : dustCost) {
				dustList.add(e.text());
			}

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

				for (Element e : moreElements) {
					listRatings.add(e.text());
				}

				if (isNextPage) {
					adapter.update(deckNames, classes, listRatings);
					v.setVisibility(View.GONE);
					//lvDecks.setSelection(firstVisibleItem + 1);

					return;
				} else {
					adapter = new GuideListAdapter(DeckGuides.this, deckNames,
							classes, listRatings);

					lvDecks.setAdapter(adapter);
				}

				lvDecks.setOnScrollListener(new OnScrollListener() {

					@Override
					public void onScroll(AbsListView view,
							int firstVisibleItem, int visibleItemCount,
							int totalItemCount) {

						final int lastItem = firstVisibleItem
								+ visibleItemCount;
						if (lastItem == totalItemCount) {
							if (preLast != lastItem) {
								pageNum++;
								Log.w("Loading next page", url + "&page="
										+ pageNum);

								new FetchDecks(DeckGuides.this, true)
										.execute(url + "&page=" + pageNum);

								preLast = lastItem;
							}
						}

					}

					@Override
					public void onScrollStateChanged(AbsListView view,
							int scrollState) {
						// TODO Auto-generated method stub

					}
				});

				Log.w("Fetch Guides", "Done fetching guides");
			} else {
				Log.w("Load failed!", "Loading guides failed!");
			}
			super.onPostExecute(result);
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

		deckNames.clear();
		deckLinks.clear();
		classes.clear();

		switch (arg2) {
		case 0:
			new FetchDecks(DeckGuides.this, false)
					.execute("http://www.hearthpwn.com/decks?filter-class=0&sort=-rating");
			break;
		case 1:
			new FetchDecks(DeckGuides.this, false)
					.execute("http://www.hearthpwn.com/decks?filter-class=4&sort=-rating");
			break;
		case 2:
			new FetchDecks(DeckGuides.this, false)
					.execute("http://www.hearthpwn.com/decks?filter-class=8&sort=-rating");
			break;
		case 3:
			new FetchDecks(DeckGuides.this, false)
					.execute("http://www.hearthpwn.com/decks?filter-class=16&sort=-rating");
			break;
		case 4:
			new FetchDecks(DeckGuides.this, false)
					.execute("http://www.hearthpwn.com/decks?filter-class=32&sort=-rating");
			break;
		case 5:
			new FetchDecks(DeckGuides.this, false)
					.execute("http://www.hearthpwn.com/decks?filter-class=64&sort=-rating");
			break;
		case 6:
			new FetchDecks(DeckGuides.this, false)
					.execute("http://www.hearthpwn.com/decks?filter-class=128&sort=-rating");
			break;
		case 7:
			new FetchDecks(DeckGuides.this, false)
					.execute("http://www.hearthpwn.com/decks?filter-class=256&sort=-rating");
			break;
		case 8:
			new FetchDecks(DeckGuides.this, false)
					.execute("http://www.hearthpwn.com/decks?filter-class=512&sort=-rating");
			break;
		case 9:
			new FetchDecks(DeckGuides.this, false)
					.execute("http://www.hearthpwn.com/decks?filter-class=1024&sort=-rating");
			break;

		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
