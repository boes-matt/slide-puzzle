/*
 * SlidePuzzle
 * Copyright (C) 2012 Matt Boes. 
 */

package com.boes.slidepuzzle.ai;

import com.boes.slidepuzzle.model.Board;

public class HeuristicNaive extends Heuristic {

	public HeuristicNaive(Board goal) {
		super(goal);
	}

	@Override
	public int heuristic(Board current) { 
		return 0; 
	}
	
}
