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
		int colStart = 1;
		int colEndExclusive = current.size - 1;
		int sum = 0;
		
		for (int rowNum = 1; rowNum < current.size - 1; ++rowNum) {
			int rowOffset = rowNum * current.size;
			for (int col = colStart; col < colEndExclusive; ++col) {
				int index = rowOffset + col;
				char boardPiece = current.board.charAt(index);
				if (boardPiece != Board.BLANK) {
					char goalPiece = goal.board.charAt(index);
					if (boardPiece != goalPiece) sum += 1;
				}
			}
		}	
		return sum;	
	}

}
