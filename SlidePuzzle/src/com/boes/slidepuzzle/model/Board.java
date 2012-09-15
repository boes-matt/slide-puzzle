package com.boes.slidepuzzle.model;

import java.util.List;
import java.util.ArrayList;

import com.boes.slidepuzzle.model.Move.Action;

public class Board {

	public static final char BORDER = '|';
	public static final char BLANK = '0';

	// Immutable Board
	public final String board; // Make board char[] rather than String to speed up?
	public final int size;
	public final int blankIndex;
	public final List<Move> validMoves;
	
	public Board(String boardString) {
		// boardString is of the form "012 345 678"
		String[] rows = boardString.split(" ");
		StringBuilder result = new StringBuilder();
		result.append(BORDER).append(rows[0].replaceAll(".", String.valueOf(BORDER))).append(BORDER);
		for (String row : rows) {
			result.append(BORDER).append(row).append(BORDER);
		}
		result.append(BORDER).append(rows[0].replaceAll(".", String.valueOf(BORDER))).append(BORDER);
		// board is of the form "||||| |012| |345| |678| |||||" but without the spaces
		board = result.toString();
		size = rows.length + 2; // Add 2 for border on each side
		blankIndex = board.indexOf(BLANK);
		validMoves = getValidMoves();
	}
	
	public Board(Board oldBoard, Move move) {
		char[] temp = oldBoard.board.toCharArray();
		temp[oldBoard.blankIndex] = oldBoard.board.charAt(move.fromIndex);
		temp[move.fromIndex] = oldBoard.board.charAt(oldBoard.blankIndex);
		board = new String(temp);
		size = oldBoard.size;
		blankIndex = move.fromIndex;
		validMoves = getValidMoves();
	}
	
	private List<Move> getValidMoves() {		
		List<Move> moves = new ArrayList<Move>();
		if (board.charAt(blankIndex - 1) != BORDER) moves.add(new Move(this, Action.RIGHT));
		if (board.charAt(blankIndex + 1) != BORDER) moves.add(new Move(this, Action.LEFT));
		if (board.charAt(blankIndex - size) != BORDER) moves.add(new Move(this, Action.DOWN));		
		if (board.charAt(blankIndex + size) != BORDER) moves.add(new Move(this, Action.UP));
		return moves;
	}
		
	public Action getAction(int row, int col) {
		int delta = blankIndex - getIndex(row, col);
		if (delta == size) return Action.DOWN;
		if (delta == -size) return Action.UP;
		if (delta == 1) return Action.RIGHT;
		if (delta == -1) return Action.LEFT;
		return null;
	}
	
	public int getIndex(int row, int col) {
		return (row + 1) * size + col + 1;
	}
	
	public int getRow(int boardIndex) {
		return boardIndex / size - 1;
	}
	
	public int getCol(int boardIndex) {
		return boardIndex % size - 1;
	}
	
	public char getChar(int row, int col) {
		return board.charAt(getIndex(row, col));
	}
	
	public int getSize() {
		// size of board without borders
		return size - 2;
	}
		
	@Override
	public boolean equals(Object obj) {
		String other = ((Board) obj).board;
		return board.equals(other);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((board == null) ? 0 : board.hashCode());
		return result;
	}	
	
	@Override
	public String toString() {
		// Returns a string of the form "||||| |012| |345| |678| |||||"
		StringBuffer sb = new StringBuffer(board);
		for (int i = 1; i < size; i++) {
			sb.insert(size * i + (i - 1), ' ');			
		}
		return sb.toString();
	}
	
	public String getBoardString() {
		// For restoring Android instance state
		// Returns a string of the form "012 345 678"
		StringBuffer sb = new StringBuffer();
		for (int row = 0; row < getSize(); ++row) {
			for (int col = 0; col < getSize(); ++col) {
				if (col == 0 && row != 0) sb.append(' ');
				sb.append(getChar(row, col));
			}
		}
		return sb.toString();
	}

}
