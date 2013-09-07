package com.jt.hearthstone;

import java.util.Vector;

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
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ClassFragmentHolder extends ActionBarActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	ImageView ivClassIcon;
	TextView tvClassName;
	int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_fragment_holder);

		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mViewPager = (ViewPager) findViewById(R.id.classPager);
		ivClassIcon = (ImageView) findViewById(R.id.ivClassIcon); 
		tvClassName = (TextView) findViewById(R.id.tvClassName);
		
		// Position of item clicked
		position = getIntent().getIntExtra("position", 0);
		
		// Fragment Holder
		Vector<Fragment> fragments = new Vector<Fragment>();
		// Create a new InfoFragment
		InfoFragment fragmentOne = new InfoFragment();
		// Add it to our fragment holder
		fragments.add(fragmentOne);
		
		FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(adapter);
		
		switch (position) {
		case 0:
			ivClassIcon.setImageResource(R.drawable.druid_icon);
			tvClassName.setText("Druid");
			break;
		case 1:
			ivClassIcon.setImageResource(R.drawable.hunter_icon);
			tvClassName.setText("Hunter");
			break;
		case 2:
			ivClassIcon.setImageResource(R.drawable.mage_icon);
			tvClassName.setText("Mage");
			break;
		case 3:
			ivClassIcon.setImageResource(R.drawable.paladin_icon);
			tvClassName.setText("Paladin");
			break;
		case 4:
			ivClassIcon.setImageResource(R.drawable.priest_icon);
			tvClassName.setText("Priest");
			break;
		case 5:
			ivClassIcon.setImageResource(R.drawable.rogue_icon);
			tvClassName.setText("Rogue");
			break;
		case 6:
			ivClassIcon.setImageResource(R.drawable.shaman_icon);
			tvClassName.setText("Shaman");
			break;
		case 7:
			ivClassIcon.setImageResource(R.drawable.warlock_icon);
			tvClassName.setText("Warlock");
			break;
		case 8:
			ivClassIcon.setImageResource(R.drawable.warrior_icon);
			tvClassName.setText("Warrior");
			break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.class_fragment_holder, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}

	public class FragmentAdapter extends FragmentPagerAdapter {

		 FragmentManager mManager;
		 Vector<Fragment> localFragmentArray;

		 public FragmentAdapter(FragmentManager fm, Vector<Fragment> loadFragment) {
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
		 public void destroyItem(ViewGroup container, int position, Object object) {
		  this.notifyDataSetChanged();
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
		     switch (position)
		     {
		     case 0:
		    	 return "Info";
		     case 1:
		    	 return "Cards Available";
		     }
			 return null;
		    }

		}
}
