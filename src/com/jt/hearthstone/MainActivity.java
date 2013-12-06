package com.jt.hearthstone;

import static butterknife.Views.findById;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {
	private Button btnCardList;
	private Button btnClasses;
	private Button btnDeckBuilder;
	private Button btnArena;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set main view to Activity_Main layout
		setContentView(R.layout.activity_main);

		// Show ActionBar (Title bar)
		getSupportActionBar().show();

		// Set ActionBar Title
		getSupportActionBar().setTitle("Hearthstone Companion");

		Typeface font = TypefaceCache.get(getAssets(),
				"fonts/belwebd.ttf");
		
		btnCardList = findById(this, R.id.btnCardList);
		btnClasses = findById(this, R.id.btnClasses);
		btnDeckBuilder = findById(this, R.id.btnDeckBuilder);
		btnArena = findById(this, R.id.btnArena);

		btnCardList.setTypeface(font);
		btnClasses.setTypeface(font);
		btnDeckBuilder.setTypeface(font);
		btnArena.setTypeface(font);
		btnCardList.setShadowLayer(1, 1, 1, Color.WHITE);
		btnClasses.setShadowLayer(1, 1, 1, Color.WHITE);
		btnDeckBuilder.setShadowLayer(1, 1, 1, Color.WHITE);
		btnArena.setShadowLayer(1, 1, 1, Color.WHITE);

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
		Intent intent = new Intent(MainActivity.this, CardListActivity.class);
		startActivity(intent);
	}

	public void classesPressed(View view) {
		startActivity(new Intent(MainActivity.this, ClassesActivity.class));
	}
	
	public void arenaPressed(View view) {
		startActivity(new Intent(MainActivity.this, ArenaSimulator.class));
	}

	public void deckSelectorPressed(View view) {
		startActivity(new Intent(MainActivity.this, DeckSelector.class));
	}
}