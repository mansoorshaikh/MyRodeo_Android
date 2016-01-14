package com.mobiwebcode.Rodeo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class Event_Details extends Activity {
	ListView Lv_EventDetails;
	String screenOrientation = "portrait";
	EditText lastId = null, lastTimeID = null;
	public boolean Eitable = false, hasfocus = true;
	String oldText = "";
	String toSorted = "", sortTextValue = "";
	TextView title, EventDetails_RoundNo_textView;
	private SQLiteDatabase RodeoDatabase;
	private static String DB_PATH = "/data/data/com.Syneotek.Rodeo/databases/";
	private static final String DB_NAME = "rodeo.db";
	private String path = DB_PATH + DB_NAME;
	String Contestants = "contestants";
	String ROUND_DETAILS = "rounddetails";
	private String ContestantName, Avg, Time, Round, Position, Score, Eventid,
			Eventgroup;
	public static Events_VO selectedEventVO = new Events_VO();
	TextView forLastInRoundTextView, forLastInAvgTextView;
	int contestantAdded = 0;
	ArrayList<ContestantVO> contestantList = new ArrayList<ContestantVO>();
	Button Back, Edit, Share, Sort, AddCont, NextRound;
	TextView EventDetails_Time_textView, TotaleTime_textView;
	int currentRound = 0;
	LinearLayout contestantListLinearLayout, headingsLinearLayout;
	ArrayList<ContestantVO> tempArrayList;
	SharedPreferences myPrefs = null;

	public void addContestants(int round) {
		try {

			Cursor cursor = null;

			cursor = Constants.RodeoDatabase.rawQuery(
					"SELECT * FROM " + Contestants + " where eventid="
							+ selectedEventVO.eventID, null);
			if (cursor.getCount() == 0) {
				for (int i = 0; i < Integer
						.parseInt(selectedEventVO.noofcontestants); i++) {
					ContentValues values = new ContentValues();
					values.put("contestantname", "Contestant " + (i + 1));
					values.put("eventid", selectedEventVO.eventID);
					long contestantid = Constants.RodeoDatabase.insert(
							Contestants, null, values);

					for (int contestantcount = 0; contestantcount < Integer
							.parseInt(Events_LookUpRodeo.selectedRodeoVO.numberofrounds); contestantcount++) {
						values = new ContentValues();
						values.put("contestantid", contestantid);
						values.put("time", "0.00");
						values.put("round", contestantcount + 1);
						values.put("position", contestantcount + 1);
						values.put("score", "0.00");
						long rounddetailid = Constants.RodeoDatabase.insert(
								ROUND_DETAILS, null, values);
					}
				}
				cursor.close();
				readContestants(round);
			} else {
				readContestants(round);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void SortContestantList(View view) {

		CharSequence colors[] = new CharSequence[] { "Contestant Name",
				"Place in Round", "Place in Average", };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Option to sort");
		builder.setItems(colors, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// the user clicked on colors[which]
				if (which == 0) {
					Collections.sort(contestantList,
							new CustomComparator_ContName());
					toSorted = "ContestantName";
					sortTextValue = "Sort\nName";
				} else if (which == 1) {
					if (selectedEventVO.eventType.equals("Timed Event")) {
						contestantList = sortTimedArrayList(contestantList);
						toSorted = "Time";
						sortTextValue = "Place\nRound";
					} else {
						Collections.sort(contestantList,
								new CustomComparator_Score());
						toSorted = "Score";
						sortTextValue = "Place\nRound";
					}
				} else if (which == 2) {
					if (selectedEventVO.eventType.equals("Timed Event"))
						contestantList = sortTimedArrayList_Avg(contestantList);
					else
						Collections.sort(contestantList,
								new CustomComparator_Avg_Score());
					toSorted = "Avg";
					sortTextValue = "Place\nAverage";
				}

				fillUI();
			}
		});
		builder.show();
	}

	public void nextRoundFunction(View view) {
		if (currentRound < Integer
				.parseInt(Events_LookUpRodeo.selectedRodeoVO.numberofrounds)) {
			currentRound++;
			EventDetails_RoundNo_textView = (TextView) findViewById(R.id.EventDetails_RoundNo_textView);
			EventDetails_RoundNo_textView.setText("Round " + currentRound);
			readContestants(currentRound);
		} else {
			Builder alert = new AlertDialog.Builder(Event_Details.this);
			alert.setTitle("My Rodeo");
			alert.setMessage("No next round available");
			alert.setPositiveButton("OK", null);
			alert.show();
		}
	}

	public void prevRoundFunction(View view) {
		if (currentRound > 1) {
			currentRound--;
			EventDetails_RoundNo_textView = (TextView) findViewById(R.id.EventDetails_RoundNo_textView);
			EventDetails_RoundNo_textView.setText("Round " + currentRound);
			readContestants(currentRound);
		} else {
			Builder alert = new AlertDialog.Builder(Event_Details.this);
			alert.setTitle("My Rodeo");
			alert.setMessage("No previous round available");
			alert.setPositiveButton("OK", null);
			alert.show();
		}
	}

	public void Share(View view) {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
		shareIntent.setType("text/plain");
		startActivity(Intent.createChooser(shareIntent, "Choose anything"));

	}

	public int readContestants(int round) {
		int contestantscount = 0;
		try {
			contestantList.clear();

			Cursor cursor = null;

			cursor = Constants.RodeoDatabase.rawQuery(
					"SELECT * FROM " + Contestants + " where eventid="
							+ selectedEventVO.eventID, null);
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					ContestantVO contestant = new ContestantVO();
					contestant.contid = cursor.getInt(cursor
							.getColumnIndex("contestantid"));
					contestant.contName = cursor.getString(cursor
							.getColumnIndex("contestantname"));
					DecimalFormat twoDForm = new DecimalFormat("#.##");
					SharedPreferences myPrefs = this.getSharedPreferences(
							"myPrefs", MODE_WORLD_READABLE);
					if (selectedEventVO.eventType.equals("Timed Event")) {

						contestant.Avg = String.valueOf(String.format("%."
								+ myPrefs.getString("timeformat", "") + "f",
								getAverageFortime(contestant)));
					} else {
						contestant.Avg = String.valueOf(String.format("%."
								+ myPrefs.getString("scoreformat", "") + "f",
								getAveragescore(contestant)));
					}
					contestantList.add(contestant);

				}
			}
			cursor.close();

			for (int count = 0; count < contestantList.size(); count++) {
				ContestantVO contestant = contestantList.get(count);
				cursor = Constants.RodeoDatabase.rawQuery("SELECT * FROM "
						+ ROUND_DETAILS + " where contestantid="
						+ contestant.contid + " AND round=" + round, null);
				SharedPreferences myPrefs = this.getSharedPreferences(
						"myPrefs", MODE_WORLD_READABLE);
				if (cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						contestant.roundID = cursor.getInt(cursor
								.getColumnIndex("roundid"));
						contestant.Round = round;
						contestant.position = cursor.getInt(cursor
								.getColumnIndex("position"));
						contestant.Time = String.valueOf(String.format("%."
								+ myPrefs.getString("timeformat", "") + "f",
								Double.parseDouble(cursor.getString(cursor
										.getColumnIndex("time")))));
						contestant.Score = String.valueOf(String.format("%."
								+ myPrefs.getString("scoreformat", "") + "f",
								Double.parseDouble(cursor.getString(cursor
										.getColumnIndex("score")))));
					}
				}
				cursor.close();
			}
			if (toSorted.equals("ContestantName")) {
				Collections.sort(contestantList,
						new CustomComparator_ContName());
			} else if (toSorted.equals("Time")) {
				contestantList = sortTimedArrayList(contestantList);
			} else if (toSorted.equals("Score")) {
				Collections.sort(contestantList, new CustomComparator_Score());
			} else if (toSorted.equals("Avg")) {
				if (selectedEventVO.eventType.equals("Timed Event"))
					tempArrayList = sortTimedArrayList_Avg(contestantList);
				else
					Collections.sort(contestantList,
							new CustomComparator_Avg_Score());
			}

			fillUI();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return contestantscount;
	}

	ArrayList<ContestantVO> sortScoredArrayList_Avg(
			ArrayList<ContestantVO> contestantList) {
		ArrayList<ContestantVO> sortnotZeroList = new ArrayList<ContestantVO>();
		ArrayList<ContestantVO> sortZeroList = new ArrayList<ContestantVO>();
		for (int count = 0; count < contestantList.size(); count++) {
			ContestantVO contestant = contestantList.get(count);

			if (Double.parseDouble(contestant.Score) > 0.00
					&& Double.parseDouble(contestant.Avg) > 0.00) {
				sortnotZeroList.add(contestant);
			} else if (Double.parseDouble(contestant.Avg) == 0.00) {
				sortZeroList.add(contestant);
			}
		}
		Collections.sort(sortnotZeroList, new CustomComparator_Avg_Score());
		contestantList = new ArrayList<ContestantVO>();
		for (int count = 0; count < sortnotZeroList.size(); count++) {
			contestantList.add(sortnotZeroList.get(count));

		}
		for (int count = 0; count < sortZeroList.size(); count++) {
			contestantList.add(sortZeroList.get(count));
		}
		return contestantList;
	}

	ArrayList<ContestantVO> sortTimedArrayList_Avg(
			ArrayList<ContestantVO> contestantList) {
		ArrayList<ContestantVO> sortnotZeroList = new ArrayList<ContestantVO>();
		ArrayList<ContestantVO> sortZeroList = new ArrayList<ContestantVO>();
		for (int count = 0; count < contestantList.size(); count++) {
			ContestantVO contestant = contestantList.get(count);
			if (Double.parseDouble(contestant.Time) > 0.00
					&& Double.parseDouble(contestant.Avg) > 0.00) {
				sortnotZeroList.add(contestant);
			} else {
				sortZeroList.add(contestant);
			}
		}
		Collections.sort(sortnotZeroList, new CustomComparator_Avg_Time());
		contestantList = new ArrayList<ContestantVO>();
		for (int count = 0; count < sortnotZeroList.size(); count++) {
			contestantList.add(sortnotZeroList.get(count));

		}
		for (int count = 0; count < sortZeroList.size(); count++) {
			contestantList.add(sortZeroList.get(count));
		}
		return contestantList;
	}

	ArrayList<ContestantVO> sortTimedArrayList(
			ArrayList<ContestantVO> contestantList) {
		ArrayList<ContestantVO> sortnotZeroList = new ArrayList<ContestantVO>();
		ArrayList<ContestantVO> sortZeroList = new ArrayList<ContestantVO>();
		for (int count = 0; count < contestantList.size(); count++) {
			ContestantVO contestant = contestantList.get(count);

			if (Double.parseDouble(contestant.Time) > 0.00) {
				sortnotZeroList.add(contestant);
			} else if (Double.parseDouble(contestant.Time) == 0.00) {
				sortZeroList.add(contestant);
			}

		}
		Collections.sort(sortnotZeroList, new CustomComparator_Time());
		contestantList = new ArrayList<ContestantVO>();
		for (int count = 0; count < sortnotZeroList.size(); count++) {
			contestantList.add(sortnotZeroList.get(count));

		}
		for (int count = 0; count < sortZeroList.size(); count++) {
			contestantList.add(sortZeroList.get(count));
		}
		return contestantList;
	}

	public static void setAutoOrientationEnabled(Context context,
			boolean enabled) {
		Settings.System.putInt(context.getContentResolver(),
				Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.event_details_portrait_landscape);
			screenOrientation = "landscape";
			title = (TextView) findViewById(R.id.EventDetail_landscape_Title_textView);
		} else {
			screenOrientation = "portrait";
			setContentView(R.layout.event_details_portrait);
			title = (TextView) findViewById(R.id.EventDetail_Title_textView);
		}
		setAutoOrientationEnabled(Event_Details.this, true);
		lastId = new EditText(Event_Details.this);
		lastId.setId(2000);
		lastTimeID = new EditText(Event_Details.this);
		lastTimeID.setId(4000);
		currentRound = 1;
		addContestants(currentRound);

		title.setTypeface(HomeActivity.font);
		title.setText(selectedEventVO.eventName);
		EventDetails_RoundNo_textView = (TextView) findViewById(R.id.EventDetails_RoundNo_textView);
		EventDetails_RoundNo_textView.setText("Round " + currentRound);

		NextRound = (Button) findViewById(R.id.EventDetails_NextRound_button);

		NextRound.setTypeface(HomeActivity.font);
		Back = (Button) findViewById(R.id.EventDetails_Back_button);
		Back.setTypeface(HomeActivity.font);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			screenOrientation = "landscape";
			setContentView(R.layout.event_details_portrait_landscape);
			EventDetails_RoundNo_textView = (TextView) findViewById(R.id.EventDetails_RoundNo_textView);
			EventDetails_RoundNo_textView.setText("Round " + currentRound);
			title = (TextView) findViewById(R.id.EventDetail_landscape_Title_textView);
			title.setText(selectedEventVO.eventName);

		} else {
			screenOrientation = "portrait";
			setContentView(R.layout.event_details_portrait);
			EventDetails_RoundNo_textView = (TextView) findViewById(R.id.EventDetails_RoundNo_textView);
			EventDetails_RoundNo_textView.setText("Round " + currentRound);
			title = (TextView) findViewById(R.id.EventDetail_Title_textView);
			title.setText(selectedEventVO.eventName);

		}
		fillUI();
	}

	private double getAveragescore(ContestantVO cvo) {
		double avg = 0.00;
		Cursor cursor = null;
		int roundcount = currentRound;
		while (roundcount > 0) {
			cursor = Constants.RodeoDatabase.rawQuery("SELECT * FROM "
					+ ROUND_DETAILS + " where round=" + roundcount
					+ " AND contestantid=" + cvo.contid, null);
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					avg = avg
							+ Double.valueOf(String.format(
									"%." + myPrefs.getString("scoreformat", "")
											+ "f", Double.parseDouble(cursor
											.getString(cursor
													.getColumnIndex("score")))));
				}
				cursor.close();
			}
			roundcount--;
		}

		cvo.Avg = String.valueOf(avg);
		return avg;
	}

	private double getAverageFortime(ContestantVO cvo) {
		double avg = 0.00;
		Cursor cursor = null;
		int roundcount = currentRound;
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		while (roundcount > 0) {
			cursor = Constants.RodeoDatabase.rawQuery("SELECT * FROM "
					+ ROUND_DETAILS + " where round=" + roundcount
					+ " AND contestantid=" + cvo.contid, null);
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					avg = avg
							+ Double.valueOf(String.format(
									"%." + myPrefs.getString("timeformat", "")
											+ "f", Double.parseDouble(cursor
											.getString(cursor
													.getColumnIndex("time")))));
				}
				cursor.close();
			}
			roundcount--;
		}

		cvo.Avg = String.valueOf(avg);
		return avg;

	}

	private double getRoundPredictions_ScoredEvent(
			ContestantVO currentContestant, int contestantnumber) {
		double roundscore = 0;
		tempArrayList = (ArrayList<ContestantVO>) contestantList.clone();
		if (selectedEventVO.eventType.equals("Timed Event")) {
			tempArrayList = sortTimedArrayList(tempArrayList);
		} else {
			Collections.sort(tempArrayList, new CustomComparator_Score());
		}
		ContestantVO firstContestant = tempArrayList.get(contestantnumber);
		if (firstContestant.Score.equals(currentContestant.Score)) {
			return 0;
		} else {
			roundscore = Double.parseDouble(firstContestant.Score)
					- Double.parseDouble(currentContestant.Score);
			if (roundscore < 0)
				return 0;
		}

		return roundscore;
	}

	private double getAvgPredictions_ScoredEvent(
			ContestantVO currentContestant, int contestantnumber) {
		double roundscore = 0;
		tempArrayList = (ArrayList<ContestantVO>) contestantList.clone();
		if (selectedEventVO.eventType.equals("Timed Event")) {
			tempArrayList = sortTimedArrayList_Avg(tempArrayList);
		} else {
			tempArrayList = sortScoredArrayList_Avg(tempArrayList);
		}
		ContestantVO firstContestant = tempArrayList.get(contestantnumber);
		if (firstContestant.Avg.equals(currentContestant.Avg)) {
			return 0;
		} else {
			roundscore = Double.parseDouble(firstContestant.Avg)
					- Double.parseDouble(currentContestant.Avg);
			if (roundscore < 0)
				return 0;
		}
		return roundscore;
	}

	private double getRoundPredictions_TimedEvent(
			ContestantVO currentContestant, int contestantnumber) {

		tempArrayList = (ArrayList<ContestantVO>) contestantList.clone();
		if (selectedEventVO.eventType.equals("Timed Event")) {

			tempArrayList = sortTimedArrayList(tempArrayList);
		} else {
			Collections.sort(tempArrayList, new CustomComparator_Score());
		}

		double roundscore = 0;
		ContestantVO firstContestant = tempArrayList.get(contestantnumber);
		if (firstContestant.Time.equals(currentContestant.Time)) {
			return 0;
		} else {
			roundscore = Double.parseDouble(firstContestant.Time)
					- Double.parseDouble(currentContestant.Time);
			if (roundscore < 0)
				return 0;
		}

		return roundscore;
	}

	private double getAvgPredictions_TimedEvent(ContestantVO currentContestant,
			int contestantnumber) {
		tempArrayList = (ArrayList<ContestantVO>) contestantList.clone();
		if (selectedEventVO.eventType.equals("Timed Event")) {
			tempArrayList = sortTimedArrayList_Avg(tempArrayList);
		} else {
			Collections.sort(tempArrayList, new CustomComparator_Score());
		}

		double roundscore = 0;
		ContestantVO firstContestant = tempArrayList.get(contestantnumber);
		if (firstContestant.Avg.equals(currentContestant.Avg)) {
			return 0;
		} else {
			roundscore = Double.parseDouble(firstContestant.Avg)
					- Double.parseDouble(currentContestant.Avg);
			if (roundscore < 0)
				return 0;
		}
		return roundscore;
	}

	public class CustomComparator_Score implements Comparator<ContestantVO> {
		@Override
		public int compare(ContestantVO o1, ContestantVO o2) {
			if ((Float.parseFloat(o2.Score) - Float.parseFloat(o1.Score)) > 0)
				return 1;
			else if ((Float.parseFloat(o2.Score) - Float.parseFloat(o1.Score)) < 0)
				return -1;
			else
				return 0;
		}
	}

	public class CustomComparator_Time implements Comparator<ContestantVO> {
		@Override
		public int compare(ContestantVO o1, ContestantVO o2) {
			System.out.println(Float.parseFloat(o1.Time)
					- Float.parseFloat(o2.Time));
			if ((Float.parseFloat(o1.Time) - Float.parseFloat(o2.Time)) > 0)
				return 1;
			else if ((Float.parseFloat(o1.Time) - Float.parseFloat(o2.Time)) < 0)
				return -1;
			else
				return 0;

		}
	}

	public class CustomComparator_CotestantID implements
			Comparator<ContestantVO> {
		@Override
		public int compare(ContestantVO o1, ContestantVO o2) {
			return o1.contid - o2.contid;
		}
	}

	public class CustomComparator_Avg_Score implements Comparator<ContestantVO> {
		@Override
		public int compare(ContestantVO o1, ContestantVO o2) {
			if ((Float.parseFloat(o2.Avg) - Float.parseFloat(o1.Avg)) > 0)
				return 1;
			else if ((Float.parseFloat(o2.Avg) - Float.parseFloat(o1.Avg)) < 0)
				return -1;
			else
				return 0;
		}
	}

	public class CustomComparator_Avg_Time implements Comparator<ContestantVO> {
		@Override
		public int compare(ContestantVO o1, ContestantVO o2) {
			if ((Float.parseFloat(o1.Avg) - Float.parseFloat(o2.Avg)) > 0)
				return 1;
			else if ((Float.parseFloat(o1.Avg) - Float.parseFloat(o2.Avg)) < 0)
				return -1;
			else
				return 0;
		}
	}

	public class CustomComparator_ContName implements Comparator<ContestantVO> {
		@Override
		public int compare(ContestantVO o1, ContestantVO o2) {
			return o1.contName.compareToIgnoreCase(o2.contName);
		}
	}

	int getPosition(ArrayList<ContestantVO> contList, String contName) {
		tempArrayList = (ArrayList<ContestantVO>) contestantList.clone();
		if (selectedEventVO.eventType.equals("Timed Event")) {
			if (toSorted.equals("Avg"))
				tempArrayList = sortTimedArrayList_Avg(tempArrayList);
			else
				tempArrayList = sortTimedArrayList(tempArrayList);
		} else {
			if (toSorted.equals("Avg"))
				Collections.sort(tempArrayList,
						new CustomComparator_Avg_Score());
			else
				Collections.sort(tempArrayList, new CustomComparator_Score());
		}

		for (int cont = 0; cont < tempArrayList.size(); cont++) {
			ContestantVO cvo = tempArrayList.get(cont);
			if (cvo.contName.equals(contName))
				return cont + 1;
		}

		return 0;
	}

	String getScoreTime(String contName) {
		tempArrayList = (ArrayList<ContestantVO>) contestantList.clone();
		if (selectedEventVO.eventType.equals("Timed Event")) {
			if (toSorted.equals("Avg"))
				tempArrayList = sortTimedArrayList_Avg(tempArrayList);
			else if (toSorted.equals("Time")
					|| toSorted.equals("ContestantName"))
				tempArrayList = sortTimedArrayList(tempArrayList);
		} else {
			if (toSorted.equals("Avg"))
				Collections.sort(tempArrayList,
						new CustomComparator_Avg_Score());
			else if (toSorted.equals("Score")
					|| toSorted.equals("ContestantName"))
				Collections.sort(tempArrayList, new CustomComparator_Score());
		}

		for (int cont = 0; cont < tempArrayList.size(); cont++) {
			ContestantVO cvo = tempArrayList.get(cont);
			if (cvo.contName.equals(contName)) {
				if (selectedEventVO.eventType.equals("Timed Event"))
					return cvo.Time;
				else
					return cvo.Score;
			}
		}

		return "";
	}

	String getAvg(String contName) {
		tempArrayList = (ArrayList<ContestantVO>) contestantList.clone();
		if (selectedEventVO.eventType.equals("Timed Event")) {
			if (toSorted.equals("Avg"))
				tempArrayList = sortTimedArrayList_Avg(tempArrayList);
			else if (toSorted.equals("Time")
					|| toSorted.equals("ContestantName"))
				tempArrayList = sortTimedArrayList(tempArrayList);
		} else {
			if (toSorted.equals("Avg"))
				Collections.sort(tempArrayList,
						new CustomComparator_Avg_Score());
			else if (toSorted.equals("Score")
					|| toSorted.equals("ContestantName"))
				Collections.sort(tempArrayList, new CustomComparator_Score());
		}

		for (int cont = 0; cont < tempArrayList.size(); cont++) {
			ContestantVO cvo = tempArrayList.get(cont);
			if (cvo.contName.equals(contName)) {
				if (selectedEventVO.eventType.equals("Timed Event"))
					return cvo.Avg;
				else
					return cvo.Avg;
			}
		}
		return "";
	}
	void fillUI() {
		contestantListLinearLayout = (LinearLayout) findViewById(R.id.contestantListLinearLayout);
		contestantListLinearLayout.removeAllViews();
		headingsLinearLayout = (LinearLayout) findViewById(R.id.headingsLinearLayout);

		headingsLinearLayout.removeAllViews();
		TextView contestantTextView = new TextView(Event_Details.this);
		contestantTextView.setText("Contestant Name");
		contestantTextView.setTextColor(Color.BLACK);

		// Round Textview
		TextView rndTextView = new TextView(Event_Details.this);
		if (sortTextValue.equals(""))
			rndTextView.setText("Place\nRound");
		else {
			rndTextView.setText(sortTextValue);
		}

		rndTextView.setTextColor(Color.BLACK);

		// Avg Textview
		TextView avgTextView = new TextView(Event_Details.this);
		avgTextView.setText("Avg");
		avgTextView.setTextColor(Color.BLACK);

		// Score/Time Textview
		TextView scoreTextView = new TextView(Event_Details.this);
		scoreTextView.setText("Time");
		scoreTextView.setTextColor(Color.BLACK);

		// First in score Textview
		TextView firstinscoreTextView = new TextView(Event_Details.this);
		firstinscoreTextView.setText("For 1st\nin Rnd");
		firstinscoreTextView.setTextColor(Color.BLACK);

		// last in score Textview
		TextView lastinscoreTextView = new TextView(Event_Details.this);
		lastinscoreTextView.setText("For " + selectedEventVO.places
				+ "\nin Rnd");
		lastinscoreTextView.setTextColor(Color.BLACK);

		// First in avg Tgextview
		TextView firstinavgTextView = new TextView(Event_Details.this);
		firstinavgTextView.setText("For 1st\nin Avg");
		firstinavgTextView.setTextColor(Color.BLACK);

		// last in avg Textview
		TextView lastinavgTextView = new TextView(Event_Details.this);
		lastinavgTextView.setText("For " + selectedEventVO.places
				+ "\nin Avg");
		lastinavgTextView.setTextColor(Color.BLACK);

		for (int contestantcount = 0; contestantcount < contestantList.size(); contestantcount++) {
			ContestantVO contestantVO = contestantList.get(contestantcount);

			LinearLayout contestantRow = new LinearLayout(Event_Details.this);
			contestantRow.setOrientation(LinearLayout.HORIZONTAL);
			contestantRow.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));

			Display display = getWindowManager().getDefaultDisplay();
			int width = display.getWidth(); // deprecated

			LayoutParams contestantportrait_LayoutParams = new LayoutParams(
					width / 4 + 100, LinearLayout.LayoutParams.WRAP_CONTENT);
			contestantportrait_LayoutParams.setMargins(5, 10, 5, 10);
			LayoutParams contestantlandscape_LayoutParams = new LayoutParams(
					width / 8 + 100, LinearLayout.LayoutParams.WRAP_CONTENT);
			contestantlandscape_LayoutParams.setMargins(5, 10, 5, 10);
			LayoutParams portrait_LayoutParams = new LayoutParams(
					(width / 4) - 40, LinearLayout.LayoutParams.WRAP_CONTENT);
			portrait_LayoutParams.setMargins(5, 10, 5, 10);
			LayoutParams landscape_LayoutParams = new LayoutParams(
					(width / 8) - 20, LinearLayout.LayoutParams.WRAP_CONTENT);
			landscape_LayoutParams.setMargins(5, 10, 5, 10);
			// contestant name edit text

			final CustomEditText contestantNameEditText = new CustomEditText(
					Event_Details.this);
			LayoutParams contestantNameEditText_LayoutParams = new LayoutParams(
					width / 4, LinearLayout.LayoutParams.WRAP_CONTENT);
			contestantNameEditText_LayoutParams.setMargins(5, 3, 5, 2);
			// contestantNameEditText
			// .setLayoutParams(contestantNameEditText_LayoutParams);
			contestantNameEditText.setText(contestantVO.contName);
			contestantNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
			contestantNameEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			contestantNameEditText.setTextColor(Color.BLUE);
			contestantNameEditText.setTextSize(15);
			contestantNameEditText.setTypeface(null, Typeface.BOLD);
			contestantNameEditText.setId(100 + contestantcount);
			contestantNameEditText.setFocusableInTouchMode(false);
			contestantNameEditText
					.setOnEditorActionListener(new OnEditorActionListener() {
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
									|| (actionId == EditorInfo.IME_ACTION_DONE)) {
								if (lastId.getId() != 2000) {
									ContestantVO cvo = contestantList
											.get(lastId.getId() - 100);
									ContentValues contentValues = new ContentValues();
									if (!lastId.getText().toString().equals("")) {
										contentValues.put("contestantname",
												lastId.getText().toString());
										Constants.RodeoDatabase.update(
												Contestants, contentValues,
												"contestantid " + "="
														+ cvo.contid, null);
										readContestants(currentRound);
									} else {
										lastId.setText(cvo.contName);
									}
									lastId.setFocusableInTouchMode(false);
									lastId = new EditText(Event_Details.this);
									lastId.setId(2000);
								}
							}
							return false;
						}
					});

			contestantNameEditText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (lastTimeID.getId() != 4000) {
						ContestantVO cvo = contestantList.get(lastTimeID
								.getId() - 200);
						ContentValues contentValues = new ContentValues();
						if (selectedEventVO.eventType.equals("Timed Event")) {
							if (!lastTimeID.getText().toString().equals("")) {
								contentValues.put("time", lastTimeID.getText()
										.toString());
								Constants.RodeoDatabase.update(ROUND_DETAILS,
										contentValues, "roundid " + "="
												+ cvo.roundID, null);
								readContestants(currentRound);
							} else
								lastTimeID.setText(cvo.Time);
						} else {
							if (!lastTimeID.getText().toString().equals("")) {
								contentValues.put("score", lastTimeID.getText()
										.toString());
								Constants.RodeoDatabase.update(ROUND_DETAILS,
										contentValues, "roundid " + "="
												+ cvo.roundID, null);
								readContestants(currentRound);
							} else
								lastTimeID.setText(cvo.Score);
						}
						lastTimeID.setFocusableInTouchMode(false);
						lastTimeID = new EditText(Event_Details.this);
						lastTimeID.setId(4000);
					}

					if (lastId.getId() == 2000) {
						lastId = (EditText) v;
					}

					if (((EditText) v).getText().toString()
							.contains("Contestant ")
							|| ((EditText) v).getText().toString().equals("")) {
						((EditText) v).setText("");
//						contestantNameEditText.setFocusableInTouchMode(true);
//						contestantNameEditText.setFocusable(true);
//						((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
					}

					if (lastId.getId() != v.getId()) {
						ContestantVO cvo = contestantList.get(lastId.getId() - 100);
						ContentValues contentValues = new ContentValues();
						if (!lastId.getText().toString().equals("")) {
							contentValues.put("contestantname", lastId
									.getText().toString());
							Constants.RodeoDatabase.update(Contestants,
									contentValues, "contestantid " + "="
											+ cvo.contid, null);
							readContestants(currentRound);
						} else {
							lastId.setText(cvo.contName);
						}
						lastId.setFocusableInTouchMode(false);
						lastId.setFocusable(false);
						lastId = (EditText) v;
					}
					((EditText) v).setFocusableInTouchMode(true);
					((EditText) v).setFocusable(true);
					((EditText) v).requestFocus();
					
