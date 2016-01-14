package com.mobiwebcode.Rodeo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class CustomEditText extends EditText {
	Context mContext = null;

	public CustomEditText(Context context) {
		super(context);
		mContext = context;
		// TODO Auto-generated constructor stub
	}

	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// User has pressed Back key. So hide the keyboard
			return true;
			// TODO: Hide your view as you do it in your activity
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			// Eat the event
			return true;
		}
		return false;
	}

}
