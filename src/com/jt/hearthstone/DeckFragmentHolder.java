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

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class DeckFragmentHolder extends ActionBarActivity {

	static ActionBar aBar;
	ViewPager myPager;
	ArrayList<String> listDecks = DeckSelector.listDecks;
	List<Cards> cardList;
	boolean isGrid = false;
	int position;
	public static FragmentAdapter adapter;
	DeckActivity fragmentOne;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deck_fragment_holder);
		// Show the Up button in the action bar.
		aBar = getSupportActionBar();
		aBar.setDisplayHomeAsUpEnabled(true);
		myPager = (ViewPager) findViewById(R.id.pager);

		position = getIntent().getIntExtra("position", 0);
		cardList = (List<Cards>) getDeck(listDecks.get(position));

		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		CardListFragment fragZero = new CardListFragment();
		fragments.add(fragZero);
		
		fragmentOne = new DeckActivity();
		fragments.add(fragmentOne);

		ChartActivity fragTwo = new ChartActivity();
		fragments.add(fragTwo);

		adapter = new FragmentAdapter(
				getSupportFragmentManager(), fragments);
		myPager.setOffscreenPageLimit(3);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(1);
		setTitle(getIntent().getStringExtra("name"));
		List<Integer> deckClasses = (List<Integer>) getDeck("deckclasses");
		switch (deckClasses.get(position)) {
		case 0:
			getSupportActionBar().setIcon(R.drawable.druid);
			break;
		case 1:
			getSupportActionBar().setIcon(R.drawable.hunter);
			break;
		case 2:
			getSupportActionBar().setIcon(R.drawable.mage);
			break;
		case 3:
			getSupportActionBar().setIcon(R.drawable.paladin);
			break;
		case 4:
			getSupportActionBar().setIcon(R.drawable.priest);
			break;
		case 5:
			getSupportActionBar().setIcon(R.drawable.rogue);
			break;
		case 6:
			getSupportActionBar().setIcon(R.drawable.shaman);
			break;
		case 7:
			getSupportActionBar().setIcon(R.drawable.warlock);
			break;
		case 8:
			getSupportActionBar().setIcon(R.drawable.warrior);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
			NavUtils.navigateUpFromSameTask(this);
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class FragmentAdapter extends FragmentPagerAdapter {

		FragmentManager mManager;
		ArrayList<Fragment> localFragmentArray;

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
		public int getItemPosition(Object object) {
		    return POSITION_NONE;
		}  

//		@Override
//		public void destroyItem(ViewGroup container, int position, Object object) {
//			this.notifyDataSetChanged();
//		}

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
				return "Mana Chart";
			}
			return null;
		}

	}

	private <T> List<?> getDeck(String deckName) {
		InputStream instream = null;
		List<?> list = null;
		try {
			instream = openFileInput(deckName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			if (instream != null) {
				ObjectInputStream objStream = new ObjectInputStream(instream);
				try {
					list = (List<?>) objStream.readObject();
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
				list = new ArrayList<T>();
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
	
	private void saveDeck(String deckName, Object object) {
		FileOutputStream fos = null;
		try {
			fos = openFileOutput(deckName, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
