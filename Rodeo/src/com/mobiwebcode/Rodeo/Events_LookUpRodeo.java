package com.mobiwebcode.Rodeo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Events_LookUpRodeo extends Activity implements OnClickListener {
	boolean iseventadded2 = false;
	public static RodeoVO selectedRodeoVO = new RodeoVO();
	boolean scrolling = false;
	TextView TitleText;
	EditText selectedEventEditText;
	ArrayList<Events_VO> eventList = new ArrayList<Events_VO>();
	private SQLiteDatabase RodeoDatabase;
	private static String DB_PATH = "/data/data/com.Syneotek.Rodeo/databases/";
	private static final String DB_NAME = "rodeo.db";
	private String path = DB_PATH + DB_NAME;
	String EVENTS_TABLE = "events";
	public static ArrayList<RodeoVO> ListRodeoName = new ArrayList<RodeoVO>();
	ListView listView;
	Button Back, addEventbtn;
	String eventtype = "";
	String[] eventName = new String[] { "Bareback", "Barrel Racing",
			"Break Away Roping", "Bull Riding", "Calf Roping", "Goat Tying",
			"Pole Bending", "Ribbon Roping", "Saddle Bronc", "Steer Wrestling",
			"Team Roping", "Other" };
	ArrayList<String> timedEventsArrayList = new ArrayList<String>();
	ArrayList<String> scoredEventsArrayList = new ArrayList<String>();
	String Events_Table = "events";
	EditText input;
	AlertDialog.Builder alertD;
	EditText contestants;
	EditText places;
	LinearLayout layout;

	public void readEvents() {
		try {
			eventList.clear();

			Cursor cursor = null;

			cursor = Constants.RodeoDatabase.rawQuery("SELECT * FROM "
					+ EVENTS_TABLE + " where rodeoid="
					+ selectedRodeoVO.rodeoid, null);
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Events_VO evo = new Events_VO();
					evo.eventID = cursor.getString(cursor
							.getColumnIndex("eventid"));
					evo.eventName = cursor.getString(cursor
							.getColumnIndex("eventname"));
					evo.noofcontestants = cursor.getString(cursor
							.getColumnIndex("contestants"));
					evo.places = cursor.getString(cursor
							.getColumnIndex("places"));
					evo.eventType = cursor.getString(cursor
							.getColumnIndex("eventType"));
					eventList.add(evo);
				}
			}
			cursor.close();
			listView.setAdapter(new EventAdapter());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onBackPressed() {
		startActivity(new Intent(Events_LookUpRodeo.this, Rodeos.class));
	}

	void showEventList() {

		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
				Events_LookUpRodeo.this);
		builderSingle.setTitle("Select event:-");
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				Events_LookUpRodeo.this,
				android.R.layout.select_dialog_singlechoice);
		for (int count = 0; count < eventName.length; count++) {
			int found = 0;
			for (int innercount = 0; innercount < eventList.size(); innercount++) {
				Events_VO evo = eventList.get(innercount);
				if (evo.eventName.equals(eventName[count])) {
					found = 1;
					break;
				}
			}
			if (found == 0) {
				arrayAdapter.add(eventName[count]);
			}
		}
		builderSingle.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builderSingle.setAdapter(arrayAdapter,
				new DialogInterface.OnClickListener() {
					String strName = "";

					@Override
					public void onClick(DialogInterface dialog, int which) {
						input = new EditText(Events_LookUpRodeo.this);
						strName = arrayAdapter.getItem(which);
						if (strName.equals("Other")) {
							AlertDialog.Builder alert = new AlertDialog.Builder(
									Events_LookUpRodeo.this);
							input.setText("");
							input.setSingleLine();
							input.setImeOptions(EditorInfo.IME_ACTION_DONE);
							input.setHint("Event Name");

							alert.setView(input);
							alert.setMessage("Enter Event Name");

							alert.setPositiveButton("Scored Event",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											// TODO Auto-generated method stub
											if (!input.getText().toString()
													.equals("")) {
												eventtype = "Scored Event";
												contestantplacesDialogue();
											}
										}
									});
							alert.setNeutralButton("Timed Event",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											// TODO Auto-generated method stub
											if (!input.getText().toString()
													.equals("")) {
												eventtype = "Timed Event";
												contestantplacesDialogue();
											}
										}
									});
							alert.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											// TODO Auto-generated method stub
											arg0.dismiss();
										}
									});
							alert.show();

						} else {
							input.setText(strName);
							contestantplacesDialogue();
						}
					}
				});
		builderSingle.show();
	}

	void contestantplacesDialogue() {
		layout = new LinearLayout(Events_LookUpRodeo.this);
		layout.removeAllViews();
		layout.setOrientation(LinearLayout.VERTICAL);
		contestants = new EditText(Events_LookUpRodeo.this);
		places = new EditText(Events_LookUpRodeo.this);
		SharedPreferences myPrefs = this.getSharedPreferences("myPrefs",
				MODE_WORLD_READABLE);
		SharedPreferences.Editor prefsEditor = myPrefs.edit();

		contestants.setHint("No of Contestants");
		contestants.setRawInputType(Configuration.KEYBOARD_12KEY);
		contestants.setImeOptions(EditorInfo.IME_ACTION_DONE);
		contestants.setText(myPrefs.getString("noofcontestants", ""));
		layout.addView(contestants);

		places.setHint("No of Places");
		places.setRawInputType(Configuration.KEYBOARD_12KEY);
		places.setImeOptions(EditorInfo.IME_ACTION_DONE);
		places.setText(myPrefs.getString("noofplacespaid", ""));
		layout.addView(places);
		alertD.setView(layout);

		alertD.setMessage("Enter No of contestants and Places");
		alertD.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				addEvent(eventtype);
			}
		});

		alertD.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						arg0.dismiss();
					}
				});
		alertD.show();
	}

	void addEvent(String eventtype) {
		ContentValues val = new ContentValues();
		val.put("eventname", input.getText().toString());
		val.put("contestants", contestants.getText().toString());
		val.put("rodeoid", selectedRodeoVO.rodeoid);
		val.put("places", places.getText().toString());
		if (eventtype.equals("")) {
			if (timedEventsArrayList.contains(input.getText().toString()))
				eventtype = "Timed Event";
			else
				eventtype = "Scored Event";
		}
		val.put("eventType", eventtype);
		long eventid = Constants.RodeoDatabase.insert(Events_Table, null, val);
		eventtype = "";
		readEvents();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lookup_rodeo);
		addEventbtn = (Button) findViewById(R.id.addEventbtn);
		alertD = new AlertDialog.Builder(Events_LookUpRodeo.this);
		addEventbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showEventList();
			}
		});

		timedEventsArrayList.add("Team Roping");
		timedEventsArrayList.add("Calf Roping");
		timedEventsArrayList.add("Barrel Racing");
		timedEventsArrayList.add("Ribbon Roping");
		timedEventsArrayList.add("Steer Wrestling");
		timedEventsArrayList.add("Break Away Roping");
		timedEventsArrayList.add("Goat Tying");
		timedEventsArrayList.add("Pole Bending");

		scoredEventsArrayList.add("Bull Riding");
		scoredEventsArrayList.add("Saddle Bronc");
		scoredEventsArrayList.add("Bareback");

		listView = (ListView) findViewById(R.id.Event_listView);
		readEvents();
		Back = (Button) findViewById(R.id.Events_Back_button);
		Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				eventList.clear();
				startActivity(new Intent(Events_LookUpRodeo.this, Rodeos.class));
			}
		});
		TitleText = (TextView) findViewById(R.id.Events_Title_textView);
		TitleText.setTypeface(HomeActivity.font);
	}

	public class EventAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return eventList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return eventList.size();
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View arg1, ViewGroup arg2) {
			View view = arg1;
			final EditText eventNameEditText;
			final EditText noofContestantsEditText;
			final EditText noofPlacesEditText;

			LayoutInflater vi = (LayoutInflater) getLayoutInflater();
			view = vi.inflate(R.layout.list_item_events_lookuprodeo, null);

			eventNameEditText = (EditText) view
					.findViewById(R.id.List_Events_EventName_editText);
			if (eventNameEditText != null)
				eventNameEditText.setId(100 + position);
			eventNameEditText.setTypeface(HomeActivity.font);
			noofContestantsEditText = (EditText) view
					.findViewById(R.id.List_Events_Cont_editText);
			if (noofContestantsEditText != null)
				noofContestantsEditText.setId(200 + position);
			noofContestantsEditText.setTypeface(HomeActivity.font);
			noofPlacesEditText = (EditText) view
					.findViewById(R.id.List_Events_Plase_editText);
			if (noofPlacesEditText != null)
				noofPlacesEditText.setId(300 + position);
			noofPlacesEditText.setTypeface(HomeActivity.font);

			eventNameEditText.setKeyListener(null);
			noofContestantsEditText.setKeyListener(null);
			noofPlacesEditText.setKeyListener(null);

			Events_VO evo = eventList.get(position);
			eventNameEditText.setText(evo.eventName);
			noofContestantsEditText.setText(evo.noofcontestants);
			noofPlacesEditText.setText(evo.places);

			eventNameEditText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onEditTextClicked(position);
				}
			});

			noofContestantsEditText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onEditTextClicked(position);
				}
			});

			noofPlacesEditText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onEditTextClicked(position);
				}
			});

			return view;
		}
	}

	private void onEditTextClicked(int position) {
		Intent edt1 = new Intent(Events_LookUpRodeo.this, Event_Details.class);
		Event_Details.selectedEventVO = eventList.get(position);
		startActivity(edt1);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
