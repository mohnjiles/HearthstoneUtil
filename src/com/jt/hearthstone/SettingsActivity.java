package com.jt.hearthstone;


import java.util.List;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

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
				Crouton.makeText(SettingsActivity.this, "Caches cleared.", Style.INFO).show();
				return true;
			}
		});
		button2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
		
			@Override
			public boolean onPreferenceClick(Preference preference) {
				List<String> deckList = (List<String>) Utils.getDeck(SettingsActivity.this, "decklist");
				List<Integer> classesDeck = (List<Integer>) Utils.getDeck(SettingsActivity.this, "deckclasses");
				if (deckList.size() > 0) {
					deckList.clear();
					Crouton.makeText(SettingsActivity.this, "Deck List cleared.", Style.INFO).show();
					Log.i("deckList.clear()", "Deck list cleared");
					Utils.saveDeck(SettingsActivity.this, "decklist", deckList);
					Log.i("Deck saved", "Deck List Saved");
				}
				if (classesDeck.size() > 0) {
					classesDeck.clear();
					Crouton.makeText(SettingsActivity.this, "Deck Classes cleared.", Style.INFO).show();
					Log.i("deckClasses.clear()", "Deck Classes cleared.");
					Utils.saveDeck(SettingsActivity.this, "deckclasses", classesDeck);
					Log.i("Deck saved", "Deck Classes Saved");
				}
				
				
				return true;
			}
		});
	}
	

}
