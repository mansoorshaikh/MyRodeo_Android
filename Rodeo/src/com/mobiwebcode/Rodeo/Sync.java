package com.mobiwebcode.Rodeo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Sync extends Activity {
	Button Back, Search;
	ListView Lv_Sync;
	EditText Ed_Search;

	TextView title;
	ArrayList<String> SyncList = new ArrayList<String>();
	ArrayList<String> SearchList = new ArrayList<String>();

	void addValuesIntoSyncList() {
		SyncList.add("Johnsonvile Rodeo new");
		SyncList.add("Jackpot 5/12/14");
		SyncList.add("Circuit Finals");
		SyncList.add("Clarksville Rodeo");
		SyncList.add("John Deo Rodeo1");
		SyncList.add("John Deo Rodeo1");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync);
		addValuesIntoSyncList();

		title = (TextView) findViewById(R.id.Sync_Title_textView);
		title.setTypeface(HomeActivity.font);
		Back = (Button) findViewById(R.id.Sync_Back_button);
		Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(Sync.this, Rodeos.class));
				finish();
			}
		});
		Ed_Search = (EditText) findViewById(R.id.Sync_Search_editText);
		Search = (Button) findViewById(R.id.Sync_Search_button);
		Search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SearchList.clear();
				for (int i = 0; i < SyncList.size(); i++) {
					if (SyncList
							.get(i)
							.toLowerCase()
							.contains(
									Ed_Search.getText().toString().trim()
											.toLowerCase())) {
						SearchList.add(SyncList.get(i));
					}
				}
				Lv_Sync.setAdapter(new AdapterSync(SearchList));
			}
		});

		Lv_Sync = (ListView) findViewById(R.id.Sync_listView);
		Lv_Sync.setAdapter(new AdapterSync(SyncList));
	}

	public class AdapterSync extends BaseAdapter {
		ArrayList<String> List = new ArrayList<String>();

		public AdapterSync(ArrayList<String> List) {
			this.List = List;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return List.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return List.size();
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View v, ViewGroup arg2) {
			View view = v;
			final TextView RedoeName;
			Button Add;
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getLayoutInflater();
				view = vi.inflate(R.layout.list_item_sync, null);
			}

			RedoeName = (TextView) view
					.findViewById(R.id.List_Item_Sync_RodeoName_textView);
			RedoeName.setTypeface(HomeActivity.font);
			Add = (Button) view.findViewById(R.id.List_Item_Sync_Add_button);
			RedoeName.setText(List.get(position));

			Add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					AlertDialog.Builder alert = new AlertDialog.Builder(
							Sync.this);
					alert.setCancelable(false);
					alert.setMessage("Added Successfully");
					alert.setPositiveButton("ok",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// Rodeos.ListRodeoName.add(RedoeName
									// .getText().toString());

									arg0.dismiss();
									Ed_Search.setText("");
									SyncList.remove(position);
									Lv_Sync.setAdapter(new AdapterSync(SyncList));
								}
							});
					alert.show();

				}
			});

			return view;
		}

	}

}
