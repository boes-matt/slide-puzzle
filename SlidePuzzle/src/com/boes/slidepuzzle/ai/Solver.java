/*
 * SlidePuzzle
 * Copyright (C) 2012 Matt Boes.
 * All rights reserved.
 */

package com.boes.slidepuzzle.ai;

import java.util.List;

import com.boes.slidepuzzle.model.Board;
import com.boes.slidepuzzle.model.Path;

public interface Solver {

	public Path shortestPath(Board start);
	public Board generateBoard();
	public List<Board> generateBoards(int numBoards);
	
}