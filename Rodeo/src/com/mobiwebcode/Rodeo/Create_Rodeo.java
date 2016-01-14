package com.mobiwebcode.Rodeo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Create_Rodeo extends Activity {
	private SQLiteDatabase RodeoDatabase;
	String CreateRodeo = "rodeodetails";
	String Events_Table = "events";
	private static String DB_PATH = "/data/data/com.Syneotek.Rodeo/databases/";
	private static final String DB_NAME = "rodeo.db";
	private String path = DB_PATH + DB_NAME;
	Button events, Back, Start, Save, locationmapButton;
	DatePicker Dtpicker;
	EditText EdDate, LocationEdittext, Name, NoOfRounds;
	TextView ActivityTitle;
	int year;
	int month;
	int day;
	final String months[] = new String[] { "January", "February", "March",
			"April", "May", "June", "July", "August", "September", "October",
			"November", "December" };
	static final int DATE_DIALOG_ID = 999;
	String AddressC = "";
	private String rodeoname;
	private String location2 = "";
	private String rodeostartdate;
	private String numberofrounds;

	public void loadDefaults() {
		final SharedPreferences myPrefs = this.getSharedPreferences("myPrefs",
				MODE_WORLD_READABLE);
		final SharedPreferences.Editor prefsEditor = myPrefs.edit();
		prefsEditor.putString("timeformat", "2");
		prefsEditor.putString("scoreformat", "2");
		prefsEditor.putString("noofrounds", "1");
		prefsEditor.putString("noofcontestants", "10");
		prefsEditor.putString("noofplacespaid", "5");
		prefsEditor.commit();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if (!Location_Map.AddressC.equals("")
				&& !Location_Map.AddressC.equals(AddressC)) {
			LocationEdittext.setText(Location_Map.AddressC);
		} else {
			LocationEdittext.setText(AddressC);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_rodeo);
		try{
		SharedPreferences myPrefs = this.getSharedPreferences("myPrefs",
				MODE_WORLD_READABLE);
		SharedPreferences.Editor prefsEditor = myPrefs.edit();
		if (myPrefs.getString("noofrounds", "") == null
				|| myPrefs.getString("noofrounds", "").equals(""))
			loadDefaults();

		Dtpicker = (DatePicker) findViewById(R.id.CreateRodeo_datePicker);
		events = (Button) findViewById(R.id.CreateRodeo_Events_button);
		events.setTypeface(HomeActivity.font);
		events.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyConstants.FromRodeos = false;
				startActivity(new Intent(Create_Rodeo.this, Events.class));
			}
		});
		Name = (EditText) findViewById(R.id.CreateRodeo_Name_button);
		Name.setText("Rodeo " + readRodeo());
		Name.setSelection(Name.getText().length());

		Name.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				v.setFocusable(true);
				v.setFocusableInTouchMode(true);
				v.requestFocus();
				if (Name.getText().toString().contains("Rodeo "))
					Name.setText("");
				else {
					Name.setSelection(Name.getText().length());
				}
				return false;
			}
		});

		Name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

			}
		});
		Back = (Button) findViewById(R.id.CreateRodeo_Back_button);
		Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		LocationEdittext = (EditText) findViewById(R.id.CreateRodeo_Location_button);
		LocationEdittext.setSingleLine();
		LocationEdittext.setImeOptions(EditorInfo.IME_ACTION_DONE);

		locationmapButton = (Button) findViewById(R.id.locationmapButton);
		locationmapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(Create_Rodeo.this, Location_Map.class));
			}
		});

		NoOfRounds = (EditText) findViewById(R.id.CreateRodeo_NumberOfEvents_button);
		NoOfRounds.setText(myPrefs.getString("noofrounds", ""));
		Start = (Button) findViewById(R.id.CreateRodeo_StartRodeo_button);
		Start.setTypeface(HomeActivity.font);
		Start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Name.getText().toString().equals("")
						|| LocationEdittext.getText().toString().equals("")
						|| EdDate.getText().toString().equals("")
						|| NoOfRounds.getText().toString().equals("")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							Create_Rodeo.this);
					builder.setMessage("Please fill in all values to add rodeo")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				} else if (Events.addedEventsList.size() == 0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							Create_Rodeo.this);
					builder.setMessage(
							"Please fill in at least one event to add rodeo")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				} else
					addRodeo("1");
			}

		});

		ActivityTitle = (TextView) findViewById(R.id.CreateRodeo_Title_textView);
		ActivityTitle.setTypeface(HomeActivity.font);
		setCurrentDateOnView();
		Save = (Button) findViewById(R.id.CreateRodeo_SaveRodeo_button);
		Save.setTypeface(HomeActivity.font);
		Save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Name.getText().toString().equals("")
						|| LocationEdittext.getText().toString().equals("")
						|| EdDate.getText().toString().equals("")
						|| NoOfRounds.getText().toString().equals("")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							Create_Rodeo.this);
					builder.setMessage("Please fill in all values to add rodeo")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				} else if (Events.addedEventsList.size() == 0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							Create_Rodeo.this);
					builder.setMessage(
							"Please fill in at least one event to add rodeo")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				} else
					addRodeo("0");
			}
		});

		
		Geocoder gCoder = new Geocoder(Create_Rodeo.this);
		ArrayList<Address> addresses = null;
		
		if (AppUtils.isNetworkAvailable(Create_Rodeo.this)) {
		
		for (int i = 0; i < 10; i++) {
		if (HomeActivity.Latitude != null && HomeActivity.Longutude != null) {
			try {
				addresses = (ArrayList<Address>) gCoder.getFromLocation(
						HomeActivity.Latitude, HomeActivity.Longutude, 2);
				
				if (addresses != null && addresses.size() > 0)
				{
				//i+=10;
				break;
				}
				
				if(i==7)
					Toast.makeText(getApplicationContext(),"Location Not Found Please Retry" , Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				//System.out.println("/nREtry Counter "+i);
				e.printStackTrace();
			}
		}//end of if
			
		}//end of for
		
			if (addresses != null && addresses.size() > 0) {
				if (addresses.get(0).getFeatureName() != null)
					AddressC = addresses.get(0).getFeatureName();
				if (addresses.get(0).getLocality() != null)
					AddressC = AddressC + "-" + addresses.get(0).getLocality();
				if (addresses.get(0).getAdminArea() != null)
					AddressC = AddressC + "-" + addresses.get(0).getAdminArea();
				if (addresses.get(0).getCountryName() != null)
					AddressC = AddressC + "-"
							+ addresses.get(0).getCountryName();
			}
			if (!Location_Map.AddressC.equals("")
					&& !Location_Map.AddressC.equals(AddressC)) {
				LocationEdittext.setText(Location_Map.AddressC);
			} else {
				LocationEdittext.setText(AddressC);
			}
		
		
		}//end of if
		else {
			AppUtils.ShowAlertDialog(Create_Rodeo.this,"No Internet Connection Available");
		}
		
//		try {
//			 turnGPSOn(); // method to turn on the GPS if its in off state.
//             getMyCurrentLocation();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	} //end of oncreate

	public void addRodeo(final String isStarted) {
		try {

			ContentValues values = new ContentValues();
			values.put("rodeoname", Name.getText().toString());
			values.put("location", LocationEdittext.getText().toString());
			values.put("isstarted", isStarted);
			values.put("rodeostartdate", EdDate.getText().toString());
			values.put("numberofrounds", NoOfRounds.getText().toString());
			final long rodeoid = Constants.RodeoDatabase.insert(CreateRodeo,
					null, values);

			for (int i = 0; i < Events.addedEventsList.size(); i++) {
				Events_VO evo = Events.addedEventsList.get(i);

				ContentValues val = new ContentValues();
				val.put("eventname", evo.eventName);
				val.put("contestants", evo.noofcontestants);
				val.put("rodeoid", rodeoid);
				val.put("places", evo.places);
				val.put("eventType", evo.eventType);
				long eventid = Constants.RodeoDatabase.insert(Events_Table,
						null, val);

			}

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Rodeo Created Successfully")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Events.addedEventsList.clear();
									if (isStarted.equals("0"))
										finish();
									else if (isStarted.equals("1")) {
										RodeoVO rvo = new RodeoVO();
										rvo.rodeoid = (int) rodeoid;
										rvo.rodeoname = Name.getText()
												.toString();
										rvo.location = LocationEdittext.getText()
												.toString();
										rvo.rodeostartdate = EdDate.getText()
												.toString();
										rvo.numberofrounds = NoOfRounds
												.getText().toString();
										Events_LookUpRodeo.selectedRodeoVO = rvo;
										startActivity(new Intent(
												Create_Rodeo.this,
												Events_LookUpRodeo.class));
									}
								}
							});
			AlertDialog alert = builder.create();
			alert.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int readRodeo() {
		int rodeocount = 0;
		try {

			Cursor cursor = null;

			cursor = Constants.RodeoDatabase.rawQuery("SELECT * FROM "
					+ CreateRodeo, null);
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					rodeocount++;
				}
			}
			cursor.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rodeocount;
	}

	private void setCurrentDateOnView() {
		EdDate = (EditText) findViewById(R.id.CreateRodeo_Date_button);
		EdDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Dtpicker.setVisibility(View.VISIBLE);
				showDialog(DATE_DIALOG_ID);
				Dtpicker.setVisibility(View.INVISIBLE);

			}
		});
		Dtpicker = (DatePicker) findViewById(R.id.CreateRodeo_datePicker);
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		EdDate.setText(new StringBuilder().append(months[month]).append("  ")
				.append(day).append(" ,  ").append(year).append(" "));
		Dtpicker.init(year, month, day, null);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, datePickerListener, year, month,
					day);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			Dtpicker.setVisibility(View.INVISIBLE);
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			EdDate.setText(new StringBuilder().append(months[month])
					.append("  ").append(day).append(",  ").append(year)
					.append(""));
			Dtpicker.init(year, month, day, null);
			Dtpicker.setVisibility(View.INVISIBLE);
		}

	};
	
	public void turnGPSOn(){
        try
        {
       
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

       
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
        }
        catch (Exception e) {
           
        }
    }
