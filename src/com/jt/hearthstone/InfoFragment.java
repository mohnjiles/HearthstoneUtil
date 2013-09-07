package com.jt.hearthstone;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.R.integer;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class InfoFragment extends Fragment {

	public static int lastPage = 0;
	ImageLoader loader = ImageLoader.getInstance();
	ImageView ivHeroPower;
	ImageView ivFreeCardOne;
	ImageView ivFreeCardTwo;
	ImageView ivFreeCardThree;
	ImageView ivFreeCardFour;
	ImageView ivFreeCardFive;
	int position;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View V = inflater.inflate(R.layout.info_fragment, container, false);
		
		position = getActivity().getIntent().getIntExtra("position", 0);
	
		ivHeroPower = (ImageView) V.findViewById(R.id.ivHeroPower);
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
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showStubImage(R.drawable.ic_launcher)
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .build();
		loader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		
		switch (position) {
		case 0: // Druid
			loader.displayImage("http://jt.comyr.com/images/Shapeshift.png", ivHeroPower, options);
			loader.displayImage("http://jt.comyr.com/images/Moonfire.png", ivFreeCardOne, options);
			loader.displayImage("http://jt.comyr.com/images/Savage%20Roar.png", ivFreeCardTwo, options);
			loader.displayImage("http://jt.comyr.com/images/Swipe.png", ivFreeCardThree, options);
			loader.displayImage("http://jt.comyr.com/images/Starfire.png", ivFreeCardFour, options);
			loader.displayImage("http://jt.comyr.com/images/Ironbark%20Protector.png", ivFreeCardFive, options);
			break;
		case 1: // Hunter
			break;
		case 2: // Mage
			break;
			// TODO: the rest of this
		}
		
		
		
	}
}
