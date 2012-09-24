/*
 * SlidePuzzle
 * Copyright (C) 2012 Matt Boes. 
 */

package com.boes.slidepuzzle.ai;

import java.util.List;

import com.boes.slidepuzzle.model.Board;
import com.boes.slidepuzzle.model.Path;
import com.boes.slidepuzzle.test.Log;

public class SolverDebugDecorator implements Solver {

	private Solver solver;
	private Log log;
	
	public SolverDebugDecorator(Solver solver, Log log) {
		this.solver = solver;
		this.log = log;
	}
	
	@Override
	public Path shortestPath(Board start) {
		long startTime = System.nanoTime();
		Path path = solver.shortestPath(start);
		long endTime = System.nanoTime();		
		int totalMillis = (int) ((endTime - startTime) / 1000000);
		log.addResult(start, path, totalMillis);
		return path;
	}

	@Override
	public List<Board> generateBoards(int numBoards) {
		return solver.generateBoards(numBoards);
	}

}
