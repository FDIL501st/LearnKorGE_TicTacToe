import korlibs.io.async.*
import kotlinx.coroutines.*

/**
 * Object that is used to help determine the winLine logic of the game.
 * Does so by checking the array of tiles that it stores a reference to.
 */
object BoardLogic {

    private lateinit var tiles: Array<Tile>

    // the winning line, initially set to -1, NONE as no winning line yet
    // updated by functions that checks if X or O has won
    var winLine = Pair(-1, WinDirection.NONE)

    // event when game ends
    val onGameEnd = Signal<GameEndEvent>()

    fun onGameEnd(handler: suspend (GameEndEvent) -> Unit) {
        // add the handler to the custom event
        onGameEnd.add {
            launchImmediately(Dispatchers.Default) {
                handler(it)
            }
        }
    }

    /**
     * Set the tiles.
     */
    fun initTiles(tiles: Array<Tile>) {
        this.tiles = tiles
    }

    /**
     * checks if O has won.
     * Set the starting index and the direction if O has won.
     */
    fun hasOWon() {
        // first check lines and see if they don't return -1
        val colsCheck = checkCols(TileState.O)
        val rowsCheck = checkRows(TileState.O)
        val diagsCheck = checkDiags(TileState.O)

        winLine = if (colsCheck != -1)
            Pair(colsCheck, WinDirection.COLUMN)
        else if (rowsCheck != -1)
            Pair(rowsCheck, WinDirection.ROW)
        else if (diagsCheck != -1)
            Pair(diagsCheck, WinDirection.DIAGONAL)
        else
            Pair(-1, WinDirection.NONE)
    }

    /**
     * checks if X has won.
     * Sets the starting index and the direction if O has won.
     */
    fun hasXWon() {
        // first check lines and see if they don't return -1
        val colsCheck = checkCols(TileState.X)
        val rowsCheck = checkRows(TileState.X)
        val diagsCheck = checkDiags(TileState.X)

        winLine = if (colsCheck != -1)
            Pair(colsCheck, WinDirection.COLUMN)
        else if (rowsCheck != -1)
            Pair(rowsCheck, WinDirection.ROW)
        else if (diagsCheck != -1)
            Pair(diagsCheck, WinDirection.DIAGONAL)
        else
            Pair(-1, WinDirection.NONE)
    }

    /**
     * Checks if all the tiles are not Empty.
     */
    fun checkBoardFilled(): Boolean {
        for (tile in tiles) {
            if (tile.state != TileState.EMPTY)
                return false
        }
        return true
    }

    /**
     * Checks the columns if any of them have the same tileState.
     * Returns the starting column index(0, 1, 2) depending on which column has the same tileState.
     * Return -1 if none of them have the same tileState.
     * For winLine logic, should not pass in EMPTY.
     */
    private fun checkCols(tileState: TileState): Int {
        // check for tileState being 3 in a row
        // start by checking top row which are tileState

        for (i in 0..2) {
            if (tiles[i].state == tileState) {
                // check other 2 in column being same tileState
                return if (tiles[i+3].state == tileState && tiles[i+6].state == tileState)
                    i
                else
                    -1
            }
        }

        return -1
    }

    /**
     * Checks the rows if any of them have the same tileState.
     * Returns the starting row index(0, 3, 6) depending on which row has the same tileState.
     * Return -1 if none of them have the same tileState.
     * For winLine logic, should not pass in EMPTY.
     */
    private fun checkRows(tileState: TileState): Int {
        for (i in intArrayOf(0, 3, 6)) {
            if (tiles[i].state == tileState) {
                // check other 2 in row being same tileState
                return if (tiles[i+1].state == tileState && tiles[i+2].state == tileState)
                    i
                else
                    -1
            }
        }
        return -1
    }

    /**
     * Checks the diagonals if any of them have the same tileState.
     * Returns the starting diagonals index(0, 2) depending on which row has the same tileState.
     * Return -1 if none of them have the same tileState.
     * For winLine logic, should not pass in EMPTY.
     */
    private fun checkDiags(tileState: TileState): Int {
        // first check index 0 diagonal
        if (tiles[0].state == tileState) {
            // check the other 2 in diagonal if same
            return if (tiles[4].state == tileState && tiles[8].state == tileState)
                0
            else
                -1
        }

        // check other index 2 diagonal
        if (tiles[2].state == tileState) {
            // check other 2 in diagonal if same
            return if (tiles[4].state == tileState && tiles[6].state == tileState)
                2
            else
                -1
        }

        return -1
    }

    /**
     * Disables all mouse events in the board.
     * Used to stop the game as board can no longer change.
     */
    private fun freezeBoard() {
        for (tile in tiles) {
            tile.tile.mouseEnabled = false
        }
    }
}

/**
 * Determines winLine direction. Can figure out from starting where the 3 in a row is.
 */
enum class WinDirection {
    NONE,       // did not winLine
    ROW,        // left to right
    COLUMN,     // top to bottom
    DIAGONAL,   // top to bottom (diagonally)
}