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

public class AboutFragment extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.title)
				.setMessage(R.string.about_text)
				.setPositiveButton(R.string.play,
						new DialogInterface.OnClickListener() {
					
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dismiss();
							}
						}
				).create();
	}
	
}