package com.mobiwebcode.Rodeo;

import java.util.ArrayList;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class Events extends Activity {
	boolean iseventadded2 = false;
	int currentRound = 0;
	String Contestants = "contestants";
	int WheelPosition;
	boolean scrolling = false;
	TextView TitleText;
	EditText selectedEventEditText;
	EditText lastId = null, lastTimeID = null;
	SlidingDrawer SDEvents;
	WheelView WheelEvents;
	ListView listView;
	Button Back;
	Button WheelDone, WheelCancle;
	int arrayListPosition;
	String Count = "", Plasess = "";
	ArrayList<String> usedEventsList = new ArrayList<String>();
	ArrayList<ContestantVO> contestantList = new ArrayList<ContestantVO>();
	ArrayList<Events_VO> initialEventsList = new ArrayList<Events_VO>();
	public static ArrayList<Events_VO> addedEventsList = new ArrayList<Events_VO>();
	ArrayList<String> mainEventList = new ArrayList<String>();
	ArrayList<String> wheelEventList = new ArrayList<String>();
	ArrayList<Events_VO> currentEventList = new ArrayList<Events_VO>();
	String[] eventName = new String[] { "Bareback", "Barrel Racing",
			"Break Away Roping", "Bull Riding", "Calf Roping", "Goat Tying",
			"Pole Bending", "Ribbon Roping", "Saddle Bronc", "Steer Wrestling",
			"Team Roping", "Other" };
	ArrayList<String> timedEventsArrayList = new ArrayList<String>();
	ArrayList<String> scoredEventsArrayList = new ArrayList<String>();

	public void loadDefaults() {
		final SharedPreferences myPrefs = this.getSharedPreferences("myPrefs",
				MODE_WORLD_READABLE);
		final SharedPreferences.Editor prefsEditor = myPrefs.edit();
		prefsEditor.putString("timeformat", "2");
		prefsEditor.putString("scoreformat", "2");
		prefsEditor.putString("noofrounds", "3");
		prefsEditor.putString("noofcontestants", "10");
		prefsEditor.putString("noofplacespaid", "3");
		prefsEditor.commit();

	}

	void addValueintoEventList() {
		wheelEventList.clear();
		SDEvents = (SlidingDrawer) findViewById(R.id.Events_slidingDrawer);
		WheelCancle = (Button) findViewById(R.id.Events_Wheel_Cancle_Button);
		WheelCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SDEvents.animateClose();
				SDEvents.setVisibility(View.GONE);
			}
		});
		WheelDone = (Button) findViewById(R.id.Events_Wheel_Done_button);
		WheelEvents = (WheelView) findViewById(R.id.Events_Wheel);

		for (int i = 0; i < initialEventsList.size(); i++) {
			Events_VO tempvo = initialEventsList.get(i);
			if (mainEventList.contains(tempvo.eventName)) {
				usedEventsList.add(tempvo.eventName);
			}
		}

		for (int count = 0; count < addedEventsList.size(); count++) {
			Events_VO tempvo = addedEventsList.get(count);
			if (!usedEventsList.contains(tempvo.eventName)) {
				usedEventsList.add(tempvo.eventName);
			}
		}

		for (int i = 0; i < mainEventList.size(); i++) {
			Events_VO items = new Events_VO();
			if (!usedEventsList.contains(mainEventList.get(i))) {
				items.eventName = mainEventList.get(i);
				items.noofcontestants = "";
				items.places = "";
				items.isEventAdded = false;
				currentEventList.add(items);
				if (addedEventsList.size() == 0) {
					wheelEventList.add(mainEventList.get(i));
				} else {
					for (int j = 0; j < addedEventsList.size(); j++) {
						Events_VO addedEvents_VO = addedEventsList.get(j);
						if (!mainEventList.get(i).equals(
								addedEvents_VO.eventName)) {
							wheelEventList.add(mainEventList.get(i));
							break;
						}
					}
				}
			}
		}

		listView.setAdapter(new EventAdapter());

		WheelEvents.setVisibleItems(2);
		WheelEvents.setCurrentItem(1);
		WheelEvents.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					WheelPosition = newValue;
				}
			}
		});
		WheelEvents.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
				WheelPosition = WheelEvents.getCurrentItem();
			}
		});

		WheelDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SDEvents.animateClose();
				SDEvents.setVisibility(View.GONE);
				final Events_VO items = new Events_VO();
				if (wheelEventList.get(WheelPosition).toString().trim()
						.equalsIgnoreCase("Other")) {
					AlertDialog.Builder alert = new AlertDialog.Builder(
							Events.this);
					final EditText input = new EditText(Events.this);
					input.setSingleLine();
					input.setImeOptions(EditorInfo.IME_ACTION_DONE);
					input.setHint("Event Name");
					alert.setView(input);
					alert.setMessage("Enter Event Name");
					alert.setPositiveButton("Timed Event",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									selectedEventEditText.setText(input
											.getText().toString());
									Events_VO updatevo = new Events_VO();
									updatevo = initialEventsList
											.get(selectedEventEditText.getId() - 100);
									updatevo.eventName = input.getText()
											.toString();
									updatevo.eventType = "Timed Event";
									addValueintoEventList();
									arg0.dismiss();
								}
							});
					alert.setNeutralButton("Scored Event",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									selectedEventEditText.setText(input
											.getText().toString());
									Events_VO updatevo = new Events_VO();
									updatevo = initialEventsList
											.get(selectedEventEditText.getId() - 100);
									updatevo.eventName = input.getText()
											.toString();
									updatevo.eventType = "Scored Event";
									addValueintoEventList();
									arg0.dismiss();
								}
							});
					alert.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									arg0.dismiss();
								}
							});
					alert.show();
				} else {
					items.eventName = wheelEventList.get(WheelPosition);
					items.noofcontestants = "8";
					items.places = "3";
					items.isEventAdded = false;
					selectedEventEditText.setText(wheelEventList
							.get(WheelPosition));
					usedEventsList.clear();

					Events_VO updatevo = new Events_VO();
					updatevo = initialEventsList.get(selectedEventEditText
							.getId() - 100);
					updatevo.eventName = wheelEventList.get(WheelPosition);
					if (timedEventsArrayList.contains(wheelEventList
							.get(WheelPosition)))
						updatevo.eventType = "Timed Event";
					else
						updatevo.eventType = "Scored Event";
					addValueintoEventList();

				}

			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events);

		SharedPreferences myPrefs = this.getSharedPreferences("myPrefs",
				MODE_WORLD_READABLE);
		SharedPreferences.Editor prefsEditor = myPrefs.edit();

		if (myPrefs.getString("noofcontestants", "") == null
				|| myPrefs.getString("noofcontestants", "").equals(""))
			if (myPrefs.getString("noofplacespaid", "") == null
					|| myPrefs.getString("noofplacespaid", "").equals(""))

				loadDefaults();

		for (int count = 0; count < addedEventsList.size(); count++) {
			Events_VO tempEvents_VO = addedEventsList.get(count);
			usedEventsList.add(tempEvents_VO.eventName);
		}

		for (int i = 0; i < 8; i++) {
			Events_VO eventvo = new Events_VO();
			eventvo.eventName = "Event " + (i + 1);
			eventvo.places = "3";
			eventvo.noofcontestants = "8";
			initialEventsList.add(eventvo);
		}

		selectedEventEditText = new EditText(Events.this);

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
		Back = (Button) findViewById(R.id.Events_Back_button);
		Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		for (int i = 0; i < eventName.length; i++) {
			mainEventList.add(eventName[i]);
		}

		addValueintoEventList();
		TitleText = (TextView) findViewById(R.id.Events_Title_textView);
		TitleText.setTypeface(HomeActivity.font);
	}

	public class EventAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return initialEventsList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return initialEventsList.size();
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View arg1, ViewGroup arg2) {
			View view = arg1;
			final EditText eventName;
			final CustomEditText noofContestantsEditText;
			final CustomEditText noofPlacesEditText;
			final Button addEventButton;
			final SharedPreferences myPrefs = Events.this.getSharedPreferences(
					"myPrefs", MODE_WORLD_READABLE);
			LayoutInflater vi = (LayoutInflater) getLayoutInflater();
			view = vi.inflate(R.layout.list_item_events, null);

			eventName = (EditText) view
					.findViewById(R.id.List_Events_EventName_editText);
			if (eventName != null)
				eventName.setId(100 + position);
			eventName.setTypeface(HomeActivity.font);

			noofContestantsEditText = (CustomEditText) view
					.findViewById(R.id.List_Events_Cont_editText);
			noofContestantsEditText.setFocusable(false);
			noofContestantsEditText.setFocusableInTouchMode(false);

			noofContestantsEditText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					noofContestantsEditText.setText("");
					noofContestantsEditText.setFocusable(true);
					noofContestantsEditText.setFocusableInTouchMode(true);
					noofContestantsEditText.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(noofContestantsEditText,
							InputMethodManager.SHOW_IMPLICIT);
				}
			});

			noofContestantsEditText
					.setOnEditorActionListener(new OnEditorActionListener() {
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
									|| (actionId == EditorInfo.IME_ACTION_DONE)) {
								if (noofContestantsEditText.getText()
										.toString().equals("")) {
									noofContestantsEditText.setText(myPrefs
											.getString("noofcontestants", ""));
									noofContestantsEditText.setFocusable(false);
									noofContestantsEditText
											.setFocusableInTouchMode(false);
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(
											noofContestantsEditText
													.getWindowToken(), 0);
								}
							}
							return false;
						}
					});

			if (noofContestantsEditText != null)
				noofContestantsEditText.setId(200 + position);
			noofContestantsEditText.setTypeface(HomeActivity.font);

			noofPlacesEditText = (CustomEditText) view
					.findViewById(R.id.List_Events_Plase_editText);
			noofPlacesEditText.setFocusable(false);
			noofPlacesEditText.setFocusableInTouchMode(false);

			noofPlacesEditText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					noofPlacesEditText.setText("");
					noofPlacesEditText.setFocusable(true);
					noofPlacesEditText.setFocusableInTouchMode(true);
					noofPlacesEditText.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(noofPlacesEditText,
							InputMethodManager.SHOW_IMPLICIT);
				}
			});

			noofPlacesEditText
					.setOnEditorActionListener(new OnEditorActionListener() {
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
									|| (actionId == EditorInfo.IME_ACTION_DONE)) {
								if (noofPlacesEditText.getText().toString()
										.equals("")) {
									noofPlacesEditText.setText(myPrefs
											.getString("noofplacespaid", ""));
									noofPlacesEditText.setFocusable(false);
									noofPlacesEditText
											.setFocusableInTouchMode(false);
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(
											noofPlacesEditText.getWindowToken(),
											0);
								}
							}
							return false;
						}
					});

			if (noofPlacesEditText != null)
				noofPlacesEditText.setId(300 + position);
			noofPlacesEditText.setTypeface(HomeActivity.font);
			addEventButton = (Button) view
					.findViewById(R.id.List_Events_Add_button);
			addEventButton.setId(400 + position);

			Events_VO items = new Events_VO();
			items = initialEventsList.get(position);
			if (items.isEventAdded == true) {
				eventName.setEnabled(false);
				noofContestantsEditText.setEnabled(false);
				noofPlacesEditText.setEnabled(false);
				addEventButton.setVisibility(View.GONE);
			}

			int evNo = position + 1;
			eventName.setHint("Event" + evNo);

			noofContestantsEditText.setText(myPrefs.getString(
					"noofcontestants", ""));
			noofPlacesEditText.setText(myPrefs.getString("noofplacespaid", ""));

			noofContestantsEditText.setEnabled(true);
			noofPlacesEditText.setEnabled(true);
			noofContestantsEditText.setClickable(true);

			if (items.isEventAdded) {
				eventName.setBackgroundResource(Color.TRANSPARENT);
				noofContestantsEditText
						.setBackgroundResource(Color.TRANSPARENT);
				noofPlacesEditText.setBackgroundResource(Color.TRANSPARENT);
				addEventButton.setVisibility(View.INVISIBLE);
				eventName.setTextColor(Color.BLACK);
				noofPlacesEditText.setTextColor(Color.RED);
				noofContestantsEditText.setTextColor(Color.RED);
			} else {
				addEventButton.setText("+");
			}

			eventName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (MyConstants.FromRodeos) {
						startActivity(new Intent(Events.this,
								Event_Details.class));
					} else {
						selectedEventEditText = eventName;
						WheelEvents
								.setViewAdapter(new WheelAdapter(Events.this));
						if (!SDEvents.isOpened()) {
							SDEvents.animateOpen();
							SDEvents.setVisibility(View.VISIBLE);
							arrayListPosition = position;
						}
					}
				}
			});

			addEventButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					eventName.setBackgroundResource(Color.TRANSPARENT);
					noofContestantsEditText
							.setBackgroundResource(Color.TRANSPARENT);
					noofPlacesEditText.setBackgroundResource(Color.TRANSPARENT);
					eventName.setTextColor(Color.BLACK);
					noofPlacesEditText.setTextColor(Color.RED);
					noofContestantsEditText.setTextColor(Color.RED);
					Events_VO evo = initialEventsList.get(addEventButton
							.getId() - 400);
					evo.position = String.valueOf(addEventButton.getId() - 400);

					evo.isEventAdded = true;

					final EditText contestantNameEditText = (EditText) findViewById(addEventButton
							.getId() - 300);
					final EditText contestantcountEditText = (EditText) findViewById(addEventButton
							.getId() - 200);
					final EditText placescountEditText = (EditText) findViewById(addEventButton
							.getId() - 100);

					System.out.println("contestantNameEditText : "
							+ contestantNameEditText.getText().toString()
							+ "contestantcountEditText : "
							+ contestantcountEditText.getText().toString()
							+ "placescountEditText : "
							+ placescountEditText.getText().toString());

					evo.eventName = contestantNameEditText.getText().toString();
					evo.noofcontestants = contestantcountEditText.getText()
							.toString();
					evo.places = placescountEditText.getText().toString();
					final Events_VO evo_ = evo;
					if (evo.eventType.equals("")) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								Events.this);
						builder.setMessage("Please select the event type")
								.setCancelable(false)
								.setPositiveButton("Scored Event",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												evo_.eventType = "Scored Event";
												addedEventsList.add(evo_);
												addEventButton
														.setVisibility(View.GONE);
												contestantNameEditText
														.setEnabled(false);
												contestantcountEditText
														.setEnabled(false);
												placescountEditText
														.setEnabled(false);

											}
										})
								.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {

											}
										})
								.setNeutralButton("Timed Event",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												evo_.eventType = "Timed Event";
												addedEventsList.add(evo_);
												addEventButton
														.setVisibility(View.GONE);
												contestantNameEditText
														.setEnabled(false);
												contestantcountEditText
														.setEnabled(false);
												placescountEditText
														.setEnabled(false);
											}
										});
						AlertDialog alert = builder.create();
						alert.show();
					} else {
						addedEventsList.add(evo);
						addEventButton.setVisibility(View.GONE);
						contestantNameEditText.setEnabled(false);
						contestantcountEditText.setEnabled(false);
						placescountEditText.setEnabled(false);
					}
				}
			});
			int found = 0;
			for (int count = 0; count < addedEventsList.size(); count++) {
				Events_VO evo = addedEventsList.get(count);
				if (evo.position.equals(String.valueOf(position))) {
					found = 1;
					Events_VO tempEvents_VO = addedEventsList.get(count);
					eventName.setText(tempEvents_VO.eventName);
					noofContestantsEditText
							.setText(tempEvents_VO.noofcontestants);
					noofPlacesEditText.setText(tempEvents_VO.places);
					eventName.setFocusable(false);
					eventName.setFocusableInTouchMode(false);
					noofContestantsEditText.setFocusable(false);
					noofPlacesEditText.setFocusable(false);
					noofContestantsEditText.setFocusableInTouchMode(false);
					noofPlacesEditText.setFocusableInTouchMode(false);
					addEventButton.setVisibility(View.GONE);
				}
			}

			if (found == 0) {
				eventName.setText(items.eventName);
			}

			return view;
		}
	}

	class WheelAdapter extends AbstractWheelTextAdapter {

		protected WheelAdapter(Context context) {
			super(context);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return wheelEventList.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return wheelEventList.get(index);
		}
	}

}
