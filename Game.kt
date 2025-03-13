data class Game(
    val title: String,
    val description: String,
    val category: GameCategory
)

enum class GameCategory {
    MATH, SCIENCE, CODING, ENGINEERING, ARTS
} 