import korlibs.image.color.*
import korlibs.korge.scene.*
import korlibs.korge.view.*

/**
 * Abstract class used to define common background between scenes.
 * Will not be used as a scene by itself.
 */
abstract class SceneBackground : Scene(){
    override suspend fun SContainer.sceneInit() {
        // make the background
        // which is beige colour of entire screen (not maximized screen)
        val bg = solidRect(getGlobalBounds().size, color = Colors.BEIGE)
    }
}
