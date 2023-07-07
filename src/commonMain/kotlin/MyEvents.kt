/**
 * an event which has information on how the game ends, who won.
 */
data class GameEndEvent(
    val gameEnd: GameEnd
)

/**
 * Possible ways a game can end.
 * Draw, X wins, O wins
 */
enum class GameEnd {
    DRAW,
    X,
    O
}
