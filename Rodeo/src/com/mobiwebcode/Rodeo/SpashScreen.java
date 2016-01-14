package com.mobiwebcode.Rodeo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SpashScreen extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spash_screen);

		new Thread(new Runnable() {

			public void run() {
				try {

					try {

						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} finally {

					startActivity(new Intent(SpashScreen.this,
							HomeActivity.class));
					finish();
				}

			}

		}).start();

	}
}
