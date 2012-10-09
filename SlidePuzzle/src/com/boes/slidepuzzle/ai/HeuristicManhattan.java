/*
 * SlidePuzzle
 * Copyright (C) 2012 Matt Boes.
 * All rights reserved.
 */

package com.boes.slidepuzzle.ai;

import com.boes.slidepuzzle.model.Board;

public class HeuristicManhattan extends Heuristic {
	
	public HeuristicManhattan(Board goal) {
		super(goal);
	}
	
	@Override
	public int heuristic(Board current) {
		int sum = 0;
		for (int row = 0; row < current.getSize(); ++row) {
			for (int col = 0; col < current.getSize(); ++col) {
				char piece = current.getChar(row, col);
				if (piece != Board.BLANK) {
					// Use goalMap to speed up lookups from O(n) to O(1)
					int goalIndex = goalMap.get(piece);
					int goalRow = goal.getRow(goalIndex);
					int goalCol = goal.getCol(goalIndex);
					int x = Math.abs(col - goalCol);
					int y = Math.abs(row - goalRow);					
					sum += x + y;
				}
			}
		}
		return sum;
	}

}