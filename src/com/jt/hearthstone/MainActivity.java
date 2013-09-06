package com.jt.hearthstone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Spinner;

public class MainActivity extends ActionBarActivity {

	Spinner spinner;
	GridView grid;
	Cards[] cards;
	ImageAdapter adapter;
	CheckBox includeNeutralCards;
	
	public static ImageLoader loader = ImageLoader.getInstance();
	public static ArrayList<Cards> druidCards;
	public static boolean any = true;
	public static boolean druid = false;
	public static boolean hunter = false;
	public static boolean mage = false;
	public static boolean paladin = false;
	public static boolean priest = false;
	public static boolean rogue = false;
	public static boolean shaman = false;
	public static boolean warlock = false;
	public static boolean warrior = false;

	public static ArrayList<Cards> cardList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().show();

		setTitle("Hearthstone Utilities");

		grid = (GridView) findViewById(R.id.cardsGrid);
		spinner = (Spinner) findViewById(R.id.spinner1);
		includeNeutralCards = (CheckBox) findViewById(R.id.checkBox1);
		CustomOnItemSelectedListener listener = new CustomOnItemSelectedListener();
		spinner.setOnItemSelectedListener(listener);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).denyCacheImageMultipleSizesInMemory().build();
		loader.init(config);

		Gson gson = new Gson();

		InputStream is = getResources().openRawResource(R.raw.cards);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String jsonString = writer.toString();

		cards = gson.fromJson(jsonString, Cards[].class);
		if (cardList == null) {
			cardList = new ArrayList<Cards>();
			for (Cards card : cards) {
				cardList.add(card);
			}
		}
		Collections.sort(cardList, new CardComparator());
		adapter = new ImageAdapter(this);
		grid.setAdapter(adapter);

		includeNeutralCards
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							for (Cards card : cards) {
								cardList.remove(card);
								if (card.getClasss() == null) {
									cardList.add(card);
								}
							}

							Collections.sort(cardList, new CardComparator());
							adapter.notifyDataSetChanged();
							grid.invalidate();
							grid.setAdapter(adapter);
						} else {
							spinner.setSelection(1);
						}
					}

				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class CardComparator implements Comparator<Cards> {
		public int compare(Cards left, Cards right) {
			return left.getCost().toString().compareTo(right.getCost().toString());
		}
	}

	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			switch (pos) {
			case 0:
				cardList.clear();
				for (Cards card : cards) {
					cardList.add(card);	
				}
				Collections.sort(cardList, new CardComparator());
				adapter.notifyDataSetChanged();
				grid.invalidate();
				grid.setAdapter(adapter);
				break;
			case 1:
				cardList.clear();
				for (Cards card : cards) {
					if (card.getClasss() != null) {
						if (card.getClasss().intValue() == 11) {
							cardList.add(card);
						}
					}

				}
				Collections.sort(cardList, new CardComparator());
				adapter.notifyDataSetChanged();
				grid.invalidate();
				grid.setAdapter(adapter);
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-ged();
		}
	}
}
