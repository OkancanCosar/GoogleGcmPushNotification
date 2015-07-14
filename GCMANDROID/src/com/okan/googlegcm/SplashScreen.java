package com.okan.googlegcm;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.okan.googlegcm.gcm.RegisterApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

public class SplashScreen extends Activity {

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String TAG = "OKANCANCOSAR";
	GoogleCloudMessaging gcm;
	String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);


		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
			regid = getRegistrationId(getApplicationContext()); //registration_id olup olmad���n� kontrol ediyoruz

			if(regid.isEmpty()){
				Log.e(TAG, "YENI KAYIT ��LEMLER� YAPILIYOR.");
				//RegisterApp clas�n� �al��t�r�yoruz ve de�erleri g�nderiyoruz
				new RegisterApp(getApplicationContext(), gcm, getAppVersion(getApplicationContext())).execute();
			}else{
				Log.e(TAG, "C�HAZ DAHA �NCEDEN KAYDED�LM��. UYGULAMAYA DEVAM ED�L�YOR");
				startActivity(new Intent(getApplicationContext(),Anasayfa.class));
				finish();
			}

		}
		else
			Log.e(TAG, "PLAY STORE SERV�S� BULUNAMADI....");

	}

	/*
	 * GOOGLE PLAY SERV�CE'LER�N�N VAR OLUP OLMADIGININ KONTROL� ���N 
	 * */
	private boolean checkPlayServices() { 
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "Google Play Servis Y�KLENMEM��");
				finish();
			}
			return false;
		}
		return true;
	}

	/*
	 * SHAREDPREFERANCE �LE RegistrationId'Y� TUTTU�UMUZ YERDEN GER� �A�IRIYORUZ. 
	 * */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "UYGULAMA �LK DEFA �ALI�IYOR.");
			return "";
		}

		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(getApplicationContext());//yine SharedPreferences a kaydedilmi� version de�erini ald�k
		if (registeredVersion != currentVersion) {//versionlar uyu�muyorsa g�ncelleme olmu� demektir. Yani tekrardan registration i�lemleri yap�lcak
			Log.i(TAG, "App version de�i�mi�.");
			return "";
		}
		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(SplashScreen.class.getSimpleName(),	Context.MODE_PRIVATE);
	}

	private static int getAppVersion(Context context) { //Versiyonu geri d�ner
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Paket versiyonu bulunamad�: " + e);
		}
	}
}