package fm.mrc.quantumexplorers.model

val profileDefinitions = mapOf(
    "Memory Master" to CognitiveProfile(
        name = "Memory Master",
        strengths = listOf(
            "Excellent at remembering and recalling information",
            "Strong ability to connect past experiences with new learning",
            "Good at recognizing patterns and sequences",
            "Builds strong mental models and associations"
        ),
        weaknesses = listOf(
            "May rely too heavily on memorization over understanding",
            "Can struggle when information isn't presented sequentially",
            "Might find it challenging to think outside established patterns"
        ),
        areasToImprove = listOf(
            "Practice applying knowledge in new and different contexts",
            "Develop creative thinking skills alongside memory work",
            "Learn to break down complex information into manageable chunks",
            "Explore different learning strategies beyond memorization"
        )
    ),
    "Focus Finder" to CognitiveProfile(
        name = "Focus Finder",
        strengths = listOf(
            "Strong attention to detail and concentration",
            "Excellent at spotting patterns and inconsistencies",
            "Good at maintaining focus on tasks",
            "Thorough and methodical approach to learning"
        ),
        weaknesses = listOf(
            "May get caught up in details and miss the bigger picture",
            "Can struggle with frequent context switching",
            "Might find it difficult to work in distracting environments"
        ),
        areasToImprove = listOf(
            "Practice switching between different tasks smoothly",
            "Develop strategies for maintaining focus in various environments",
            "Learn to balance detail-oriented work with broader perspectives",
            "Build techniques for managing distractions"
        )
    ),
    "Problem Solver" to CognitiveProfile(
        name = "Problem Solver",
        strengths = listOf(
            "Excellent analytical and strategic thinking",
            "Strong ability to plan and organize solutions",
            "Good at breaking down complex problems",
            "Systematic approach to challenges"
        ),
        weaknesses = listOf(
            "May overthink simple solutions",
            "Can struggle with ambiguous situations",
            "Might find it difficult to act without a complete plan"
        ),
        areasToImprove = listOf(
            "Practice working with incomplete information",
            "Develop comfort with ambiguity and uncertainty",
            "Learn to balance planning with action",
            "Explore creative problem-solving approaches"
        )
    ),
    "Perception Pro" to CognitiveProfile(
        name = "Perception Pro",
        strengths = listOf(
            "Strong visual and spatial awareness",
            "Excellent pattern recognition abilities",
            "Good at understanding relationships between concepts",
            "Creative approach to learning"
        ),
        weaknesses = listOf(
            "May struggle with purely verbal or written information",
            "Can find it difficult to explain thinking processes",
            "Might overlook sequential details"
        ),
        areasToImprove = listOf(
            "Develop verbal and written communication skills",
            "Practice explaining thought processes step by step",
            "Learn to combine visual and verbal learning strategies",
            "Build sequential thinking skills"
        )
    ),
    "Language Leader" to CognitiveProfile(
        name = "Language Leader",
        strengths = listOf(
            "Strong verbal and written communication",
            "Excellent at expressing ideas and concepts",
            "Good at understanding and using language",
            "Effective at explaining complex topics"
        ),
        weaknesses = listOf(
            "May rely too heavily on verbal explanation",
            "Can struggle with visual or spatial tasks",
            "Might find it challenging to learn through physical activity"
        ),
        areasToImprove = listOf(
            "Develop visual and spatial thinking skills",
            "Practice learning through physical activities",
            "Learn to combine verbal with other learning styles",
            "Build non-verbal communication skills"
        )
    ),
    "Active Learner" to CognitiveProfile(
        name = "Active Learner",
        strengths = listOf(
            "Learns best through hands-on experience",
            "Strong kinesthetic awareness",
            "Good at physical problem-solving",
            "Excellent at learning by doing"
        ),
        weaknesses = listOf(
            "May struggle with passive learning situations",
            "Can find it difficult to sit still for long periods",
            "Might have trouble with abstract concepts"
        ),
        areasToImprove = listOf(
            "Develop strategies for learning in passive situations",
            "Practice focusing during sedentary activities",
            "Learn to connect physical experience with abstract concepts",
            "Build skills for traditional classroom learning"
        )
    )
)

fun determineProfile(answers: Map<String, Int>): String {
    val maxCount = answers.values.maxOrNull() ?: 0
    val topProfiles = answers.filter { it.value == maxCount }.keys
    
    return when {
        topProfiles.size > 1 -> "${topProfiles.first()} & ${topProfiles.last()}"
        topProfiles.isEmpty() -> "Undefined"
        else -> topProfiles.first()
    }
} 