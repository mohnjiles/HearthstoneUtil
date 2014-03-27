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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends HearthstoneActivity {
	private Button btnCardList;
	private Button btnClasses;
	private Button btnDeckBuilder;
	private Button btnArena;
	private Button btnNews;
	private CustomDrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private String[] mActivityNames;
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		getSupportActionBar().show();
		getSupportActionBar().setTitle("Hearthstone Companion");

		Typeface font = TypefaceCache.get(getAssets(), "fonts/belwebd.ttf");

		btnCardList = findById(this, R.id.btnCardList);
		btnClasses = findById(this, R.id.btnClasses);
		btnDeckBuilder = findById(this, R.id.btnDeckBuilder);
		btnArena = findById(this, R.id.btnArena);
		btnNews = findById(this, R.id.btnNews);
		mDrawerLayout = findById(this, R.id.drawerLayout);
		mDrawerList = findById(this, R.id.left_drawer);

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

		adView = findById(this, R.id.adView);

		AdRequest.Builder adRequestBuilder = new AdRequest.Builder().addTestDevice(
				AdRequest.DEVICE_ID_EMULATOR).addTestDevice(
				"6AEDAF2D22A9E9C74B304FF9FC273280");

		String android_id = Settings.Secure.getString(
				this.getContentResolver(), Settings.Secure.ANDROID_ID);
		String deviceId = md5(android_id).toUpperCase(Utils.curLocale);
		
		if (HearthstoneUtil.isDebugEnabled) {

			adRequestBuilder.addTestDevice(deviceId);
		}

		AdRequest mAdRequest = adRequestBuilder.build();
		boolean isTestDevice = mAdRequest.isTestDevice(this);

		Log.w("MainActivity Ads", "is Admob Test Device ? " + deviceId + " "
				+ isTestDevice); // to confirm it worked
		adView.loadAd(mAdRequest);

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.app_name, /* "open drawer" description */
		R.string.close_drawer /* "close drawer" description */
		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle("Hearthstone Companion");
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle("Hearthstone Companion");
			}
		};

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
							startActivity(new Intent(MainActivity.this,
									CardListActivity.class));
							break;
						case 1:
							startActivity(new Intent(MainActivity.this,
									DeckGuides.class));
							break;
						case 2:
							startActivity(new Intent(MainActivity.this,
									NewsActivity.class));
							break;
						case 3:
							startActivity(new Intent(MainActivity.this,
									ArenaSimulator.class));
							break;
						case 4:
							startActivity(new Intent(MainActivity.this,
									DeckSelector.class));
							break;
						}
					}
				});

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean isfirstTime = prefs.getBoolean("first_time_drawer", true);

		if (isfirstTime) {
			mDrawerLayout.openDrawer(mDrawerList);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("first_time_drawer", false);
			editor.commit();
		}

		new CheckVersion().execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

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
					Reader readerz = new BufferedReader(new InputStreamReader(
							inStream, "UTF-8"));
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
					Reader reader = new BufferedReader(new InputStreamReader(
							fis, "UTF-8"));
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
			HttpGet httpGetRequest = new HttpGet(
					"http://54.224.222.135/cards.json");

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

		@Override
		protected void onPostExecute(Void result) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setTitle("Restart app");
			builder.setMessage("Card data updated. App restart required for changes to take effect. Restart now?");

			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent mStartActivity = new Intent(
									MainActivity.this, MainActivity.class);
							int mPendingIntentId = 123456;
							PendingIntent mPendingIntent = PendingIntent
									.getActivity(MainActivity.this,
											mPendingIntentId, mStartActivity,
											PendingIntent.FLAG_CANCEL_CURRENT);
							AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
							mgr.set(AlarmManager.RTC,
									System.currentTimeMillis() + 100,
									mPendingIntent);
							System.exit(0);
						}
					});

			builder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

			AlertDialog dialog = builder.create();

			dialog.show();

			super.onPostExecute(result);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// Resume the AdView.
		adView.resume();
	}

	@Override
	public void onPause() {
		// Pause the AdView.
		adView.pause();

		super.onPause();
	}

	@Override
	public void onDestroy() {
		// Destroy the AdView.
		adView.destroy();

		super.onDestroy();
	}

	public static final String md5(final String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}