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

import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class DeckActivity extends Fragment {

	ListView lvDeck;
	GridView gvDeck;
	TextView tvNumCards;
	ImageView ivSwipe;

	DeckListAdapter adapter;
	ImageAdapter adapter2;

	List<Cards> cardList;

	private TextView tvCardName;
	private TextView tvType;
	private TextView tvQuality;
	private TextView tvSet;
	private TextView tvCrafted;
	private TextView tvClass;
	private ImageView ivCardImage;
	private PopupWindow pWindow;

	private ArrayList<String> listDecks = DeckSelector.listDecks;
	private ImageLoader loader = ImageLoader.getInstance();

	private int position;
	private int pos;
	private boolean isGrid = false;
	private Typeface font;

	private ChartActivity chartFrag;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View V = inflater.inflate(R.layout.activity_deck, container, false);

		font = TypefaceCache
				.get(getActivity().getAssets(), "fonts/belwebd.ttf");

		Intent intent = getActivity().getIntent();
		position = intent.getIntExtra("position", 0);

		cardList = getDeck(listDecks.get(position));
		lvDeck = findById(V, R.id.lvDeck);
		gvDeck = findById(V, R.id.gvDeck);
		tvNumCards = findById(V, R.id.tvNumCards);
		ivSwipe = findById(V, R.id.imageView1);
		registerForContextMenu(lvDeck);
		registerForContextMenu(gvDeck);

		chartFrag = (ChartActivity) getActivity().getSupportFragmentManager()
				.findFragmentByTag(makeFragmentName(R.id.pager, 2));

		if (!loader.isInited()) {
			loader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		}

		int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		if (screenSize < Configuration.SCREENLAYOUT_SIZE_LARGE
				|| getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setHasOptionsMenu(true);
		}
		if (cardList.size() != 0) {
			ivSwipe.setVisibility(View.GONE);
		}
		tvNumCards.setTypeface(font);

		return V;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		doSomeStuff((List<Cards>) getDeck(listDecks.get(position)),
				listDecks.get(position));

		if (isGrid) {
			lvDeck.setVisibility(View.INVISIBLE);
			gvDeck.setVisibility(View.VISIBLE);
		} else {
			gvDeck.setVisibility(View.INVISIBLE);
			lvDeck.setVisibility(View.VISIBLE);
		}

		gvDeck.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				initiatePopupWindow(position);
			}
		});
		lvDeck.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				initiatePopupWindow(position);
			}
		});

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.lvDeck || v.getId() == R.id.gvDeck) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(cardList.get(info.position).getName());
			pos = info.position;
			String menuItems = "Remove card \""
					+ cardList.get(info.position).getName() + "\"";
			menu.add(Menu.FIRST, 0, 0, menuItems);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		final int bigSp = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 14, getResources()
						.getDisplayMetrics());
		int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;

		if (item.getGroupId() == Menu.FIRST) {
			cardList.remove(pos);
			Log.i("Card Removed", "Card removed, pos: " + pos);
			saveDeck(listDecks.get(position), cardList);
			Log.i("Deck Saved", "Deck name: " + listDecks.get(position));
			adapter.notifyDataSetChanged();
			adapter2.notifyDataSetChanged();
			lvDeck.setAdapter(adapter);
			gvDeck.setAdapter(adapter2);
			tvNumCards.setText("" + cardList.size() + " / 30");

			if (cardList.size() == 0
					&& screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL) {
				tvNumCards.setTextSize(bigSp);
				tvNumCards
						.setText("Looks like there's nothing here. Swipe right to get started!");
				ivSwipe.setVisibility(View.VISIBLE);
			} else if (cardList.size() == 0
					&& screenSize > Configuration.SCREENLAYOUT_SIZE_NORMAL) {
				tvNumCards.setTextSize(bigSp);
				tvNumCards
						.setText("Looks like there's nothing here. Add cards from the left to get started!");
				ivSwipe.setVisibility(View.VISIBLE);
			}

			if (chartFrag.mChart == null) {

			} else {
				((ViewGroup) chartFrag.mChart.getParent())
						.removeView(chartFrag.mChart);
				chartFrag.mCurrentSeries.clear();
				addSampleData();
				chartFrag.layout2.addView(chartFrag.mChart);
			}

			if (chartFrag.mPieChart == null) {

			} else {
				((ViewGroup) chartFrag.mPieChart.getParent())
						.removeView(chartFrag.mPieChart);
				chartFrag.mSeries.clear();
				chartFrag.mRenderer2.removeAllRenderers();
				addPieData(cardList);
				chartFrag.layout.addView(chartFrag.mPieChart);
			}

		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.deck, menu);

		MenuItem listSwitcher = menu.findItem(R.id.action_switch);

		if (isGrid) {
			listSwitcher.setTitle("Switch to list view");
			listSwitcher.setIcon(R.drawable.collections_view_as_list);
		} else {
			listSwitcher.setTitle("Switch to grid view");
			listSwitcher.setIcon(R.drawable.collections_view_as_grid);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_switch:
			if (isGrid) {
				gvDeck.setVisibility(View.INVISIBLE);
				lvDeck.setVisibility(View.VISIBLE);
				isGrid = false;
				item.setIcon(R.drawable.collections_view_as_grid);
				item.setTitle("Switch to grid view");
			} else {
				gvDeck.setVisibility(View.VISIBLE);
				lvDeck.setVisibility(View.INVISIBLE);
				isGrid = true;
				item.setIcon(R.drawable.collections_view_as_list);
				item.setTitle("Switch to list view");
			}
			break;
		case R.id.action_clear:
			AlertDialog dialog;
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Remove all cards from this deck?");
			builder.setPositiveButton("Remove All",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							cardList.clear();
							saveDeck(listDecks.get(position), cardList);
							tvNumCards.setText("0 / 30");
						}
					});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			dialog = builder.create();
			dialog.show();
		}
		return super.onOptionsItemSelected(item);
	}

	public void refresh() {
		adapter.notifyDataSetChanged();
		adapter2.notifyDataSetChanged();
		gvDeck.setAdapter(adapter2);
		lvDeck.setAdapter(adapter);
	}

	private void saveDeck(String deckName, Object object) {
		FileOutputStream fos = null;
		try {
			fos = getActivity().openFileOutput(deckName, Context.MODE_PRIVATE);
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

		doSomeStuff((List<Cards>) getDeck(deckName), deckName);
	}

	private void initiatePopupWindow(int position) {
		try {
			// get screen size of device
			int screenSize = getResources().getConfiguration().screenLayout
					& Configuration.SCREENLAYOUT_SIZE_MASK;

			// convert px to dips for multiple screens
			int dipsWidthPortrait_Normal = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 300, getResources()
							.getDisplayMetrics());
			int dipsHeightPortrait_Normal = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 475, getResources()
							.getDisplayMetrics());
			int dipsWidthLandscape_Normal = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 475, getResources()
							.getDisplayMetrics());
			int dipsHeightLandscape_Normal = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 300, getResources()
							.getDisplayMetrics());
			int dipsWidthPortrait_Large = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 425, getResources()
							.getDisplayMetrics());
			int dipsHeightPortrait_Large = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 550, getResources()
							.getDisplayMetrics());
			int dipsWidthLandscape_Large = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 550, getResources()
							.getDisplayMetrics());
			int dipsHeightLandscape_Large = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 425, getResources()
							.getDisplayMetrics());
			int dipsWidthPortrait_Small = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 200, getResources()
							.getDisplayMetrics());
			int dipsHeightPortrait_Small = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 350, getResources()
							.getDisplayMetrics());
			int dipsWidthLandscape_Small = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 350, getResources()
							.getDisplayMetrics());
			int dipsHeightLandscape_Small = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 200, getResources()
							.getDisplayMetrics());

			// We need to get the instance of the LayoutInflater,
			// Gotta give the PopupWindow a layout
			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.card_popup, null);

			// make different popupWindows for different screen sizes
			switch (screenSize) {

			// XLARGE = 10"+ Tablets usually
			case Configuration.SCREENLAYOUT_SIZE_XLARGE:
				doSomeWindow(layout, dipsWidthLandscape_Large,
						dipsHeightLandscape_Large, dipsWidthPortrait_Large,
						dipsHeightPortrait_Large);
				break;

			// LARGE = 7"+ Tablets usually, maybe some giant phones
			case Configuration.SCREENLAYOUT_SIZE_LARGE:
				doSomeWindow(layout, // View of the popupWindow
						dipsWidthLandscape_Large, // Width for landscape
						dipsHeightLandscape_Large, // Height for landscape
						dipsWidthPortrait_Large, // Width for portrait
						dipsHeightPortrait_Large); // Height for portrait
				break;

			// NORMAL = 95% of all phones
			case Configuration.SCREENLAYOUT_SIZE_NORMAL:
				doSomeWindow(layout, dipsWidthLandscape_Normal,
						dipsHeightLandscape_Normal, dipsWidthPortrait_Normal,
						dipsHeightPortrait_Normal);
				break;
			default:
				doSomeWindow(layout, dipsWidthLandscape_Small,
						dipsHeightLandscape_Small, dipsWidthPortrait_Small,
						dipsHeightPortrait_Small);
				break;
			}

			// Get card image
			String url = "http://54.224.222.135/"
					+ cardList.get(position).getImage() + ".png";
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.cards).cacheInMemory(false)
					.cacheOnDisc(true).build();
			loader.displayImage(url, ivCardImage, options);

			// Get card name
			tvCardName.setText(cardList.get(position).getName());

			int classs = 0;
			if (cardList.get(position).getClasss() != null) {
				classs = cardList.get(position).getClasss().intValue();
			}

			int type = cardList.get(position).getType().intValue();
			int quality = cardList.get(position).getQuality().intValue();
			int set = cardList.get(position).getSet().intValue();

			tvCrafted.setText(cardList.get(position).getDescription());

			if (classs == Classes.DRUID.getValue()) {
				int druid = getResources().getColor(R.color.druid);
				tvClass.setTextColor(druid);
				tvClass.setText("Druid");
			} else if (classs == Classes.HUNTER.getValue()) {
				int hunter = getResources().getColor(R.color.hunter);
				tvClass.setTextColor(hunter);
				tvClass.setText("Hunter");
			} else if (classs == Classes.MAGE.getValue()) {
				int mage = getResources().getColor(R.color.mage);
				tvClass.setTextColor(mage);
				tvClass.setText("Mage");
			} else if (classs == Classes.PALADIN.getValue()) {
				int paladin = getResources().getColor(R.color.paladin);
				tvClass.setTextColor(paladin);
				tvClass.setText("Paladin");
			} else if (classs == Classes.PRIEST.getValue()) {
				int priest = getResources().getColor(R.color.priest);
				tvClass.setTextColor(priest);
				tvClass.setText("Priest");
			} else if (classs == Classes.ROGUE.getValue()) {
				int rogue = getResources().getColor(R.color.rogue);
				tvClass.setTextColor(rogue);
				tvClass.setText("Rogue");
			} else if (classs == Classes.SHAMAN.getValue()) {
				int shaman = getResources().getColor(R.color.shaman);
				tvClass.setTextColor(shaman);
				tvClass.setText("Shaman");
			} else if (classs == Classes.WARLOCK.getValue()) {
				int warlock = getResources().getColor(R.color.warlock);
				tvClass.setTextColor(warlock);
				tvClass.setText("Warlock");
			} else if (classs == Classes.WARRIOR.getValue()) {
				int warrior = getResources().getColor(R.color.warrior);
				tvClass.setTextColor(warrior);
				tvClass.setText("Warrior");
			} else if (classs == 0) {
				tvClass.setTextColor(Color.GREEN);
				tvClass.setText("All Classes");
			}

			// Set the type (minion, ability, etc)
			switch (type) {
			case 3:
				tvType.setText("Hero");
				break;
			case 4:
				tvType.setText("Minion");
				break;
			case 5:
				tvType.setText("Ability");
				break;
			case 7:
				tvType.setText("Weapon");
				break;
			case 10:
				tvType.setText("Hero Power");
				break;
			default: // If card doesn't have a type, just hide the textview.
				tvType.setVisibility(View.GONE);
				break;
			}

			// Set rarity of the card
			switch (quality) {
			case 0:
				int free = getResources().getColor(R.color.free);
				tvQuality.setTextColor(free);
				tvQuality.setText("Free");
				break;
			case 1:
				tvQuality.setText("Common");
				break;
			case 3:
				int rare = getResources().getColor(R.color.rare);
				tvQuality.setTextColor(rare);
				tvQuality.setText("Rare");
				break;
			case 4:
				int epic = getResources().getColor(R.color.epic);
				tvQuality.setTextColor(epic);
				tvQuality.setText("Epic");
				break;
			case 5:
				int legendary = getResources().getColor(R.color.legendary);
				tvQuality.setTextColor(legendary);
				tvQuality.setText("Legendary");
				break;
			default: // No rarity? This should only happen for some abilities.
				tvQuality.setVisibility(View.GONE); // Hides it.
				break;
			}

			switch (set) {
			case 2:
				tvSet.setText("Set: Basic");
				break;
			case 3:
				tvSet.setText("Set: Expert");
				break;
			case 4:
				tvSet.setText("Set: Reward");
				break;
			case 5:
				tvSet.setText("Set: Missions");
				break;
			}

			// If we ran in to a problem
		} catch (Exception e) {
			Log.w("PopupWindow",
					"" + e.getMessage() + e.getStackTrace()[0].getLineNumber());
		}
	}

	// Runs the popupWindoww, getting view from inflater & dimensions based on
	// screen size
	@SuppressWarnings("deprecation")
	private void doSomeWindow(View layout, int widthLandscape,
			int heightLandscape, int widthPortrait, int heightPortrait) {

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			pWindow = new PopupWindow(layout, widthLandscape, heightLandscape,
					true);
			pWindow.setBackgroundDrawable(new BitmapDrawable());
			pWindow.setOutsideTouchable(true);
			pWindow.setAnimationStyle(R.style.AnimationPopup);
			pWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
			pWindow.setFocusable(true);

		} else {
			pWindow = new PopupWindow(layout, widthPortrait, heightPortrait,
					true);
			pWindow.setBackgroundDrawable(new BitmapDrawable());
			pWindow.setOutsideTouchable(true);
			pWindow.setAnimationStyle(R.style.AnimationPopup);
			pWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
			pWindow.setFocusable(true);

		}

		ivCardImage = findById(pWindow.getContentView(), R.id.ivCardImages);
		tvCardName = findById(pWindow.getContentView(), R.id.tvCardName);
		tvClass = findById(pWindow.getContentView(), R.id.tvClass);
		tvCrafted = findById(pWindow.getContentView(), R.id.tvCrafted);
		tvQuality = findById(pWindow.getContentView(), R.id.tvQuality);
		tvSet = findById(pWindow.getContentView(), R.id.tvSet);
		tvType = findById(pWindow.getContentView(), R.id.tvType);

		tvCardName.setTypeface(font);
		tvClass.setTypeface(font);
		tvCrafted.setTypeface(font);
		tvQuality.setTypeface(font);
		tvSet.setTypeface(font);
		tvType.setTypeface(font);

	}

	@SuppressWarnings("unchecked")
	private List<Cards> getDeck(String deckName) {
		InputStream instream = null;
		List<Cards> list = null;
		try {
			instream = getActivity().openFileInput(deckName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			if (instream != null) {
				ObjectInputStream objStream = new ObjectInputStream(instream);
				try {
					list = (List<Cards>) objStream.readObject();
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
				list = new ArrayList<Cards>();
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

	private void addSampleData() {
		int[] costs = new int[50];
		for (Cards card : cardList) {
			if (card.getCost() != null) {
				costs[card.getCost().intValue()]++;
				Log.i("cost", "" + costs[card.getCost().intValue()]);
				chartFrag.mCurrentSeries.add(card.getCost().intValue(),
						costs[card.getCost().intValue()]);
			}
		}
	}

	private void addPieData(List<Cards> cardList) {
		int minions = 0;
		int abilities = 0;
		int weapons = 0;
		final int[] colors = { Color.rgb(0, 171, 249), Color.rgb(245, 84, 0),
				Color.rgb(60, 242, 0) };
		final int[] colors2 = { Color.rgb(0, 108, 229), Color.rgb(225, 23, 3),
				Color.rgb(8, 196, 0) };
		for (Cards card : cardList) {
			if (card.getType() != null && card.getType().intValue() == 4) {
				minions++;
			} else if (card.getType() != null && card.getType().intValue() == 5) {
				abilities++;
			} else if (card.getType() != null && card.getType().intValue() == 7) {
				weapons++;
			}
		}
		if (abilities != 0) {
			chartFrag.mSeries.add("Spells", abilities);
			SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
			seriesRenderer.setDisplayChartValues(true);
			seriesRenderer.setGradientEnabled(true);
			seriesRenderer.setGradientStart(0, colors[0]);
			seriesRenderer.setGradientStop(20, colors2[0]);
			chartFrag.mRenderer2.addSeriesRenderer(seriesRenderer);
		}
		if (minions != 0) {
			chartFrag.mSeries.add("Minions", minions);
			SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
			seriesRenderer.setDisplayChartValues(true);
			seriesRenderer.setGradientEnabled(true);
			seriesRenderer.setGradientStart(0, colors[1]);
			seriesRenderer.setGradientStop(20, colors2[1]);
			chartFrag.mRenderer2.addSeriesRenderer(seriesRenderer);
		}
		if (weapons != 0) {
			chartFrag.mSeries.add("Weapons", weapons);
			SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
			seriesRenderer.setDisplayChartValues(true);
			seriesRenderer.setGradientEnabled(true);
			seriesRenderer.setGradientStart(0, colors[2]);
			seriesRenderer.setGradientStop(20, colors2[2]);
			chartFrag.mRenderer2.addSeriesRenderer(seriesRenderer);
		}
	}

	public void doSomeStuff(List<Cards> result, String deckName) {
		
		// Get text sizes in sp
		int sp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				10, getActivity().getResources().getDisplayMetrics());
		int bigSp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				14, getActivity().getResources().getDisplayMetrics());

		// Get screen size
		int screenSize = getActivity().getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		
		// If a null list was passed, try to manually lookup deck
		if (result == null) {
			result = (List<Cards>) getDeck(deckName);
		}
		cardList = result;
		if (result.size() == 0
				&& screenSize <= Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			tvNumCards.setTextSize(bigSp);
			tvNumCards
					.setText("Looks like there's nothing here. Swipe right to get started!");
			ivSwipe.setVisibility(View.VISIBLE);
		} else if (result.size() == 0
				&& screenSize > Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			tvNumCards.setTextSize(bigSp);
			tvNumCards
					.setText("Looks like there's nothing here. Add cards from the left to get started!");
			ivSwipe.setVisibility(View.VISIBLE);
		} else {
			tvNumCards.setTextSize(sp);
			tvNumCards.setText("" + result.size() + " / 30");
			ivSwipe.setVisibility(View.GONE);
		}
		adapter2 = new ImageAdapter(getActivity(), result);
		gvDeck.setAdapter(adapter2);
		adapter = new DeckListAdapter(getActivity(), position,
				result);
		lvDeck.setAdapter(adapter);
	}

	private static String makeFragmentName(int viewId, int index) {
		return "android:switcher:" + viewId + ":" + index;
	}
}
