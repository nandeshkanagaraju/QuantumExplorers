package fm.mrc.quantumexplorers.model

// Represents a dynamic assessment question with branching logic
data class DynamicAssessmentQuestion(
    val id: String,
    val question: String,
    val options: List<DynamicAssessmentOption>,
    // Optional follow-up questions based on profile tendencies
    val followUpQuestions: Map<String, String> = emptyMap()
)

// Represents an option in a dynamic assessment question
data class DynamicAssessmentOption(
    val text: String,
    val profile: String,
    // The ID of the next question to ask if this option is selected
    // If null, the assessment will follow the default path
    val nextQuestionId: String? = null
)

// Represents the state of the dynamic assessment
data class DynamicAssessmentState(
    val currentQuestionId: String,
    val answeredQuestions: List<String> = emptyList(),
    val profileScores: Map<String, Int> = emptyMap(),
    // Tracks which profiles are trending to personalize follow-up questions
    val profileTendencies: Set<String> = emptySet()
)

// Question bank for dynamic assessment
val dynamicQuestionBank = mapOf(
    "start" to DynamicAssessmentQuestion(
        id = "start",
        question = "You're starting a new project. What's your first step?",
        options = listOf(
            DynamicAssessmentOption("Research similar projects and remember key details", "Memory Master", "memory_path"),
            DynamicAssessmentOption("Make a detailed plan with specific steps", "Problem Solver", "problem_path"),
            DynamicAssessmentOption("Gather materials and start experimenting", "Active Learner", "active_path"),
            DynamicAssessmentOption("Look for patterns or visual inspiration", "Perception Pro", "perception_path"),
            DynamicAssessmentOption("Write down your ideas or discuss with others", "Language Leader", "language_path"),
            DynamicAssessmentOption("Focus on the most important aspects first", "Focus Finder", "focus_path")
        )
    ),
    
    // Memory Master path
    "memory_path" to DynamicAssessmentQuestion(
        id = "memory_path",
        question = "When learning something new, what helps you remember it best?",
        options = listOf(
            DynamicAssessmentOption("Connecting it to something I already know", "Memory Master", "memory_followup"),
            DynamicAssessmentOption("Breaking it down into logical steps", "Problem Solver"),
            DynamicAssessmentOption("Practicing it repeatedly", "Active Learner"),
            DynamicAssessmentOption("Creating a mental image or visualization", "Perception Pro")
        ),
        followUpQuestions = mapOf(
            "Problem Solver" to "Do you prefer to solve problems step-by-step or look at the whole picture first?",
            "Active Learner" to "How important is hands-on experience in your learning process?"
        )
    ),
    
    "memory_followup" to DynamicAssessmentQuestion(
        id = "memory_followup",
        question = "You need to remember a list of items. What's your strategy?",
        options = listOf(
            DynamicAssessmentOption("Create a story connecting all the items", "Memory Master", "common_question1"),
            DynamicAssessmentOption("Group similar items together in categories", "Problem Solver"),
            DynamicAssessmentOption("Write them down multiple times", "Focus Finder"),
            DynamicAssessmentOption("Create a visual map or diagram", "Perception Pro")
        )
    ),
    
    // Problem Solver path
    "problem_path" to DynamicAssessmentQuestion(
        id = "problem_path",
        question = "When faced with a complex problem, how do you approach it?",
        options = listOf(
            DynamicAssessmentOption("Break it down into smaller, manageable parts", "Problem Solver", "problem_followup"),
            DynamicAssessmentOption("Look for patterns or similarities to problems I've solved before", "Memory Master"),
            DynamicAssessmentOption("Try different approaches until something works", "Active Learner"),
            DynamicAssessmentOption("Visualize the problem and possible solutions", "Perception Pro")
        )
    ),
    
    "problem_followup" to DynamicAssessmentQuestion(
        id = "problem_followup",
        question = "When planning a project, what's most important to you?",
        options = listOf(
            DynamicAssessmentOption("Having a clear, logical sequence of steps", "Problem Solver", "common_question1"),
            DynamicAssessmentOption("Flexibility to adapt as new information emerges", "Active Learner"),
            DynamicAssessmentOption("Understanding all the details and requirements", "Focus Finder"),
            DynamicAssessmentOption("Having a clear vision of the end result", "Perception Pro")
        )
    ),
    
    // Active Learner path
    "active_path" to DynamicAssessmentQuestion(
        id = "active_path",
        question = "How do you prefer to learn a new skill?",
        options = listOf(
            DynamicAssessmentOption("Jump in and learn by doing, adjusting as I go", "Active Learner", "active_followup"),
            DynamicAssessmentOption("Watch demonstrations and then try it myself", "Perception Pro"),
            DynamicAssessmentOption("Follow a step-by-step tutorial", "Problem Solver"),
            DynamicAssessmentOption("Read about techniques and best practices first", "Language Leader")
        )
    ),
    
    "active_followup" to DynamicAssessmentQuestion(
        id = "active_followup",
        question = "When you make a mistake while learning something new, what do you do?",
        options = listOf(
            DynamicAssessmentOption("Try again with a different approach", "Active Learner", "common_question1"),
            DynamicAssessmentOption("Analyze what went wrong and why", "Problem Solver"),
            DynamicAssessmentOption("Look for examples of how to do it correctly", "Perception Pro"),
            DynamicAssessmentOption("Ask someone to explain where I went wrong", "Language Leader")
        )
    ),
    
    // Perception Pro path
    "perception_path" to DynamicAssessmentQuestion(
        id = "perception_path",
        question = "When trying to understand a concept, what helps you most?",
        options = listOf(
            DynamicAssessmentOption("Seeing diagrams, charts, or visual representations", "Perception Pro", "perception_followup"),
            DynamicAssessmentOption("Hearing it explained in different ways", "Language Leader"),
            DynamicAssessmentOption("Working through examples step by step", "Problem Solver"),
            DynamicAssessmentOption("Connecting it to real-world applications", "Active Learner")
        )
    ),
    
    "perception_followup" to DynamicAssessmentQuestion(
        id = "perception_followup",
        question = "When reading a story, what stands out to you most?",
        options = listOf(
            DynamicAssessmentOption("The visual scenes and imagery", "Perception Pro", "common_question1"),
            DynamicAssessmentOption("The characters' development and motivations", "Language Leader"),
            DynamicAssessmentOption("The sequence of events and plot structure", "Problem Solver"),
            DynamicAssessmentOption("Memorable quotes and dialogue", "Memory Master")
        )
    ),
    
    // Language Leader path
    "language_path" to DynamicAssessmentQuestion(
        id = "language_path",
        question = "How do you best express your ideas?",
        options = listOf(
            DynamicAssessmentOption("Through writing or speaking", "Language Leader", "language_followup"),
            DynamicAssessmentOption("By creating diagrams or visual aids", "Perception Pro"),
            DynamicAssessmentOption("By demonstrating or showing examples", "Active Learner"),
            DynamicAssessmentOption("By organizing them into logical frameworks", "Problem Solver")
        )
    ),
    
    "language_followup" to DynamicAssessmentQuestion(
        id = "language_followup",
        question = "When learning from others, what do you value most?",
        options = listOf(
            DynamicAssessmentOption("Clear explanations and discussions", "Language Leader", "common_question1"),
            DynamicAssessmentOption("Demonstrations and visual examples", "Perception Pro"),
            DynamicAssessmentOption("Opportunities to ask questions and get feedback", "Active Learner"),
            DynamicAssessmentOption("Well-structured, logical presentations", "Problem Solver")
        )
    ),
    
    // Focus Finder path
    "focus_path" to DynamicAssessmentQuestion(
        id = "focus_path",
        question = "When working on a task, how do you maintain focus?",
        options = listOf(
            DynamicAssessmentOption("Eliminate distractions and concentrate on one thing at a time", "Focus Finder", "focus_followup"),
            DynamicAssessmentOption("Set clear goals and track my progress", "Problem Solver"),
            DynamicAssessmentOption("Take breaks and switch between different activities", "Active Learner"),
            DynamicAssessmentOption("Create a structured environment with visual reminders", "Perception Pro")
        )
    ),
    
    "focus_followup" to DynamicAssessmentQuestion(
        id = "focus_followup",
        question = "When examining information, what's your approach?",
        options = listOf(
            DynamicAssessmentOption("Pay close attention to details and specifics", "Focus Finder", "common_question1"),
            DynamicAssessmentOption("Look for patterns and connections", "Perception Pro"),
            DynamicAssessmentOption("Analyze the logical structure and organization", "Problem Solver"),
            DynamicAssessmentOption("Identify the most important points to remember", "Memory Master")
        )
    ),
    
    // Common questions that all paths eventually lead to
    "common_question1" to DynamicAssessmentQuestion(
        id = "common_question1",
        question = "When working in a group, what role do you naturally take?",
        options = listOf(
            DynamicAssessmentOption("The one who remembers what was discussed and decided previously", "Memory Master", "common_question2"),
            DynamicAssessmentOption("The one who keeps everyone on track and focused on the goal", "Focus Finder", "common_question2"),
            DynamicAssessmentOption("The one who plans and organizes the approach", "Problem Solver", "common_question2"),
            DynamicAssessmentOption("The one who notices patterns and connections between ideas", "Perception Pro", "common_question2"),
            DynamicAssessmentOption("The one who communicates and explains ideas clearly", "Language Leader", "common_question2"),
            DynamicAssessmentOption("The one who takes initiative and tries new approaches", "Active Learner", "common_question2")
        )
    ),
    
    "common_question2" to DynamicAssessmentQuestion(
        id = "common_question2",
        question = "What's your biggest challenge when learning something difficult?",
        options = listOf(
            DynamicAssessmentOption("Remembering all the information and details", "Memory Master", "final_question"),
            DynamicAssessmentOption("Staying focused and avoiding distractions", "Focus Finder", "final_question"),
            DynamicAssessmentOption("Understanding how all the pieces fit together logically", "Problem Solver", "final_question"),
            DynamicAssessmentOption("Recognizing patterns and visualizing concepts", "Perception Pro", "final_question"),
            DynamicAssessmentOption("Explaining my understanding or asking questions", "Language Leader", "final_question"),
            DynamicAssessmentOption("Getting enough hands-on practice", "Active Learner", "final_question")
        )
    ),
    
    "final_question" to DynamicAssessmentQuestion(
        id = "final_question",
        question = "What learning environment helps you succeed?",
        options = listOf(
            DynamicAssessmentOption("Structured with clear expectations and examples", "Memory Master"),
            DynamicAssessmentOption("Quiet and free from distractions", "Focus Finder"),
            DynamicAssessmentOption("Organized with logical progression of concepts", "Problem Solver"),
            DynamicAssessmentOption("Visual and spatial with diagrams and models", "Perception Pro"),
            DynamicAssessmentOption("Discussion-based with opportunities to express ideas", "Language Leader"),
            DynamicAssessmentOption("Hands-on with freedom to experiment", "Active Learner")
        )
    )
)

