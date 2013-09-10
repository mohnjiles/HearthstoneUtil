package com.jt.hearthstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set main view to Activity_Main layout
		setContentView(R.layout.activity_main);
		// Show ActionBar (Title bar)
		getSupportActionBar().show();
		// Set ActionBar Title
		setTitle("Hearthstone Companion");
		
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
			startActivity(new Intent(MainActivity.this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void cardListPressed(View view) {
		startActivity(new Intent(MainActivity.this, CardListActivity.class));
	}
	public void classesPressed(View view) {
		startActivity(new Intent(MainActivity.this, ClassesActivity.class));
	}
}