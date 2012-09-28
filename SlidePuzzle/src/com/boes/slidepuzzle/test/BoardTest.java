/*
 * SlidePuzzle
 * Copyright (C) 2012 Matt Boes. 
 */

package com.boes.slidepuzzle.test;

import java.util.List;
import java.util.ArrayList;

import com.boes.slidepuzzle.model.Board;
import com.boes.slidepuzzle.model.Move;
import com.boes.slidepuzzle.model.Move.Action;
import com.boes.slidepuzzle.model.Path;

import com.boes.slidepuzzle.ai.Solver;
import com.boes.slidepuzzle.ai.SolverAStar;
import com.boes.slidepuzzle.ai.Heuristic;
import com.boes.slidepuzzle.ai.HeuristicNaive;
import com.boes.slidepuzzle.ai.HeuristicMisplaced;
import com.boes.slidepuzzle.ai.HeuristicManhattan;
import com.boes.slidepuzzle.ai.SolverDebugDecorator;
import com.boes.slidepuzzle.ai.SolverMemoDecorator;

import junit.framework.Assert;
import junit.framework.TestCase;

public class BoardTest extends TestCase {
	
	public void testBoardEquals() {
		Board b1 = new Board("012 345 678");		
		Board b2 = new Board(b1, new Move(b1, Action.LEFT));		
		Assert.assertTrue("b1 not equal to b2", !b1.equals(b2));
		
		Board b3 = new Board(b2, new Move(b2, Action.RIGHT));		
		Assert.assertTrue("b1 is equal to b3", b1.equals(b3));
	}

	public void testBoardValidMoves() {
		Board b1 = new Board("012 345 678");
		ArrayList<Move> moves = new ArrayList<Move>();
		moves.add(new Move(b1, Action.LEFT)); // order matters for comparison purposes, left before up
		moves.add(new Move(b1, Action.UP));
		Assert.assertEquals(moves, b1.validMoves);
	}

	public void testBoardToString() {
		Board start = new Board("012 345 678");
		Move nextMove = new Move(start, Action.LEFT);
		Board nextBoard = new Board(start, nextMove);
		Path path = new Path(start);
		path.add(nextMove, nextBoard);
		Heuristic naive = new HeuristicNaive(nextBoard); // nextBoard is the goal here
		path.estimatedTotalCostToGoal += naive.heuristic(nextBoard);
		Assert.assertEquals("Cost (1, 1) [012 345 678, (1, 0) LEFT, 102 345 678]" , path.toString());		
	}
	
	public void testSolve3Puzzle() {
		Board start = new Board("12 30");
		Solver solver = new SolverAStar(new Board("01 23"));
		Path path = solver.shortestPath(start);
		
		System.out.println("TEST 1 for 3-Puzzle...");
		System.out.println("Path: " + path);
		Assert.assertEquals(null, path);
		System.out.println("passed\n");
	}

	public void testSolve3Puzzle2() {
		Board start = new Board("21 03");
		Board goal = new Board("01 23");
		Solver solver = new SolverAStar(goal);
		
		Path expected = new Path(start);
		Move move = new Move(start, Action.DOWN);
		expected.add(move, goal);
		Path actual = solver.shortestPath(start);
		
		System.out.println("TEST 2 for 3-Puzzle...");
		System.out.println("Expected path: " + expected);
		System.out.println("Actual path:   " + actual);
		
		Assert.assertEquals(expected, actual);
		System.out.println("passed\n");
	}
	
	public void testSolve3Puzzle3() {
		Board start = new Board("32 10");
		Board goal = new Board("01 23");
		Solver solver = new SolverAStar(goal);
		
		Action[] actions = {Action.RIGHT, Action.DOWN, Action.LEFT, Action.UP, Action.RIGHT, Action.DOWN};
		Path expected = makePath(start, actions, new HeuristicNaive(goal), goal);
		Path actual = solver.shortestPath(start);
		
		System.out.println("TEST 3 for 3-Puzzle...");
		System.out.println("Expected path: " + expected);
		System.out.println("Actual path:   " + actual);
		
		// Compare numOfMoves rather than paths, because there could be more than one optimal path
		Assert.assertEquals(expected.getNumOfMoves(), actual.getNumOfMoves()); 
		System.out.println("passed\n");
	}
	
