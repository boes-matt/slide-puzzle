/*
 * SlidePuzzle
 * Copyright (C) 2012 Matt Boes. 
 */

package com.boes.slidepuzzle.ai;

import java.util.HashMap;
import java.util.Map;

import com.boes.slidepuzzle.model.Board;

public abstract class Heuristic {
	
	protected final Board goal;
	protected Map<Character, Integer> goalMap;
	
	public Heuristic(Board goal) {
		this.goal = goal;
		
		goalMap = new HashMap<Character, Integer>();
		for (int row = 0; row < goal.getSize(); ++row) {
			for (int col = 0; col < goal.getSize(); ++col) {
				goalMap.put(goal.getChar(row, col), goal.getIndex(row, col));
			}
		}
	}
		
	public abstract int heuristic(Board current);
	
}
