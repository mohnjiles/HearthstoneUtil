package com.jt.hearthstone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

@ReportsCrashes(formKey = "", formUri = "http://54.224.222.135:5984/acra-db/_design/acra-storage/_update/report", formUriBasicAuthLogin = "dbreporter", formUriBasicAuthPassword = "dbpassword", reportType = org.acra.sender.HttpSender.Type.JSON, httpMethod = org.acra.sender.HttpSender.Method.PUT, mode = ReportingInteractionMode.DIALOG, resDialogText = R.string.app_crash, resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, resToastText = R.string.app_crash

)
public class HearthstoneUtil extends Application {

	private static Context context;

	@Override
	public final void onCreate() {
		super.onCreate();
		ACRA.init(this);
		Log.w("ACRA INIT", "ACRA INIT");
		HearthstoneUtil.context = getApplicationContext();

	}

	public static Context getAppContext() {
		return HearthstoneUtil.context;
	}

}