// Method to turn off the GPS
    public void turnGPSOff(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }
   
    // turning off the GPS if its in on state. to avoid the battery drain.
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        turnGPSOff();
    }
   
    /**
     * Check the type of GPS Provider available at that instance and
     * collect the location informations
     *
     * @Output Latitude and Longitude
     */
    void getMyCurrentLocation() {
       
       
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locListener = new MyLocationListener();
       
       
         try{gps_enabled=locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
           try{network_enabled=locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

            //don't start listeners if no provider is enabled
            //if(!gps_enabled && !network_enabled)
                //return false;

            if(gps_enabled){
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
               
            }
           
           
            if(gps_enabled){
                location=locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
               
               
            }
           
 
            if(network_enabled && location==null){
                locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
               
            }
       
       
            if(network_enabled && location==null)    {
                location=locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); 
           
            }
       
        if (location != null) {
           
            MyLat = location.getLatitude();
            MyLong = location.getLongitude();

       
        } else {
            Location loc= getLastKnownLocation(this);
            if (loc != null) {
               
                MyLat = loc.getLatitude();
                MyLong = loc.getLongitude();
               

            }
        }
        locManager.removeUpdates(locListener); // removes the periodic updates from location listener to //avoid battery drainage. If you want to get location at the periodic intervals call this method using //pending intent.
        String FeatureName="";
        try
        {
// Getting address from found locations.
        Geocoder geocoder;
       
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
         addresses = geocoder.getFromLocation(MyLat, MyLong, 1);

//          FeatureName=addresses.get(0).getFeatureName();
//        StateName= addresses.get(0).getAdminArea();
//        CityName = addresses.get(0).getLocality();
//        CountryName = addresses.get(0).getCountryName();
        
        
        if (addresses.get(0).getFeatureName() != null)
			AddressC = addresses.get(0).getFeatureName();
		if (addresses.get(0).getLocality() != null)
			AddressC = AddressC + "-" + addresses.get(0).getLocality();
		if (addresses.get(0).getAdminArea() != null)
			AddressC = AddressC + "-" + addresses.get(0).getAdminArea();
		if (addresses.get(0).getCountryName() != null)
			AddressC = AddressC + "-"
					+ addresses.get(0).getCountryName();
        
			if (!Location_Map.AddressC.equals("")
					&& !Location_Map.AddressC.equals(AddressC)) {
				LocationEdittext.setText(Location_Map.AddressC);
			} else {
				LocationEdittext.setText(AddressC);
			}
        // you can get more details other than this . like country code, state code, etc.
       
       
        System.out.println(" StateName " + StateName);
        System.out.println(" CityName " + CityName);
        System.out.println(" CountryName " + CountryName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        
       
//        textView2.setText(""+MyLat);
//        textView3.setText(""+MyLong);
//        textView1.setText("\n StateName= " +FeatureName+" - "+ StateName +"\n CityName =" + CityName +"\n CountryName =" + CountryName);
    }
   
    // Location listener class. to get location.
    public class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            if (location != null) {
            }
        }

        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
   
    private boolean gps_enabled=false;
    private boolean network_enabled=false;
    Location location;
   
    Double MyLat, MyLong;
    String CityName="";
    String StateName="";
    String CountryName="";
   
// below method to get the last remembered location. because we don't get locations all the times .At some instances we are unable to get the location from GPS. so at that moment it will show us the last stored location. 

    public static Location getLastKnownLocation(Context context)
    {
        Location location = null;
        LocationManager locationmanager = (LocationManager)context.getSystemService("location");
        List list = locationmanager.getAllProviders();
        boolean i = false;
        Iterator iterator = list.iterator();
        do
        {
            //System.out.println("---------------------------------------------------------------------");
            if(!iterator.hasNext())
                break;
            String s = (String)iterator.next();
            //if(i != 0 && !locationmanager.isProviderEnabled(s))
            if(i != false && !locationmanager.isProviderEnabled(s))
                continue;
           // System.out.println("provider ===> "+s);
            Location location1 = locationmanager.getLastKnownLocation(s);
            if(location1 == null)
                continue;
            if(location != null)
            {
                //System.out.println("location ===> "+location);
                //System.out.println("location1 ===> "+location);
                float f = location.getAccuracy();
                float f1 = location1.getAccuracy();
                if(f >= f1)
                {
                    long l = location1.getTime();
                    long l1 = location.getTime();
                    if(l - l1 <= 600000L)
                        continue;
                }
            }
            location = location1;
           // System.out.println("location  out ===> "+location);
            //System.out.println("location1 out===> "+location);
            i = locationmanager.isProviderEnabled(s);
           // System.out.println("---------------------------------------------------------------------");
        } while(true);
        return location;
    }

}
