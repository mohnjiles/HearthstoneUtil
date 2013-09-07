package com.jt.hearthstone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Spinner;

public class MainActivity extends ActionBarActivity {

	Spinner spinner;
	GridView grid;
	Cards[] cards;
	ImageAdapter adapter;
	CheckBox includeNeutralCards;
	
	public static ImageLoader loader = ImageLoader.getInstance();
	public static ArrayList<Cards> cardList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set main view to Activity_Main layout
		setContentView(R.layout.activity_main);
		// Show ActionBar (Title bar)
		getSupportActionBar().show();
		// Set ActionBar Title
		setTitle("Hearthstone Utilities");
		
		// Start up GSON
		Gson gson = new Gson();

		// Find our views
		grid = (GridView) findViewById(R.id.cardsGrid);
		spinner = (Spinner) findViewById(R.id.spinner1);
		includeNeutralCards = (CheckBox) findViewById(R.id.checkBox1);
		
		// Create a new instance of our custom OnItemSelectedListener 
		CustomOnItemSelectedListener listener = new CustomOnItemSelectedListener();
		
		// Set the spinner (drop down selector) to listen to our custom listener
		spinner.setOnItemSelectedListener(listener);
		
		// ImageLoader config for the ImageLoader that gets our card images
		// denyCacheImage blah blah does what it says. We use this because
		// I don't know. Maybe to save memory(RAM).
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).denyCacheImageMultipleSizesInMemory().build();
		
		// Initialize the ImageLoader
		loader.init(config);

		// Get our JSON for GSON from the cards.json file in our "raw" directory
		InputStream is = getResources().openRawResource(R.raw.cards);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is,
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
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// The json String from the file
		String jsonString = writer.toString();
		// Set our pojo from the GSON data
		cards = gson.fromJson(jsonString, Cards[].class);
		
		// Load default card list
		if (cardList == null) {
			cardList = new ArrayList<Cards>();
			for (Cards card : cards) {
				cardList.add(card);
			}
		}
		// Sort the card list with our own custom Comparator
		// -- this sorts by Mana Cost
		Collections.sort(cardList, new CardComparator());
		// Create a new instace of our ImageAdapter class 
		adapter = new ImageAdapter(this);
		// Set the gridview's adapter to our custom adapter
		grid.setAdapter(adapter);
		
		// TODO: This may or may not work.
		// It's a Listener for when we check or uncheck the check box
		// It's supposed to add generic cards to the list when you check it,
		// but when we uncheck it nothing really happens.
		includeNeutralCards
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							for (Cards card : cards) {
								if (card.getClasss() == null) {
									cardList.add(card);
								}
							}

							Collections.sort(cardList, new CardComparator());
							adapter.notifyDataSetChanged();
							grid.invalidate();
							grid.setAdapter(adapter);
						} else {
							spinner.setSelection(1);
						}
					}

				});

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
	
	// Our custom Comparator implementation. Sorts cards by mana cost.
	public class CardComparator implements Comparator<Cards> {
		public int compare(Cards left, Cards right) {
			return left.getCost().toString().compareTo(right.getCost().toString());
		}
	}

	// Our custom OnItemSelectedListener for our GridView
	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			switch (pos) {
			//All Class Card List
			case 0:
				// Clear the current ArrayList so we can repopulate it
				cardList.clear();
				// Repopulate the card list with class cards
				for (Cards card : cards) {
					cardList.add(card);	
				}
				Collections.sort(cardList, new CardComparator());
				adapter.notifyDataSetChanged();
				grid.invalidate();
				grid.setAdapter(adapter);
				break;
				
			//Druid Class Card List
			case 1:
				// Clear the current ArrayList so we can repopulate it
				cardList.clear();
				// Repopulate the card list with class cards
				for (Cards card : cards) {
					if (card.getClasss() != null) {
						if (card.getClasss().intValue() == 11) {
							cardList.add(card);
						}
					}

				}
				
				Collections.sort(cardList, new CardComparator());
				adapter.notifyDataSetChanged();
				grid.invalidate();
				grid.setAdapter(adapter);
				break;
				
			// Hunter Class Card List
			case 2:
				// Clear the current ArrayList so we can repopulate it
				cardList.clear();
				// Repopulate the card list with class cards
				for (Cards card : cards) {
					if (card.getClasss() != null) {
						if (card.getClasss().intValue() == 3) {
							cardList.add(card);
						}
					}

				}
				
				Collections.sort(cardList, new CardComparator());
				adapter.notifyDataSetChanged();
				grid.invalidate();
				grid.setAdapter(adapter);
				break;
				
			// Mage Class Card List
			case 3:
				// Clear the current ArrayList so we can repopulate it
				cardList.clear();
				// Repopulate the card list with class cards
				for (Cards card : cards) {
					if (card.getClasss() != null) {
						if (card.getClasss().intValue() == 8) {
							cardList.add(card);
						}
					}

				}
				
				Collections.sort(cardList, new CardComparator());
				adapter.notifyDataSetChanged();
				grid.invalidate();
				grid.setAdapter(adapter);
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO 
		}
	}
}
