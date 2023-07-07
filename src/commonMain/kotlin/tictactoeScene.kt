import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.input.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*


class tictactoeScene : SceneBackground() {
    override suspend fun SContainer.sceneMain() {
        val c = container() {
            val bg = solidRect(200, 200, color = Colors.WHITE)
            val img = image(resourcesVfs["korge.png"].readBitmap()) {
                scale(0.2)
                centerOn(bg)
            }

            centerOnStage()

            // disable mouse events so it doesn't catch any
            mouseEnabled = false
        }

        val btn = uiButton(label = "Back") {
            // when button is clicked, send back to starting screen
            onClick {
                sceneContainer.back()
            }
        }

        // make a tile and add to scene
        val t1 = Tile(xImgBitmap, oImgBitmap)
        t1.tile.addTo(this)

        val btnEmpty = uiButton("Empty") {
            onClick { t1.changeState(TileState.EMPTY) }
            alignLeftToRightOf(btn, padding = 5)
        }

        val btnX = uiButton("X") {
            onClick { t1.changeState(TileState.X) }
            alignLeftToRightOf(btnEmpty, padding = 5)
        }

        uiButton("O") {
            onClick { t1.changeState(TileState.O) }
            alignLeftToRightOf(btnX, padding = 5)
        }

        // move tile to proper spot
        t1.tile.centerXOn(c)
        t1.tile.alignBottomToTopOf(c, padding = 10)
    }
}
