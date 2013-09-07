package com.jt.hearthstone;


import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);	
		setTitle("Hearthstone Util Settings");
		addPreferencesFromResource(R.xml.settings);
		
		Preference button = (Preference) findPreference("button");
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
	}
	

}
