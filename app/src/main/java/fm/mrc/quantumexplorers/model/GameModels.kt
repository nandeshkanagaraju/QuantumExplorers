package fm.mrc.quantumexplorers.model

import fm.mrc.quantumexplorers.R

data class GameInfo(
    val title: String,
    val description: String,
    val difficulty: Int = 1, // 1-5
    val category: GameCategory,
    val points: Int = 0,
    val icon: String = "",
    val unlocked: Boolean = true,
    val onClick: () -> Unit = {}
)

enum class GameCategory {
    MATH,
    SCIENCE,
    CODING,
    ENGINEERING,
    ARTS,
    TECHNOLOGY
}

// Add these new data classes for game questions
data class GameQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String,
    val reward: GameReward,
    val circuitLevel: CircuitLevel? = null,
    val diagramResources: DiagramResources? = null
)

data class GameReward(
    val description: String,
    val points: Int,
    val icon: String
)

// Add new data classes for circuit components
data class CircuitComponent(
    val type: CircuitComponentType,
    val position: Pair<Int, Int>,
    val orientation: Int = 0, // 0, 90, 180, 270 degrees
    val value: Float? = null // For components like resistors
)

enum class CircuitComponentType {
    BATTERY,
    RESISTOR,
    LED,
    WIRE,
    SWITCH,
    BULB
}

data class CircuitLevel(
    val components: List<CircuitComponent>,
    val availableComponents: List<CircuitComponentType>,
    val targetState: Boolean, // true if bulb should light up
    val points: Int
)

data class DiagramResources(
    val originalDiagramResId: Int,
    val modifiedDiagramResId: Int
)

