package com.jt.hearthstone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Fragment that holds five ImageViews to display Basic class cards
 * 
 * @author JT
 * 
 */
public class BasicCardsFragment extends Fragment {

	private ImageLoader loader = ImageLoader.getInstance();

	private ImageView ivFreeCardOne;
	private ImageView ivFreeCardTwo;
	private ImageView ivFreeCardThree;
	private ImageView ivFreeCardFour;
	private ImageView ivFreeCardFive;

	private Cards[] cards;

	private String url = "http://54.224.222.135/";
	private int position;

	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.cards).cacheInMemory(false)
			.cacheOnDisc(true).build();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View V = inflater.inflate(R.layout.basic_cards_fragment, container,
				false);

		position = getActivity().getIntent().getIntExtra("position", 0);

		ivFreeCardOne = (ImageView) V.findViewById(R.id.ivFreeCardOne);
		ivFreeCardTwo = (ImageView) V.findViewById(R.id.ivFreeCardTwo);
		ivFreeCardThree = (ImageView) V.findViewById(R.id.ivFreeCardThree);
		ivFreeCardFour = (ImageView) V.findViewById(R.id.ivFreeCardFour);
		ivFreeCardFive = (ImageView) V.findViewById(R.id.ivFreeCardFive);

		return V;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (!loader.isInited()) {
			loader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		}

		getCards();

		switch (position) {
		case 0: // Druid
			loader.displayImage(url + "EX1_173.png", ivFreeCardOne, options);
			loader.displayImage(url + "CS2_011.png", ivFreeCardTwo, options);
			loader.displayImage(url + "CS2_008.png", ivFreeCardThree, options);
			loader.displayImage(url + "CS2_012.png", ivFreeCardFour, options);
			loader.displayImage(url + "CS2_232.png", ivFreeCardFive, options);
			break;
		case 1: // Hunter
			setCards("Animal Companion", "Starving Buzzard", "Hunter's Mark",
					"Tundra Rhino", "Kill Command");
			break;
		case 2: // Mage
			setCards("Frostbolt", "Mirror Image", "Frost Nova",
					"Water Elemental", "Flamestrike");
			break;
		case 3: // Paladin
			setCards("Truesilver Champion", "Consecration", "Humility",
					"Guardian of Kings", "Blessing of Kings");
			break;
		case 4: // Priest
			setCards("Divine Spirit", "Mind Vision", "Holy Nova", "Fade",
					"Mind Control");
			break;
		case 5: // Rogue
			setCards("Assassin's Blade", "Fan of Knives", "Shiv", "Vanish",
					"Sprint");
			break;
		case 6: // Shaman
			setCards("Bloodlust", "Flametongue Totem", "Totemic Might",
					"Windspeaker", "Fire Elemental");
			break;
		case 7: // Warlock
			setCards("Corruption", "Mortal Coil", "Soulfire",
					"Sacrificial Pact", "Dread Infernal");
			break;
		case 8: // Warrior
			setCards("Cleave", "Kor'kron Elite", "Whirlwind", "Shield Block",
					"Arcanite Reaper");
			break;
		}
	}

	// GSON to POJO
	private void getCards() {
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
		// The json String from the file
		String jsonString = writer.toString();

		// Set our pojo from the GSON data
		cards = gson.fromJson(jsonString, Cards[].class);
	}

	/**
	 * Function that sets card images based on the name
	 * of the card passed to it.
	 * 
	 * @param card1 Name of the first card to load, as a String
	 * @param card2 Name of the second card to load, as a String
	 * @param card3 Name of the third card to load, as a String
	 * @param card4 Name of the fourth card to load, as a String
	 * @param card5 Name of the fifth card to load, as a String
	 */
	private void setCards(String card1, String card2, String card3,
			String card4, String card5) {
		for (Cards card : cards) {
			String name = card.getName();
			if (name.equals(card1)) {
				loader.displayImage(url + card.getImage() + ".png",
						ivFreeCardOne, options);
			} else if (name.equals(card2)) {
				loader.displayImage(url + card.getImage() + ".png",
						ivFreeCardTwo, options);
			} else if (name.equals(card3)) {
				loader.displayImage(url + card.getImage() + ".png",
						ivFreeCardThree, options);
			} else if (name.equals(card4)) {
				loader.displayImage(url + card.getImage() + ".png",
						ivFreeCardFour, options);
			} else if (name.equals(card5)) {
				loader.displayImage(url + card.getImage() + ".png",
						ivFreeCardFive, options);
			}
		}
	}

}
