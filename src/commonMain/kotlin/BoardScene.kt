import korlibs.image.color.*
import korlibs.image.text.*
import korlibs.korge.input.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*

class BoardScene : SceneBackground() {
    override suspend fun SContainer.sceneMain() {
        // make all tiles and setup winLogic first

        // make all 9 tiles
        val tiles = Array(9) { _ -> Tile(xImgBitmaps, oImgBitmaps)}

        // board is made up of 3 x 3 blocks
        /* here is a show of index of tiles and how they will be displayed on screen

        0 1 2
        3 4 5
        6 7 8

        This will be a good reference when seeing which index of tiles is effected when trying to
        predict behaviour of the game and placement of tile.
         */

        // setup board logic
        BoardLogic.initTiles(tiles)

        // make a board and place on screen, centered X, a bit lower on the screen
        val board = makeBoard(tiles).addTo(this).centerOnStage()
        board.positionY(board.y + 50)       // move board a bit lower on the screen from center
        // 50 is an experimental number to move board down by, it was chosen cause it looks good

        // add reset button to clear board and reset board
        val resetBtn = roundRect(size = resetBitmap.size.toFloat(), radius = RectCorners(5)) {
            name = "Reset"

            image(resetBitmap)

            // center on X
            centerXOnStage()

            onClick {
                // reset board
                BoardLogic.resetBoard()

                // remove winLine from board
                board["winLine"].first.removeFromParent()

                // change turn back to X
                turn = Turn.X
            }
        }

        // underneath reset button, have text block that shows whose turn it is or result of game
        val bg = roundRect(Size(200, 50), RectCorners(0)) {
            centerXOn(resetBtn)
            alignTopToBottomOf(resetBtn, padding = 10)
        }

        // place msg on bg
        val msg = textBlock(makeMsg("Turn: $turn"), align = TextAlignment.MIDDLE_CENTER) {
            width = bg.width
            centerOn(bg)
        }

        // setup what happens when a tile is changed
        for (tile in tiles) {
            tile.onTileStateChanged {
                if (turn == Turn.X) {
                    // check if X won
                    BoardLogic.hasXWon()
                }
                else {
                    // check if O won
                    BoardLogic.hasOWon()
                }

                // only check if board is full if no winner
                if (BoardLogic.winLine.first == -1) {

                    if (BoardLogic.checkBoardFilled()) {
                        // board is filled so trigger game end where draw
                        val drawEvent = GameEndEvent(GameEnd.DRAW)

                        BoardLogic.onGameEnd(drawEvent)
                    }

                    // board not full, so change turn
                    else {
                        // no winner and board is not filled, so change turn
                        turn = if (turn == Turn.X)
                            Turn.O
                        else
                            Turn.X
                        // update turn message
                        writeTurnMessage(msg)
                    }
                }
            }
        }


        BoardLogic.onGameEnd {
            // when game ends
            // print state game passed
            println(it.gameEnd)

            // draw line across 3 in a row (if it exists)
            when (BoardLogic.winLine.second) {
                WinDirection.ROW -> {
                    // draw win line
                    drawHorizontalWinLine(board)
                    // write win message
                    writeWinMessage(msg)
                }
                WinDirection.COLUMN ->  {
                    // draw win line
                    drawVerticalWinLine(board)
                    // write win message
                    writeWinMessage(msg)
                }
                WinDirection.DIAGONAL -> {
                    // draw win line
                    drawDiagonalWinLine(board)
                    // write win message
                    writeWinMessage(msg)
                }
                else -> {
                    // write draw message
                    writeDrawMessage(msg)
                }
            }

            // freeze the board as game ended
            BoardLogic.freezeBoard()
        }

    }

