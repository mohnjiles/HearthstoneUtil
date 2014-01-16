package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {
	private Button btnCardList;
	private Button btnClasses;
	private Button btnDeckBuilder;
	private Button btnArena;
	private Button btnNews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set main view to Activity_Main layout
		setContentView(R.layout.activity_main);

		// Show ActionBar (Title bar)
		getSupportActionBar().show();

		// Set ActionBar Title
		getSupportActionBar().setTitle("Hearthstone Companion");

		Typeface font = TypefaceCache.get(getAssets(),
				"fonts/belwebd.ttf");
		
		btnCardList = findById(this, R.id.btnCardList);
		btnClasses = findById(this, R.id.btnClasses);
		btnDeckBuilder = findById(this, R.id.btnDeckBuilder);
		btnArena = findById(this, R.id.btnArena);
		btnNews = findById(this, R.id.btnNews);

		btnCardList.setTypeface(font);
		btnClasses.setTypeface(font);
		btnDeckBuilder.setTypeface(font);
		btnArena.setTypeface(font);
		btnNews.setTypeface(font);
		btnCardList.setShadowLayer(1, 1, 1, Color.WHITE);
		btnClasses.setShadowLayer(1, 1, 1, Color.WHITE);
		btnDeckBuilder.setShadowLayer(1, 1, 1, Color.WHITE);
		btnArena.setShadowLayer(1, 1, 1, Color.WHITE);
		btnNews.setShadowLayer(1, 1, 1, Color.WHITE);
		Log.w("Before async", "before async");
		new CheckVersion().execute();
		Log.w("After async", "after async");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(MainActivity.this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void cardListPressed(View view) {
		Intent intent = new Intent(MainActivity.this, CardListActivity.class);
		startActivity(intent);
	}

	public void classesPressed(View view) {
		startActivity(new Intent(MainActivity.this, DeckGuides.class));
	}
	
	public void arenaPressed(View view) {
		startActivity(new Intent(MainActivity.this, ArenaSimulator.class));
	}

	public void deckSelectorPressed(View view) {
		startActivity(new Intent(MainActivity.this, DeckSelector.class));
	}
	
	public void newsPressed(View view) {
		startActivity(new Intent(MainActivity.this, NewsActivity.class));
	}
	
	private class CheckVersion extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// Create a new HTTP Client
			DefaultHttpClient defaultClient = new DefaultHttpClient();
			// Setup the get request
			HttpGet httpGetRequest = new HttpGet(
					"http://54.224.222.135/version.txt");

			HttpResponse httpResponse = null;
			BufferedReader reader = null;
			String version = "";

			try {
				httpResponse = defaultClient.execute(httpGetRequest);
				reader = new BufferedReader(new InputStreamReader(httpResponse
						.getEntity().getContent(), "UTF-8"));
				version = reader.readLine();
				
				FileInputStream inStream = openFileInput("version.txt");
				Writer writer = new StringWriter();
				char[] buffer = new char[1024];
				try {
					Reader readerz = new BufferedReader(new InputStreamReader(inStream,
							"UTF-8"));
					int n;
					while ((n = readerz.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} catch (FileNotFoundException e) {
					OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
							openFileOutput("version.txt", Context.MODE_PRIVATE));
					outputStreamWriter.write(version);
					outputStreamWriter.close();
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						inStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return version;
		}

		@Override
		protected void onPostExecute(String result) {
			String version = null;
			try {
				FileInputStream fis = openFileInput("version.txt");
				Writer writer = new StringWriter();
				char[] buffer = new char[1024];
				try {
					Reader reader = new BufferedReader(new InputStreamReader(fis,
							"UTF-8"));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						fis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// The json String from the file
				version = writer.toString();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (!result.equals(version)) {
				try {
					OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
							openFileOutput("version.txt", Context.MODE_PRIVATE));
					outputStreamWriter.write(result);
					
					Log.w("Server Version", "Server version: " + result);
					Log.w("Current Version", "Current Version: " + version);
					Log.i("updating version.txt", "updating version.txt");
					
					outputStreamWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				
				new UpdateJson().execute();
			}
		}
		

	}
	
	private class UpdateJson extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			// Create a new HTTP Client
			DefaultHttpClient defaultClient = new DefaultHttpClient();
			// Setup the get request
			HttpGet httpGetRequest = new HttpGet("http://54.224.222.135/cards.json");

			// Execute the request in the client
			HttpResponse httpResponse = null;
			BufferedReader reader = null;

			try {
				httpResponse = defaultClient.execute(httpGetRequest);
				reader = new BufferedReader(new InputStreamReader(httpResponse
						.getEntity().getContent(), "UTF-8"));
				String json = reader.readLine();
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
						openFileOutput("cards.json", Context.MODE_PRIVATE));
				outputStreamWriter.write(json);
				Log.i("updating cards.json", "updating cards.json");
				outputStreamWriter.close();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			return null;
		}
	}
}