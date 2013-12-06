package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StreamCorruptedException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.StaticLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CardListActivity extends ActionBarActivity {

	Spinner spinner;
	Spinner spinnerSort;
	Spinner spinnerMechanic;
	CheckBox includeNeutralCards;
	CheckBox cbReverse;
	SearchView mSearchView;

	static boolean reverse = false;

	private GridView grid;
	private ListView listCards;
	private PopupWindow pWindow;
	private TextView tvCardName;
	private TextView tvType;
	private TextView tvQuality;
	private TextView tvSet;
	private TextView tvCrafted;
	private TextView tvClass;
	private TextView tvMechanic;
	private TextView tvSort;
	private TextView tvClassSort;
	private TextView tvCost;
	private TextView tvCostGold;
	private TextView tvDisenchant;
	private TextView tvDisenchantGold;
	private ImageView ivCardImage;
	private ImageView ivDust1;
	private ImageView ivDust2;
	private ImageView ivDust3;
	private ImageView ivDust4;
	
	private MenuItem searchItem;
	private List<Cards> deckOne;
	private List<Cards> deckTwo;
	private List<Cards> deckThree;
	private List<Cards> deckFour;
	private List<Cards> deckFive;
	private List<Cards> deckSix;
	private List<Cards> deckSeven;
	private List<Cards> deckEight;
	private List<Cards> deckNine;
	private List<Cards> deckTen;

	private ArrayList<Cards> cardList;
	private ArrayList<String> deckList;
	private Cards[] cards;
	private ImageAdapter adapter;
	private CustomListAdapter adapter2;
	private ImageLoader loader = ImageLoader.getInstance();
	private String query;

	private int pos = OnItemSelectedListenerStandalone.position;
	private int position;
	private int menuItemIndex;
	private boolean isGrid = false;
	private Typeface font;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set main view to Activity_Main layout
		setContentView(R.layout.card_list_standalone);

		font = TypefaceCache.get(getAssets(), "fonts/belwebd.ttf");

		// Get views with ButterKnife
		grid = findById(this, R.id.gvDeck);
		listCards = findById(this, R.id.cardsList);
		includeNeutralCards = findById(this, R.id.cbGenerics);
		cbReverse = findById(this, R.id.cbReverse);
		spinner = findById(this, R.id.spinClass);
		spinnerSort = findById(this, R.id.spinSort);
		spinnerMechanic = findById(this, R.id.spinnerMechanic);
		tvClassSort = findById(this, R.id.tvInstructions);
		tvSort = findById(this, R.id.textView2);
		tvMechanic = findById(this, R.id.tvCost);

		// Show ActionBar (Top bar)
		getSupportActionBar().show();
		registerForContextMenu(listCards);
		registerForContextMenu(grid);

		// Set ActionBar Title
		getSupportActionBar().setTitle("Hearthstone Companion");

		// Show Up button on ActionBar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Set grid invisible, list is default.
		grid.setVisibility(View.INVISIBLE);

		// ImageLoader config for the ImageLoader that gets our card images
		// denyCacheImage blah blah does what it says. We use this because
		// I don't know. Maybe to save memory(RAM).
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).denyCacheImageMultipleSizesInMemory().build();

		// Initialize the ImageLoader
		if (!loader.isInited()) {
			loader.init(config);
		}

		// Get our JSON for GSON from the cards.json file in our "raw" directory
		// and use it to set up the list of cards
		setupCardList();
		// Get deck list from file
		getDeckList();

		grid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				initiatePopupWindow(position);
			}
		});
		listCards.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				initiatePopupWindow(position);
			}
		});

		// Set the spinner (drop down selector) to listen to our custom listener

		// Sort the card list with our own custom Comparator
		// -- this sorts by Mana Cost
		Collections.sort(cardList, new CardComparator(pos, reverse));

		// Create a new instance of our ImageAdapter class
		adapter = new ImageAdapter(this, cardList);
		adapter2 = new CustomListAdapter(this, cardList);

		// Set the gridview's adapter to our custom adapter
		grid.setAdapter(adapter);
		listCards.setAdapter(adapter2);

		// This works now! Listener for when CheckBox is checked
		includeNeutralCards
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					// Called when checkbox is checked or unchecked
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						if (mSearchView != null) {
							query = mSearchView.getQuery().toString()
									.toLowerCase();
						} else {
							query = "";
						}

						// if the user is checking the box, add generic cards
						if (isChecked
								&& !spinner.getSelectedItem().equals("All")) {
							String mechanic = spinnerMechanic.getSelectedItem()
									.toString();
							for (Cards card : cards) {
								if (card.getClasss() == null
										&& !mechanic.equals("Any")
										&& card.getDescription() != null
										&& card.getName().toLowerCase()
												.contains(query)
										&& card.getDescription().contains(
												mechanic)) {

									cardList.add(card);
								} else {
									if (card.getClasss() == null
											&& mechanic.equals("Any")
											&& card.getName().toLowerCase()
													.contains(query)) {
										cardList.add(card);
									}
								}
							}

							Collections.sort(cardList, new CardComparator(
									spinnerSort.getSelectedItemPosition(),
									cbReverse.isChecked()));
							adapter.notifyDataSetChanged();
							adapter2.notifyDataSetChanged();
							grid.setAdapter(adapter);
							listCards.setAdapter(adapter2);

							// Otherwise, user is unchecking the box, so remove
							// all generic cards.
							// Why haven't I been using more ArrayLists in my
							// other app?????
						} else {
							for (Cards card : cards) {
								if (card.getClasss() == null
										&& !spinner.getSelectedItem().equals(
												"All")) {
									cardList.remove(card);
								}
							}
							Collections.sort(cardList, new CardComparator(
									spinnerSort.getSelectedItemPosition(),
									cbReverse.isChecked()));
							adapter.notifyDataSetChanged();
							adapter2.notifyDataSetChanged();
							grid.setAdapter(adapter);
							listCards.setAdapter(adapter2);
						}
					}

				});

		cbReverse
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					// Called when checkbox is checked or unchecked
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						reverse = isChecked;
						Collections.sort(cardList, new CardComparator(
								spinnerSort.getSelectedItemPosition(),
								isChecked));
						reverse = isChecked;
						adapter.notifyDataSetChanged();
						adapter2.notifyDataSetChanged();
						grid.setAdapter(adapter);
						listCards.setAdapter(adapter2);
					}

				});

		// Arrays to load into the Spinners
		String[] mechanicNames = getResources()
				.getStringArray(R.array.Mechanic);
		String[] sortNames = getResources().getStringArray(R.array.Sort);
		String[] classNames = getResources().getStringArray(R.array.Classes);

		// Setup Adapters and set the spinners to listen to them.
		CustomArrayAdapter spinAdapter = new CustomArrayAdapter(this,
				R.layout.spinner_row, R.id.name, classNames);
		spinAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row);

		CustomArrayAdapter spinSortAdapter = new CustomArrayAdapter(this,
				R.layout.spinner_row, R.id.name, sortNames);
		spinSortAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row);

		CustomArrayAdapter spinMechanicAdapter = new CustomArrayAdapter(this,
				R.layout.spinner_row, R.id.name, mechanicNames);
		spinMechanicAdapter
				.setDropDownViewResource(R.layout.spinner_dropdown_row);

		spinner.setAdapter(spinAdapter);
		spinnerSort.setAdapter(spinSortAdapter);
		spinnerMechanic.setAdapter(spinMechanicAdapter);

		// Set UI fonts
		cbReverse.setTypeface(font);
		includeNeutralCards.setTypeface(font);
		tvClassSort.setTypeface(font);
		tvSort.setTypeface(font);
		tvMechanic.setTypeface(font);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.card_list, menu);
		searchItem = menu.findItem(R.id.action_search);
		mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		mSearchView.setOnQueryTextListener(new SearchListener2(this, cardList,
				cards, grid, listCards, adapter, adapter2, searchItem));

		OnItemSelectedListenerStandalone listener = new OnItemSelectedListenerStandalone(
				this, mSearchView, cardList, cards, adapter, adapter2);
		spinner.setOnItemSelectedListener(listener);
		spinnerSort.setOnItemSelectedListener(listener);
		spinnerMechanic.setOnItemSelectedListener(listener);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_settings:
			// When Settings button is clicked, start Settings Activity
			startActivity(new Intent(CardListActivity.this,
					SettingsActivity.class));
			return true;
		case android.R.id.home:
			// When the back button on the ActionBar is pressed, go back one
			// Activity
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_switch:
			// Switches between GridView and ListView visibility
			if (isGrid) {
				grid.setVisibility(View.INVISIBLE);
				listCards.setVisibility(View.VISIBLE);
				item.setTitle("Switch to grid view");
				item.setIcon(R.drawable.collections_view_as_grid);
				isGrid = false;
				return true;
			} else {
				grid.setVisibility(View.VISIBLE);
				listCards.setVisibility(View.INVISIBLE);
				item.setTitle("Switch to list view");
				item.setIcon(R.drawable.collections_view_as_list);
				isGrid = true;
				return true;
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		String[] menuItems = new String[1];

		if (v.getId() == R.id.cardsList) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(cardList.get(info.position).getName());
			position = info.position;

			if (deckList == null || deckList.size() == 0) {
				menuItems = new String[1];
			} else {
				menuItems = new String[deckList.size()];
			}
			menu.add(Menu.NONE, 0, 0, "Add to new deck");
			int j = 0;

			if (deckList != null) {
				while (j < deckList.size()) {
					menuItems[j] = "Add to \"" + deckList.get(j) + "\"";
					j++;
				}

				for (int i = 0; i < menuItems.length; i++) {
					menu.add(Menu.NONE, i, i, menuItems[i]);
				}
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		menuItemIndex = item.getItemId();
		final List<Integer> deckClasses = (List<Integer>) Utils.getDeck(this,
				"deckclasses");
		switch (menuItemIndex) {
		case 0:
			if (item.getTitle().equals("Add to new deck")) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.dialog_layout,
						(ViewGroup) findViewById(R.id.linearLayout));
				// layout_root should be the name of the "top-level" layout node
				// in the dialog_layout.xml file.
				final EditText nameBox = (EditText) layout
						.findViewById(R.id.etDeckName);
				final Spinner spinnerClass = (Spinner) layout
						.findViewById(R.id.spinClass);

				// Building dialog
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				String[] classes = getResources().getStringArray(
						R.array.ClassesWithoutAny);
				CustomArrayAdapter spinAdapter = new CustomArrayAdapter(this,
						R.layout.spinner_row, R.id.name, classes);
				spinAdapter
						.setDropDownViewResource(R.layout.spinner_dropdown_row);
				spinnerClass.setAdapter(spinAdapter);
				builder.setView(layout);
				builder.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								if (deckList == null) {
									deckList = new ArrayList<String>();
								}
								deckList.add(nameBox.getText().toString());
								deckClasses.add(spinnerClass
										.getSelectedItemPosition());
								FileOutputStream fos = null;
								try {
									fos = openFileOutput("decklist",
											Context.MODE_PRIVATE);
								} catch (FileNotFoundException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								ObjectOutputStream oos;
								try {
									oos = new ObjectOutputStream(fos);
									oos.writeObject(deckList);
									oos.close();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								/*************** save corresponding class number **********/
								try {
									fos = openFileOutput("deckclasses",
											Context.MODE_PRIVATE);
								} catch (FileNotFoundException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

								try {
									oos = new ObjectOutputStream(fos);
									oos.writeObject(deckClasses);
									oos.close();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								ArrayList<Cards> newDeck = new ArrayList<Cards>();
								Utils.saveDeck(CardListActivity.this, nameBox
										.getText().toString(), newDeck);
								addCards(newDeck, deckList.size() - 1);
							}
						});
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				AlertDialog dialog = builder.create();
				dialog.show();

				return true;
			} else {
				addCards(deckOne, menuItemIndex);
				return true;
			}
		case 1:
			addCards(deckTwo, menuItemIndex);
			return true;
		case 2:
			addCards(deckThree, menuItemIndex);
			return true;
		case 3:
			addCards(deckFour, menuItemIndex);
			return true;
		case 4:
			addCards(deckFive, menuItemIndex);
			return true;
		case 5:
			addCards(deckSix, menuItemIndex);
			return true;
		case 6:
			addCards(deckSeven, menuItemIndex);
			return true;
		case 7:
			addCards(deckEight, menuItemIndex);
			return true;
		case 8:
			addCards(deckNine, menuItemIndex);
			return true;
		case 9:
			addCards(deckTen, menuItemIndex);
			return true;

		}
		return true;
	}

	private void initiatePopupWindow(int position) {
		try {
			// get screen size of device
			int screenSize = getResources().getConfiguration().screenLayout
					& Configuration.SCREENLAYOUT_SIZE_MASK;

			int dipsWidthPortrait_Normal = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 300, getResources()
							.getDisplayMetrics());
			int dipsHeightPortrait_Normal = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 460, getResources()
							.getDisplayMetrics());
			int dipsWidthLandscape_Normal = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 475, getResources()
							.getDisplayMetrics());
			int dipsHeightLandscape_Normal = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 300, getResources()
							.getDisplayMetrics());

			int dipsWidthPortrait_Large = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 425, getResources()
							.getDisplayMetrics());
			int dipsHeightPortrait_Large = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 600, getResources()
							.getDisplayMetrics());
			int dipsWidthLandscape_Large = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 650, getResources()
							.getDisplayMetrics());
			int dipsHeightLandscape_Large = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 425, getResources()
							.getDisplayMetrics());

			int dipsWidthPortrait_Small = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 200, getResources()
							.getDisplayMetrics());
			int dipsHeightPortrait_Small = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 350, getResources()
							.getDisplayMetrics());
			int dipsWidthLandscape_Small = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 350, getResources()
							.getDisplayMetrics());
			int dipsHeightLandscape_Small = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 200, getResources()
							.getDisplayMetrics());

			int dipsWidthPortrait_XLarge = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 600, getResources()
							.getDisplayMetrics());
			int dipsHeightPortrait_XLarge = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 750, getResources()
							.getDisplayMetrics());
			int dipsWidthLandscape_XLarge = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 750, getResources()
							.getDisplayMetrics());
			int dipsHeightLandscape_XLarge = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 600, getResources()
							.getDisplayMetrics());

			// We need to get the instance of the LayoutInflater,
			// Gotta give the PopupWindow a layout
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.card_popup, null);

			// make different popupWindows for different screen sizes
			switch (screenSize) {

			// XLARGE = 10"+ Tablets usually
			case Configuration.SCREENLAYOUT_SIZE_XLARGE:
				doSomeWindow(layout, dipsWidthLandscape_XLarge,
						dipsHeightLandscape_XLarge, dipsWidthPortrait_XLarge,
						dipsHeightPortrait_XLarge);
				break;

			// LARGE = 7"+ Tablets usually, maybe some giant phones
			case Configuration.SCREENLAYOUT_SIZE_LARGE:
				doSomeWindow(layout, // View of the popupWindow
						dipsWidthLandscape_Large, // Width for landscape
						dipsHeightLandscape_Large, // Height for landscape
						dipsWidthPortrait_Large, // Width for portrait
						dipsHeightPortrait_Large); // Height for portrait
				break;

			// NORMAL = 95% of all phones
			case Configuration.SCREENLAYOUT_SIZE_NORMAL:
				doSomeWindow(layout, dipsWidthLandscape_Normal,
						dipsHeightLandscape_Normal, dipsWidthPortrait_Normal,
						dipsHeightPortrait_Normal);
				break;
			default:
				doSomeWindow(layout, dipsWidthLandscape_Small,
						dipsHeightLandscape_Small, dipsWidthPortrait_Small,
						dipsHeightPortrait_Small);
				break;
			}
			// Get card image
			final String url = "http://54.224.222.135/"
					+ cardList.get(position).getImage() + ".png";
			final DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.cards).cacheInMemory(false)
					.cacheOnDisc(true).build();
			loader.displayImage(url, ivCardImage, options);

			final ObjectAnimator animator = (ObjectAnimator) AnimatorInflater
					.loadAnimator(this, R.animator.flipping);
			final ObjectAnimator reverseAnimator = (ObjectAnimator) AnimatorInflater
					.loadAnimator(this, R.animator.flipping_reverse);
			final int pos = position;

			animator.setTarget(ivCardImage);
			animator.setDuration(500);

			reverseAnimator.setTarget(ivCardImage);
			reverseAnimator.setDuration(500);

			ivCardImage.setOnClickListener(new View.OnClickListener() {

				boolean isGolden = false;

				@Override
				public void onClick(View v) {
					if (isGolden) {
						reverseAnimator.start();
						isGolden = false;
					} else {
						animator.start();
						isGolden = true;
					}

					Handler handler = new Handler();
					delayedLoad(handler, pos);
				}
			});

			// Get card name
			tvCardName.setText(cardList.get(position).getName());

			int classs = 0;
			if (cardList.get(position).getClasss() != null) {
				classs = cardList.get(position).getClasss().intValue();
			}

			int type = cardList.get(position).getType().intValue();
			int quality = cardList.get(position).getQuality().intValue();
			int set = cardList.get(position).getSet().intValue();

			tvCrafted.setText(cardList.get(position).getDescription());

			if (classs == Classes.DRUID.getValue()) {
				int druid = getResources().getColor(R.color.druid);
				tvClass.setTextColor(druid);
				tvClass.setText("Druid");
			} else if (classs == Classes.HUNTER.getValue()) {
				int hunter = getResources().getColor(R.color.hunter);
				tvClass.setTextColor(hunter);
				tvClass.setText("Hunter");
			} else if (classs == Classes.MAGE.getValue()) {
				int mage = getResources().getColor(R.color.mage);
				tvClass.setTextColor(mage);
				tvClass.setText("Mage");
			} else if (classs == Classes.PALADIN.getValue()) {
				int paladin = getResources().getColor(R.color.paladin);
				tvClass.setTextColor(paladin);
				tvClass.setText("Paladin");
			} else if (classs == Classes.PRIEST.getValue()) {
				int priest = getResources().getColor(R.color.priest);
				tvClass.setTextColor(priest);
				tvClass.setText("Priest");
			} else if (classs == Classes.ROGUE.getValue()) {
				int rogue = getResources().getColor(R.color.rogue);
				tvClass.setTextColor(rogue);
				tvClass.setText("Rogue");
			} else if (classs == Classes.SHAMAN.getValue()) {
				int shaman = getResources().getColor(R.color.shaman);
				tvClass.setTextColor(shaman);
				tvClass.setText("Shaman");
			} else if (classs == Classes.WARLOCK.getValue()) {
				int warlock = getResources().getColor(R.color.warlock);
				tvClass.setTextColor(warlock);
				tvClass.setText("Warlock");
			} else if (classs == Classes.WARRIOR.getValue()) {
				int warrior = getResources().getColor(R.color.warrior);
				tvClass.setTextColor(warrior);
				tvClass.setText("Warrior");
			} else if (classs == 0) {
				tvClass.setTextColor(Color.GREEN);
				tvClass.setText("All Classes");
			}

			// Set the type (minion, ability, etc)
			switch (type) {
			case 3:
				tvType.setText("Hero");
				break;
			case 4:
				tvType.setText("Minion");
				break;
			case 5:
				tvType.setText("Ability");
				break;
			case 7:
				tvType.setText("Weapon");
				break;
			case 10:
				tvType.setText("Hero Power");
				break;
			default: // If card doesn't have a type, just hide the textview.
				tvType.setVisibility(View.GONE);
				break;
			}

			// Set rarity of the card
			
			tvCost.setTextColor(Color.rgb(17, 228, 241));
			tvCostGold.setTextColor(Color.rgb(17, 228, 241));
			tvDisenchant.setTextColor(Color.rgb(17, 228, 241));
			tvDisenchantGold.setTextColor(Color.rgb(17, 228, 241));
			switch (quality) {
			case 0:
				int free = getResources().getColor(R.color.free);
				tvQuality.setTextColor(free);
				tvQuality.setText("Free");
				tvCost.setVisibility(View.INVISIBLE);
				tvCostGold.setVisibility(View.INVISIBLE);
				tvDisenchant.setVisibility(View.INVISIBLE);
				tvDisenchantGold.setVisibility(View.INVISIBLE);
				ivDust1.setVisibility(View.INVISIBLE);
				ivDust2.setVisibility(View.INVISIBLE);
				ivDust3.setVisibility(View.INVISIBLE);
				ivDust4.setVisibility(View.INVISIBLE);
				break;
			case 1:
				tvQuality.setText("Common");
				if (set == 3) {
					tvCost.setText("Crafted: 40");
					tvCostGold.setText("Golden: 400");
					tvDisenchant.setText("Disenchant: 5");
					tvDisenchantGold.setText("Golden: 50");
				} else {
					tvCost.setVisibility(View.INVISIBLE);
					tvCostGold.setVisibility(View.INVISIBLE);
					tvDisenchant.setVisibility(View.INVISIBLE);
					tvDisenchantGold.setVisibility(View.INVISIBLE);
					ivDust1.setVisibility(View.INVISIBLE);
					ivDust2.setVisibility(View.INVISIBLE);
					ivDust3.setVisibility(View.INVISIBLE);
					ivDust4.setVisibility(View.INVISIBLE);
				}
				break;
			case 3:
				int rare = getResources().getColor(R.color.rare);
				tvQuality.setTextColor(rare);
				tvQuality.setText("Rare");
				if (set == 3) {
					tvCost.setText("Crafted: 100");
					tvCostGold.setText("Golden: 800");
					tvDisenchant.setText("Disenchant: 20");
					tvDisenchantGold.setText("Golden: 100");
				}
				break;
			case 4:
				int epic = getResources().getColor(R.color.epic);
				tvQuality.setTextColor(epic);
				tvQuality.setText("Epic");
				if (set == 3) {
					tvCost.setText("Crafted: 400");
					tvCostGold.setText("Golden: 1600");
					tvDisenchant.setText("Disenchant: 100");
					tvDisenchantGold.setText("Golden: 400");
				}
				break;
			case 5:
				int legendary = getResources().getColor(R.color.legendary);
				tvQuality.setTextColor(legendary);
				tvQuality.setText("Legendary");
				if (set == 3) {
					tvCost.setText("Crafted: 1600");
					tvCostGold.setText("Golden: 3200");
					tvDisenchant.setText("Disenchant: 400");
					tvDisenchantGold.setText("GoldeN: 1600");
				}
				break;
			default: // No rarity? This should only happen for some abilities.
				tvQuality.setVisibility(View.GONE); // Hides it.
				break;
			}

			switch (set) {
			case 2:
				tvSet.setText("Basic");
				break;
			case 3:
				tvSet.setText("Expert");
				break;
			case 4:
				tvSet.setText("Reward");
				break;
			case 5:
				tvSet.setText("Missions");
				break;
			}

			// If we ran in to a problem
		} catch (Exception e) {
			Log.w("PopupWindow",
					"" + e.getMessage() + e.getStackTrace()[0].getLineNumber());
		}
	}

	// Runs the popupWindoww, getting view from inflater & dimensions based on
	// screen size
	@SuppressWarnings("deprecation")
	private void doSomeWindow(View layout, int widthLandscape,
			int heightLandscape, int widthPortrait, int heightPortrait) {

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			pWindow = new PopupWindow(layout, widthLandscape, heightLandscape,
					true);
			pWindow.setBackgroundDrawable(new BitmapDrawable());
			pWindow.setOutsideTouchable(true);
			pWindow.setAnimationStyle(R.style.AnimationPopup);
			pWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
			pWindow.setFocusable(true);

		} else {
			pWindow = new PopupWindow(layout, widthPortrait, heightPortrait,
					true);
			pWindow.setBackgroundDrawable(new BitmapDrawable());
			pWindow.setOutsideTouchable(true);
			pWindow.setAnimationStyle(R.style.AnimationPopup);
			pWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
			pWindow.setFocusable(true);

		}

		ivCardImage = findById(pWindow.getContentView(), R.id.ivCardImages);
		tvCardName = findById(pWindow.getContentView(), R.id.tvCardName);
		tvClass = findById(pWindow.getContentView(), R.id.tvClass);
		tvCrafted = findById(pWindow.getContentView(), R.id.tvCrafted);
		tvQuality = findById(pWindow.getContentView(), R.id.tvQuality);
		tvSet = findById(pWindow.getContentView(), R.id.tvSet);
		tvType = findById(pWindow.getContentView(), R.id.tvType);
		tvCost = findById(pWindow.getContentView(), R.id.tvCost);
		tvCostGold = findById(pWindow.getContentView(), R.id.tvCostGold);
		tvDisenchant = findById(pWindow.getContentView(), R.id.tvDisenchant);
		tvDisenchantGold = findById(pWindow.getContentView(),
				R.id.tvDisenchantGold);
		ivDust1 = findById(pWindow.getContentView(), R.id.imageView1);
		ivDust2 = findById(pWindow.getContentView(), R.id.ImageView01);
		ivDust3 = findById(pWindow.getContentView(), R.id.ImageView02);
		ivDust4 = findById(pWindow.getContentView(), R.id.ImageView03);

		tvCardName.setTypeface(font);
		tvClass.setTypeface(font);
		tvCrafted.setTypeface(font);
		tvQuality.setTypeface(font);
		tvSet.setTypeface(font);
		tvType.setTypeface(font);
		tvCost.setTypeface(font);
		tvCostGold.setTypeface(font);
		tvDisenchant.setTypeface(font);
		tvDisenchantGold.setTypeface(font);
	}

	private void addCards(List<Cards> list, int menuItemIndex) {

		// Add check for full deck
		if (Utils.getDeck(this, deckList.get(menuItemIndex)).size() == 30) {

			Crouton.makeText(
					this,
					"Cannot add card. Deck \"" + deckList.get(menuItemIndex)
							+ "\" is full.", Style.ALERT).show();
			return;
		}

		if (Utils.getDeck(this, deckList.get(menuItemIndex)) != null) {
			list = (List<Cards>) Utils.getDeck(this,
					deckList.get(menuItemIndex));
		} else {
			list = new ArrayList<Cards>();
		}

		list.add(cardList.get(position));
		Utils.saveDeck(this, deckList.get(menuItemIndex), list);
	}

	private void getDeckList() {
		InputStream instream = null;
		try {
			instream = openFileInput("decklist");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (instream != null) {
				ObjectInputStream objStream = new ObjectInputStream(instream);
				try {
					deckList = (ArrayList<String>) objStream.readObject();
					if (instream != null) {
						instream.close();
					}
					if (objStream != null) {
						objStream.close();
					}

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setupCardList() {
		Gson gson = new Gson();
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
		} else {
			cardList.clear();
			for (Cards card : cards) {
				cardList.add(card);
			}
		}
	}

	private void delayedLoad(Handler handler, int position) {

		final DisplayImageOptions noStubOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc(true).cacheInMemory(false).build();
		final String url = "http://54.224.222.135/"
				+ cardList.get(position).getImage() + ".png";
		final int cardListPos = position;

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (ivCardImage.getTag() == null
						|| ivCardImage.getTag() == "Standard") {
					ivCardImage.setImageBitmap(ImageCache.get(
							CardListActivity.this, Utils.getResIdByName(
									CardListActivity.this,
									cardList.get(cardListPos).getImage()
											.toString()
											+ "_premium")));
					ivCardImage.setTag("Premium");
				} else {
					loader.cancelDisplayTask(ivCardImage);
					loader.displayImage(url, ivCardImage, noStubOptions);
					ivCardImage.setTag("Standard");
				}

			}
		}, 350);
	}
}
