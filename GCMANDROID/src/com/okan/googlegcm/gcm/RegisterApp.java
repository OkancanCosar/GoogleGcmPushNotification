package com.okan.googlegcm.gcm;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.okan.googlegcm.Anasayfa;
import com.okan.googlegcm.SplashScreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

/*			(HERHANG� B��EY DE���T�RMEN�ZE GEREK YOKTUR.)
 * AsyncTask KULLANILARAK RegistrationId Y� 
 * 						SHAREDPREFERANCE �LE TELEFONA
 * 						HTTP CL�ENT �LE DE SERVER�N�ZDEK� DATABASE'E EKLER.
 * */
public class RegisterApp extends AsyncTask<Void, Void, String> {

	private static final String TAG = "OKANCANCOSAR";
	Context ctx;
	GoogleCloudMessaging gcm;
	String regid = null; 
	private int appVersion;

	public RegisterApp(Context ctx, GoogleCloudMessaging gcm, int appVersion){
		this.ctx = ctx;
		this.gcm = gcm;
		this.appVersion = appVersion;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}


	@Override
	protected String doInBackground(Void... arg0) { //
		String msg = "";
		try {
			if (gcm == null) {
				gcm = GoogleCloudMessaging.getInstance(ctx);//GCM objesi olu�turduk ve gcm referans�na ba�lad�k
			}
			regid = gcm.register(Constants.PROJECT_ID);
			//gcm objesine PROJECT_ID mizi g�ndererek regid de�erimizi ald�k.
			//Bu de�erimizi hem sunucular�m�za g�nderece�iz Hemde Androidde sakl�yaca��z
			msg = "Registration ID=" + regid;
			sendRegistrationIdToBackend();	//Sunuculara regid g�nderme i�lemini yapacak method
			storeRegistrationId(ctx, regid);//Androidde regid sakl� tutacak method
		} catch (IOException ex) {
			msg = "Error :" + ex.getMessage();

		}
		return msg;
	}

	private void storeRegistrationId(Context ctx, String regid) {//Androidde regid ve appversion sakl� tutacak method
		//Burada SharedPreferences kullanarak kay�t yapmaktad�r
		//SharedPreferences hakk�nda ayr�nt�l� dersi Blo�umuzda bulabilirsiniz.
		final SharedPreferences prefs = ctx.getSharedPreferences(SplashScreen.class.getSimpleName(),Context.MODE_PRIVATE);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("registration_id", regid);
		editor.putInt("appVersion", appVersion);
		editor.commit();

	}

	@SuppressWarnings("deprecation")
	private void sendRegistrationIdToBackend() {
		URI url = null;
		try {
			url = new URI(Constants.URL + regid);
		} catch (URISyntaxException e) {
			Log.e(TAG, "sendRegistrationIdToBackend= "+ e.getMessage().toString());
			e.printStackTrace();
		} 

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		request.setURI(url);
		try {
			httpclient.execute(request);
		} catch (ClientProtocolException e) {
			Log.e(TAG, "sendRegistrationIdToBackend= "+ e.getMessage().toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "sendRegistrationIdToBackend= "+ e.getMessage().toString());
			e.printStackTrace();
		}
	}


	@Override
	protected void onPostExecute(String result) {
		//doInBackground i�lemi bittikten sonra �al���r
		super.onPostExecute(result);

		Log.e(TAG, "Registration Tamamland�.Art�k notification g�nederilebilir");
		Log.v(TAG, result);
		Intent i = new Intent(ctx,Anasayfa.class);//Anasayfaya Y�nlendir
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		ctx.startActivity(i);

	}
}