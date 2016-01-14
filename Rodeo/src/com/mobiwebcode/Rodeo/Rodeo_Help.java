package com.mobiwebcode.Rodeo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Rodeo_Help extends Activity {
	Button Back;
	TextView mainTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		mainTextView = (TextView) findViewById(R.id.mainTextView);
		mainTextView.setTypeface(HomeActivity.font);
		mainTextView
				.setText("If you are experiencing problems with the function of the My Rodeo App.  First ensure that when entering data that you are hitting 'Done' at the conclusion of every entry.  Or if Creating a Rodeo that your hitting '+' at he end of every event entry."
						+ "If you are experiencing difficulty with location while creating Rodeo shut off your device and restart it."
						+ "If you are still experiencing difficulties please feel free to email us at help@myrodeoapp.com"
						+ "To leave us feedback so we can better improve the app please also respond to help@myrodeoapp.com"
						+ "To leave us feedback in the appstore please follow this link www.myrodeoapp.com");

		Back = (Button) findViewById(R.id.Help_Back_button);
		Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
