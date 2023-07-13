import korlibs.image.color.*
import korlibs.korge.input.*
import korlibs.korge.view.*
import korlibs.korge.view.Circle
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
        roundRect(Size(50, 50), RectCorners(0)) {
            // reset image
            image(resetBitmap)

            onClick {
                // reset board
                BoardLogic.resetBoard()
                // change turn back to X
                turn = Turn.X
            }

            // center on X
            centerXOnStage()
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
                if (BoardLogic.winLine.first == -1 && BoardLogic.checkBoardFilled()) {
                    // board is filled so trigger game end where draw
                    val drawEvent = GameEndEvent(GameEnd.DRAW)

                    BoardLogic.onGameEnd(drawEvent)
                }

                else {
                    // no winner and board is not filled, so change turn
                    turn = if (turn == Turn.X)
                        Turn.O
                    else
                        Turn.X
                }
            }
        }


        BoardLogic.onGameEnd {
            // when game ends
            // print state game passed
            println(it.gameEnd)

            // also freeze the board
            BoardLogic.freezeBoard()
        }

    }

    private fun makeBoard(tiles: Array<Tile>): Container {
        val board = Container()

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

        val h2 = RoundRect(Size(3*s + 2*lineThickness + 2*extra, lineThickness),
            RectCorners(lineThickness/2), Colors.BLACK)

        // vertical lines
        val v1 = RoundRect(Size(lineThickness, 3*s + 2*lineThickness + 2*extra),
            RectCorners(lineThickness/2), Colors.BLACK)

        val v2 = RoundRect(Size(lineThickness, 3*s + 2*lineThickness + 2*extra),
            RectCorners(lineThickness/2), Colors.BLACK)

        // place each part of the board one at a time

        // place first tile
        tiles[0].tile.addTo(board)

        // place v1 beside it and move it extra that sticks out (-extra on y-axis)
        v1.addTo(board).alignLeftToRightOf(tiles[0].tile).positionY(v1.y - extra)

        // place tiles[1] right of v1
        tiles[1].tile.addTo(board).alignLeftToRightOf(v1)

        // place v2 left of tiles[1], set Y position same as v1
        v2.addTo(board).centerOn(v1).alignLeftToRightOf(tiles[1].tile)

        // place tiles[2] right of v2
        tiles[2].tile.addTo(board).alignLeftToRightOf(v2)

        // top row placed, place h1 below it, have extra part poke out (-extra on x-axis)
        h1.addTo(board).alignTopToBottomOf(tiles[0].tile).positionX(h1.x - extra)

        // place next row, tiles[3..5] right below h1
        tiles[3].tile.addTo(board).alignTopToBottomOf(h1)
        tiles[4].tile.addTo(board).alignTopToBottomOf(h1).alignLeftToRightOf(v1)
        tiles[5].tile.addTo(board).alignTopToBottomOf(h1).alignLeftToRightOf(v2)

        // add h2 below row just made
        h2.addTo(board).centerOn(h1).alignTopToBottomOf(tiles[3].tile)

        // place last row, tiles[6..8] right below h2
        tiles[6].tile.addTo(board).alignTopToBottomOf(h2)
        tiles[7].tile.addTo(board).alignTopToBottomOf(h2).alignLeftToRightOf(v1)
        tiles[8].tile.addTo(board).alignTopToBottomOf(h2).alignLeftToRightOf(v2)

        return board
    }
}
