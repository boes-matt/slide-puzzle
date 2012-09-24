/*
 * SlidePuzzleAndroid
 * Copyright (C) 2012 Matt Boes. 
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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.boes.slidepuzzle.ai.HeuristicManhattan;
import com.boes.slidepuzzle.ai.Solver;
import com.boes.slidepuzzle.ai.SolverAStar;
import com.boes.slidepuzzle.ai.SolverMemoDecorator;
import com.boes.slidepuzzle.model.Board;
import com.boes.slidepuzzle.model.Move;
import com.boes.slidepuzzle.model.Move.Action;
import com.boes.slidepuzzle.model.Path;
import com.boes.slidepuzzle.android.R;

public class BoardView extends View implements OnClickListener {

	private static final String TAG = BoardView.class.getSimpleName();
	
	private MainActivity game;
	public TextView moveView;
	public TextView goalView;
	public BoardView boardView;
	public Button newPuzzle;
	public Button solve;
	public Button skipAhead;
	
	private Solver solver;
	private Board board;
	private Board goal;
	private int moves;
	private Path shortestPath;
	public int solving;
	private float playerDuration;
	private float aiDuration = 1500;
	
	private Map<Character, Piece> pieces;
	private Animator an;
	private Piece touched;
	private Rect dirty;
	private int rowDelta, colDelta;
	
	public BoardView(Context context) {
		super(context);
		//setup(context);
		game = (MainActivity) context;
	}
	
	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//setup(context);
		game = (MainActivity) context;
	}

	public void setup() {
		//game = (MainActivity) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		goal = new Board("012 345 678");
		solver = new SolverMemoDecorator(new SolverAStar(goal, new HeuristicManhattan(goal)));
		board = generateBoard(solver);
		moves = -1;			
		
		solving = 0;
		playerDuration = 300;
		
		pieces = createPieces();
		an = new Animator(playerDuration, new OvershootInterpolator());
		touched = null;
		dirty = new Rect();
		rowDelta = colDelta = 0;
		
		moveView = (TextView) game.findViewById(R.id.moves);
		goalView = (TextView) game.findViewById(R.id.goal);
		newPuzzle = (Button) game.findViewById(R.id.newPuzzle);
		solve = (Button) game.findViewById(R.id.solve);
		skipAhead = (Button) game.findViewById(R.id.skipAhead);

		newPuzzle.setOnClickListener(this);
		solve.setOnClickListener(this);
		skipAhead.setOnClickListener(this);

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
		Piece.width = w / (float) board.getSize();
		Piece.height = h / (float) board.getSize();
		for (Piece piece : pieces.values()) piece.createBitmap();
		
		updateAI(); // if does not get drawn from call in setup; FIX hack
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		for (Piece piece : pieces.values()) {
			if (touched == null || !piece.equals(touched))  // touched is null on game start or new puzzle
				canvas.drawBitmap(piece.bitmap, piece.col * Piece.width, piece.row * Piece.height, null);
			else {
				float fraction = an.getInterpolatedFraction();
				float left = ((touched.col - colDelta) + (colDelta * fraction)) * Piece.width;
				float top = ((touched.row - rowDelta) + (rowDelta * fraction)) * Piece.height;
				canvas.drawBitmap(touched.bitmap, left, top, null);
				
				dirty.set((int) left, (int) top, (int) (left + Piece.width + 1.0f), (int) (top + Piece.height + 1.0f));
				if (an.isAnimating()) invalidate(dirty);
				else {
					canvas.drawBitmap(piece.bitmap, piece.col * Piece.width, piece.row * Piece.height, null); // Needed?
					if (solving > 0) onTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					else an.setDuration(playerDuration);
				}
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int col, row;
		if (solving > 0 && event.getX() == 0 && event.getY() == 0) { // Need getX, getY checks to disable player moves during a solving animation
			--solving;
			Move move = shortestPath.removeFirstMove();
			col = board.getCol(move.fromIndex);
			row = board.getRow(move.fromIndex);
		} else if (solving == 0) {
			col = (int) (event.getX() / Piece.width);
			row = (int) (event.getY() / Piece.height);			
		} else {
			return true;
		}
		
		
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
				checkWin();
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
			if (solving == 0) {
				TranslateAnimation out = new TranslateAnimation(0, getWidth(), 0, 0);
				TranslateAnimation in = new TranslateAnimation(-getWidth(), 0, 0, 0);
				out.setDuration(900);
				in.setDuration(900);
				out.setStartTime(Animation.START_ON_FIRST_FRAME);
				in.setStartTime(Animation.START_ON_FIRST_FRAME);
				Interpolator i = new DecelerateInterpolator();
				out.setInterpolator(i);
				in.setInterpolator(i);
				
				BoardView bv = new BoardView(game);
				bv.setup();
				this.startAnimation(out);
				bv.startAnimation(in);
				
				ViewGroup parent = (ViewGroup) getParent();
				int index = parent.indexOfChild(this);
				parent.removeView(this);
				parent.addView(bv, index, getLayoutParams());
			}
			break;
		case R.id.solve:
			if (shortestPath != null && solving == 0 && shortestPath.getNumOfMoves() > 0) {
				solving = shortestPath.getNumOfMoves();
				an.setDuration(aiDuration);
				onTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0, 0, 0, 0, 0, 0, 0, 0));				
			}
			break;
		case R.id.skipAhead:
			int skip = 5;
			if (shortestPath != null && solving == 0 && shortestPath.getNumOfMoves() > 0) {
				solving = Math.min(skip, shortestPath.getNumOfMoves());
				an.setDuration(aiDuration);
				onTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0, 0, 0, 0, 0, 0, 0, 0));				
			}
			break;			
		}
	}
	
	private Piece getPiece(int row, int col) {
		char p = board.getChar(row, col);
		return pieces.get(p);
	}
	
	private void checkWin() {
		if (board.equals(goal)) {
			Toast toast = Toast.makeText(game, R.string.win, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}
	
	private void updateAI() {
		if (solving > 0) {
			String spacing = " ";
			int count = shortestPath.getNumOfMoves();
			if (count < 10) spacing = "   ";
			
			goalView.setText(game.getString(R.string.goal) + spacing + count);
			return;
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				shortestPath = solver.shortestPath(board);
				goalView.post(new Runnable() {
					@Override
					public void run() {
						String spacing = " ";
						int count = shortestPath.getNumOfMoves();
						if (count < 10) spacing = "   ";
						
						goalView.setText(game.getString(R.string.goal) + spacing + count);
					}
				});
			}
		}).start();
		
	}

	private void updateMoves() {
		++moves;
		String message = game.getString(R.string.moves) + " " + moves;
		Log.d(TAG, message);		
		moveView.setText(message);
	}
	
	private Board generateBoard(Solver solver) {
		return solver.generateBoards(1).get(0);
	}
}