// Update gameQuestions map to include the new games
val gameQuestions = mapOf(
    "Diagram Detective" to listOf(
        GameQuestion(
            question = "Spot the differences in these cell diagrams! Which organelle is missing in the second image?",
            options = listOf("Mitochondria", "Nucleus", "Golgi Apparatus", "Endoplasmic Reticulum"),
            correctAnswer = 0,
            explanation = "The mitochondria, the powerhouse of the cell, is missing in the second diagram!",
            reward = GameReward("Cell Spotter", 85, "🔬"),
            diagramResources = DiagramResources(
                originalDiagramResId = R.drawable.cell_complete,
                modifiedDiagramResId = R.drawable.cell_no_mitochondria
            )
        ),
        GameQuestion(
            question = "Compare these atomic models. What's different in the second model?",
            options = listOf(
                "Missing electron", 
                "Extra neutron", 
                "Different electron shell", 
                "Reversed spin"
            ),
            correctAnswer = 2,
            explanation = "The second model shows a different electron shell configuration, affecting the atom's energy state!",
            reward = GameReward("Atomic Observer", 90, "⚛️"),
            diagramResources = DiagramResources(
                originalDiagramResId = R.drawable.atom_normal,
                modifiedDiagramResId = R.drawable.atom_modified
            )
        ),
        GameQuestion(
            question = "Find the difference in these circuit diagrams. What component changed?",
            options = listOf(
                "Resistor orientation", 
                "LED direction", 
                "Battery polarity", 
                "Wire length"
            ),
            correctAnswer = 0,
            explanation = "The resistor's orientation is reversed, which could affect the circuit's behavior!",
            reward = GameReward("Circuit Inspector", 90, "⚡"),
            diagramResources = DiagramResources(
                originalDiagramResId = R.drawable.circuit_normal,
                modifiedDiagramResId = R.drawable.circuit_modified
            )
        ),
        GameQuestion(
            question = "Compare these DNA structures. What's missing in the second image?",
            options = listOf(
                "Base pair", 
                "Backbone", 
                "Hydrogen bond", 
                "Phosphate group"
            ),
            correctAnswer = 0,
            explanation = "A base pair is missing, which would disrupt the DNA structure!",
            reward = GameReward("DNA Detective", 85, "🧬"),
            diagramResources = DiagramResources(
                originalDiagramResId = R.drawable.dna_normal,
                modifiedDiagramResId = R.drawable.dna_modified
            )
        )
    ),
    "Quantum Particle Adventure" to listOf(
        GameQuestion(
            question = "Help the quantum particle jump through the correct energy level! Which orbital should it choose?",
            options = listOf("1s orbital", "2p orbital", "3d orbital", "4f orbital"),
            correctAnswer = 1,
            explanation = "The 2p orbital is the correct choice as it matches the energy required for the quantum jump!",
            reward = GameReward("Quantum Jump Master", 100, "⚛️")
        )
    ),
    "DNA Builder" to listOf(
        GameQuestion(
            question = "Match the correct base pair to complete the DNA strand: Adenine pairs with...",
            options = listOf("Thymine", "Cytosine", "Guanine", "Another Adenine"),
            correctAnswer = 0,
            explanation = "Adenine (A) always pairs with Thymine (T) in DNA through hydrogen bonding!",
            reward = GameReward("DNA Architect", 75, "🧬")
        )
    ),
    "Circuit Creator" to listOf(
        GameQuestion(
            question = "Build a basic circuit to light up the bulb. What component should connect the battery to the bulb?",
            options = listOf("Resistor", "Another Bulb", "Capacitor", "Inductor"),
            correctAnswer = 0,
            explanation = "A resistor is needed to limit current flow and protect the bulb from burning out!",
            reward = GameReward("Circuit Master", 100, "💡"),
            circuitLevel = CircuitLevel(
                components = listOf(
                    CircuitComponent(CircuitComponentType.BATTERY, Pair(0, 1)),
                    CircuitComponent(CircuitComponentType.BULB, Pair(2, 1))
                ),
                availableComponents = listOf(
                    CircuitComponentType.RESISTOR,
                    CircuitComponentType.WIRE
                ),
                targetState = true,
                points = 100
            )
        ),
        GameQuestion(
            question = "Add a switch to control the bulb. Where should it go?",
            options = listOf(
                "Between battery and resistor",
                "After the bulb",
                "Between resistor and bulb",
                "Parallel to battery"
            ),
            correctAnswer = 0,
            explanation = "Placing the switch between the battery and resistor creates a proper control point for the circuit!",
            reward = GameReward("Switch Master", 120, "⚡"),
            circuitLevel = CircuitLevel(
                components = listOf(
                    CircuitComponent(CircuitComponentType.BATTERY, Pair(0, 1)),
                    CircuitComponent(CircuitComponentType.RESISTOR, Pair(1, 1)),
                    CircuitComponent(CircuitComponentType.BULB, Pair(3, 1))
                ),
                availableComponents = listOf(
                    CircuitComponentType.SWITCH,
                    CircuitComponentType.WIRE
                ),
                targetState = true,
                points = 120
            )
        )
    ),
    "Geometric Art Studio" to listOf(
        GameQuestion(
            question = "Create a perfect golden ratio spiral! What comes after a 13 unit square?",
            options = listOf("21 units", "18 units", "15 units", "24 units"),
            correctAnswer = 0,
            explanation = "The Fibonacci sequence continues: 8, 13, 21...",
            reward = GameReward("Golden Ratio Master", 90, "🎨")
        )
    ),
    // Keep existing game questions
    "Math Quest: The Number Kingdom" to listOf(
        GameQuestion(
            question = "A wizard gives you 2 potions. Each potion multiplies your strength by 3. What is your new strength if you started at 5?",
            options = listOf("15", "30", "25", "10"),
            correctAnswer = 1,
            explanation = "Each potion multiplies by 3, so it's 5 × 3 × 3 = 45",
            reward = GameReward("Magic Multiplication Spell", 50, "✨")
        )
    ),
    "Physics Playground" to listOf(
        GameQuestion(
            question = "You push a 5kg box with a force of 10N. What is its acceleration?",
            options = listOf("2 m/s²", "5 m/s²", "10 m/s²"),
            correctAnswer = 0,
            explanation = "F = ma, so a = F/m = 10/5 = 2 m/s²",
            reward = GameReward("Force Master Badge", 40, "🏋️")
        )
    ),
    "Math Detective" to listOf(
        GameQuestion(
            question = "Spot the difference in these equations! Which term changed in the second equation?",
            options = listOf(
                "Exponent", 
                "Coefficient", 
                "Variable", 
                "Operation"
            ),
            correctAnswer = 1,
            explanation = "The coefficient changed from 3 to 5 in the second equation!",
            reward = GameReward("Equation Expert", 100, "📐"),
            diagramResources = DiagramResources(
                originalDiagramResId = R.drawable.equation_normal,
                modifiedDiagramResId = R.drawable.equation_modified
            )
        ),
        GameQuestion(
            question = "Find the mistake in this geometric sequence! Which number breaks the pattern?",
            options = listOf(
                "2", 
                "6", 
                "18", 
                "48"
            ),
            correctAnswer = 3,
            explanation = "48 breaks the pattern! The sequence should be 2, 6, 18, 54 (multiplying by 3 each time).",
            reward = GameReward("Pattern Finder", 90, "🔢"),
            diagramResources = DiagramResources(
                originalDiagramResId = R.drawable.sequence_normal,
                modifiedDiagramResId = R.drawable.sequence_modified
            )
        ),
        GameQuestion(
            question = "Compare these triangles. What property is different?",
            options = listOf(
                "Angle measure", 
                "Side length", 
                "Area", 
                "Perimeter"
            ),
            correctAnswer = 0,
            explanation = "The angle in the second triangle is 60° instead of 45°!",
            reward = GameReward("Geometry Master", 95, "📐"),
            diagramResources = DiagramResources(
                originalDiagramResId = R.drawable.triangle_normal,
                modifiedDiagramResId = R.drawable.triangle_modified
            )
        )
    )
) 