// Function to determine the next question based on the current state
fun getNextQuestion(state: DynamicAssessmentState): DynamicAssessmentQuestion? {
    val currentQuestion = dynamicQuestionBank[state.currentQuestionId] ?: return null
    
    // If this is the final question, return null to indicate assessment is complete
    if (state.currentQuestionId == "final_question") {
        return null
    }
    
    // Get profile tendencies to potentially customize follow-up questions
    val profileTendencies = state.profileScores.entries
        .sortedByDescending { it.value }
        .take(2)
        .map { it.key }
        .toSet()
    
    // Check if there's a follow-up question based on profile tendencies
    for (profile in profileTendencies) {
        currentQuestion.followUpQuestions[profile]?.let { followUpQuestion ->
            // Create a custom follow-up question based on the profile tendency
            return DynamicAssessmentQuestion(
                id = "custom_followup_${profile.lowercase().replace(" ", "_")}",
                question = followUpQuestion,
                options = listOf(
                    DynamicAssessmentOption("I prefer step-by-step approaches", "Problem Solver", "common_question1"),
                    DynamicAssessmentOption("I like to see the whole picture first", "Perception Pro", "common_question1"),
                    DynamicAssessmentOption("I learn best through practical experience", "Active Learner", "common_question1"),
                    DynamicAssessmentOption("I rely on clear explanations and instructions", "Language Leader", "common_question1")
                )
            )
        }
    }
    
    return null
}

