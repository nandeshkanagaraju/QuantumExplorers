package fm.mrc.quantumexplorers.model

data class CognitiveProfile(
    val name: String,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val areasToImprove: List<String>
)

data class AssessmentQuestion(
    val question: String,
    val options: List<AssessmentOption>
)

data class AssessmentOption(
    val text: String,
    val profile: String
) 