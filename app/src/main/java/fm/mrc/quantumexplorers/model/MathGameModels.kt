package fm.mrc.quantumexplorers.model

data class MathGame(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: Int,
    val type: MathGameType,
    val questions: List<MathQuestion>,
    val unlocked: Boolean = true
)

data class MathQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String
)

enum class MathGameType {
    ARITHMETIC,
    ALGEBRA,
    GEOMETRY,
    QUANTUM_MATH
}

// Sample math games
val mathGames = listOf(
    MathGame(
        id = "quantum_arithmetic",
        title = "Quantum Numbers",
        description = "Practice quantum arithmetic with fun exercises",
        difficulty = 1,
        type = MathGameType.QUANTUM_MATH,
        questions = listOf(
            MathQuestion(
                question = "If an electron transitions from energy level 3 to 1, how many levels does it drop?",
                options = listOf("1", "2", "3", "4"),
                correctAnswer = 1,
                explanation = "The electron drops 2 energy levels from level 3 to level 1"
            ),
            // Add more questions...
        )
    )
    // Add more games...
) 