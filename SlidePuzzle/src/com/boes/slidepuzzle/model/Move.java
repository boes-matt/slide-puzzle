/*
 * SlidePuzzle
 * Copyright (C) 2012 Matt Boes. 
 */

package com.boes.slidepuzzle.model;

public class Move {
		
	public enum Action {LEFT, RIGHT, UP, DOWN};	
	
	public final int fromIndex;
	public final Action action;
	private final Board fromBoard; // used in toString()
			
	public Move(Board fromBoard, Action action) {
		switch (action) {
		case RIGHT: fromIndex = fromBoard.blankIndex - 1; break;
		case LEFT: fromIndex = fromBoard.blankIndex + 1; break;
		case DOWN: fromIndex = fromBoard.blankIndex - fromBoard.size; break;
		case UP: fromIndex = fromBoard.blankIndex + fromBoard.size; break;
		default: fromIndex = -1;
		}
		this.action = action;
		this.fromBoard = fromBoard;
	}
	
	@Override
	public boolean equals(Object obj) {
		Move other = (Move) obj;
		return fromIndex == other.fromIndex && action == other.action;
	}
	
	@Override
	public String toString() {
		// Returns a string of the form "(0, 0) RIGHT"
		int row = fromBoard.getRow(fromIndex);
		int col = fromBoard.getCol(fromIndex);
		StringBuilder result = new StringBuilder();
		result.append("(").append(col).append(", ").append(row).append(") ").append(action);
		return result.toString();
	}

}
