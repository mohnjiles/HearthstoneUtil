package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class NewsDetailActivity extends ActionBarActivity {

	private WebView wvDetails;
	private String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_detail);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		
		String title = intent.getStringExtra("title");
		
		getSupportActionBar().setTitle(title);
		
		wvDetails = findById(this, R.id.wvDetail);
		
		new FetchNewsDetail().execute();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.news_detail, menu);
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
	
	private class FetchNewsDetail extends AsyncTask<Void, Void, Document> {

		@Override
		protected Document doInBackground(Void... params) {
			Document doc = null;
			try {
				doc = Jsoup
						.connect(url)
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
			
			Element content = null;
			
			if (result != null) {
				result.select("img").remove();
				content = result.select("div.article-content").first();
				String html = content.toString();
				wvDetails.loadData(html, "text/html; charset=UTF-8", null);
				wvDetails.setBackgroundColor(Color.argb(1, 0, 0, 0));
			}
			super.onPostExecute(result);
		}
	}

}
