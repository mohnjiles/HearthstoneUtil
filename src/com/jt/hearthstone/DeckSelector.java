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
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class DeckSelector extends ActionBarActivity {


	static ArrayList<String> listDecks = new ArrayList<String>();
	static ArrayList<Integer> deckClasses;
	static ProgressDialog dialog;
	
	private GridView gvDecks;
	
	private DecksAdapter adapter;
	private int position;
	
	private Typeface font;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deck_selector);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Decks");
		gvDecks = findById(this, R.id.gvDecks);
		registerForContextMenu(gvDecks);
		deckClasses = (ArrayList<Integer>) Utils.getDeck(this, "deckclasses");
		
		font = TypefaceCache.get(getAssets(), "fonts/belwebd.ttf");

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
					listDecks = (ArrayList<String>) objStream.readObject();
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
		adapter = new DecksAdapter(this, listDecks, deckClasses);
		gvDecks.setAdapter(adapter);
		gvDecks.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(DeckSelector.this,
						DeckFragmentHolder.class);
				intent.putExtra("position", arg2);
				intent.putExtra("name", listDecks.get(arg2));
				startActivity(intent);
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deck_selector, menu);
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
		case R.id.action_add:
			// Preparing views
			LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.dialog_layout,
					(ViewGroup) findViewById(R.id.linearLayout));
			// layout_root should be the name of the "top-level" layout node in
			// the dialog_layout.xml file.
			final EditText nameBox = (EditText) layout
					.findViewById(R.id.etDeckName);
			final Spinner spinner = (Spinner) layout
					.findViewById(R.id.spinClass);
			final TextView tvOne = (TextView) layout
					.findViewById(R.id.tvCost);
			final TextView tvTwo = (TextView) layout
					.findViewById(R.id.tvNumCards);
			
			tvOne.setTypeface(font);
			tvTwo.setTypeface(font);

			// Building dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			String[] classes = getResources().getStringArray(
					R.array.ClassesWithoutAny);
			CustomArrayAdapter spinAdapter = new CustomArrayAdapter(this,
					R.layout.spinner_row, R.id.name, classes);
			spinAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row);
			nameBox.setTypeface(font);
			spinner.setAdapter(spinAdapter);
			builder.setView(layout);
			builder.setPositiveButton("Save",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							listDecks.add(nameBox.getText().toString());
							deckClasses.add(spinner.getSelectedItemPosition());
							Utils.saveDeck(DeckSelector.this, "decklist", listDecks);
							/*************** save corresponding class number **********/
							Utils.saveDeck(DeckSelector.this, "deckclasses", deckClasses);
							adapter.notifyDataSetChanged();
							gvDecks.setAdapter(adapter);
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
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		if (v.getId() == R.id.gvDecks) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(listDecks.get(info.position));
			position = info.position;
			String menuItems = "Remove deck \"" + listDecks.get(info.position)
					+ "\"";
			menu.add(Menu.NONE, 0, 0, menuItems);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		this.deleteFile(listDecks.get(position));
		deckClasses.remove(position);
		Utils.saveDeck(this, "deckclasses", deckClasses);

		listDecks.remove(position);
		Utils.saveDeck(this, "decklist", listDecks);
		adapter.notifyDataSetChanged();
		gvDecks.setAdapter(adapter);
		return super.onContextItemSelected(item);
	}

}
