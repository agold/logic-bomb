Logic Bomb
==========
Logic Bomb is a clone of the Minesweeper game written with Java and Swing. 
In its current iteration, Logic Bomb only implements the traditional Minesweeper game. 
In future iterations, the ability to decompose the game board into a system of Boolean logic equations and then solve that system will be added. 
This will allow Logic Bomb to be used as an education tool in learning Boolean algebra.

Building
==========
Clone the repository and run
    make

Playing
==========
Logic Bomb includes the standard mouse actions, namely:
	- Click to clear a single cell
	- Right-click to flag as a mine
	- Double-click a cleared cell when its flag quota is met to clear-around
	- Empty region clearing
As well, the board can be set to a custom size and number of mines.

The game is won when all empty cells have been cleared. The mines do not need
to be marked. The game is lost if a cell containing a mine is cleared.
