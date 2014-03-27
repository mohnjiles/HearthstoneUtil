package com.jt.hearthstone;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class HearthstoneActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HearthstoneUtil.enableTransparentActionBar(this);
	}
	
}
