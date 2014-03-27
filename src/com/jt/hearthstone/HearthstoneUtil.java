package com.jt.hearthstone;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Debug;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;

@ReportsCrashes(formKey = "", formUri = "http://54.224.222.135:5984/acra-db/_design/acra-storage/_update/report", formUriBasicAuthLogin = "dbreporter", formUriBasicAuthPassword = "dbpassword", reportType = org.acra.sender.HttpSender.Type.JSON, httpMethod = org.acra.sender.HttpSender.Method.PUT, mode = ReportingInteractionMode.DIALOG, resDialogText = R.string.app_crash, resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, resToastText = R.string.app_crash

)
public class HearthstoneUtil extends Application {

	private static Context context;
	static boolean isDebugEnabled = true;

	protected static void enableTransparentActionBar(ActionBarActivity cxt) {
		cxt.getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		cxt.getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.argb(128, 0, 0, 0)));
	}

	@Override
	public final void onCreate() {
		super.onCreate();
		if (!isDebugEnabled) {
			ACRA.init(this);
			Log.w("ACRA INIT", "ACRA INIT");
		}
		
		HearthstoneUtil.context = getApplicationContext();
		new Utils.SetupCardList().execute();

	}

	public static Context getAppContext() {
		return HearthstoneUtil.context;
	}

}
