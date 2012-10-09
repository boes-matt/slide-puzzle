/*
 * SlidePuzzleAndroid
 * Copyright (C) 2012 Matt Boes.
 * All rights reserved. 
 */

package com.boes.slidepuzzle.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class WinFragment extends DialogFragment {

	/*
	 * Dialog template:
	 * You Win!
	 * Now, how hard was that?
	 * You solved a 22-move puzzle in 100 moves. 1/4 Stars!
	 * Nice work! Can you do better?
	 * (P) New Puzzle (N) Close
	 */
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.win)
				.setMessage(getMessage())
				.setNegativeButton(R.string.close,
						new DialogInterface.OnClickListener() {
					
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dismiss();
							}
						})
				.setPositiveButton(R.string.newPuzzle, 
						new DialogInterface.OnClickListener() {
						
							@Override
							public void onClick(DialogInterface dialog, int which) {
								((MainActivity) getActivity()).boardView.newPuzzle();
							}
						})
				.create();
	}
	
	private String getMessage() {
		MainActivity game = (MainActivity) getActivity();
		StringBuilder sb = new StringBuilder();
		
		sb.append("Now, how hard was that?")
		  .append("\n\n");
		
		sb.append("You solved a ")
		  .append(game.boardView.perfectPlay)
		  .append("-move puzzle in ")
		  .append(game.boardView.moves)
		  .append(" moves.  ")
		  .append(calculateStars(game.boardView.moves, game.boardView.perfectPlay))
		  .append("/4 Stars!")
		  .append("\n\n");
		
		sb.append("Nice work!  Can you do better?");
				
		return sb.toString();
	}
	
	private int calculateStars(int moves, int perfectPlay) {
		int stars;	
		if (moves <= 1.25 * perfectPlay) stars = 4;
		else if (moves <= 2.5 * perfectPlay) stars = 3;
		else if (moves <= 3.75 * perfectPlay) stars = 2;
		else stars = 1;
		return stars;
	}
	
}