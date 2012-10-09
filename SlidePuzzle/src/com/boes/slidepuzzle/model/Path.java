/*
 * SlidePuzzle
 * Copyright (C) 2012 Matt Boes.
 * All rights reserved.
 */

package com.boes.slidepuzzle.model;

import java.util.ArrayList;
import java.util.List;

public class Path implements Comparable<Path> {

	// A path is of the form [Board, Move, Board, ...] 
	private final List<Object> path;
	private int pathCost;
	public int estimatedTotalCostToGoal;
	
	public Path(Board start) {
		path = new ArrayList<Object>();
		path.add(start);
		pathCost = 0;
		estimatedTotalCostToGoal = pathCost;
	}
	
	public Path(Path oldPath) {
		path = new ArrayList<Object>(oldPath.path);
		pathCost = oldPath.pathCost;
		estimatedTotalCostToGoal = pathCost;
	}
	
	public Path add(Move nextMove, Board nextBoard) {
		// obj is either a Board or a Move
		path.add(nextMove);
		path.add(nextBoard);
		++pathCost;
		estimatedTotalCostToGoal = pathCost;
		return this;
	}
	
	public Move removeFirstMove() {
		path.remove(0); // remove current (first) board 
		Move first = (Move) path.remove(0); // remove first move
		--pathCost;
		estimatedTotalCostToGoal = pathCost;
		return first;
	}

	public Board getLastBoard() {
		return (Board) path.get(path.size() - 1);
	}

	public int getNumOfMoves() {
		return pathCost;
	}
	
	@Override
	public int compareTo(Path other) {
		if (estimatedTotalCostToGoal < other.estimatedTotalCostToGoal) return -1;
		else if (estimatedTotalCostToGoal > other.estimatedTotalCostToGoal) return 1;
		else return 0;
	}

	@Override
	public boolean equals(Object obj) {
		Path other = (Path) obj;
		return pathCost == other.pathCost && path.equals(other.path);
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Cost (").append(pathCost).append(", ").append(estimatedTotalCostToGoal).append(") ").append(path);
		return result.toString();
	}
	
}