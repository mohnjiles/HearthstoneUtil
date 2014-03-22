package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.AdapterView;
import android.widget.ListView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class NewsDetailActivity extends ActionBarActivity {

	private WebView wvDetails;
	private String url;
	private String title;
	private String html;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_detail);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();

		url = intent.getStringExtra("url");
		title = intent.getStringExtra("title");

		getSupportActionBar().setTitle(title);

		wvDetails = findById(this, R.id.wvDetail);

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
							startActivity(new Intent(NewsDetailActivity.this,
									CardListActivity.class));
							break;
						case 1:
							startActivity(new Intent(NewsDetailActivity.this,
									DeckGuides.class));
							break;
						case 2:
							Utils.navigateUp(NewsDetailActivity.this);
							break;
						case 3:
							startActivity(new Intent(NewsDetailActivity.this,
									ArenaSimulator.class));
							break;
						case 4:
							startActivity(new Intent(NewsDetailActivity.this,
									DeckSelector.class));
							break;
						}
					}
				});

		if (savedInstanceState != null) {

			html = savedInstanceState.getString("html");

			if (html == null) {
				new FetchNewsDetail(this).execute();
			}

			wvDetails.loadData(html, "text/html; charset=UTF-8", null);
			
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
				wvDetails.setBackgroundColor(Color.argb(1, 0, 0, 0));
			} else {
				wvDetails.setBackgroundColor(0x00000000);
			}
			
			int screenSize = getResources().getConfiguration().screenLayout
					& Configuration.SCREENLAYOUT_SIZE_MASK;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
					&& screenSize >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
				wvDetails.getSettings().setTextZoom(150);
			}

		} else {
			new FetchNewsDetail(this).execute();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.news_detail, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putString("html", html);

		super.onSaveInstanceState(outState);
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

	private class FetchNewsDetail extends AsyncTask<Void, Void, Document> {
		private Context c;
		private ProgressDialog dialog;

		public FetchNewsDetail(Context c) {
			this.c = c;
			dialog = new ProgressDialog(c);
		}

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
		protected void onPreExecute() {
			dialog = ProgressDialog.show(c, "", "Loading " + title + "...");
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Document result) {
			dialog.cancel();
			Element content = null;

			if (result != null) {

				Log.w("url", url);

				result.select("img[itemprop=image]").remove();
				content = result.select("div.article-content").first();
				html = content.toString().replace("'", "&apos;");
				wvDetails.loadData(html, "text/html; charset=UTF-8", null);
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
					wvDetails.setBackgroundColor(Color.argb(1, 0, 0, 0));
				} else {
					wvDetails.setBackgroundColor(0x00000000);
				}
				wvDetails.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

			} else {
				Crouton.makeText(
						NewsDetailActivity.this,
						"Failed to load article. Check your internet connectoin and try again later",
						Style.ALERT).show();
			}
			super.onPostExecute(result);
		}
	}

}
