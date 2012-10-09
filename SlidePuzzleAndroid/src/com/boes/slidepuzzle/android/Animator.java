/*
 * SlidePuzzleAndroid
 * Copyright (C) 2012 Matt Boes.
 * All rights reserved.
 */

package com.boes.slidepuzzle.android;

import android.view.animation.Interpolator;

public class Animator {
	
	private float duration;
	private Interpolator interpolator;
	private boolean animating;
	private long start;
	private long elapsed;
	
	public Animator(float duration, Interpolator interpolator) {
		this.duration = duration;
		this.interpolator = interpolator;
		animating = false;		
	}

	public void start() {
		animating = true;
		start = System.currentTimeMillis();
	}
	
	public boolean isAnimating() {
		return animating;
	}
	
	public void setDuration(float duration) {
		this.duration = duration;
	}

	public float getInterpolatedFraction() {
		return interpolator.getInterpolation(getElapsedFraction());
	}	
	
	private float getElapsedFraction() {
		elapsed = System.currentTimeMillis();
		float fraction = (elapsed - start) / duration;
		if (fraction >= 1.0f) {
			animating = false;
			return 1.0f;
		} else return fraction;
	}
	
}