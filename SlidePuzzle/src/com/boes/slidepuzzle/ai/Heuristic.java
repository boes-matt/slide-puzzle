package com.boes.slidepuzzle.ai;

import com.boes.slidepuzzle.model.Board;

public abstract class Heuristic {
	
	final Board goal;
	
	public Heuristic(Board goal) {
		this.goal = goal;
	}
	
	public abstract int heuristic(Board current);
	
}
