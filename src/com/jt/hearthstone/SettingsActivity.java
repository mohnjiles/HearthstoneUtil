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
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

public class SettingsActivity extends PreferenceActivity{
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setTitle("Hearthstone Companion Settings");
		addPreferencesFromResource(R.xml.settings);
		
		CheckBoxPreference mCheckBoxPref = (CheckBoxPreference) findPreference("first_time");
		PreferenceCategory mCategory = (PreferenceCategory) findPreference("search_category");
		mCategory.removePreference(mCheckBoxPref);
		
		Preference button = (Preference) findPreference("button");
		Preference button2 = (Preference) findPreference("delete");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				ImageLoader loader = ImageLoader.getInstance();
				loader.clearDiscCache();
				loader.clearMemoryCache();
				Toast.makeText(SettingsActivity.this, "Caches cleared.", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		button2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
		
			@Override
			public boolean onPreferenceClick(Preference preference) {
				List<String> deckList = (List<String>) getDeck("decklist");
				List<Integer> classesDeck = (List<Integer>) getDeck("deckclasses");
				if (deckList.size() > 0) {
					deckList.clear();
					Toast.makeText(SettingsActivity.this, "Deck List cleared.", Toast.LENGTH_SHORT).show();
					Log.i("deckList.clear()", "Deck list cleared");
					saveDeck("decklist", deckList);
					Log.i("Deck saved", "Deck List Saved");
				}
				if (classesDeck.size() > 0) {
					classesDeck.clear();
					Toast.makeText(SettingsActivity.this, "Deck Classes cleared.", Toast.LENGTH_SHORT).show();
					Log.i("deckClasses.clear()", "Deck Classes cleared.");
					saveDeck("deckclasses", classesDeck);
					Log.i("Deck saved", "Deck Classes Saved");
				}
				
				
				return true;
			}
		});
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
