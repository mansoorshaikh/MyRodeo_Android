package com.mobiwebcode.Rodeo;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Getting_Started extends Activity {
	Button Back;
	TextView textView1,textView2,textView3,textView4,textView5,textView6,textView7,textView8,textView9;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getting_started);
		try {
		Back = (Button) findViewById(R.id.GettinStarted_Back_button);
		Back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		textView1 = (TextView) findViewById(R.id.mainTextView1);
		textView2 = (TextView) findViewById(R.id.mainTextView2);
		textView3 = (TextView) findViewById(R.id.mainTextView3);
		textView4 = (TextView) findViewById(R.id.mainTextView4);
		textView5 = (TextView) findViewById(R.id.mainTextView5);
		textView6 = (TextView) findViewById(R.id.mainTextView6);
		textView7 = (TextView) findViewById(R.id.mainTextView7);
		textView8 = (TextView) findViewById(R.id.mainTextView8);
		textView9 = (TextView) findViewById(R.id.mainTextView9);
		try {
		 Typeface font, font_brushscript;
			font = Typeface.createFromAsset(getAssets(), "Segoe Print.ttf");
			font_brushscript = Typeface.createFromAsset(getAssets(),
					"BrushScriptStd.ttf");
		
		textView1.setTypeface(font);
		textView3.setTypeface(font);
		textView5.setTypeface(font);
		textView7.setTypeface(font);
		textView9.setTypeface(font);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		//AssetManager assetManager = getAssets();

		// To load text file
		//InputStream input;
//		try {
//			input = assetManager.open("getting_started.txt");
//
//			int size = input.available();
//			byte[] buffer = new byte[size];
//			input.read(buffer);
//			input.close();
//
//			// byte buffer into a string
//			String text = new String(buffer);
//
//			textView1.setText(stripHtml(text));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}
