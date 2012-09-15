slide-puzzle
============

The classic slide (number) puzzle.  Set up for the 8-puzzle.  Use the backend (SlidePuzzle) to power client apps, or build the Android app (SlidePuzzleAndroid).

SlidePuzzle project structure
-----------------------------

The puzzle backend   

[com.boes.slidepuzzle.model](https://github.com/boes-matt/slide-puzzle/tree/master/SlidePuzzle/src/com/boes/slidepuzzle/model): Board, Move, and Path classes   
[com.boes.slidepuzzle.ai](https://github.com/boes-matt/slide-puzzle/tree/master/SlidePuzzle/src/com/boes/slidepuzzle/ai): Solver and Heuristic interfaces and classes   
[com.boes.slidepuzzle.test](https://github.com/boes-matt/slide-puzzle/tree/master/SlidePuzzle/src/com/boes/slidepuzzle/test): JUnit test class and Log class   

See BoardTest class for example usage.   

SlidePuzzleAndroid project structure
------------------------------------

The Android app (frontend)

[com.boes.slidepuzzle.android](https://github.com/boes-matt/slide-puzzle/tree/master/SlidePuzzleAndroid/src/com/boes/slidepuzzle/android): Android activity, BoardView, and helper classes

