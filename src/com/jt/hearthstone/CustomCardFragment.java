package com.jt.hearthstone;

import java.util.List;

import android.support.v4.app.Fragment;

public abstract class CustomCardFragment extends Fragment {
	
	protected abstract void setCardList(List<Cards> cardList, int tag);
}
