# Matts Slide Puzzle

Matts Slide Puzzle is an implementation of the classic slide puzzle or 8-puzzle. Order the tiles 1 through 8, with the BLANK in the bottom right hand corner.

  * Game in the [Android Play](https://play.google.com/store/apps/details?id=com.boes.slidepuzzle.android) market
  * Coded solver using A* search algorithm to assist gameplay
  * Custom board view, and animation support for Froyo and Gingerbread
  * Over 180,000 possible puzzles, ranging 0-31 moves away from the goal.

<img src="https://raw.github.com/boes-matt/slide-puzzle/master/screenshots/unsolved.png" height="545" />
&nbsp;&nbsp;
<img src="https://raw.github.com/boes-matt/slide-puzzle/master/screenshots/youwin.png" height="545" />

## SlidePuzzle project structure

The puzzle engine (backend)   

[com.boes.slidepuzzle.model](https://github.com/boes-matt/slide-puzzle/tree/master/SlidePuzzle/src/com/boes/slidepuzzle/model): Board, Move, and Path classes   
[com.boes.slidepuzzle.ai](https://github.com/boes-matt/slide-puzzle/tree/master/SlidePuzzle/src/com/boes/slidepuzzle/ai): Solver and Heuristic interfaces and classes   
[com.boes.slidepuzzle.test](https://github.com/boes-matt/slide-puzzle/tree/master/SlidePuzzle/src/com/boes/slidepuzzle/test): JUnit test class and Log class   

## SlidePuzzleAndroid project structure

The Android app (frontend)

[com.boes.slidepuzzle.android](https://github.com/boes-matt/slide-puzzle/tree/master/SlidePuzzleAndroid/src/com/boes/slidepuzzle/android): Android activity, BoardView, and other classes
