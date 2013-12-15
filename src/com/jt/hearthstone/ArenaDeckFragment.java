package com.jt.hearthstone;

import static butterknife.Views.findById;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Fragment that holds a ListView that contains the chosen cards for the current
 * arena run.
 * 
 * @author JT
 * 
 */
public class ArenaDeckFragment extends Fragment {

	public ListView lvArena;
	public TextView tvDeckSize;

	private List<Cards> cardList;

	/**
	 * Overriding onCreateView to inflate our XML layout and to find the views.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.arena_deck_fragment, container,
				false);

		lvArena = findById(v, R.id.lvArena);
		tvDeckSize = findById(v, R.id.tvDeckSize);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ArenaFragment arenaFrag = (ArenaFragment) getActivity()
				.getSupportFragmentManager().findFragmentByTag(
						Utils.makeFragmentName(R.id.pager, 0));

		cardList = arenaFrag.listDeck;
		
		Utils.setContext(getActivity());
		
		lvArena.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Utils.initiatePopupWindow(cardList, position, parent);
			}
		});

	}

}
