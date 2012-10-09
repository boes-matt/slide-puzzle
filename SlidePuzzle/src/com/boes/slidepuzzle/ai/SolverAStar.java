/*
 * SlidePuzzle
 * Copyright (C) 2012 Matt Boes.
 * All rights reserved.
 */

package com.boes.slidepuzzle.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import com.boes.slidepuzzle.model.Board;
import com.boes.slidepuzzle.model.Move;
import com.boes.slidepuzzle.model.Path;

public class SolverAStar implements Solver {
	
	private final Board goal;
	private final Heuristic heuristic;
	
	public SolverAStar(Board goal) {
		this.goal = goal;
		heuristic = new HeuristicNaive(goal);
	}
	
	public SolverAStar(Board goal, Heuristic heuristic) {
		this.goal = goal;
		this.heuristic = heuristic;
	}
	
	@Override
	public Path shortestPath(Board start) {
		Set<Board> visited = new HashSet<Board>();
		Queue<Path> paths = new PriorityQueue<Path>();
		paths.add(new Path(start));
		
		while (!paths.isEmpty()) {
			Path path = paths.remove();
			Board last = path.getLastBoard();
			if (last.equals(goal)) return path;
			if (!visited.contains(last)) {
				visited.add(last);
				for (Entry<Board, Move> successor : successors(last).entrySet()) {
					Board nextBoard = successor.getKey();
					Move nextMove = successor.getValue();
					Path newPath = new Path(path).add(nextMove, nextBoard);
					newPath.estimatedTotalCostToGoal += heuristic.heuristic(nextBoard);
					paths.add(newPath);
				}
			}			
		}
		return null;
	}
		
	private Map<Board, Move> successors(Board board) {
		Map<Board, Move> result = new HashMap<Board, Move>();
		for (Move nextMove : board.validMoves) {
			Board nextBoard = new Board(board, nextMove);
			result.put(nextBoard, nextMove);
		}
		return result;
	}

	@Override
	public Board generateBoard() {
		Board current = goal;
		int nMoves = (int) (Math.random() * 1000);
		Set<Board> visited = new HashSet<Board>();
		for (int i = 0; i < nMoves; ++i) {
			visited.add(current);
			Set<Board> succ = successors(current).keySet();
			succ.removeAll(visited);			
			Object[] nextBoards = succ.toArray();
			if (nextBoards.length == 0) return current;
			current = (Board) nextBoards[(int) (Math.random() * (nextBoards.length-1))];				
		}
		return current;
	}
	
	@Override
	public List<Board> generateBoards(int numBoards) {
		List<Board> boards = new ArrayList<Board>();
		for (int i = 0; i < numBoards; ++i) {
			boards.add(generateBoard());
		}
		return boards;
	}

}