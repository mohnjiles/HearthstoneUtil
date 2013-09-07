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

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.StaticLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jt.hearthstone.R.color;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class CardListActivity extends ActionBarActivity {

	Spinner spinner;
	GridView grid;
	Cards[] cards;
	ImageAdapter adapter;
	PopupWindow pWindow;
	CheckBox includeNeutralCards;
	TextView tvCardName;
	TextView tvType;
	TextView tvQuality;
	TextView tvSet;
	TextView tvEnchant;
	TextView tvCrafted;
	TextView tvClass;
	ImageView ivCardImage;
	
	private static final int druid = Classes.DRUID.getValue();
	int hunter = Classes.HUNTER.getValue();
	int mage = Classes.MAGE.getValue();
	int paladin = Classes.PALADIN.getValue();
	int priest = Classes.PRIEST.getValue();
	int rogue = Classes.ROGUE.getValue();
	int shaman = Classes.SHAMAN.getValue();
	int warlock = Classes.WARLOCK.getValue();
	int warrior = Classes.WARRIOR.getValue();
	
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
	        	initiatePopupWindow(position);
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
	
	private void initiatePopupWindow(int position) {
		try {
			// get screen size of device
			int screenSize = getResources().getConfiguration().screenLayout &
			        Configuration.SCREENLAYOUT_SIZE_MASK;
			
			// convert px to dips for multiple screens
			int dipsWidthPortrait_Normal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics()); 
			int dipsHeightPortrait_Normal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 475, getResources().getDisplayMetrics());
			int dipsWidthLandscape_Normal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 475, getResources().getDisplayMetrics()); 
			int dipsHeightLandscape_Normal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
			int dipsWidthPortrait_Large = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 425, getResources().getDisplayMetrics()); 
			int dipsHeightPortrait_Large = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 550, getResources().getDisplayMetrics());
			int dipsWidthLandscape_Large = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 550, getResources().getDisplayMetrics()); 
			int dipsHeightLandscape_Large = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 425, getResources().getDisplayMetrics());
			int dipsWidthPortrait_Small = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics()); 
			int dipsHeightPortrait_Small = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics());
			int dipsWidthLandscape_Small = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics()); 
			int dipsHeightLandscape_Small = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
			
			// We need to get the instance of the LayoutInflater
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.card_popup, null);
			
			// make different popupWindowws for different screen sizes
			switch (screenSize) {
				case Configuration.SCREENLAYOUT_SIZE_XLARGE:
					doSomeWindow(
			        		layout, 
			        		dipsWidthLandscape_Large, 
			        		dipsHeightLandscape_Large,
			        		dipsWidthPortrait_Large,  
			        		dipsHeightPortrait_Large); 
					break;
				case Configuration.SCREENLAYOUT_SIZE_LARGE:
					doSomeWindow(
			        		layout, // View of the popupWindow
			        		dipsWidthLandscape_Large, // Width for landscape orientation
			        		dipsHeightLandscape_Large, // Height for landscape
			        		dipsWidthPortrait_Large,  // Width for portrait orientation
			        		dipsHeightPortrait_Large); // Height for portrait
					break;
			    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			        doSomeWindow(
			        		layout, 
			        		dipsWidthLandscape_Normal, 
			        		dipsHeightLandscape_Normal,
			        		dipsWidthPortrait_Normal, 
			        		dipsHeightPortrait_Normal);
			        break;
			    default:
			    	doSomeWindow(
			        		layout, 
			        		dipsWidthLandscape_Small, 
			        		dipsHeightLandscape_Small,
			        		dipsWidthPortrait_Small, 
			        		dipsHeightPortrait_Small);
			        break;
			}
			
			String url = "http://jt.comyr.com/images/" + cardList.get(position).getName().replace(" ", "%20").replace(":", "") + ".png";
			loader.displayImage(url, ivCardImage);
			tvCardName.setText(cardList.get(position).getName());
			int classs = cardList.get(position).getClasss().intValue();
			int type = cardList.get(position).getClasss().intValue();
			int quality = cardList.get(position).getQuality().intValue();
			
			if (classs == Classes.DRUID.getValue()) {
				int druid = getResources().getColor(R.color.druid);
				tvClass.setTextColor(druid);
				tvClass.setText("Druid");
			}
			// Set the type (minion, ability, etc)
			if (type == 3) {
				tvType.setText("Hero");
			} else if (type == 4) {
				tvType.setText("Minion");
			} else if (type == 5) {
				tvType.setText("Ability");
			} else if (type == 7) {
				tvType.setText("Weapon");
			} else if (type == 10) {
				tvType.setText("Hero Power");
			}
			
			switch (quality) {
			case 0:
				int free = getResources().getColor(R.color.free);
				tvQuality.setTextColor(free);
				tvQuality.setText("Free");
				break;
			case 1:
				tvQuality.setText("Common");
				break;
			case 3:
				int rare = getResources().getColor(R.color.rare);
				tvQuality.setTextColor(rare);
				tvQuality.setText("Rare");
				break;
			case 4:
				int epic = getResources().getColor(R.color.epic);
				tvQuality.setTextColor(epic);
				tvQuality.setText("Epic");
				break;
			case 5:
				int legendary = getResources().getColor(R.color.legendary);
				tvQuality.setTextColor(legendary);
				tvQuality.setText("Legendary");
				break;
			}
			
			// If we ran in to a problem
			} catch (Exception e) {
				Log.w("PopupWindoww", "" + e.getMessage() + e.getStackTrace()[0].getLineNumber());
			}
		}

		
		// Runs the popupWindoww, getting view from inflater & dimensions based on screen size
		private void doSomeWindow(View layout, int widthLandscape, int heightLandscape, 
				int widthPortrait, int heightPortrait) {
			
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
			
			ivCardImage = (ImageView) pWindow.getContentView().findViewById(R.id.ivCardImages);
			tvCardName = (TextView) pWindow.getContentView().findViewById(R.id.tvCardName);
			tvClass = (TextView) pWindow.getContentView().findViewById(R.id.tvClass);
			tvCrafted = (TextView) pWindow.getContentView().findViewById(R.id.tvCrafted);
			tvEnchant = (TextView) pWindow.getContentView().findViewById(R.id.tvEnchant);
			tvQuality = (TextView) pWindow.getContentView().findViewById(R.id.tvQuality);
			tvSet = (TextView) pWindow.getContentView().findViewById(R.id.tvSet);
			tvType = (TextView) pWindow.getContentView().findViewById(R.id.tvType);
		}
}