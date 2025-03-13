package fm.mrc.quantumexplorers.model

import fm.mrc.quantumexplorers.R

data class LearningUnit(
    val id: String,
    val title: String,
    val description: String,
    val category: GameCategory,
    val content: List<LearningContent>,
    val difficulty: Int, // 1-5
    val estimatedMinutes: Int,
    val icon: String,
    val unlocked: Boolean = true
)

data class LearningContent(
    val type: ContentType,
    val title: String,
    val body: String,
    val imageResId: Int? = null,
    val codeSnippet: String? = null
)

enum class ContentType {
    TEXT,
    IMAGE,
    CODE,
    INTERACTIVE
}

// Sample learning units
val learningUnits = mapOf(
    "Quantum Mechanics" to listOf(
        LearningUnit(
            id = "quantum_basics",
            title = "Quantum Basics",
            description = "Introduction to the fascinating world of quantum mechanics",
            category = GameCategory.SCIENCE,
            content = listOf(
                LearningContent(
                    type = ContentType.TEXT,
                    title = "What is Quantum Mechanics?",
                    body = "Quantum mechanics is a fundamental theory in physics that describes the behavior of matter and energy at the atomic and subatomic scales..."
                ),
                LearningContent(
                    type = ContentType.IMAGE,
                    title = "Atomic Structure",
                    body = "Visual representation of an atom",
                    imageResId = R.drawable.atom_modified
                ),
                LearningContent(
                    type = ContentType.INTERACTIVE,
                    title = "Build an Atom",
                    body = "Interactive exercise to build your own atom"
                )
            ),
            difficulty = 1,
            estimatedMinutes = 30,
            icon = "⚛️"
        ),
        LearningUnit(
            id = "quantum_states",
            title = "Quantum States",
            description = "Learn about quantum states and superposition",
            category = GameCategory.SCIENCE,
            content = listOf(
                LearningContent(
                    type = ContentType.TEXT,
                    title = "Understanding Quantum States",
                    body = "A quantum state describes the properties of a quantum system..."
                ),
                LearningContent(
                    type = ContentType.IMAGE,
                    title = "Energy Levels",
                    body = "Visualization of quantum energy levels",
                    imageResId = R.drawable.quantum_states
                )
            ),
            difficulty = 2,
            estimatedMinutes = 45,
            icon = "🌟"
        )
    ),
    "Mathematics" to listOf(
        LearningUnit(
            id = "quantum_math",
            title = "Quantum Mathematics",
            description = "Essential mathematical concepts for quantum mechanics",
            category = GameCategory.MATH,
            content = listOf(
                LearningContent(
                    type = ContentType.TEXT,
                    title = "Complex Numbers in Quantum Mechanics",
                    body = "Complex numbers are fundamental to quantum mechanics..."
                ),
                LearningContent(
                    type = ContentType.INTERACTIVE,
                    title = "Complex Number Calculator",
                    body = "Practice working with complex numbers"
                )
            ),
            difficulty = 3,
            estimatedMinutes = 40,
            icon = "🔢"
        )
    ),
    "Programming" to listOf(
        LearningUnit(
            id = "quantum_programming",
            title = "Quantum Programming",
            description = "Introduction to quantum computing concepts",
            category = GameCategory.CODING,
            content = listOf(
                LearningContent(
                    type = ContentType.TEXT,
                    title = "Quantum Bits (Qubits)",
                    body = "Understanding the basic unit of quantum information..."
                ),
                LearningContent(
                    type = ContentType.CODE,
                    title = "First Quantum Program",
                    body = """
                        // Simple quantum circuit
                        quantum_circuit = QuantumCircuit(2, 2)
                        quantum_circuit.h(0)  // Hadamard gate
                        quantum_circuit.cx(0, 1)  // CNOT gate
                    """
                )
            ),
            difficulty = 4,
            estimatedMinutes = 60,
            icon = "💻"
        )
    )
) 