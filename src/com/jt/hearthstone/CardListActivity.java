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

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import com.jt.hearthstone.ImageAdapter;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;

public class CardListActivity extends ActionBarActivity {

	Spinner spinner;
	GridView grid;
	Cards[] cards;
	ImageAdapter adapter;
	PopupWindow pWindow;
	CheckBox includeNeutralCards;
	
	public static ImageLoader loader = ImageLoader.getInstance();
	public static ArrayList<Cards> cardList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set main view to Activity_Main layout
		setContentView(R.layout.activity_card_list);
		
		// Show ActionBar (Top bar)
		getSupportActionBar().show();
		
		// Set ActionBar Title
		setTitle("Hearthstone Utilities");
		
		// Show Up button on ActionBar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Start up GSON
		Gson gson = new Gson();

		// Find (assign?) our views
		grid = (GridView) findViewById(R.id.cardsGrid);
		spinner = (Spinner) findViewById(R.id.spinner1);
		includeNeutralCards = (CheckBox) findViewById(R.id.checkBox1);
		
		// Create a new instance of our custom OnItemSelectedListener 
		CustomOnItemSelectedListener listener = new CustomOnItemSelectedListener();

		
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
		
		
		
		
		
		
	    grid.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	int dipsWidthPortrait_Normal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, 
	    				getResources().getDisplayMetrics()); 
	    		int dipsHeightPortrait_Normal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 475, 
	    				getResources().getDisplayMetrics());
	        	doSomeWindow(dipsWidthPortrait_Normal, dipsHeightPortrait_Normal, dipsWidthPortrait_Normal, dipsHeightPortrait_Normal);
	        }
	    });

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
		
		
		// Set the spinner (drop down selector) to listen to our custom listener
		
		// Sort the card list with our own custom Comparator
		// -- this sorts by Mana Cost
		Collections.sort(cardList, new CardComparator());
		
		// Create a new instance of our ImageAdapter class 
		adapter = new ImageAdapter(this);
		
		// Set the gridview's adapter to our custom adapter
		grid.setAdapter(adapter);
		
		
		// This works now! Listener for when CheckBox is checked
		includeNeutralCards
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, // called when you check or uncheck
							boolean isChecked) {
						// if the user is checking the box, add generic cards
						if (isChecked) {
							for (Cards card : cards) {
								if (card.getClasss() == null) {
									cardList.add(card);
								}
							}

							Collections.sort(cardList, new CardComparator());
							grid.setAdapter(adapter);
							
						// Otherwise, user is unchecking the box, so remove all generic cards.
						// Why haven't I been using more ArrayLists in my other app?????
						} else {
							for (Cards card : cards) {
								if (card.getClasss() == null) {
									cardList.remove(card);
								}
							}
							Collections.sort(cardList, new CardComparator());
							grid.setAdapter(adapter);
						}
					}

				});
		spinner.setOnItemSelectedListener(listener);

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
			// When Settings button is clicked, start Settings Activity
			startActivity(new Intent(CardListActivity.this, SettingsActivity.class));
			return true;
		case android.R.id.home: 
			// When the back button on the ActionBar is pressed, go back one Activity
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			switch (pos) {
			//All Class Card List
			case 0:
				setCardList();
				break;
			//Druid Class Card List
			case 1:
				setCardList(Classes.DRUID);
				break;
				
			// Hunter Class Card List
			case 2:
				setCardList(Classes.HUNTER);
				break;
				
			// Mage Class Card List
			case 3:
				setCardList(Classes.MAGE);
				break;
				
			// Paladin Class Card List
			case 4:
				setCardList(Classes.PALADIN);
				break;
			case 5:
				setCardList(Classes.PRIEST);
				break;
			case 6:
				setCardList(Classes.ROGUE);
				break;
			case 7:
				setCardList(Classes.SHAMAN);
				break;
			case 8:
				setCardList(Classes.WARLOCK);
				break;
			case 9:
				setCardList(Classes.WARRIOR);
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO 
		}
	}
	
	/* Handy little function to set the card list
	This is called when you select any class other than "Any" from the Spinner */
	private void setCardList(Classes classes) {
		// Clear the current ArrayList so we can repopulate it
		cardList.clear();
		// Repopulate the card list with class cards
		for (Cards card : cards) {
			if (card.getClasss() != null) {
				if (card.getClasss().intValue() == classes.getValue()) {
					if (!card.getName().equals(classes.getHeroName())) { // Ignore hero cards (stored in enum)
						cardList.add(card);
					} 
				}
			}

		}
		
		Collections.sort(cardList, new CardComparator());
		grid.setAdapter(adapter);
	}
	
	/* Handy little function to set the default card list
		This is called when you select "Any" from the Spinner 
		Overloaded version of previous function */
	private void setCardList(){
		// Clear the current ArrayList so we can repopulate it
		cardList.clear();
		// Repopulate the card list with class cards
		for (Cards card : cards) {
			cardList.add(card);
		}
		Collections.sort(cardList, new CardComparator());
		grid.setAdapter(adapter);
	}
	
	private void doSomeWindow(int widthLandscape, int heightLandscape, 
			int widthPortrait, int heightPortrait) {
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.card_popup, null);
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			pWindow = new PopupWindow(layout, widthLandscape, heightLandscape, true);
			pWindow.setBackgroundDrawable(new BitmapDrawable());
			pWindow.setOutsideTouchable(true);
			pWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
			pWindow.setFocusable(true);
			
		} else {
			pWindow = new PopupWindow(layout, widthPortrait, heightPortrait, true);
			pWindow.setBackgroundDrawable(new BitmapDrawable());
			pWindow.setOutsideTouchable(true);
			pWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
			pWindow.setFocusable(true);
			
		}
	}
}