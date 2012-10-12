/*
 * SlidePuzzleAndroid
 * Copyright (C) 2012 Matt Boes.
 * All rights reserved. 
 */

package com.boes.slidepuzzle.android;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Interpolator;
import android.view.animation.DecelerateInterpolator;

import com.boes.slidepuzzle.model.Board;
import com.boes.slidepuzzle.model.Move;
import com.boes.slidepuzzle.model.Move.Action;
import com.boes.slidepuzzle.model.Path;
import com.boes.slidepuzzle.android.R;

public class BoardView extends View implements OnClickListener {

	private static final String TAG = BoardView.class.getSimpleName();
	private MainActivity game;

	private Board board;
	protected int moves;
	private Path shortestPath;
	protected int perfectPlay = -1; // move benchmark set on first call to updateAI
	private final float playerDuration = 300; // Piece animation duration on player touch.

	private Map<Character, Piece> pieces;
	private Piece touched;
	private Rect dirty;
	private int rowDelta, colDelta;	
	private Animator an;
	
	private int solving; // Equal to n number of moves from goal when AI is animating solution to puzzle.  Counts down to 0.
	private final MotionEvent cpuTouch = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	private final float aiDuration = 1500; // Piece animation duration on cpuTouch.	
	
	public BoardView(Context context) {
		super(context);
		game = (MainActivity) context;
	}
	
	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		game = (MainActivity) context;
	}

	public void setupPuzzle() {
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		board = game.solver.generateBoard();
		moves = -1; // incremented to 0 in updateMoves()
		solving = 0;
		an = new Animator(playerDuration, new OvershootInterpolator());
		
		pieces = createPieces();
		touched = null;
		dirty = new Rect();
		rowDelta = colDelta = 0;
		
		updateAI();
		updateMoves();
	}
	
	private Map<Character, Piece> createPieces() {
		Map<Character, Piece> result = new HashMap<Character, Piece>();
		for (int row = 0; row < board.getSize(); ++row)
			for (int col = 0; col < board.getSize(); ++col) {
				char p = board.getChar(row, col);
				if (p != Board.BLANK) result.put(p, new Piece(String.valueOf(p), row, col));
			}
		return result;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Piece.game = game;
		Piece.width = w / (float) board.getSize();
		Piece.height = h / (float) board.getSize();
		
		// TODO: Speed up by only creating bitmaps on game startup rather than for each new puzzle?
		for (Piece piece : pieces.values()) piece.createBitmap();
		
		// Called only once on game startup
		if (game.outBoard == null) {
			game.outBoard = new TranslateAnimation(0, w, 0, 0);
			game.inBoard = new TranslateAnimation(-w, 0, 0, 0);
			game.outBoard.setDuration(900);
			game.inBoard.setDuration(900);
			game.outBoard.setStartTime(Animation.START_ON_FIRST_FRAME);
			game.inBoard.setStartTime(Animation.START_ON_FIRST_FRAME);
			Interpolator i = new DecelerateInterpolator();
			game.outBoard.setInterpolator(i);
			game.inBoard.setInterpolator(i);		
		}		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK); // Draw background
		
		for (Piece piece : pieces.values()) // Draw all non-touched pieces
			if (!piece.equals(touched))
				canvas.drawBitmap(piece.bitmap, piece.col * Piece.width, piece.row * Piece.height, null);
		
		// touched is null on game start, animation end, or new puzzle.  Otherwise,
		if (touched != null) { // Draw touched piece last
			float fraction = an.getInterpolatedFraction();
			float left = ((touched.col - colDelta) + (colDelta * fraction)) * Piece.width;
			float top = ((touched.row - rowDelta) + (rowDelta * fraction)) * Piece.height;
			canvas.drawBitmap(touched.bitmap, left, top, null);

			dirty.set((int) left, (int) top, (int) (left + Piece.width + 1.0f), (int) (top + Piece.height + 1.0f));
			if (an.isAnimating()) invalidate(dirty);
			else {
				// Redraw board on animation end.  Fixes case when quick moves leave board incompletely drawn.
				touched = null;
				invalidate(dirty);
				
				if (solving > 0) onTouchEvent(cpuTouch);
				else {
					checkWin();
					an.setDuration(playerDuration);
				}
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int col, row;
		// If AI is animating solution and event is a cpuTouch, simulate move.
		if (solving > 0 && event.equals(cpuTouch)) {
			--solving;
			Move move = shortestPath.removeFirstMove();
			col = board.getCol(move.fromIndex);
			row = board.getRow(move.fromIndex);
		// If AI is NOT animating solution, then event is a player touch.
		} else if (solving == 0) { 
			col = (int) (event.getX() / Piece.width);
			row = (int) (event.getY() / Piece.height);
		// If AI is animating solution but event is a player touch, then ignore event and return.
		} else return true;
		
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			Action action = board.getAction(row, col);
			if (action != null && !an.isAnimating()) {
				touched = getPiece(row, col);
				int blankRow = board.getRow(board.blankIndex);
				int blankCol = board.getCol(board.blankIndex);
				rowDelta = blankRow - touched.row;
				colDelta = blankCol - touched.col;
				
				float left = touched.col * Piece.width;
				float top = touched.row * Piece.height;
				dirty.set((int) left, (int) top, (int) (left + Piece.width + 1.0f), (int) (top + Piece.height + 1.0f));

				touched.row = blankRow;
				touched.col = blankCol;
				board = new Board(board, new Move(board, action));

				an.start();
				updateMoves();
				updateAI();
				invalidate(dirty);
			}
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.newPuzzle:
			pause();
			newPuzzle();
			break;
		case R.id.solve:
			moveAhead(shortestPath.getNumOfMoves());
			break;
		case R.id.skipAhead:
			moveAhead(5);
			break;			
		}
	}
	
	public void newPuzzle() {
		game.boardView = new BoardView(game);
		game.setupBoardView();
		this.startAnimation(game.outBoard);
		game.boardView.startAnimation(game.inBoard);
		
		ViewGroup parent = (ViewGroup) getParent();
		int index = parent.indexOfChild(this);
		parent.removeView(this);
		parent.addView(game.boardView, index, getLayoutParams());		
	}
	
	private void moveAhead(int moves) {
		Log.d(TAG, "moveAhead: moves = " + moves);
		Log.d(TAG, "moveAhead: shortestPath = " + shortestPath);
		Log.d(TAG, "moveAhead: solving = " + solving);

		if (shortestPath != null && solving == 0 && shortestPath.getNumOfMoves() > 0) {
			solving = Math.min(moves, shortestPath.getNumOfMoves());
			an.setDuration(aiDuration);
			onTouchEvent(cpuTouch);				
		}		
	}
	
	private Piece getPiece(int row, int col) {
		char p = board.getChar(row, col);
		return pieces.get(p);
	}
	
	private void checkWin() {
		if (board.equals(game.goal)) game.showMyDialog(MainActivity.DIALOG_WIN);
	}
	
	private void updateAI() {
		if (solving > 0) setGoalText();
		// TODO: Interrupt previous thread before calling new thread?  For when player moves are faster than solver.
		else new Thread(new Runnable() {
			@Override
			public void run() {
				shortestPath = game.solver.shortestPath(board);
				if (perfectPlay == -1) perfectPlay = shortestPath.getNumOfMoves();
				game.goalView.post(new Runnable() {
					@Override
					public void run() { setGoalText(); }
				});
			}
		}).start();
	}
	
	private void setGoalText() {
		String spacing = " ";
		int count = shortestPath.getNumOfMoves();
		if (count < 10) spacing = "   ";	
		game.goalView.setText(game.getString(R.string.goal) + spacing + count);
	}

	private void updateMoves() {
		++moves;
		String message = game.getString(R.string.moves) + " " + moves;
		Log.d(TAG, message);		
		game.moveView.setText(message);
	}
	
	public void pause() {
		solving = 0;
	}
	
}