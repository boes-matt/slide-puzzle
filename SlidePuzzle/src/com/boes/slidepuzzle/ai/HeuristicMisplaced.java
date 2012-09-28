/*
 * SlidePuzzle
 * Copyright (C) 2012 Matt Boes. 
 */

package com.boes.slidepuzzle.ai;

import com.boes.slidepuzzle.model.Board;

public class HeuristicMisplaced extends Heuristic {
	
	public HeuristicMisplaced(Board goal) {
		super(goal);
	}

	@Override
	public int heuristic(Board current) {
		int sum = 0;
		for (int row = 0; row < current.getSize(); ++row) {
			for (int col = 0; col < current.getSize(); ++col) {
				char piece = current.getChar(row, col);
				if (piece != Board.BLANK && piece != goal.getChar(row, col)) sum += 1;
			}
		}	
		return sum;
	}
	
}
