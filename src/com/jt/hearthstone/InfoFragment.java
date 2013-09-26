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
	
	private ImageLoader loader = ImageLoader.getInstance();
	private ImageView ivHeroPower;
	private int position;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Inflate the layout for this fragment
		View V = inflater.inflate(R.layout.info_fragment, container, false);
		
		position = getActivity().getIntent().getIntExtra("position", 0);
		ivHeroPower = (ImageView) V.findViewById(R.id.ivHeroPower);

		return V;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.cards).cacheInMemory(false)
				.cacheOnDisc(true).build();
		
		if (!loader.isInited()) {
			loader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		}

		switch (position) {
		case 0: // Druid
			loader.displayImage("http://54.224.222.135/CS2_017.png",
					ivHeroPower, options);
			break;
		case 1: // Hunter
			loader.displayImage("http://54.224.222.135/DS1h_292.png", ivHeroPower, options);
			break;
		case 2: // Mage
			loader.displayImage("http://54.224.222.135/CS2_034.png", ivHeroPower, options);
			break;
		case 3: // Paladin
			loader.displayImage("http://54.224.222.135/CS2_101.png", ivHeroPower, options);
			break;
		case 4: // Priest
			loader.displayImage("http://54.224.222.135/CS1h_001.png", ivHeroPower, options);
			break;
		case 5: // Rogue
			loader.displayImage("http://54.224.222.135/CS2_083b.png", ivHeroPower, options);
			break;
		case 6: // Shaman
			loader.displayImage("http://54.224.222.135/CS2_049.png", ivHeroPower, options);
			break;
		case 7: // Warlock
			loader.displayImage("http://54.224.222.135/CS2_056.png", ivHeroPower, options);
			break;
		case 8: // Warrior
			loader.displayImage("http://54.224.222.135/CS2_102.png", ivHeroPower, options);
			break;
		}

	}
}