	public void testSolve8Puzzle() {
		Board start = new Board("134 025 678");
		Board goal = new Board("012 345 678");
		Solver solver = new SolverAStar(goal);
		
		Action[] actions = {Action.UP, Action.LEFT, Action.LEFT, Action.DOWN,
							Action.RIGHT, Action.DOWN, Action.LEFT, Action.UP, 
							Action.UP, Action.RIGHT, Action.RIGHT, Action.DOWN,
							Action.LEFT, Action.DOWN, Action.RIGHT};
		Path expected = makePath(start, actions, new HeuristicNaive(goal), goal);
		Path actual = solver.shortestPath(start);
		
		System.out.println("TEST 1...");
		System.out.println("Expected path: " + expected);
		System.out.println("Actual path:   " + actual);
		
		// Compare numOfMoves rather than paths, because there could be more than one optimal path		
		Assert.assertEquals(expected.getNumOfMoves(), actual.getNumOfMoves());
		System.out.println("passed\n");
	}
	
	private Path makePath(Board board, Action[] actions, Heuristic heuristic, Board goal) {
		// For comparing against results from Solver
		Path path = new Path(board);
		for (Action action : actions) {
			Move nextMove = new Move(board, action);
			Board nextBoard = new Board(board, nextMove);
			path.add(nextMove, nextBoard);
			path.estimatedTotalCostToGoal += heuristic.heuristic(nextBoard);
			board = nextBoard;
		}
		return path;
	}
	
	public void testSolve8Puzzle2() {
		Board start = new Board("123 456 780");
		Board goal = new Board("012 345 678");
		
		Log logNaive = new Log("Naive");
		Log logManHat = new Log("ManHat");
		Log logMisplaced = new Log("Misplaced");
		
		Solver solverNaive = new SolverDebugDecorator(new SolverAStar(goal), logNaive);
		Solver solverManHat = new SolverDebugDecorator(new SolverAStar(goal, new HeuristicManhattan(goal)), logManHat);
		Solver solverMisplaced = new SolverDebugDecorator(new SolverAStar(goal, new HeuristicMisplaced(goal)), logMisplaced);
		
		Path pathNaive = solverNaive.shortestPath(start);
		Path pathManHat = solverManHat.shortestPath(start);
		Path pathMisplaced = solverMisplaced.shortestPath(start);
		
		Assert.assertEquals(22, pathNaive.getNumOfMoves());
		Assert.assertEquals(22, pathManHat.getNumOfMoves());
		Assert.assertEquals(22, pathMisplaced.getNumOfMoves());
		
		System.out.println("TEST 2...");
		logNaive.printLog();
		logManHat.printLog();
		logMisplaced.printLog();		
	}
	
	public void testSolve8Puzzle3() {
		Board start = new Board("134 025 678");
		Solver solver = new SolverAStar(new Board("123 456 780"));
		Path path = solver.shortestPath(start);
		System.out.println("TEST 3...");
		System.out.println("Path: " + path);
		Assert.assertEquals(19, path.getNumOfMoves());
		System.out.println("passed\n");
	}
	
	public void testSolve8Puzzle4() {
		Board start = new Board("724 506 831");
		Board goal = new Board("012 345 678");
		
		Log logNaive = new Log("Naive");		
		Log logManHat = new Log("ManHat");
		Log logMisplaced = new Log("Misplaced");
		
		Solver solverNaive = new SolverDebugDecorator(new SolverAStar(goal), logNaive);
		Solver solverManhattan = new SolverDebugDecorator(new SolverAStar(goal, new HeuristicManhattan(goal)), logManHat);
		Solver solverMisplaced = new SolverDebugDecorator(new SolverAStar(goal, new HeuristicMisplaced(goal)), logMisplaced);
		
		solverNaive.shortestPath(start);
		solverManhattan.shortestPath(start);
		solverMisplaced.shortestPath(start);
		
		System.out.println("TEST 4...");
		logNaive.printLog();
		logManHat.printLog();
		logMisplaced.printLog();
	}
	
