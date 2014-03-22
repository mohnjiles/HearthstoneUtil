package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
	private TextView tvMechanic;
	private TextView tvSort;
	private TextView tvClassSort;

	private MenuItem searchItem;

	private CustomDrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private String[] mActivityNames;

	private ArrayList<Cards> cardList;
	private ArrayList<String> deckList;
	private Cards[] cards;
	private ImageAdapter adapter;
	private CustomListAdapter adapter2;
	private String query;

	private int pos = OnItemSelectedListenerStandalone.position;
	private int position;
	private int menuItemIndex;
	private boolean isGrid = true;
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
		mDrawerLayout = findById(this, R.id.drawerLayout);
		mDrawerList = findById(this, R.id.left_drawer);

		// Show ActionBar (Top bar)
		getSupportActionBar().show();

		// Set ActionBar Title
		getSupportActionBar().setTitle("Hearthstone Companion");

		// Show Up button on ActionBar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Set grid invisible, list is default.
		listCards.setVisibility(View.INVISIBLE);

		// Get our JSON for GSON from the cards.json file in our "raw" directory
		// and use it to set up the list of cards
		setupCardList();

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
							mDrawerLayout.closeDrawers();
							break;
						case 1:
							startActivity(new Intent(CardListActivity.this,
									DeckGuides.class));
							break;
						case 2:
							startActivity(new Intent(CardListActivity.this,
									NewsActivity.class));
							break;
						case 3:
							startActivity(new Intent(CardListActivity.this,
									ArenaSimulator.class));
							break;
						case 4:
							startActivity(new Intent(CardListActivity.this,
									DeckSelector.class));
							break;
						}
					}
				});

		Collections.sort(cardList, new CardComparator(pos, reverse));

		// Create a new instance of our ImageAdapter class
		adapter = new ImageAdapter(CardListActivity.this, cardList);
		adapter2 = new CustomListAdapter(CardListActivity.this, cardList);

		// Set the gridview's adapter to our custom adapter
		grid.setAdapter(adapter);
		listCards.setAdapter(adapter2);

		// Get deck list from file
		getDeckList();

		MyWindow.setContext(this);

		grid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				MyWindow.setCardList(cardList);
				MyWindow.initiatePopupWindow(position, parent);
			}
		});
		listCards.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				MyWindow.setCardList(cardList);
				MyWindow.initiatePopupWindow(position, parent);
			}
		});

		// Sort the card list with our own custom Comparator
		// -- this sorts by Mana Cost

		registerForContextMenu(listCards);
		registerForContextMenu(grid);

		// This works now! Listener for when CheckBox is checked
		includeNeutralCards
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						if (mSearchView != null) {
							query = mSearchView.getQuery().toString()
									.toLowerCase(Utils.curLocale);
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
										&& card.getName()
												.toLowerCase(Utils.curLocale)
												.contains(query)
										&& card.getDescription().contains(
												mechanic)) {

									cardList.add(card);

								} else if (card.getClasss() == null
										&& mechanic.equals("Any")
										&& (card.getName().toLowerCase(
												Utils.curLocale)
												.contains(query))) {
									cardList.add(card);
								}
							}

							// Otherwise, user is unchecking the box, so remove
							// all generic cards.
						} else {
							for (Cards card : cards) {
								if (card.getClasss() == null
										&& !spinner.getSelectedItem().equals(
												"All")) {
									cardList.remove(card);
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
					}

				});

		cbReverse
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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
		inflater.inflate(R.menu.card_list_standalone, menu);
		searchItem = menu.findItem(R.id.action_search);
		menu.findItem(R.id.action_switch).setIcon(
				R.drawable.collections_view_as_list);
		mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		mSearchView.setOnQueryTextListener(new SearchListener2(
				CardListActivity.this, cardList, cards, grid, listCards,
				adapter, adapter2, searchItem));

		OnItemSelectedListenerStandalone listener = new OnItemSelectedListenerStandalone(
				CardListActivity.this, mSearchView, cardList, cards, adapter,
				adapter2);
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
			// When the back button on the ActionBar is pressed, go up one
			// Activity
			Utils.navigateUp(this);
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

		if (v.getId() == R.id.cardsList || v.getId() == R.id.gvDeck) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(cardList.get(info.position).getName());
			position = info.position;

			menu.add(Menu.NONE, 0, 0, "Add to new deck");

			if (deckList != null && deckList.size() != 0) {

				menuItems = new String[deckList.size()];

				for (int i = 0; i < deckList.size(); i++) {
					menuItems[i] = "Add to \"" + deckList.get(i) + "\"";
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
		final List<Integer> deckClasses = (List<Integer>) DeckUtils
				.getIntegerDeck(this, "deckclasses");
		if (!item.getTitle().equals("")
				&& item.getTitle().equals("Add to new deck")) {
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
			spinAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row);
			spinnerClass.setAdapter(spinAdapter);
			builder.setView(layout);
			builder.setPositiveButton("Save",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
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

								oos = new ObjectOutputStream(fos);
								oos.writeObject(deckClasses);
								oos.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							addCards(nameBox.getText().toString());
						}
					});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();

			return true;
		} else {
			addCards(deckList.get(menuItemIndex));
			return true;
		}
	}

	private void addCards(String deckName) {

		List<Cards> list = DeckUtils.getCardsList(this, deckName);

		if (list == null) {
			list = new ArrayList<Cards>();
			Log.w("CardListActivity", "addCards made a new ArrayList");
		}

		if (list.size() == 30) {
			Crouton.makeText(
					this,
					"Cannot add card. Deck \"" + deckList.get(menuItemIndex)
							+ "\" is full.", Style.ALERT).show();
			Log.w("CardListActivity", "addCards can't add that card");
			return;
		}

		list.add(cardList.get(position));

		Crouton.makeText(
				this,
				cardList.get(position).getName() + " added to \"" + deckName
						+ "\"", Style.INFO).show();

		Log.w("CardListActivity", "addCards added a card");
		new DeckUtils.SaveDeck(this, deckName, list).execute();
		Log.w("CardListActivity", "addCards might've saved the deck");
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
		cards = Utils.cards;
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
}