//					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.showSoftInput(((EditText) v),
//							InputMethodManager.SHOW_IMPLICIT);
					
					((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
				}
			});

			// Round edit text

			EditText roundEditText = new EditText(Event_Details.this);
			LayoutParams roundEditText_LayoutParams = new LayoutParams(50,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			roundEditText_LayoutParams.setMargins(5, 3, 5, 2);
			// roundEditText.setLayoutParams(roundEditText_LayoutParams);
			roundEditText.setText(String.valueOf(getPosition(contestantList,
					contestantVO.contName)));

			roundEditText.setFocusableInTouchMode(false);

			// Avg edit text

			EditText avgEditText = new EditText(Event_Details.this);
			LayoutParams avgEditText_LayoutParams = new LayoutParams(50,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			avgEditText_LayoutParams.setMargins(5, 3, 5, 2);
			// avgEditText.setLayoutParams(avgEditText_LayoutParams);
			avgEditText.setFocusableInTouchMode(false);

			// Time edit text

			CustomEditText timeEditText = new CustomEditText(Event_Details.this);
			LayoutParams timeEditText_LayoutParams = new LayoutParams(50,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			timeEditText_LayoutParams.setMargins(5, 3, 5, 2);
			timeEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			timeEditText.setTextColor(Color.BLUE);
			timeEditText.setTypeface(null, Typeface.BOLD);
			timeEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			timeEditText.setFocusableInTouchMode(false);
			timeEditText.setId(200 + contestantcount);

			timeEditText
					.setOnEditorActionListener(new OnEditorActionListener() {
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
									|| (actionId == EditorInfo.IME_ACTION_DONE)) {
								if (lastTimeID.getId() != 4000) {
									ContestantVO cvo = contestantList
											.get(lastTimeID.getId() - 200);
									ContentValues contentValues = new ContentValues();
									if (selectedEventVO.eventType
											.equals("Timed Event")) {
										if (!lastTimeID.getText().toString()
												.equals("")) {
											contentValues.put("time",
													lastTimeID.getText()
															.toString());
											Constants.RodeoDatabase
													.update(ROUND_DETAILS,
															contentValues,
															"roundid "
																	+ "="
																	+ cvo.roundID,
															null);
										} else {
											lastTimeID.setText(cvo.Time);
										}
									} else {
										if (!lastTimeID.getText().toString()
												.equals("")) {
											contentValues.put("score",
													lastTimeID.getText()
															.toString());
											Constants.RodeoDatabase
													.update(ROUND_DETAILS,
															contentValues,
															"roundid "
																	+ "="
																	+ cvo.roundID,
															null);
										} else {
											lastTimeID.setText(cvo.Score);
										}
									}
									lastTimeID.setFocusableInTouchMode(false);
									lastTimeID = new EditText(
											Event_Details.this);
									lastTimeID.setId(4000);
									readContestants(currentRound);
								}
							}
							return false;
						}
					});

			timeEditText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (lastId.getId() != 2000) {
						ContestantVO cvo = contestantList.get(lastId.getId() - 100);
						ContentValues contentValues = new ContentValues();
						if (!lastId.getText().toString().equals("")) {
							contentValues.put("contestantname", lastId
									.getText().toString());
							Constants.RodeoDatabase.update(Contestants,
									contentValues, "contestantid " + "="
											+ cvo.contid, null);
						} else {
							lastId.setText(cvo.contName);
						}
						lastId.setFocusableInTouchMode(false);
						lastId = new EditText(Event_Details.this);
						lastId.setId(2000);
					}

					System.out.println("has focus" + v.getId());
					if (lastTimeID.getId() == 4000)
						lastTimeID = (EditText) v;
					if (((EditText) v).getText().toString().startsWith("0.")
							|| ((EditText) v).getText().toString().equals("")) {
						((EditText) v).setText("");
					}
					if (lastTimeID.getId() != v.getId()) {
						ContestantVO cvo = contestantList.get(lastTimeID
								.getId() - 200);
						ContentValues contentValues = new ContentValues();
						if (selectedEventVO.eventType.equals("Timed Event")) {
							if (!lastTimeID.getText().toString().equals("")) {
								contentValues.put("time", lastTimeID.getText()
										.toString());
								Constants.RodeoDatabase.update(ROUND_DETAILS,
										contentValues, "roundid " + "="
												+ cvo.roundID, null);
								readContestants(currentRound);
							} else {
								lastTimeID.setText(cvo.Time);
							}
						} else {
							if (!lastTimeID.getText().toString().equals("")) {
								contentValues.put("score", lastTimeID.getText()
										.toString());
								Constants.RodeoDatabase.update(ROUND_DETAILS,
										contentValues, "roundid " + "="
												+ cvo.roundID, null);
								readContestants(currentRound);
							} else {
								lastTimeID.setText(cvo.Score);
							}
						}
						lastTimeID.setFocusableInTouchMode(false);
						lastTimeID.setFocusable(false);
						lastTimeID = (EditText) v;
					}

					((EditText) v).setFocusableInTouchMode(true);
					((EditText) v).setFocusable(true);
					((EditText) v).requestFocus();
					((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
				}
			});
			// Fist in round edit text

			EditText firstInround_Score_EditText = new EditText(
					Event_Details.this);
			LayoutParams firstInround_Score_EditText_LayoutParams = new LayoutParams(
					50, LinearLayout.LayoutParams.WRAP_CONTENT);
			firstInround_Score_EditText_LayoutParams.setMargins(5, 3, 5, 2);
			// firstInround_Score_EditText
			// .setLayoutParams(firstInround_Score_EditText_LayoutParams);
			firstInround_Score_EditText.setFocusableInTouchMode(false);

			// last in round edit text

			EditText lastInround_Score_EditText = new EditText(
					Event_Details.this);
			LayoutParams lastInround_Score_EditText_LayoutParams = new LayoutParams(
					50, LinearLayout.LayoutParams.WRAP_CONTENT);
			lastInround_Score_EditText_LayoutParams.setMargins(5, 3, 5, 2);
			// lastInround_Score_EditText
			// .setLayoutParams(lastInround_Score_EditText_LayoutParams);
			lastInround_Score_EditText.setFocusableInTouchMode(false);

			// last in round edit text

			EditText firstInround_Avg_EditText = new EditText(
					Event_Details.this);
			LayoutParams firstInround_Avg_EditText_LayoutParams = new LayoutParams(
					50, LinearLayout.LayoutParams.WRAP_CONTENT);
			firstInround_Avg_EditText_LayoutParams.setMargins(5, 3, 5, 2);
			// firstInround_Avg_EditText
			// .setLayoutParams(firstInround_Avg_EditText_LayoutParams);
			firstInround_Avg_EditText.setFocusableInTouchMode(false);

			EditText lastInround_Avg_EditText = new EditText(Event_Details.this);
			LayoutParams lastInround_Avg_EditText_LayoutParams = new LayoutParams(
					50, LinearLayout.LayoutParams.WRAP_CONTENT);
			lastInround_Avg_EditText_LayoutParams.setMargins(5, 3, 5, 2);
			// lastInround_Avg_EditText
			// .setLayoutParams(lastInround_Avg_EditText_LayoutParams);
			lastInround_Avg_EditText.setFocusableInTouchMode(false);

			if (selectedEventVO.eventType.equals("Timed Event")) {
				timeEditText.setText(String
						.valueOf(getScoreTime(contestantVO.contName)));
				avgEditText.setText(String.valueOf(String.format(
						"%." + myPrefs.getString("timeformat", "") + "f",
						Double.parseDouble(getAvg(contestantVO.contName)))));
				if (Float.parseFloat(getScoreTime(contestantVO.contName)) > 0.00) {
					firstInround_Score_EditText.setText("--");
					firstInround_Avg_EditText.setText("--");
					lastInround_Avg_EditText.setText("--");
					lastInround_Score_EditText.setText("--");
				} else {
					firstInround_Score_EditText.setText(String.valueOf(String
							.format("%." + myPrefs.getString("timeformat", "")
									+ "f",
									getRoundPredictions_TimedEvent(
											contestantVO, 0))));
					firstInround_Avg_EditText.setText(String.valueOf(String
							.format("%." + myPrefs.getString("timeformat", "")
									+ "f",
									getAvgPredictions_TimedEvent(contestantVO,
											0))));
					lastInround_Score_EditText
							.setText(String.valueOf(String.format(
									"%." + myPrefs.getString("timeformat", "")
											+ "f",
									getRoundPredictions_TimedEvent(
											contestantVO,
											(Integer.parseInt(selectedEventVO.places) - 1)))));
					lastInround_Avg_EditText
							.setText(String.valueOf(String.format(
									"%." + myPrefs.getString("timeformat", "")
											+ "f",
									getAvgPredictions_TimedEvent(
											contestantVO,
											(Integer.parseInt(selectedEventVO.places) - 1)))));
				}

				if (selectedEventVO.places.equals("1")) {
					lastInround_Score_EditText.setText("--");
					lastInround_Avg_EditText.setText("--");
				}
			} else {
				scoreTextView.setText("Score");
				timeEditText.setText(contestantVO.Score);
				avgEditText.setText(String.valueOf(String.format(
						"%." + myPrefs.getString("scoreformat", "") + "f",
						Double.parseDouble(getAvg(contestantVO.contName)))));
				if (Float.parseFloat(contestantVO.Score) > 0.00) {
					firstInround_Score_EditText.setText("--");
					firstInround_Avg_EditText.setText("--");
					lastInround_Avg_EditText.setText("--");
					lastInround_Score_EditText.setText("--");
				} else {
					firstInround_Score_EditText.setText(String.valueOf(String
							.format("%." + myPrefs.getString("scoreformat", "")
									+ "f",
									getRoundPredictions_ScoredEvent(
											contestantVO, 0))));

					firstInround_Avg_EditText.setText(String.valueOf(String
							.format("%." + myPrefs.getString("scoreformat", "")
									+ "f",
									getAvgPredictions_ScoredEvent(contestantVO,
											0))));
					lastInround_Score_EditText
							.setText(String.format(
									"%." + myPrefs.getString("scoreformat", "")
											+ "f",
									getRoundPredictions_ScoredEvent(
											contestantVO,
											(Integer.parseInt(selectedEventVO.places) - 1))));
					lastInround_Avg_EditText
							.setText(String.valueOf(String.valueOf(String.format(
									"%." + myPrefs.getString("scoreformat", "")
											+ "f",
									getAvgPredictions_ScoredEvent(
											contestantVO,
											(Integer.parseInt(selectedEventVO.places) - 1))))));
				}
				if (selectedEventVO.places.equals("1")) {
					lastInround_Score_EditText.setText("--");
					lastInround_Avg_EditText.setText("--");
				}
			}

			contestantNameEditText.setBackgroundColor(Color.TRANSPARENT);
			roundEditText.setBackgroundColor(Color.TRANSPARENT);
			avgEditText.setBackgroundColor(Color.TRANSPARENT);
			timeEditText.setBackgroundColor(Color.TRANSPARENT);
			firstInround_Score_EditText.setBackgroundColor(Color.TRANSPARENT);
			lastInround_Score_EditText.setBackgroundColor(Color.TRANSPARENT);
			firstInround_Avg_EditText.setBackgroundColor(Color.TRANSPARENT);
			lastInround_Avg_EditText.setBackgroundColor(Color.TRANSPARENT);

			if (screenOrientation.equals("portrait")) {

				contestantTextView
						.setLayoutParams(contestantportrait_LayoutParams);
				rndTextView.setLayoutParams(portrait_LayoutParams);
				avgTextView.setLayoutParams(portrait_LayoutParams);
				scoreTextView.setLayoutParams(portrait_LayoutParams);
				if (headingsLinearLayout.getChildCount() == 0) {
					headingsLinearLayout.addView(contestantTextView);
					headingsLinearLayout.addView(rndTextView);
					headingsLinearLayout.addView(avgTextView);
					headingsLinearLayout.addView(scoreTextView);
				}

				contestantNameEditText
						.setLayoutParams(contestantportrait_LayoutParams);
				roundEditText.setLayoutParams(portrait_LayoutParams);
				avgEditText.setLayoutParams(portrait_LayoutParams);
				timeEditText.setLayoutParams(portrait_LayoutParams);

				contestantRow.addView(contestantNameEditText);
				contestantRow.addView(roundEditText);
				contestantRow.addView(avgEditText);
				contestantRow.addView(timeEditText);
			} else if (screenOrientation.equals("landscape")) {

				contestantTextView
						.setLayoutParams(contestantlandscape_LayoutParams);
				rndTextView.setLayoutParams(landscape_LayoutParams);
				avgTextView.setLayoutParams(landscape_LayoutParams);
				scoreTextView.setLayoutParams(landscape_LayoutParams);
				firstinscoreTextView.setLayoutParams(landscape_LayoutParams);
				lastinscoreTextView.setLayoutParams(landscape_LayoutParams);
				firstinavgTextView.setLayoutParams(landscape_LayoutParams);
				lastinavgTextView.setLayoutParams(landscape_LayoutParams);
				if (headingsLinearLayout.getChildCount() == 0) {
					headingsLinearLayout.addView(contestantTextView);
					headingsLinearLayout.addView(rndTextView);
					headingsLinearLayout.addView(avgTextView);
					headingsLinearLayout.addView(scoreTextView);
					headingsLinearLayout.addView(firstinscoreTextView);
					headingsLinearLayout.addView(lastinscoreTextView);
					headingsLinearLayout.addView(firstinavgTextView);
					headingsLinearLayout.addView(lastinavgTextView);
				}

				contestantNameEditText
						.setLayoutParams(contestantlandscape_LayoutParams);
				roundEditText.setLayoutParams(landscape_LayoutParams);
				avgEditText.setLayoutParams(landscape_LayoutParams);
				timeEditText.setLayoutParams(landscape_LayoutParams);
				firstInround_Score_EditText
						.setLayoutParams(landscape_LayoutParams);
				lastInround_Score_EditText
						.setLayoutParams(landscape_LayoutParams);
				firstInround_Avg_EditText
						.setLayoutParams(landscape_LayoutParams);
				lastInround_Avg_EditText
						.setLayoutParams(landscape_LayoutParams);

				contestantRow.addView(contestantNameEditText);
				contestantRow.addView(roundEditText);
				contestantRow.addView(avgEditText);
				contestantRow.addView(timeEditText);
				contestantRow.addView(firstInround_Score_EditText);
				contestantRow.addView(lastInround_Score_EditText);
				contestantRow.addView(firstInround_Avg_EditText);
				contestantRow.addView(lastInround_Avg_EditText);
			}
			contestantListLinearLayout.addView(contestantRow);
		}
	}
}
