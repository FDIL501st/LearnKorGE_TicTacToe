import korlibs.korge.view.*
import korlibs.image.bitmap.*
import korlibs.io.async.*
import korlibs.korge.input.*
import kotlinx.coroutines.*

// a global flag that get changed to true when a tile state is changed to X or O
// which means a turn has ended


/**
 * A tile of the board.
 */
class Tile(xBitMap: Bitmap, oBitMap: Bitmap) {
    // tiles start empty
    var state: TileState = TileState.EMPTY

    // holds a Container object which is placed on the scene
    val tile: Container = Container()

    // custom event
    val onTileStateChanged = Signal<TileStateChangedEvent>()

    fun onTileStateChanged(handler: suspend (TileStateChangedEvent) -> Unit) {
        // add the handler to the custom event
        onTileStateChanged.add {
            launchImmediately(Dispatchers.Default) {
                handler(it)
            }
        }
    }

    // add images to the tile and make the tile start empty
    init {
        // white blank box, will give it proper size later
        val blank = SolidRect(0, 0).addTo(tile)
        // by default, SolidRect colour is white

        val x = Image(xBitMap).addTo(tile)
        Image(oBitMap).addTo(tile)
        // use x to give blank proper size
        blank.size(x.size)

        // this way all 3 parts are the same size,
        // so they take the same amount of space on screen

        //Note: Checked size, they are 100 x 100 pixels

        // make to initial tile state, empty
        makeEmpty()

        // add event listeners
        addEventListeners()
    }

    /**
     * Change state of the tile.
     * This means what the tile shows.
     */
    fun changeState(newTileState: TileState) {
        when (newTileState) {
            TileState.EMPTY -> makeEmpty()
            TileState.X -> showX()
            TileState.O -> showO()
        }
    }

    /**
     * Makes the tile empty.
     * This means the tile is white blank.
     */
    private fun makeEmpty() {
        tile[0].visible(true)
        tile[1].visible(false)
        tile[2].visible(false)

        // update state and enable click events (make them clickable again)
        state = TileState.EMPTY
        tile.mouseEnabled = true
    }

    /**
     * Makes the tile show X.
     */
    private fun showX() {
        tile[0].visible(false)
        tile[1].visible(true)
        tile[2].visible(false)

        // update state
        state = TileState.X
    }

    /**
     * Makes the tile show O.
     */
    private fun showO() {
        tile[0].visible(false)
        tile[1].visible(false)
        tile[2].visible(true)

        // update state
        state = TileState.O
    }

    /**
     * Adds event listeners for tile
     */
    private fun addEventListeners() {
        tile.onClick {
            // turns tile to X or O, depending on whose turn it currently us
            when (turn) {
                Turn.X -> showX()
                Turn.O -> showO()
            }
            // also disable mouse events
            tile.mouseEnabled = false

            // finally trigger the tile state changed event
            val tileChangedEvent = TileStateChangedEvent(
                tileStateChangedFlag = true
            )
            onTileStateChanged(tileChangedEvent)
        }

    }
}


/**
 * The states the tile can be in.
 * Empty = showing blank white box
 * X = showing X
 * O = showing O
 */
enum class TileState {
    EMPTY,
    X,
    O
}

/**
 * data class for the custom event that is called when tile state is changed
 */
data class TileStateChangedEvent(
    val tileStateChangedFlag: Boolean
)
