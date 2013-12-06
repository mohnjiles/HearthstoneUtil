package com.jt.hearthstone;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.util.Log;

@ReportsCrashes(
        formKey = "",
        	    formUri = "http://54.224.222.135:5984/acra-db/_design/acra-storage/_update/report",
        	    formUriBasicAuthLogin = "dbreporter",
        	    formUriBasicAuthPassword = "dbpassword",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.app_crash,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resToastText = R.string.app_crash
        
        
        )

public class HearthstoneUtil extends Application {
	  @Override
	  public final void onCreate() {
	    super.onCreate();
		ACRA.init(this);
		Log.w("ACRA INIT", "ACRA INIT");
	}
}
