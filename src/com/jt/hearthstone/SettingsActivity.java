package com.jt.hearthstone;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SettingsActivity extends PreferenceActivity {

	private List<String> deckList;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Hearthstone Companion Settings");
		addPreferencesFromResource(R.xml.settings);
		PackageInfo info = null;
		PackageManager manager = this.getPackageManager();
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new DeckUtils.GetStringList(this, "", 0, null, null)
				.execute("decklist");

		CheckBoxPreference mCheckBoxPref = (CheckBoxPreference) findPreference("first_time");
		CheckBoxPreference firstTimeDrawer = (CheckBoxPreference) findPreference("first_time_drawer");
		PreferenceCategory mCategory = (PreferenceCategory) findPreference("search_category");
		mCategory.removePreference(mCheckBoxPref);
		mCategory.removePreference(firstTimeDrawer);

		Preference button = (Preference) findPreference("button");
		Preference button2 = (Preference) findPreference("delete");
		Preference refresh = (Preference) findPreference("refresh");
		Preference version = (Preference) findPreference("version");
		Preference sources = (Preference) findPreference("licenses");

		sources.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				// Preparing views
				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.source_dialog,
						(ViewGroup) findViewById(R.id.linearLayout));
				WebView wvLicenses = (WebView) layout
						.findViewById(R.id.wvLicenses);

				String libraries = getResources().getString(R.string.libraries);
				String apacheLicense = getResources()
						.getString(R.string.apache);
				String jsoupLicense = getResources().getString(R.string.jsoup);

				Log.w("DEBUG", "webview: " + wvLicenses + " libraries: "
						+ libraries + "\nApache: " + apacheLicense);
				wvLicenses.loadData(libraries + apacheLicense + jsoupLicense,
						"text/html; charset=UTF-8", null);
				
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
					wvLicenses.setBackgroundColor(Color.argb(1, 0, 0, 0));
				} else {
					wvLicenses.setBackgroundColor(0x00000000);
				}
				
				AlertDialog dialog = new AlertDialog.Builder(
						SettingsActivity.this).create();
				dialog.setView(layout);
				dialog.show();

				return true;
			}
		});

		if (info != null) {
			version.setSummary("Version: " + info.versionName);
		}

		refresh.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				File cards = new File(getFilesDir() + "/cards.json");
				File version = new File(getFilesDir() + "/version.txt");
				if (cards.exists()) {
					cards.delete();
					Log.w("cards.json", "cards.json deleted");
				}
				if (version.exists()) {
					version.delete();
					Log.w("version.txt", "version.txt deleted");
				}

				return true;
			}
		});

		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				ImageLoader loader = ImageLoader.getInstance();
				if (!loader.isInited()) {
					loader.init(Utils.config(SettingsActivity.this));
				}
				loader.clearDiscCache();
				loader.clearMemoryCache();
				Crouton.makeText(SettingsActivity.this, "Caches cleared.",
						Style.INFO).show();
				return true;
			}
		});
		button2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				List<Integer> classesDeck = (List<Integer>) DeckUtils
						.getIntegerDeck(SettingsActivity.this, "deckclasses");
				deckList = DeckUtils.listDecks;
				if (deckList.size() > 0) {
					deckList.clear();
					Crouton.makeText(SettingsActivity.this,
							"Deck List cleared.", Style.INFO).show();
					Log.i("deckList.clear()", "Deck list cleared");
					new DeckUtils.SaveDeck(SettingsActivity.this, "decklist",
							deckList).execute();
					Log.i("Deck saved", "Deck List Saved");
				}
				if (classesDeck.size() > 0) {
					classesDeck.clear();
					Crouton.makeText(SettingsActivity.this,
							"Deck Classes cleared.", Style.INFO).show();
					Log.i("deckClasses.clear()", "Deck Classes cleared.");
					new DeckUtils.SaveDeck(SettingsActivity.this,
							"deckclasses", classesDeck).execute();
					Log.i("Deck saved", "Deck Classes Saved");
				}

				return true;
			}
		});
	}
}
