package com.jt.hearthstone;

import java.util.ArrayList;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Parent class for Arena-related Fragments.
 * Sets up and loads ViewPager and Fragments.
 * 
 * @author JT
 *
 */
public class ArenaSimulator extends ActionBarActivity {

	ViewPager myPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arena_simulator);
		
		getSupportActionBar().setTitle("Arena Simulator");
		
		// Show the Back arrow in the action bar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		myPager = (ViewPager) findViewById(R.id.pager);

		ArrayList<Fragment> fragments = new ArrayList<Fragment>();

		ArenaFragment fragZero = new ArenaFragment();
		fragments.add(fragZero);
		
		ArenaDeckFragment fragOne = new ArenaDeckFragment();
		fragments.add(fragOne);
		
		FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
		
		myPager.setAdapter(adapter);

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
			return super.onOptionsItemSelected(item);
		case R.id.action_restart:
			restartArena();
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Custom FragmentPagerAdapter class used to populate the
	 * ViewPager
	 * @author JT
	 *
	 */
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
		ArenaFragment frag = (ArenaFragment) getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager, 0));
		ArenaDeckFragment frag2 = (ArenaDeckFragment) getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager, 1));
		frag.doOnce = 0;
		frag.listDeck.clear();
		frag.textView.setText("Choose a Hero");
		frag.pickRandomHero();
		
		frag2.lvArena.setAdapter(frag.adapter);
		frag2.tvDeckSize.setText("0 / 30");
	}
	
	private static String makeFragmentName(int viewId, int index) {
	     return "android:switcher:" + viewId + ":" + index;
	}

}
