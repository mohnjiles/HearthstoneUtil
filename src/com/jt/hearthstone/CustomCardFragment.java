package com.jt.hearthstone;

import java.util.List;

import android.support.v4.app.Fragment;

public abstract class CustomCardFragment extends Fragment {
	
	private String className = null;
	
	protected abstract void setCardList(List<Cards> cardList, int tag);



	public String getClassName() {
		return className;
	}



	public void setClassName(String className) {
		this.className = className;
	}
}
