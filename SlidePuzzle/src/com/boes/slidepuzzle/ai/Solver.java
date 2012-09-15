package com.boes.slidepuzzle.ai;

import java.util.List;

import com.boes.slidepuzzle.model.Board;
import com.boes.slidepuzzle.model.Path;

public interface Solver {

	public Path shortestPath(Board start);
	public List<Board> generateBoards(int numBoards);
	
}