// Function to update the assessment state based on an answer
fun updateAssessmentState(state: DynamicAssessmentState, selectedOption: DynamicAssessmentOption): DynamicAssessmentState {
    // Update profile scores
    val updatedScores = state.profileScores.toMutableMap()
    updatedScores[selectedOption.profile] = (updatedScores[selectedOption.profile] ?: 0) + 1
    
    // Determine profile tendencies
    val profileTendencies = updatedScores.entries
        .sortedByDescending { it.value }
        .take(2)
        .map { it.key }
        .toSet()
    
    // Determine next question ID
    val nextQuestionId = selectedOption.nextQuestionId ?: run {
        // If no specific next question, follow default path
        val currentQuestion = dynamicQuestionBank[state.currentQuestionId]
        when {
            state.currentQuestionId == "final_question" -> null // Assessment complete
            currentQuestion?.id?.contains("followup") == true -> "common_question1"
            currentQuestion?.id?.contains("common_question") == true -> {
                if (state.currentQuestionId == "common_question1") "common_question2" else "final_question"
            }
            else -> "common_question1"
        }
    } ?: return state.copy(currentQuestionId = "")  // Empty ID indicates assessment is complete
    
    return state.copy(
        currentQuestionId = nextQuestionId,
        answeredQuestions = state.answeredQuestions + state.currentQuestionId,
        profileScores = updatedScores,
        profileTendencies = profileTendencies
    )
}

// Enhanced profile determination that considers the dynamic question flow
fun determineDynamicProfile(profileScores: Map<String, Int>): String {
    // If no scores, return undefined
    if (profileScores.isEmpty()) return "Undefined"
    
    // Find the maximum score
    val maxScore = profileScores.values.maxOrNull() ?: 0
    
    // Find profiles with the maximum score
    val topProfiles = profileScores.filter { it.value == maxScore }.keys.toList()
    
    // Return the appropriate profile or combination
    return when {
        topProfiles.size > 1 -> "${topProfiles[0]} & ${topProfiles[1]}"
        topProfiles.isEmpty() -> "Undefined"
        else -> topProfiles[0]
    }
} 