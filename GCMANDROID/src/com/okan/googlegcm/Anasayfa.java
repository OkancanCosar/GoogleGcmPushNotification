package com.okan.googlegcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class Anasayfa extends Activity {

	SharedPreferences prefs;
	String registrationId;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.anasayfa);

		prefs = getSharedPreferences(SplashScreen.class.getSimpleName(),Context.MODE_PRIVATE);
		registrationId = prefs.getString(SplashScreen.PROPERTY_REG_ID, "");

		Log.e("OKANCANCOSAR", "KAYIT OLDUÐUMUZ REGID: \n" + registrationId);
	}
}