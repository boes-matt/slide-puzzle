/*
 * SlidePuzzleAndroid
 * Copyright (C) 2012 Matt Boes.
 * All rights reserved. 
 */

package com.boes.slidepuzzle.android;

import com.boes.slidepuzzle.ai.HeuristicManhattan;
import com.boes.slidepuzzle.ai.Solver;
import com.boes.slidepuzzle.ai.SolverAStar;
import com.boes.slidepuzzle.ai.SolverMemoDecorator;
import com.boes.slidepuzzle.android.R;
import com.boes.slidepuzzle.model.Board;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	protected BoardView boardView;
	protected TextView moveView;
	protected TextView goalView;
	protected Button newPuzzle;
	protected Button solve;
	protected Button skipAhead;
	
	protected Board goal;
	protected Solver solver;
	
	// Set in boardView once view width is set
	protected TranslateAnimation outBoard;
	protected TranslateAnimation inBoard;
	
	private static final int DIALOG_ABOUT = 0;
	public static final int DIALOG_WIN = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		boardView = (BoardView) findViewById(R.id.board);
		moveView = (TextView) findViewById(R.id.moves);
		goalView = (TextView) findViewById(R.id.goal);
		newPuzzle = (Button) findViewById(R.id.newPuzzle);
		solve = (Button) findViewById(R.id.solve);
		skipAhead = (Button) findViewById(R.id.skipAhead);
		
		goal = new Board("123 456 780");
		solver = new SolverMemoDecorator(new SolverAStar(goal, new HeuristicManhattan(goal)));		
		setupBoardView();
	}
	
	protected void setupBoardView() {
		newPuzzle.setOnClickListener(boardView);
		solve.setOnClickListener(boardView);
		skipAhead.setOnClickListener(boardView);
		boardView.setupPuzzle();
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
		boardView.pause();
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			showMyDialog(DIALOG_ABOUT);
			return true;
		default:
			break;
		}
		return false;
	}
	
	public void showMyDialog(int id) {
		switch (id) {
		case DIALOG_ABOUT:
			DialogFragment aboutFragment = new AboutFragment();
			aboutFragment.show(getSupportFragmentManager(), "about dialog");
			break;
		case DIALOG_WIN:
			DialogFragment win = new WinFragment();
			win.show(getSupportFragmentManager(), "win dialog");			
			break;
		default:
			break;
		}
	}
	
}