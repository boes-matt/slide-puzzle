/*
 * SlidePuzzle
 * Copyright (C) 2012 Matt Boes. 
 */

package com.boes.slidepuzzle.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.boes.slidepuzzle.model.Board;
import com.boes.slidepuzzle.model.Path;

public class Log {
	
	// Object[] wraps board to give it a unique hash, even if two boards are the same
	private final Map<Object[], Path> paths;
	private final Map<Object[], Integer> numOfMoves;
	private final Map<Object[], Integer> times;
	private final String name;
	
	public Log(String name) {
		paths = new HashMap<Object[], Path>();		
		numOfMoves = new HashMap<Object[], Integer>();
		times = new HashMap<Object[], Integer>();
		this.name = name;
	}
	
	public void addResult(Board start, Path path, int time) {
		Object[] uniqueBoard = {start};
		paths.put(uniqueBoard, path);
		numOfMoves.put(uniqueBoard, path.getNumOfMoves());
		times.put(uniqueBoard, time);
	}
	
	public void printLog() {
		System.out.println(name + " solver:");
		for (Entry<Object[], Path> entry : paths.entrySet()) {
			Object[] uniqueBoard = entry.getKey();
			Path path = entry.getValue();
			
			System.out.println("Board: " + uniqueBoard[0]);
			System.out.println("Path: " + path);
			System.out.println(numOfMoves.get(uniqueBoard) + " moves");
			System.out.println(times.get(uniqueBoard) + " ms");
			System.out.println();
		}
	}
		
	public void printStats() {
		System.out.println(name + " stats for " + paths.size() + " test boards:");
		printTimes();
		printNumOfMoves();
	}
	
	public static boolean logDiff(Log logA, Log logB) {
		/*
		 * This function adds the paths from logA to a hashtable for each start board,
		 * and then subsequently removes those paths from the hashtable
		 * if a path of the same length (i.e. moves) exists in logB
		 * for that particular start board.
		 * 
		 * If not, the path from logB of differing length is added to the hashtable
		 * under that start board for comparison purposes.  
		 * 
		 * Useful when comparing the results of different solvers.
		 */
		
		Map<Board, List<Path>> startBoards = new HashMap<Board, List<Path>>();
		
		// Add boards to hashtable
		for (Entry<Object[], Path> entry : logA.paths.entrySet()) {
			Board boardA = (Board) entry.getKey()[0];
			Path pathA = entry.getValue();
			
			if (startBoards.containsKey(boardA)) startBoards.get(boardA).add(pathA);
			else {
				List<Path> pathsA = new ArrayList<Path>();
				pathsA.add(pathA);
				startBoards.put(boardA, pathsA);
			}
		}
		
		// Remove matches from hashtable
		for (Entry<Object[], Path> entry : logB.paths.entrySet()) {
			Board boardB = (Board) entry.getKey()[0];
			Path pathB = entry.getValue();
			
			if (startBoards.containsKey(boardB)) {
				List<Path> pathsA = startBoards.get(boardB);
				int moves = pathsA.get(0).getNumOfMoves();
				
				if (pathB.getNumOfMoves() == moves) {
					if (pathsA.size() > 1) pathsA.remove(0);
					else startBoards.remove(boardB);					
				} else pathsA.add(pathB);
			} else {
				List<Path> pathsB = new ArrayList<Path>();
				pathsB.add(pathB);
				startBoards.put(boardB, pathsB);				
			}
		}
		
		System.out.println("Log diff:");
		if (startBoards.isEmpty()) {
			System.out.println("Logs match!");
			System.out.println();
			return true;
		} else {
			for (Entry<Board, List<Path>> entry : startBoards.entrySet()) {
				System.out.println("Board: " + entry.getKey());
				for (Path path : entry.getValue()) System.out.println("Path: " + path);
				System.out.println();
			}
			return false;
		}
	}

	public void printNumOfMoves() {
		System.out.println("Average path:  " + getAverageNumOfMoves() + " moves");
		System.out.println("Longest path:  " + getLongestNumOfMoves() + " moves");
		System.out.println("Shortest path: " + getShortestNumOfMoves() + " moves");		
		System.out.println();
	}
		
	public void printTimes() {
		System.out.println("Average time:  " + getAverageTime() + " ms");
		System.out.println("Longest time:  " + getLongestTime() + " ms");
		System.out.println("Shortest time: " + getShortestTime() + " ms");
		System.out.println();
	}
		
	public int getAverageNumOfMoves() {
		int sum = 0;
		for (int moves : numOfMoves.values()) sum += moves;
		return sum / numOfMoves.size();
	}
	
	public int getLongestNumOfMoves() {
		return Collections.max(numOfMoves.values());
	}
	
	public int getShortestNumOfMoves() {
		return Collections.min(numOfMoves.values());
	}
	
	public int getAverageTime() {
		int sum = 0;
		for (int time : times.values()) sum += time;
		return sum / times.size();
	}
	
	public int getLongestTime() {
		return Collections.max(times.values());
	}
	
	public int getShortestTime() {
		return Collections.min(times.values());
	}
	
}
