package com.boes.slidepuzzle.android;

import com.boes.slidepuzzle4.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	BoardView bv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		bv = (BoardView) findViewById(R.id.board);
		bv.setup();
	}
		
	@Override
	protected void onResume() {
		Log.d(TAG, "Resuming activity...");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "Pausing activity...");
		super.onPause();
		bv.solving = 0; // stop solving
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "Stopping activity...");
		super.onStop();
	}
		
	@Override
	protected void onDestroy() {
		Log.d(TAG, "Destroying activity...");
		super.onDestroy();
	}	
	
}
