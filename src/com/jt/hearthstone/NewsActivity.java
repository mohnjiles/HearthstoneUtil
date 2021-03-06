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

import android.R.integer;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class NewsActivity extends HearthstoneActivity {

	private ListView lvNews;
	private List<String> articleUrls;
	private List<String> newsTitles;
	private List<String> imageUrls;
	private DrawerLayout drawerLayout;
	private MessageBar mBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("News");

		lvNews = findById(this, R.id.lvNews);
		drawerLayout = findById(this, R.id.drawerLayout);
		mBar = new MessageBar(this);

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
							startActivity(new Intent(NewsActivity.this,
									CardListActivity.class));
							break;
						case 1:
							startActivity(new Intent(NewsActivity.this,
									DeckGuides.class));
							break;
						case 2:
							drawerLayout.closeDrawers();
							break;
						case 3:
							startActivity(new Intent(NewsActivity.this,
									ArenaSimulator.class));
							break;
						case 4:
							startActivity(new Intent(NewsActivity.this,
									DeckSelector.class));
							break;
						}
					}
				});

		if (savedInstanceState != null) {

			newsTitles = savedInstanceState.getStringArrayList("newsTitles");
			imageUrls = savedInstanceState.getStringArrayList("imageUrls");
			articleUrls = savedInstanceState.getStringArrayList("articleUrls");

			NewsListAdapter adapter = new NewsListAdapter(NewsActivity.this,
					newsTitles.size(), newsTitles, imageUrls);
			lvNews.setAdapter(adapter);
			lvNews.setCacheColorHint(Color.TRANSPARENT);

			lvNews.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Intent intent = new Intent(NewsActivity.this,
							NewsDetailActivity.class);
					intent.putExtra("url", articleUrls.get(arg2));
					intent.putExtra("title", newsTitles.get(arg2));
					startActivity(intent);

				}
			});
		} else {
			// Launch Asynctask to load article list
			new FetchNews(this).execute();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putStringArrayList("articleUrls",
				(ArrayList<String>) articleUrls);
		outState.putStringArrayList("newsTitles",
				(ArrayList<String>) newsTitles);
		outState.putStringArrayList("imageUrls", (ArrayList<String>) imageUrls);

		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.news, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			Utils.navigateUp(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class FetchNews extends AsyncTask<Void, Void, Document> {

		private Context c;
		private ProgressDialog dialog;

		public FetchNews(Context c) {
			this.c = c;
			dialog = new ProgressDialog(c);
		}

		@Override
		protected Document doInBackground(Void... params) {
			Document doc = null;
			try {

				// Pretend we're Google Chrome & grab the HTML doc

				doc = Jsoup
						.connect("http://us.battle.net/hearthstone/en/blog/")
						.timeout(5000)
						.userAgent(
								"Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
						.get();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return doc;
		}

		@Override
		protected void onPreExecute() {

			// Show Loading dialog
			dialog = ProgressDialog.show(c, "", "Loading news...");
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Document result) {

			dialog.cancel();

			Elements titles = null;
			Elements images = null;
			Elements urls = null;
			newsTitles = new ArrayList<String>();
			imageUrls = new ArrayList<String>();
			articleUrls = new ArrayList<String>();

			if (result != null) {

				/*
				 * Before we parse the data, need to remove a few things to make
				 * sure we get the right data
				 */

				/*
				 * Remove featured articles container so we don't get duplicate
				 * links
				 */
				result.select("div.featured-news-container").remove();

				/*
				 * Remove all links that link to the comments, because those
				 * will also be duplicates
				 */
				result.select("a.comments-link").remove();

				// Grab the article titles, image URLs, and article URLs
				titles = result.select("span.article-title");
				images = result.select("div.article-image");
				urls = result.select("a[itemprop=url]");

				for (Element e : urls) {

					/*
					 * Add links to List if the URL is actually a blog post (URL
					 * contains "blog/1"). Additional check to make sure we only
					 * get the links we want.
					 */
					if (e.attr("href").contains("blog/1")) {
						if (e.attr("href").contains("http")) {
							articleUrls.add(e.attr("href"));
						} else {
							articleUrls.add("http://us.battle.net"
									+ e.attr("href"));
						}
						Log.w("URLS", "url: " + e.attr("href"));
					}

				}

				for (Element e : images) {

					// Replace html with prefix for our image URL
					String imageUrl = (e.attr("style").toString()).replace(
							"background-image:url(//", "http://").replace(")",
							"");
					imageUrls.add(imageUrl);
				}

				for (Element e : titles) {
					// Simply read the text of the tag and add it to the List
					newsTitles.add(e.text());
				}
			} else {
				// If we failed to load the web page, inform the user
				mBar.show("Failed to load news. Check your internet connection and try again later");
			}

			NewsListAdapter adapter = new NewsListAdapter(NewsActivity.this,
					newsTitles.size(), newsTitles, imageUrls);
			lvNews.setAdapter(adapter);
			lvNews.setCacheColorHint(Color.TRANSPARENT);

			lvNews.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Intent intent = new Intent(NewsActivity.this,
							NewsDetailActivity.class);
					intent.putExtra("url", articleUrls.get(arg2));
					intent.putExtra("title", newsTitles.get(arg2));
					startActivity(intent);

				}
			});
		

			super.onPostExecute(result);
		}
	}

}
