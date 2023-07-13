import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.font.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.math.geom.*
import kotlin.properties.*

var xImgBitmaps: Array<Bitmap> = emptyArray()
var oImgBitmaps: Array<Bitmap> = emptyArray()
var resetBitmap: Bitmap by Delegates.notNull()
var font: BitmapFont by Delegates.notNull()

// global variable that tracks whose turn it currently is
var turn: Turn = Turn.X
// X goes first, so they are set initially

suspend fun main() = Korge(windowSize = Size(512, 512), backgroundColor = Colors["#2b2b2b"]) {
    // read font to be used
    font = KR.fonts.latoBlack.__file.readTtfFont().toBitmapFont(300F)

    xImgBitmaps = arrayOf(KR.board.x1.read(), KR.board.x2.read(), KR.board.x3.read())
    oImgBitmaps = arrayOf(KR.board.o1.read(), KR.board.o2.read(), KR.board.o3.read())

    // read reset image
    resetBitmap = KR.board.reset.read()

    // get reference to sceneContainer
    val sceneContainer = sceneContainer()

    // add all scenes to injector (so can use sceneContainer to change scenes)
    views.injector.mapPrototype { StartScreen() }
    views.injector.mapPrototype { BoardScene() }

    // go to starting scene of the game
	sceneContainer.changeTo<StartScreen>()
}

/**
 * Enum that determines whose turn it currently is.
 */
enum class Turn {
    X,
    O
}

// below is the default main scene created with the hello worlds

//class MyScene : Scene() {
//	override suspend fun SContainer.sceneMain() {
//		val minDegrees = (-16).degrees
//		val maxDegrees = (+16).degrees
//
//		val image = image(resourcesVfs["korge.png"].readBitmap()) {
//			rotation = maxDegrees
//			anchor(.5, .5)
//			scale(0.8)
//			position(256, 256)
//		}
//
//        // also add a button on top when clicked on changes the scene
//        val btn = uiButton(label = "Change image visibility") {
//            // what happens when button is clicked
//            onClick {
//                // changes visibility of the image
//                image.visible = !image.visible
//            }
//
//            size(200, 30)
//        }
//
//
//		while (true) {
//			image.tween(image::rotation[minDegrees], time = 1.seconds, easing = Easing.EASE_IN_OUT)
//			image.tween(image::rotation[maxDegrees], time = 1.seconds, easing = Easing.EASE_IN_OUT)
//		}
//
//	}
//}

