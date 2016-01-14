package com.mobiwebcode.Rodeo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends Activity {
	private SQLiteDatabase RodeoDatabase;
	private static String DB_PATH = "/data/data/com.mobiwebcode.Rodeo/databases/";
	private static final String DB_NAME = "rodeo.db";
	private String path = DB_PATH + DB_NAME;
	Button help, createRodeo, Setting, About, GettingStarted, LookUp;
	GPSTracker gps;
	TextView title;
	public static Typeface font, font_brushscript;

	public static Double Latitude, Longutude;
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	public static String userid = "";

	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void copyDatabase() {
		try {
			File f = new File(path);
			File f2 = new File(DB_PATH);
			if (!f.exists()) {
				InputStream in = getAssets().open("Rodeo.sqlite");
				if (!f2.exists())
					f2.mkdir();
				OutputStream out = new FileOutputStream(path);

				byte[] buffer = new byte[1024];
				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				in.close();
				out.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void loadDefaults() {
		final SharedPreferences myPrefs = this.getSharedPreferences("myPrefs",
				MODE_WORLD_READABLE);
		final SharedPreferences.Editor prefsEditor = myPrefs.edit();
		prefsEditor.putString("timeformat", "2");
		prefsEditor.putString("scoreformat", "1");
		prefsEditor.putString("noofrounds", "1");
		prefsEditor.putString("noofcontestants", "8");
		prefsEditor.putString("noofplacespaid", "3");
		prefsEditor.commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_layout);

		SharedPreferences myPrefs = this.getSharedPreferences("myPrefs",
				MODE_WORLD_READABLE);
		SharedPreferences.Editor prefsEditor = myPrefs.edit();

		if (myPrefs.getString("timeformat", "") == null
				|| myPrefs.getString("timeformat", "").equals(""))
			if (myPrefs.getString("scoreformat", "") == null
					|| myPrefs.getString("scoreformat", "").equals(""))
				loadDefaults();
		copyDatabase();
		Constants.RodeoDatabase = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);

		font = Typeface.createFromAsset(getAssets(), "Segoe Print.ttf");
		font_brushscript = Typeface.createFromAsset(getAssets(),
				"BrushScriptStd.ttf");

		gps = new GPSTracker(HomeActivity.this);
		LocationManager locationManager;
		locationManager = (LocationManager) HomeActivity.this
				.getSystemService(LOCATION_SERVICE);
		isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps.canGetLocation()) {

			Latitude = gps.getLatitude();
			Longutude = gps.getLongitude();

		} else {
			if (!isGPSEnabled && !isNetworkEnabled) {

				gps.showSettingsAlert();
			} else {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						HomeActivity.this);
				alert.setTitle("Message");
				alert.setMessage("Gps Service can not find your location plz try latter");
				alert.setPositiveButton("ok",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								dialog.dismiss();
							}
						});

				alert.create().show();
			}
		}

		// title = (TextView) findViewById(R.id.CreateRodeo_Title_textView);
		// title.setTypeface(font_brushscript);
		help = (Button) findViewById(R.id.Home_Help_button);
		help.setTypeface(font);
		help.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(HomeActivity.this, Rodeo_Help.class));
			}
		});
		createRodeo = (Button) findViewById(R.id.Home_CreateRodeo_button);
		createRodeo.setTypeface(font);
		createRodeo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Events.addedEventsList.clear();
				startActivity(new Intent(HomeActivity.this, Create_Rodeo.class));
			}
		});
		Setting = (Button) findViewById(R.id.Home_Setting_button);
		Setting.setTypeface(font);
		Setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(HomeActivity.this, Settings.class));
			}
		});
		About = (Button) findViewById(R.id.Home_About_Rodeo_button);
		About.setTypeface(font);
		About.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(HomeActivity.this, About_Rodeo.class));
			}
		});
		GettingStarted = (Button) findViewById(R.id.Home_GettingStarted_button);
		GettingStarted.setTypeface(font);
		GettingStarted.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(HomeActivity.this,
						Getting_Started.class));
			}
		});
		LookUp = (Button) findViewById(R.id.Home_LookUp_button);
		LookUp.setTypeface(font);
		LookUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(HomeActivity.this, Rodeos.class));
			}
		});

	}

}
