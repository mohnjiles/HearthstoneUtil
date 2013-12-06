package com.jt.hearthstone;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class DeckFragmentHolder extends ActionBarActivity {

	static ActionBar aBar;
	static ViewPager myPager;
	private int position;
	static List<Integer> deckClasses;
	static FragmentAdapter adapter;
	static ProgressDialog dialog;
	private MenuItem searchItem;
	private SearchView mSearchView;
	int screenSize;
	static int previousPage = 1;

	private CardListFragment cardListFrag;
	private DeckActivity deckFrag;
	private ChartActivity chartFrag;
	private SimulatorFragment simFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deck_fragment_holder);
		// Show the Up button in the action bar.
		aBar = getSupportActionBar();
		aBar.setDisplayHomeAsUpEnabled(true);
		myPager = (ViewPager) findViewById(R.id.pager);

		position = getIntent().getIntExtra("position", 0);
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		cardListFrag = new CardListFragment();
		fragments.add(cardListFrag);

		deckFrag = new DeckActivity();
		fragments.add(deckFrag);

		chartFrag = new ChartActivity();
		fragments.add(chartFrag);

		simFrag = new SimulatorFragment();
		fragments.add(simFrag);

		adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
		// AsyncTasks async = new AsyncTasks();
		// GetClassesDeck getDeck = async.new GetClassesDeck(this, position);
		// getDeck.execute();

		setStuff(getCards());

		screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;

		myPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				previousPage = arg0;

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		switch (screenSize) {
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			getMenuInflater().inflate(R.menu.deck_fragment_holder, menu);
			break;
		case Configuration.SCREENLAYOUT_SIZE_LARGE:
		case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				getMenuInflater().inflate(R.menu.cards_tablet, menu);
				searchItem = menu.findItem(R.id.action_search);
				mSearchView = (SearchView) MenuItemCompat
						.getActionView(searchItem);
				mSearchView.setOnQueryTextListener(new CustomSearchListener(
						this));
			} else {
				getMenuInflater().inflate(R.menu.deck_fragment_holder, menu);
			}
			break;
		default:
			getMenuInflater().inflate(R.menu.deck_fragment_holder, menu);
			break;
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		CardListFragment cardListFrag = (CardListFragment) getSupportFragmentManager()
				.findFragmentByTag(Utils.makeFragmentName(R.id.pager, 0));

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
			return super.onOptionsItemSelected(item);
		case R.id.action_switch:
			if (screenSize > Configuration.SCREENLAYOUT_SIZE_NORMAL
					&& getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				boolean isGrid = cardListFrag.grid.getVisibility() == View.VISIBLE;
				if (isGrid) {
					cardListFrag.grid.setVisibility(View.INVISIBLE);
					cardListFrag.listCards.setVisibility(View.VISIBLE);
					deckFrag.gvDeck.setVisibility(View.INVISIBLE);
					deckFrag.lvDeck.setVisibility(View.VISIBLE);
					item.setTitle("Switch to grid view");
					item.setIcon(R.drawable.collections_view_as_grid);
					return super.onOptionsItemSelected(item);
				} else {
					cardListFrag.grid.setVisibility(View.VISIBLE);
					cardListFrag.listCards.setVisibility(View.INVISIBLE);
					deckFrag.gvDeck.setVisibility(View.VISIBLE);
					deckFrag.lvDeck.setVisibility(View.INVISIBLE);
					item.setTitle("Switch to list view");
					item.setIcon(R.drawable.collections_view_as_list);
					return super.onOptionsItemSelected(item);
				}
			}
		case R.id.action_clear:
			if (screenSize > Configuration.SCREENLAYOUT_SIZE_NORMAL
					&& getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				AlertDialog dialog;
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Remove all cards from this deck?");
				builder.setPositiveButton("Remove All",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								deckFrag.cardList.clear();
								;
								Utils.saveDeck(DeckFragmentHolder.this,
										DeckSelector.listDecks.get(position),
										deckFrag.cardList);
								doSomeStuff((List<Cards>) Utils.getDeck(
										DeckFragmentHolder.this,
										DeckSelector.listDecks.get(position)),
										DeckSelector.listDecks.get(position));
								deckFrag.tvNumCards.setText("0 / 30");
							}
						});
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				dialog = builder.create();
				dialog.show();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public class FragmentAdapter extends FragmentPagerAdapter {

		FragmentManager mManager;
		ArrayList<Fragment> localFragmentArray;
		int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;

		public FragmentAdapter(FragmentManager fm,
				ArrayList<Fragment> loadFragment) {
			super(fm);
			localFragmentArray = loadFragment;
			mManager = fm;
		}

		@Override
		public Fragment getItem(int arg0) {
			return localFragmentArray.get(arg0);
		}

		@Override
		public int getCount() {
			return localFragmentArray.size();
		}

		@Override
		public float getPageWidth(int position) {
			if (screenSize >= Configuration.SCREENLAYOUT_SIZE_LARGE
					&& getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				return (0.5f);
			} else {
				return (1.0f);
			}
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return super.isViewFromObject(arg0, arg1);
		}

		@Override
		public Parcelable saveState() {
			return super.saveState();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (screenSize >= Configuration.SCREENLAYOUT_SIZE_LARGE
					&& getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				switch (position) {
				case 0:
					return "Search / Deck";
				case 1:
					return "Deck / Breakdown";
				case 2:
					return "Breakdown / Draw Simulator";
				}
			} else {
				switch (position) {
				case 0:
					return "Search";
				case 1:
					return "Deck";
				case 2:
					return "Breakdown";
				case 3:
					return "Draw Simulator";
				}
			}
			return null;
		}
	}

	private List<Integer> getCards() {
		InputStream instream = null;
		List<Integer> list = null;
		try {
			instream = openFileInput("deckclasses");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			if (instream != null) {
				ObjectInputStream objStream = new ObjectInputStream(instream);
				try {
					list = (List<Integer>) objStream.readObject();
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
			} else {
				list = new ArrayList<Integer>();
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	private void setStuff(List<Integer> result) {
		myPager.setOffscreenPageLimit(3);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(previousPage);

		aBar.setTitle(getIntent().getStringExtra("name"));
		switch (result.get(position)) {
		case 0:
			aBar.setIcon(R.drawable.druid);
			break;
		case 1:
			aBar.setIcon(R.drawable.hunter);
			break;
		case 2:
			aBar.setIcon(R.drawable.mage);
			break;
		case 3:
			aBar.setIcon(R.drawable.paladin);
			break;
		case 4:
			aBar.setIcon(R.drawable.priest);
			break;
		case 5:
			aBar.setIcon(R.drawable.rogue);
			break;
		case 6:
			aBar.setIcon(R.drawable.shaman);
			break;
		case 7:
			aBar.setIcon(R.drawable.warlock);
			break;
		case 8:
			aBar.setIcon(R.drawable.warrior);
			break;
		}
	}

	public void doSomeStuff(List<Cards> result, String deckName) {
		int sp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				10, getResources().getDisplayMetrics());
		int bigSp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				14, getResources().getDisplayMetrics());

		int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		if (result == null) {
			result = (List<Cards>) Utils.getDeck(this, deckName);
		}
		deckFrag.cardList = result;
		if (result.size() == 0
				&& screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			deckFrag.tvNumCards.setTextSize(bigSp);
			deckFrag.tvNumCards
					.setText("Looks like there's nothing here. Swipe right to get started!");
			deckFrag.ivSwipe.setVisibility(View.VISIBLE);
		} else if (result.size() == 0
				&& screenSize > Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			deckFrag.tvNumCards.setTextSize(bigSp);
			deckFrag.tvNumCards
					.setText("Looks like there's nothing here. Add cards from the left to get started!");
			deckFrag.ivSwipe.setVisibility(View.VISIBLE);
		} else {
			deckFrag.tvNumCards.setTextSize(sp);
			deckFrag.tvNumCards.setText("" + result.size() + " / 30");
			deckFrag.ivSwipe.setVisibility(View.GONE);
		}
		deckFrag.adapter2 = new ImageAdapter(this, result);
		deckFrag.gvDeck.setAdapter(deckFrag.adapter2);
		deckFrag.adapter = new DeckListAdapter(this, result);
		deckFrag.lvDeck.setAdapter(deckFrag.adapter);
	}

}