    private fun makeBoard(tiles: Array<Tile>): Container {
        val board = Container()
        board.name = "board"

        // make 4 lines, used to make our look 3 x 3 by separating the tiles
        // get side length of a tile
        val s = tiles[0].tile.size.height

        // amount extra want to add to each side
        // this is amount line goes past the board on each side
        val extra = 20F
        val lineThickness = 10F // thickness of line

        // line size (long side) is 3*s + 2*lineThickness + 2*extra
        // as 3 tiles + 2 lines in between the 3 tiles + 2 extra on each side

        // horizontal lines
        val h1 = RoundRect(Size(3*s + 2*lineThickness + 2*extra, lineThickness),
            RectCorners(lineThickness/2), Colors.BLACK)
        h1.name = "h1"

        val h2 = RoundRect(Size(3*s + 2*lineThickness + 2*extra, lineThickness),
            RectCorners(lineThickness/2), Colors.BLACK)
        h2.name = "h2"

        // vertical lines
        val v1 = RoundRect(Size(lineThickness, 3*s + 2*lineThickness + 2*extra),
            RectCorners(lineThickness/2), Colors.BLACK)
        v1.name = "v1"

        val v2 = RoundRect(Size(lineThickness, 3*s + 2*lineThickness + 2*extra),
            RectCorners(lineThickness/2), Colors.BLACK)
        v2.name = "v2"

        // place each part of the board one at a time

        // place first tile
        tiles[0].tile.addTo(board)
        tiles[0].tile.name = "tile0"

        // place v1 beside it and move it extra that sticks out (-extra on y-axis)
        v1.addTo(board).alignLeftToRightOf(tiles[0].tile).positionY(v1.y - extra)

        // place tiles[1] right of v1
        tiles[1].tile.addTo(board).alignLeftToRightOf(v1)
        tiles[1].tile.name = "tile1"

        // place v2 left of tiles[1], set Y position same as v1
        v2.addTo(board).centerOn(v1).alignLeftToRightOf(tiles[1].tile)

        // place tiles[2] right of v2
        tiles[2].tile.addTo(board).alignLeftToRightOf(v2)
        tiles[2].tile.name = "tile2"

        // top row placed, place h1 below it, have extra part poke out (-extra on x-axis)
        h1.addTo(board).alignTopToBottomOf(tiles[0].tile).positionX(h1.x - extra)

        // place next row, tiles[3..5] right below h1
        tiles[3].tile.addTo(board).alignTopToBottomOf(h1)
        tiles[4].tile.addTo(board).alignTopToBottomOf(h1).alignLeftToRightOf(v1)
        tiles[5].tile.addTo(board).alignTopToBottomOf(h1).alignLeftToRightOf(v2)
        tiles[3].tile.name = "tile3"
        tiles[4].tile.name = "tile4"
        tiles[5].tile.name = "tile5"

        // add h2 below row just made
        h2.addTo(board).centerOn(h1).alignTopToBottomOf(tiles[3].tile)

        // place last row, tiles[6..8] right below h2
        tiles[6].tile.addTo(board).alignTopToBottomOf(h2)
        tiles[7].tile.addTo(board).alignTopToBottomOf(h2).alignLeftToRightOf(v1)
        tiles[8].tile.addTo(board).alignTopToBottomOf(h2).alignLeftToRightOf(v2)
        tiles[6].tile.name = "tile6"
        tiles[7].tile.name = "tile7"
        tiles[8].tile.name = "tile8"

        return board
    }

    private fun makeMsg(msg: String): RichTextData {
        return RichTextData(msg, textSize = 30F, color = Colors.BLACK, font = font)
    }

    /**
     * Writes whose turn it is on the msg block.
     */
    private fun writeTurnMessage(msgBlock: TextBlock) {
        msgBlock.text = makeMsg("Turn $turn")
    }


    /**
     * Writes who won on the msg block.
     */
    private fun writeWinMessage(msgBlock: TextBlock) {
        msgBlock.text = makeMsg("Winner: $turn")
    }

    /**
     * Writes draw on msg block.
     */
    private fun writeDrawMessage(msgBlock: TextBlock) {
        msgBlock.text = makeMsg("Draw")
    }

    /**
     * Draws the horizontal win line.
     */
    private fun drawHorizontalWinLine(board: Container) {
        // make the horizontal line
        // just a copy of horizontal line in board
        val toCopy = board["h1"].first
        val line = SolidRect(toCopy.size, Colors.BLACK).addTo(board).centerXOn(toCopy)
        line.name = "winLine"

        // line already centered, just need to move up and down to place on correct row
        // to do so, just need starting tile of row, then move line to be in middle of its row

        val tile = when (BoardLogic.winLine.first) {
            0 -> board["tile0"].first       //  top row
            3 -> board["tile3"].first       //  middle row
            else -> board["tile6"].first    // bottom row
        }

        // now to place in middle of the row, center Y on it
        line.centerYOn(tile)
    }

    /**
     * Draws the vertical win line.
     */
    private fun drawVerticalWinLine(board: Container) {
        // make the vertical line
        // just a copy of vertical line in board
        val toCopy = board["v1"].first
        val line = SolidRect(toCopy.size, Colors.BLACK).addTo(board).centerYOn(toCopy)
        line.name = "winLine"

        // line already centered, just need to move left or right to place on correct column
        // to do so, just need starting tile of column, then move line to be in middle of its column

        val tile = when (BoardLogic.winLine.first) {
            0 -> board["tile0"].first       //  left column
            1 -> board["tile1"].first       //  middle column
            else -> board["tile2"].first    // right column
        }

        // now to place in middle of the column, center X on it
        line.centerXOn(tile)
    }

    /**
     * Draws the diagonal win line.
     */
    private fun drawDiagonalWinLine(board: Container) {
        // make the diagonal line
        // copy horizontal line then rotate it
        val toCopy = board["h1"].first

        val line = SolidRect(toCopy.size, Colors.BLACK).addTo(board)
        line.name = "winLine"

        // first figure out starting tile, then place and rotate
        when (BoardLogic.winLine.first) {
            //  top left to bottom right
            0 -> {
                val tile = board["tile0"].first
                line.rotation(Angle.fromDegrees(45))
                // position line to centre of tile
                line.positionX(tile.x + tile.width/3)
                line.positionY(tile.y + tile.height/3)
            }

            // top right to bottom left
            else -> {
                val tile = board["tile6"].first
                line.rotation(Angle.fromDegrees(-45))
                // position line to centre of tile
                line.positionX(tile.x + tile.width/3)
                line.positionY(tile.y + tile.height/3*2)
            }
        }

    }
}
