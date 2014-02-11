package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;
import com.viewpagerindicator.TitlePageIndicator;

public class DeckFragmentHolder extends ActionBarActivity {

	static ActionBar aBar;
	private JazzyViewPager myPager;
	private int position;
	static List<Integer> deckClasses;
	static FragmentAdapter adapter;
	static ProgressDialog dialog;
	int screenSize;
	static int previousPage = 1;
	TransitionEffect tf;
	private SharedPreferences prefs;
	
	private boolean isQuickEditMode = false;

	private CardListFragment cardListFrag;
	private DeckActivity deckFrag;
	private SimulatorFragment simFrag;
	private TitlePageIndicator titles;
	private DeckChanceFragment deckChanceFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deck_fragment_holder);
		// Show the Up button in the action bar.
		aBar = getSupportActionBar();
		aBar.setDisplayHomeAsUpEnabled(true);
		myPager = (JazzyViewPager) findViewById(R.id.pager);
		titles = (TitlePageIndicator) findViewById(R.id.titles);

		position = getIntent().getIntExtra("position", 0);
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		cardListFrag = new CardListFragment();
		fragments.add(cardListFrag);

		deckFrag = new DeckActivity();
		fragments.add(deckFrag);

		simFrag = new SimulatorFragment();
		fragments.add(simFrag);

		deckChanceFrag = new DeckChanceFragment();
		fragments.add(deckChanceFrag);

		adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);

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
							startActivity(new Intent(DeckFragmentHolder.this,
									CardListActivity.class));
							break;
						case 1:
							startActivity(new Intent(DeckFragmentHolder.this,
									DeckGuides.class));
							break;
						case 2:
							startActivity(new Intent(DeckFragmentHolder.this,
									NewsActivity.class));
							break;
						case 3:
							startActivity(new Intent(DeckFragmentHolder.this,
									ArenaSimulator.class));
							break;
						case 4:
							Utils.navigateUp(DeckFragmentHolder.this);
							break;
						}
					}
				});

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (prefs.getString("transition_effect", "Standard")
				.equals("Accordion")) {
			tf = TransitionEffect.Accordion;
		} else if (prefs.getString("transition_effect", "Standard").equals(
				"Cube Out")) {
			tf = TransitionEffect.CubeOut;
		} else if (prefs.getString("transition_effect", "Standard").equals(
				"Cube In")) {
			tf = TransitionEffect.CubeIn;
		} else if (prefs.getString("transition_effect", "Standard").equals(
				"Flip Horizontal")) {
			tf = TransitionEffect.FlipHorizontal;
		} else if (prefs.getString("transition_effect", "Standard").equals(
				"Flip Vertical")) {
			tf = TransitionEffect.FlipVertical;
		} else if (prefs.getString("transition_effect", "Standard").equals(
				"Rotate Down")) {
			tf = TransitionEffect.RotateDown;
		} else if (prefs.getString("transition_effect", "Standard").equals(
				"Rotate Up")) {
			tf = TransitionEffect.RotateUp;
		} else if (prefs.getString("transition_effect", "Standard").equals(
				"Stack")) {
			tf = TransitionEffect.Stack;
		} else if (prefs.getString("transition_effect", "Standard").equals(
				"Standard")) {
			tf = TransitionEffect.Standard;
		} else if (prefs.getString("transition_effect", "Standard").equals(
				"Tablet")) {
			tf = TransitionEffect.Tablet;
		} else if (prefs.getString("transition_effect", "Standard").equals(
				"Zoom In")) {
			tf = TransitionEffect.ZoomIn;
		} else if (prefs.getString("transition_effect", "Standard").equals(
				"Zoom Out")) {
			tf = TransitionEffect.ZoomOut;
		} else {
			tf = TransitionEffect.Standard;
		}

		setStuff(getCards());

		titles.setViewPager(myPager);
		titles.setTextColor(Color.BLACK);
		titles.setSelectedColor(Color.BLACK);
		titles.setTypeface(TypefaceCache.get(getAssets(), "fonts/belwebd.ttf"));

		screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;

		titles.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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

		getMenuInflater().inflate(R.menu.deck_fragment_holder, menu);
		
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
			Utils.navigateUp(this);
			return super.onOptionsItemSelected(item);

		case R.id.action_settings:
			startActivity(new Intent(DeckFragmentHolder.this,
					SettingsActivity.class));
			break;
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
		public Object instantiateItem(ViewGroup container, final int position) {
			Object obj = super.instantiateItem(container, position);
			myPager.setObjectForPosition(obj, position);
			return obj;
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
			return 1.0f;
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
			switch (position) {
			case 0:
				return "Search";
			case 1:
				return "Deck";
			case 2:
				return "Draw Simulator";
			case 3:
				return "Chance";
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

		myPager.setAdapter(adapter);
		myPager.setCurrentItem(previousPage);
		myPager.setOffscreenPageLimit(3);
		myPager.setTransitionEffect(tf);

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
}
