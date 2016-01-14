package com.mobiwebcode.Rodeo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends Activity {
	Button Back, Rounds,Time,Score,Places, Const, Font, Sync;
	TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		title = (TextView) findViewById(R.id.textView1);

		title.setTypeface(HomeActivity.font);
		Back = (Button) findViewById(R.id.Setting_Back_button);
		Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		Time = (Button) findViewById(R.id.Setting_TimeFormat_button);
		Score = (Button) findViewById(R.id.Setting_ScoreFormat_button);
		Places = (Button) findViewById(R.id.Setting_NoofPlaces_button);
		Rounds = (Button) findViewById(R.id.Setting_NumberofRounds_button);
		Const = (Button) findViewById(R.id.Setting_NumberOfCont_button);
		
		Time.setTypeface(HomeActivity.font);
		Score.setTypeface(HomeActivity.font);
		Places.setTypeface(HomeActivity.font);
		Rounds.setTypeface(HomeActivity.font);
		Const.setTypeface(HomeActivity.font);
	}

	public void onSettingsOptionSelected(View view) {
		final SharedPreferences myPrefs = this.getSharedPreferences("myPrefs",
				MODE_WORLD_READABLE);
		final SharedPreferences.Editor prefsEditor = myPrefs.edit();
		
		if (view.getId() == R.id.Setting_TimeFormat_button) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("My Rodeo");
			alert.setMessage("Time Format");
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			input.setText(myPrefs.getString("timeformat", ""));
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input.getText().toString();
							// Do something with value!
							if (value.equals("")) {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										Settings.this);
								builder.setMessage(
										"Please enter proper values to proceed!")
										.setCancelable(false)
										.setPositiveButton(
												"OK",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {
														// do things
													}
												});
								AlertDialog alert = builder.create();
								alert.show();
							} else {
								prefsEditor.putString("timeformat", value);
								prefsEditor.commit();
								Toast.makeText(getApplicationContext(),
										"Time Add", Toast.LENGTH_SHORT)
										.show();
							}
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});
			alert.show();
			
		} else if (view.getId() == R.id.Setting_ScoreFormat_button) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("My Rodeo");
			alert.setMessage("Score Format");
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			input.setText(myPrefs.getString("scoreformat", ""));
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input.getText().toString();
							// Do something with value!
							if (value.equals("")) {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										Settings.this);
								builder.setMessage(
										"Please enter proper values to proceed!")
										.setCancelable(false)
										.setPositiveButton(
												"OK",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {
														// do things
													}
												});
								AlertDialog alert = builder.create();
								alert.show();
							} else {
								prefsEditor.putString("scoreformat", value);
								prefsEditor.commit();
								Toast.makeText(getApplicationContext(),
										"Score Add", Toast.LENGTH_SHORT)
										.show();
							}
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});
			alert.show();
			
		} else if (view.getId() == R.id.Setting_NoofPlaces_button) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("My Rodeo");
			alert.setMessage("No Of Places");
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			input.setText(myPrefs.getString("noofplacespaid", ""));
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input.getText().toString();
							// Do something with value!
							if (value.equals("")) {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										Settings.this);
								builder.setMessage(
										"Please enter proper values to proceed!")
										.setCancelable(false)
										.setPositiveButton(
												"OK",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {
														// do things
													}
												});
								AlertDialog alert = builder.create();
								alert.show();
							} else {
								prefsEditor.putString("noofplacespaid", value);
								prefsEditor.commit();
								Toast.makeText(getApplicationContext(),
										"places Add", Toast.LENGTH_SHORT)
										.show();
							}
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});
			alert.show();

		} else if (view.getId() == R.id.Setting_NumberofRounds_button) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("My Rodeo");
			alert.setMessage("Number Of Rounds");
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			input.setText(myPrefs.getString("noofrounds", ""));
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input.getText().toString();
							// Do something with value!
							if (value.equals("")) {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										Settings.this);
								builder.setMessage(
										"Please enter proper values to proceed!")
										.setCancelable(false)
										.setPositiveButton(
												"OK",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {
														// do things
													}
												});
								AlertDialog alert = builder.create();
								alert.show();
							} else {
								prefsEditor.putString("noofrounds", value);
								prefsEditor.commit();
								Toast.makeText(getApplicationContext(),
										"NoOfRound Add", Toast.LENGTH_SHORT)
										.show();
							}
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});
			alert.show();
		} else if (view.getId() == R.id.Setting_NumberOfCont_button) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("My Rodeo");
			alert.setMessage("Number Of Contestants");
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			input.setText(myPrefs.getString("noofcontestants", ""));
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input.getText().toString();
							// Do something with value!
							if (value.equals("")) {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										Settings.this);
								builder.setMessage(
										"Please enter proper values to proceed!")
										.setCancelable(false)
										.setPositiveButton(
												"OK",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {
														// do things
													}
												});
								AlertDialog alert = builder.create();
								alert.show();
							} else {
								prefsEditor.putString("noofcontestants", value);
								prefsEditor.commit();
								Toast.makeText(getApplicationContext(),
										"NoOfContestants Add",
										Toast.LENGTH_SHORT).show();
							}
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});
			alert.show();
		}
	
		prefsEditor.commit();
		
	}
}