	public void testSolve8Puzzle5() {
		Board b1 = new Board("724 506 831");
		Board b2 = new Board("876 543 210");
		Board goal = new Board("012 345 678");
		Heuristic heuristic = new HeuristicManhattan(goal);
		
		Log log = new Log("ManHat");
		Solver solver = new SolverDebugDecorator(new SolverAStar(goal, heuristic), log);
		solver.shortestPath(b1);
		solver.shortestPath(b2);
		
		Log logMemo = new Log("Manhat Memo");
		Solver solverMemo = new SolverDebugDecorator(new SolverMemoDecorator (new SolverAStar(goal, heuristic)), logMemo);
		solverMemo.shortestPath(b1); // First time in cache
		solverMemo.shortestPath(b2);
		solverMemo.shortestPath(b1); // Should get a cache hit!
		solverMemo.shortestPath(b2);

		System.out.println("TEST 5...");
		log.printLog();
		log.printStats();
		logMemo.printLog();
		logMemo.printStats();		
	}
	
	public void testSolve8Puzzle6() {
		Board goal = new Board("012 345 678");
		Heuristic heuristic = new HeuristicManhattan(goal);
		
		Log log = new Log("Manhat Generated Boards");
		Solver solver = new SolverDebugDecorator(new SolverAStar(goal, heuristic), log);
		List<Board> boards = solver.generateBoards(100);		
		for (Board b : boards) solver.shortestPath(b);
		
		Log logMemo = new Log("Manhat Memo Generated Boards");
		Solver solverMemo = new SolverDebugDecorator(new SolverMemoDecorator (new SolverAStar(goal, heuristic)), logMemo);
		for (Board b : boards) solverMemo.shortestPath(b);

		System.out.println("TEST 6...");
		log.printStats();
		logMemo.printStats();
		
		Assert.assertTrue(Log.logDiff(log, logMemo));
	}
	
	public void testSolve8Puzzle7() {
		Board goal = new Board("012 345 678");
		Heuristic manHat = new HeuristicManhattan(goal);
		Heuristic misplaced = new HeuristicMisplaced(goal);
		
		Log logManHat = new Log("Manhat Generated Boards");
		Solver solverManHat = new SolverDebugDecorator(new SolverAStar(goal, manHat), logManHat);
		Log logMisplaced = new Log("Misplaced Generated Boards");
		Solver solverMisplaced = new SolverDebugDecorator(new SolverAStar(goal, misplaced), logMisplaced);

		List<Board> boards = solverManHat.generateBoards(100);
		for (Board b : boards) solverManHat.shortestPath(b);
		for (Board b : boards) solverMisplaced.shortestPath(b);
		
		System.out.println("TEST 7...");
		logManHat.printStats();
		logMisplaced.printStats();
		
		Assert.assertTrue(Log.logDiff(logManHat, logMisplaced));
	}
	
	public void testSolve8Puzzle8() {
		Board goal = new Board("012 345 678");
		
		Heuristic manHat = new HeuristicManhattan(goal);
		Heuristic misplaced = new HeuristicMisplaced(goal);
		Log logManHat = new Log("Manhat");
		Log logMisplaced = new Log("Misplaced");
		Solver solverManHat = new SolverDebugDecorator(new SolverAStar(goal, manHat), logManHat);
		Solver solverMisplaced = new SolverDebugDecorator(new SolverAStar(goal, misplaced), logMisplaced);
		
		Board start = new Board("532 706 481");
		solverManHat.shortestPath(start);
		solverMisplaced.shortestPath(start);
		
		Board down = new Board("502 736 481");
		solverManHat.shortestPath(down);
		solverMisplaced.shortestPath(down);

		System.out.println("TEST 8...");
		logManHat.printLog();
		logMisplaced.printLog();
		Assert.assertTrue(Log.logDiff(logManHat, logMisplaced));
	}
		
	public void testHeuristicManhattan() {
		Board goal = new Board("012 345 678");
		Heuristic manHat = new HeuristicManhattan(goal);
		
		// Successors of 532 706 481
		Board down = new Board("502 736 481");
		Board up = new Board("532 786 401");
		Board right = new Board("532 076 481");
		Board left = new Board("532 760 481");
		
		Assert.assertEquals(15, manHat.heuristic(down));
		Assert.assertEquals(17, manHat.heuristic(up));
		Assert.assertEquals(15, manHat.heuristic(right));
		Assert.assertEquals(15, manHat.heuristic(left));
		
		// Successors of 502 736 481
		Board up2 = new Board("532 706 481");
		Board right2 = new Board("052 736 481");
		Board left2 = new Board("520 736 481");

		Assert.assertEquals(16, manHat.heuristic(up2));
		Assert.assertEquals(14, manHat.heuristic(right2));
		Assert.assertEquals(16, manHat.heuristic(left2));
	}
	
}
