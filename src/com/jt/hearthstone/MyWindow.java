package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.DiscCacheUtil;

public class MyWindow {

	private static Context cxt = HearthstoneUtil.getAppContext();

	private static ImageLoader loader = ImageLoader.getInstance();
	private static List<Cards> cardList;

	private static PopupWindow pWindow;
	private static TextView tvCardName;
	private static TextView tvType;
	private static TextView tvQuality;
	private static TextView tvSet;
	private static TextView tvCrafted;
	private static TextView tvClass;
	private static TextView tvCost;
	private static TextView tvCostGold;
	private static TextView tvDisenchant;
	private static TextView tvDisenchantGold;
	private static ImageView ivCardImage;
	private static ImageView ivDust1;
	private static ImageView ivDust2;
	private static ImageView ivDust3;
	private static ImageView ivDust4;
	private static Typeface font = TypefaceCache.get(cxt.getAssets(),
			"fonts/belwebd.ttf");

	private static int dipsWidthPortrait_Normal = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, cxt
					.getResources().getDisplayMetrics());
	private static int dipsHeightPortrait_Normal = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 460, cxt
					.getResources().getDisplayMetrics());
	private static int dipsWidthLandscape_Normal = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 475, cxt
					.getResources().getDisplayMetrics());
	private static int dipsHeightLandscape_Normal = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, cxt
					.getResources().getDisplayMetrics());

	private static int dipsWidthPortrait_Large = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 425, cxt
					.getResources().getDisplayMetrics());
	private static int dipsHeightPortrait_Large = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600, cxt
					.getResources().getDisplayMetrics());
	private static int dipsWidthLandscape_Large = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 650, cxt
					.getResources().getDisplayMetrics());
	private static int dipsHeightLandscape_Large = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 425, cxt
					.getResources().getDisplayMetrics());

	private static int dipsWidthPortrait_Small = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, cxt
					.getResources().getDisplayMetrics());
	private static int dipsHeightPortrait_Small = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, cxt
					.getResources().getDisplayMetrics());
	private static int dipsWidthLandscape_Small = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, cxt
					.getResources().getDisplayMetrics());
	private static int dipsHeightLandscape_Small = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, cxt
					.getResources().getDisplayMetrics());

	private static int dipsWidthPortrait_XLarge = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600, cxt
					.getResources().getDisplayMetrics());
	private static int dipsHeightPortrait_XLarge = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 750, cxt
					.getResources().getDisplayMetrics());
	private static int dipsWidthLandscape_XLarge = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 900, cxt
					.getResources().getDisplayMetrics());
	private static int dipsHeightLandscape_XLarge = (int) TypedValue
			.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600, cxt
					.getResources().getDisplayMetrics());

	static void setContext(Context mContext) {
		cxt = mContext;
	}

	static void doSomeWindow(View layout, int widthLandscape,
			int heightLandscape, int widthPortrait, int heightPortrait) {

		if (cxt.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
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
		tvCost = findById(pWindow.getContentView(), R.id.tvCost);
		tvCostGold = findById(pWindow.getContentView(), R.id.tvCostGold);
		tvDisenchant = findById(pWindow.getContentView(), R.id.tvDisenchant);
		tvDisenchantGold = findById(pWindow.getContentView(),
				R.id.tvDisenchantGold);

		ivDust1 = findById(pWindow.getContentView(), R.id.imageView1);
		ivDust2 = findById(pWindow.getContentView(), R.id.ImageView01);
		ivDust3 = findById(pWindow.getContentView(), R.id.ImageView02);
		ivDust4 = findById(pWindow.getContentView(), R.id.ImageView03);

		tvCardName.setTypeface(font);
		tvClass.setTypeface(font);
		tvCrafted.setTypeface(font);
		tvQuality.setTypeface(font);
		tvSet.setTypeface(font);
		tvType.setTypeface(font);
		tvCost.setTypeface(font);
		tvCostGold.setTypeface(font);
		tvDisenchant.setTypeface(font);
		tvDisenchantGold.setTypeface(font);
	}

	static void initiatePopupWindow(int position, View v) {

		if (!loader.isInited()) {
			loader.init(Utils.config(cxt));
		}

		int classs = 0;
		int set = 0;
		int quality = 0;
		int type = 0;
		String url;

		final ArrayList<Cards> cardListUnique = new ArrayList<Cards>(
				new LinkedHashSet<Cards>(cardList));

		try {
			// get screen size of device
			int screenSize = cxt.getResources().getConfiguration().screenLayout
					& Configuration.SCREENLAYOUT_SIZE_MASK;

			// We need to get the instance of the LayoutInflater,
			// Gotta give the PopupWindow a layout
			LayoutInflater inflater = (LayoutInflater) cxt
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.card_popup, null);

			// make different popupWindows for different screen sizes
			switch (screenSize) {

			// XLARGE = 10"+ Tablets usually
			case Configuration.SCREENLAYOUT_SIZE_XLARGE:
				doSomeWindow(layout, dipsWidthLandscape_XLarge,
						dipsHeightLandscape_XLarge, dipsWidthPortrait_XLarge,
						dipsHeightPortrait_XLarge);
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

			final ObjectAnimator animator = (ObjectAnimator) AnimatorInflater
					.loadAnimator(cxt, R.animator.flipping);
			final ObjectAnimator reverseAnimator = (ObjectAnimator) AnimatorInflater
					.loadAnimator(cxt, R.animator.flipping_reverse);
			final int pos = position;

			animator.setTarget(ivCardImage);
			animator.setDuration(500);

			reverseAnimator.setTarget(ivCardImage);
			reverseAnimator.setDuration(500);

			ivCardImage.setOnClickListener(new View.OnClickListener() {

				boolean isGolden = false;

				@Override
				public void onClick(View v) {
					if (isGolden) {
						reverseAnimator.start();
						isGolden = false;
					} else {
						animator.start();
						isGolden = true;
					}

					Handler handler = new Handler();
					delayedLoad(cardList, handler, pos);
				}
			});

			if (v.getId() == R.id.lvDeck || v.getId() == R.id.lvArena) {
				int resId = Utils.getResIdByName(
						cxt,
						cardListUnique.get(position).getImage()
								.toLowerCase(Utils.curLocale));
				ivCardImage.setImageResource(resId);

				// Get card name
				tvCardName.setText(cardListUnique.get(position).getName());

				classs = 0;
				if (cardListUnique.get(position).getClasss() != null) {
					classs = cardListUnique.get(position).getClasss()
							.intValue();
				}

				type = cardListUnique.get(position).getType().intValue();
				quality = cardListUnique.get(position).getQuality().intValue();
				set = cardListUnique.get(position).getSet().intValue();

				tvCrafted
						.setText(cardListUnique.get(position).getDescription());
			} else {
				int resId = Utils.getResIdByName(cxt, cardList.get(position)
						.getImage().toLowerCase(Utils.curLocale));
				ivCardImage.setImageResource(resId);

				// Get card name
				tvCardName.setText(cardList.get(position).getName());

				classs = 0;
				if (cardList.get(position).getClasss() != null) {
					classs = cardList.get(position).getClasss().intValue();
				}

				type = cardList.get(position).getType().intValue();
				quality = cardList.get(position).getQuality().intValue();
				set = cardList.get(position).getSet().intValue();

				tvCrafted.setText(cardList.get(position).getDescription());
			}

			tvCrafted.setText(cardList.get(position).getDescription());

			if (classs == Classes.DRUID.getValue()) {
				int druid = cxt.getResources().getColor(R.color.druid);
				tvClass.setTextColor(druid);
				tvClass.setText("Druid");
			} else if (classs == Classes.HUNTER.getValue()) {
				int hunter = cxt.getResources().getColor(R.color.hunter);
				tvClass.setTextColor(hunter);
				tvClass.setText("Hunter");
			} else if (classs == Classes.MAGE.getValue()) {
				int mage = cxt.getResources().getColor(R.color.mage);
				tvClass.setTextColor(mage);
				tvClass.setText("Mage");
			} else if (classs == Classes.PALADIN.getValue()) {
				int paladin = cxt.getResources().getColor(R.color.paladin);
				tvClass.setTextColor(paladin);
				tvClass.setText("Paladin");
			} else if (classs == Classes.PRIEST.getValue()) {
				int priest = cxt.getResources().getColor(R.color.priest);
				tvClass.setTextColor(priest);
				tvClass.setText("Priest");
			} else if (classs == Classes.ROGUE.getValue()) {
				int rogue = cxt.getResources().getColor(R.color.rogue);
				tvClass.setTextColor(rogue);
				tvClass.setText("Rogue");
			} else if (classs == Classes.SHAMAN.getValue()) {
				int shaman = cxt.getResources().getColor(R.color.shaman);
				tvClass.setTextColor(shaman);
				tvClass.setText("Shaman");
			} else if (classs == Classes.WARLOCK.getValue()) {
				int warlock = cxt.getResources().getColor(R.color.warlock);
				tvClass.setTextColor(warlock);
				tvClass.setText("Warlock");
			} else if (classs == Classes.WARRIOR.getValue()) {
				int warrior = cxt.getResources().getColor(R.color.warrior);
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

			tvCost.setTextColor(Color.rgb(17, 228, 241));
			tvCostGold.setTextColor(Color.rgb(17, 228, 241));
			tvDisenchant.setTextColor(Color.rgb(17, 228, 241));
			tvDisenchantGold.setTextColor(Color.rgb(17, 228, 241));
			switch (quality) {
			case 0:
				int free = cxt.getResources().getColor(R.color.free);
				tvQuality.setTextColor(free);
				tvQuality.setText("Free");
				tvCost.setVisibility(View.INVISIBLE);
				tvCostGold.setVisibility(View.INVISIBLE);
				tvDisenchant.setVisibility(View.INVISIBLE);
				tvDisenchantGold.setVisibility(View.INVISIBLE);
				ivDust1.setVisibility(View.INVISIBLE);
				ivDust2.setVisibility(View.INVISIBLE);
				ivDust3.setVisibility(View.INVISIBLE);
				ivDust4.setVisibility(View.INVISIBLE);
				break;
			case 1:
				tvQuality.setText("Common");
				if (set == 3) {
					tvCost.setText("Crafted: 40");
					tvCostGold.setText("Golden: 400");
					tvDisenchant.setText("Disenchant: 5");
					tvDisenchantGold.setText("Golden: 50");
				} else {
					tvCost.setVisibility(View.INVISIBLE);
					tvCostGold.setVisibility(View.INVISIBLE);
					tvDisenchant.setVisibility(View.INVISIBLE);
					tvDisenchantGold.setVisibility(View.INVISIBLE);
					ivDust1.setVisibility(View.INVISIBLE);
					ivDust2.setVisibility(View.INVISIBLE);
					ivDust3.setVisibility(View.INVISIBLE);
					ivDust4.setVisibility(View.INVISIBLE);
				}
				break;
			case 3:
				int rare = cxt.getResources().getColor(R.color.rare);
				tvQuality.setTextColor(rare);
				tvQuality.setText("Rare");
				if (set == 3) {
					tvCost.setText("Crafted: 100");
					tvCostGold.setText("Golden: 800");
					tvDisenchant.setText("Disenchant: 20");
					tvDisenchantGold.setText("Golden: 100");
				}
				break;
			case 4:
				int epic = cxt.getResources().getColor(R.color.epic);
				tvQuality.setTextColor(epic);
				tvQuality.setText("Epic");
				if (set == 3) {
					tvCost.setText("Crafted: 400");
					tvCostGold.setText("Golden: 1600");
					tvDisenchant.setText("Disenchant: 100");
					tvDisenchantGold.setText("Golden: 400");
				}
				break;
			case 5:
				int legendary = cxt.getResources().getColor(R.color.legendary);
				tvQuality.setTextColor(legendary);
				tvQuality.setText("Legendary");
				if (set == 3) {
					tvCost.setText("Crafted: 1600");
					tvCostGold.setText("Golden: 3200");
					tvDisenchant.setText("Disenchant: 400");
					tvDisenchantGold.setText("Golden: 1600");
				}
				break;
			default: // No rarity? This should only happen for some abilities.
				tvQuality.setVisibility(View.GONE); // Hides it.
				break;
			}

			switch (set) {
			case 2:
				tvSet.setText("Basic");
				ivDust1.setVisibility(View.INVISIBLE);
				ivDust2.setVisibility(View.INVISIBLE);
				ivDust3.setVisibility(View.INVISIBLE);
				ivDust4.setVisibility(View.INVISIBLE);
				break;
			case 3:
				tvSet.setText("Expert");
				break;
			case 4:
				tvSet.setText("Reward");
				ivDust1.setVisibility(View.INVISIBLE);
				ivDust2.setVisibility(View.INVISIBLE);
				ivDust3.setVisibility(View.INVISIBLE);
				ivDust4.setVisibility(View.INVISIBLE);
				break;
			case 5:
				tvSet.setText("Missions");
				ivDust1.setVisibility(View.INVISIBLE);
				ivDust2.setVisibility(View.INVISIBLE);
				ivDust3.setVisibility(View.INVISIBLE);
				ivDust4.setVisibility(View.INVISIBLE);
				break;
			case 6:
				tvSet.setText("Promotion");
				ivDust1.setVisibility(View.INVISIBLE);
				ivDust2.setVisibility(View.INVISIBLE);
				ivDust3.setVisibility(View.INVISIBLE);
				ivDust4.setVisibility(View.INVISIBLE);
				break;
			}

			// If we ran in to a problem
		} catch (BadTokenException e) {
			throw new BadTokenException(
					"Did you forget to call MyWindow.setContext()?");
		}

	}

	private static void delayedLoad(List<Cards> cardListt, Handler handler,
			int position) {

		final List<Cards> cardList = cardListt;
		final String url = "http://54.224.222.135/Golden/"
				+ cardList.get(position).getImage()
						.toLowerCase(Utils.curLocale) + "_premium.png";
		final int cardListPos = position;

		int millis = 350;
		if (DiscCacheUtil.findInCache(url, ImageLoader.getInstance()
				.getDiscCache()) == null) {
			millis = 0;
		} else {
			millis = 350;
		}
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (ivCardImage.getTag() == null
						|| ivCardImage.getTag() == "Standard") {

					loader.cancelDisplayTask(ivCardImage);
					loader.displayImage(url, ivCardImage, Utils.noStubOptions);
					ivCardImage.setTag("Premium");
				} else {
					loader.cancelDisplayTask(ivCardImage);
					ivCardImage.setImageResource(Utils.getResIdByName(
							cxt,
							cardList.get(cardListPos).getImage()
									.toLowerCase(Utils.curLocale)));
					ivCardImage.setTag("Standard");
				}

			}
		}, millis);
	}

	public static List<Cards> getCardList() {
		return cardList;
	}

	public static void setCardList(List<Cards> cardList) {
		MyWindow.cardList = cardList;
	}

}
