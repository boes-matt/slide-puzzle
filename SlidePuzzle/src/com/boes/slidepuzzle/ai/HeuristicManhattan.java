package com.boes.slidepuzzle.ai;

import com.boes.slidepuzzle.model.Board;

public class HeuristicManhattan extends Heuristic {
	
	public HeuristicManhattan(Board goal) {
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
				int boardIndex = rowOffset + col;
				char piece = current.board.charAt(boardIndex);
				if (piece != Board.BLANK) {
					// May need faster lookup here!  Create goal hash map in constructor?
					int goalIndex = goal.board.indexOf(piece);
					sum += getDistance(boardIndex, goalIndex, current.size);
				}
			}
		}	
		return sum;
	}
	
	private int getDistance(int boardIndex, int goalIndex, int size) {
		int x = Math.abs((boardIndex / size) - (goalIndex / size));
		int y = Math.abs((boardIndex % size) - (goalIndex % size));
		return x + y;
	}

}
