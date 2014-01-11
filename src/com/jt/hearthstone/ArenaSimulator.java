package com.jt.hearthstone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.IconPageIndicator;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import android.R.integer;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;

/**
 * Parent class for Arena-related Fragments. Sets up and loads ViewPager and
 * Fragments.
 * 
 * @author JT
 * 
 */
public class ArenaSimulator extends ActionBarActivity {

	JazzyViewPager myPager;
	TitlePageIndicator titleIndicator;
	FragmentAdapter adapter;
	ActionBar aBar;
	ArenaDeckFragment deckFragment;
	TransitionEffect tf;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arena_simulator);

		aBar = getSupportActionBar();
		aBar.setTitle("Arena Simulator");

		// Show the Back arrow in the action bar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		myPager = (JazzyViewPager) findViewById(R.id.pager);
		titleIndicator = (TitlePageIndicator) findViewById(R.id.titles);

		ArrayList<Fragment> fragments = new ArrayList<Fragment>();

		ArenaFragment fragZero = new ArenaFragment();
		fragments.add(fragZero);

		ArenaDeckFragment fragOne = new ArenaDeckFragment();
		fragments.add(fragOne);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (prefs.getString("transition_effect", "Stack").equals("Accordion")) {
			tf = TransitionEffect.Accordion;
		} else if (prefs.getString("transition_effect", "Stack").equals(
				"Cube Out")) {
			tf = TransitionEffect.CubeOut;
		} else if (prefs.getString("transition_effect", "Stack").equals(
				"Cube In")) {
			tf = TransitionEffect.CubeIn;
		} else if (prefs.getString("transition_effect", "Stack").equals(
				"Flip Horizontal")) {
			tf = TransitionEffect.FlipHorizontal;
		} else if (prefs.getString("transition_effect", "Stack").equals(
				"Flip Vertical")) {
			tf = TransitionEffect.FlipVertical;
		} else if (prefs.getString("transition_effect", "Stack").equals(
				"Rotate Down")) {
			tf = TransitionEffect.RotateDown;
		} else if (prefs.getString("transition_effect", "Stack").equals(
				"Rotate Up")) {
			tf = TransitionEffect.RotateUp;
		} else if (prefs.getString("transition_effect", "Stack")
				.equals("Stack")) {
			tf = TransitionEffect.Stack;
		} else if (prefs.getString("transition_effect", "Stack").equals(
				"Standard")) {
			tf = TransitionEffect.Standard;
		} else if (prefs.getString("transition_effect", "Stack").equals(
				"Tablet")) {
			tf = TransitionEffect.Tablet;
		} else if (prefs.getString("transition_effect", "Stack").equals(
				"Zoom In")) {
			tf = TransitionEffect.ZoomIn;
		} else if (prefs.getString("transition_effect", "Stack").equals(
				"Zoom Out")) {
			tf = TransitionEffect.ZoomOut;
		} else {
			tf = TransitionEffect.Standard;
		}

		adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
		myPager.setOffscreenPageLimit(3);
		myPager.setAdapter(adapter);
		titleIndicator.setSelectedColor(Color.BLACK);
		titleIndicator.setTypeface(TypefaceCache.get(getAssets(),
				"fonts/belwebd.ttf"));
		titleIndicator.setViewPager(myPager);
		myPager.setTransitionEffect(tf);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.arena_simulator, menu);
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_restart:
			restartArena();
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Custom FragmentPagerAdapter class used to populate the ViewPager
	 * 
	 * @author JT
	 * 
	 */
	public class FragmentAdapter extends FragmentPagerAdapter implements
			Serializable {

		private static final long serialVersionUID = 1337L;

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
				return "Arena";
			case 1:
				return "Deck";
			}
			return null;
		}
	}

	private void restartArena() {
		ArenaFragment frag = (ArenaFragment) getSupportFragmentManager()
				.findFragmentByTag(Utils.makeFragmentName(R.id.pager, 0));
		ArenaDeckFragment frag2 = (ArenaDeckFragment) getSupportFragmentManager()
				.findFragmentByTag(Utils.makeFragmentName(R.id.pager, 1));
		frag.doOnce = 0;
		if (frag.listDeck != null) {
			frag.listDeck.clear();
		}

		if (frag.listChoices != null) {
			frag.listChoices.clear();
		}

		frag.textView.setText("Choose a Hero");
		frag.pickRandomHero();

		frag2.lvArena.setAdapter(frag.adapter);
		frag2.tvDeckSize.setText("0 / 30");
		frag2.update(frag.listDeck);
	}

}
