package com.jt.hearthstone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.gson.Gson;

import android.R.bool;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.StaticLayout;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	Spinner spinner;
	GridView grid;
	Cards[] cards;
	ImageAdapter adapter;
	public static ArrayList<Cards> druidCards;
	boolean any = true;
	boolean druid = false;
	boolean hunter = false;
	boolean mage = false;
	boolean paladin = false;
	boolean priest = false;
	boolean rogue = false;
	boolean shaman = false;
	boolean warlock = false;
	boolean warrior = false;

	public static ArrayList<Cards> cardList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().show();

		setTitle("Hearthstone Utilities");

		grid = (GridView) findViewById(R.id.cardsGrid);
		spinner = (Spinner) findViewById(R.id.spinner1);

		spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

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
		adapter.notifyDataSetChanged();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class CardComparator implements Comparator<Cards> {
		public int compare(Cards left, Cards right) {
			return left.getName().compareTo(right.getName());
		}
	}

	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			switch (pos) {
			case 0:
				break;
			case 1:
				druidCards = new ArrayList<Cards>();
				for (Cards card : cards) {
					if (card.getClasss().intValue() == 11) {
						druidCards.add(card);
					}
				}
				adapter.notifyDataSetChanged();
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	}
}
