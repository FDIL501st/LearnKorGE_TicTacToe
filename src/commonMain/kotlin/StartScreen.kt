import korlibs.image.color.*
import korlibs.image.text.*
import korlibs.korge.input.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*

/**
 * Starting screen of the game.
 * First screen player sees when they start the game.
 */
class StartScreen : SceneBackground() {
    override suspend fun SContainer.sceneMain() {
        // this screen will simply be a text that says welcome with a start button

        // start button sends you to tic-tac-toe board

        // make a background
        solidRect(getGlobalBounds().size, color = Colors.BEIGE)

        // add welcome text center of screen
        val welcomeText = textBlock(RichTextData("Welcome\nTic Tac Toe",
            textSize = 30F, color = Colors.BLACK, font = font),
            align = TextAlignment.MIDDLE_CENTER) {
            width = 180F
            centerOnStage()
        }

        // add button to sends you to tic-tac-toe board
        uiButton("Start") {
            // place underneath welcome text
            centerXOn(welcomeText)
            alignTopToBottomOf(welcomeText, padding = 10)

            // when button is clicked, send to tic-tac-toe board
            // keep in sceneContainer stack so back button there can send us back to here
            onClick {
                sceneContainer.pushTo<BoardScene>()
            }
        }

    }
}
