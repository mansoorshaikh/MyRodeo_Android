package com.mobiwebcode.Rodeo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class About_Rodeo extends Activity {
	Button Back;
	TextView mainTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_rodeo);
		mainTextView = (TextView) findViewById(R.id.mainTextView);
		mainTextView.setTypeface(HomeActivity.font);
		Back = (Button) findViewById(R.id.About_Rodeo_Back_button);
		Back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
