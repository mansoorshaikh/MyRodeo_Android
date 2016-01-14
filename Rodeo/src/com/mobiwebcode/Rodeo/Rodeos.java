package com.mobiwebcode.Rodeo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Rodeos extends Activity {
	Button Back;

	TextView title;
	public static boolean Isfirst = false;
	ListView Lv_RodeoName;

	private SQLiteDatabase RodeoDatabase;
	private static String DB_PATH = "/data/data/com.Syneotek.Rodeo/databases/";
	private static final String DB_NAME = "rodeo.db";
	private String path = DB_PATH + DB_NAME;
	String CreateRodeo = "rodeodetails";
	public static ArrayList<RodeoVO> ListRodeoName = new ArrayList<RodeoVO>();

	public void onBackPressed() {
		startActivity(new Intent(Rodeos.this, HomeActivity.class));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rodeos);

		Back = (Button) findViewById(R.id.Rodeos_Back_button);
		title = (TextView) findViewById(R.id.Rodeos_Title_textView);
		title.setTypeface(HomeActivity.font);
		Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(Rodeos.this, HomeActivity.class));
			}
		});

		Lv_RodeoName = (ListView) findViewById(R.id.Rodeo_listView);
		readRodeo();
	}

	public void readRodeo() {
		try {
			ListRodeoName.clear();

			Cursor cursor = null;

			cursor = Constants.RodeoDatabase.rawQuery("SELECT * FROM "
					+ CreateRodeo, null);
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					RodeoVO rvo = new RodeoVO();
					rvo.rodeoid = cursor.getInt(cursor
							.getColumnIndex("rodeoid"));
					rvo.rodeoname = cursor.getString(cursor
							.getColumnIndex("rodeoname"));
					rvo.location = cursor.getString(cursor
							.getColumnIndex("location"));
					rvo.rodeostartdate = cursor.getString(cursor
							.getColumnIndex("rodeostartdate"));
					rvo.numberofrounds = cursor.getString(cursor
							.getColumnIndex("numberofrounds"));
					ListRodeoName.add(rvo);
				}
			}
			cursor.close();
			Lv_RodeoName.setAdapter(new AdapterRodeoName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class AdapterRodeoName extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ListRodeoName.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return ListRodeoName.size();
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View v, ViewGroup arg2) {
			View view = v;
			TextView Names;
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getLayoutInflater();
				view = vi.inflate(R.layout.list_item_rodeos, null);
			}
			RodeoVO rvo = ListRodeoName.get(position);
			Names = (TextView) view
					.findViewById(R.id.LIst_Item_Rodeos_Name_button);
			Names.setTypeface(HomeActivity.font);
			Names.setText(rvo.rodeoname);
			Names.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					Events_LookUpRodeo.selectedRodeoVO = ListRodeoName
							.get(position);
					startActivity(new Intent(Rodeos.this,
							Events_LookUpRodeo.class));

				}
			});
			return view;
		}

	}

}
