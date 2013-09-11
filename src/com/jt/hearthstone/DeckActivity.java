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

import butterknife.InjectView;
import butterknife.Views;
import static butterknife.Views.findById;
import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class DeckActivity extends ActionBarActivity {
	
	TextView tvDeckTitle;
	ArrayList<String> listDecks = DeckSelector.listDecks;
	ListView lvDeck;
	public static List<Cards> cardList;
	int position;
	int pos;
	DeckListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deck);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		lvDeck = findById(this, R.id.lvDeck);
		tvDeckTitle = findById(this, R.id.textView1);
		Intent intent = getIntent();
		position = intent.getIntExtra("position", 0);
		setTitle(listDecks.get(position));
		switch (position) {
		case 0:
			getDeck("deck_one");
			break;
		case 1:
			getDeck("deck_two");
			break;
		}
		tvDeckTitle.setText(listDecks.get(position));
		adapter = new DeckListAdapter(this, position);
		lvDeck.setAdapter(adapter);
		registerForContextMenu(lvDeck);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
		
	    if (v.getId()==R.id.lvDeck) {
	        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
	        menu.setHeaderTitle(cardList.get(info.position).getName());
	        pos = info.position;
	        String menuItems = "Remove card \"" + cardList.get(info.position).getName() + "\"";
	        menu.add(Menu.NONE, 0, 0, menuItems);
	  }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    int menuItemIndex = item.getItemId();
	    cardList.remove(pos);
	    switch (position) {
	    case 0:
	    	saveDeck("deck_one", cardList);
	    	break;
	    case 1:
	    	saveDeck("deck_two", cardList);
	    	break;
	    }
	    adapter.notifyDataSetChanged();
	    lvDeck.setAdapter(adapter);
	    return true;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deck, menu);
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
	
	private void getDeck(String deckName) {
		InputStream instream = null;
		try {
			instream = openFileInput(deckName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if (instream != null) {
				ObjectInputStream objStream = new ObjectInputStream(instream);
				try {
					cardList = (List<Cards>) objStream.readObject();
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

}
