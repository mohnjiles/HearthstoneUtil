package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class NewsActivity extends ActionBarActivity {

	private ListView lvNews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("News");

		lvNews = findById(this, R.id.lvNews);

		new FetchNews().execute();
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class FetchNews extends AsyncTask<Void, Void, Document> {

		@Override
		protected Document doInBackground(Void... params) {
			Document doc = null;
			try {
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
		protected void onPostExecute(Document result) {
			Elements titles = null;
			Elements images = null;
			List<String> newsTitles = new ArrayList<String>();
			List<String> urls = new ArrayList<String>();
			if (result != null) {
				titles = result.select("span.article-title");
				images = result.select("div.article-image");

				for (Element e : images) {
					String imageUrl = (e.attr("style").toString()).replace(
							"background-image:url(//", "http://").replace(")",
							"");
					urls.add(imageUrl);
				}

				for (Element e : titles) {
					newsTitles.add(e.text());
				}
			} else {
				Crouton.makeText(
						NewsActivity.this,
						"Failed to load news. Check your internet connection and try again later",
						Style.ALERT).show();
			}

			NewsListAdapter adapter = new NewsListAdapter(NewsActivity.this,
					newsTitles.size(), newsTitles, urls);
			lvNews.setAdapter(adapter);
			super.onPostExecute(result);
		}
	}

}
