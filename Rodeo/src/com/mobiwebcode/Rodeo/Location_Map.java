package com.mobiwebcode.Rodeo;

import java.io.IOException;
import java.util.ArrayList;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Location_Map extends FragmentActivity implements LocationListener {
	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);
	private GoogleMap Gmap;
	Button Back, Done;
	public static String AddressC = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.map);
			SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.G_map);
			Gmap = fragment.getMap();

			Back = (Button) findViewById(R.id.LocationMap_Back_button);
			Back.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();

				}
			});
			Done = (Button) findViewById(R.id.LocationMap_Done_button);
			Done.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});

			Geocoder gCoder = new Geocoder(Location_Map.this);
			ArrayList<Address> addresses = null;
			try {
				addresses = (ArrayList<Address>) gCoder.getFromLocation(
						HomeActivity.Latitude, HomeActivity.Longutude, 2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			// Enabling MyLocation in Google Map
			Gmap.setMyLocationEnabled(true);
			// LOCATION_SERVICE
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			// Creating a criteria object to retrieve provider
			Criteria criteria = new Criteria();
			// Getting the name of the best provider
			String provider = locationManager.getBestProvider(criteria, true);
			// Getting Current Location From GPS
			LatLng latLng = new LatLng(HomeActivity.Latitude,
					HomeActivity.Longutude);
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.position(latLng);
			markerOptions.title(AddressC + " : " + "");
			BitmapDescriptor icon = BitmapDescriptorFactory
					.fromResource(R.drawable.pin);
			markerOptions.icon(icon);
			markerOptions.draggable(true);
			Gmap.addMarker(markerOptions);

			Gmap.setOnMarkerDragListener(new OnMarkerDragListener() {

				@Override
				public void onMarkerDragStart(Marker arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onMarkerDragEnd(Marker marker) {
					// TODO Auto-generated method stub
					LatLng position = marker.getPosition(); //
					Geocoder gCoder = new Geocoder(Location_Map.this);
					ArrayList<Address> addresses = null;
					try {
						addresses = (ArrayList<Address>) gCoder
								.getFromLocation(position.latitude,
										position.longitude, 1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (addresses != null && addresses.size() > 0) {
						if (addresses.get(0).getFeatureName() != null)
							AddressC = addresses.get(0).getFeatureName();
						if (addresses.get(0).getLocality() != null)
							AddressC = AddressC + "-"
									+ addresses.get(0).getLocality();
						if (addresses.get(0).getAdminArea() != null)
							AddressC = AddressC + "-"
									+ addresses.get(0).getAdminArea();
						if (addresses.get(0).getCountryName() != null)
							AddressC = AddressC + "-"
									+ addresses.get(0).getCountryName();
					}
					Toast.makeText(Location_Map.this, "Address== " + AddressC,
							Toast.LENGTH_LONG).show();
				}

				@Override
				public void onMarkerDrag(Marker arg0) {
					// TODO Auto-generated method stub

				}
			});

			Location location = locationManager.getLastKnownLocation(provider);

			if (location != null) {
				onLocationChanged(location);
			}

			locationManager.requestLocationUpdates(provider, 20000, 0, this);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onLocationChanged(Location location) {
		LatLng latLng = new LatLng(HomeActivity.Latitude,
				HomeActivity.Longutude);
//		 MarkerOptions markerOptions = new MarkerOptions();
//		 markerOptions.position(latLng);
//		  markerOptions.title( " :TEST " + "");
//		 BitmapDescriptor icon = BitmapDescriptorFactory
//		 .fromResource(R.drawable.pin);
//		 markerOptions.icon(icon);
//		 markerOptions.draggable(true);
//		 Gmap.addMarker(markerOptions);

		Gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		Gmap.animateCamera(CameraUpdateFactory.zoomTo((float) 11));

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
}
