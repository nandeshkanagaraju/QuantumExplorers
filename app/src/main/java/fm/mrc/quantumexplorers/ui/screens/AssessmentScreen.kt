package fm.mrc.quantumexplorers.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fm.mrc.quantumexplorers.model.AssessmentOption
import fm.mrc.quantumexplorers.model.AssessmentQuestion
import fm.mrc.quantumexplorers.ui.theme.MonigueFont

@Composable
fun AssessmentScreen(
    onComplete: (Map<String, Int>) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val answers = remember { mutableStateMapOf<String, Int>() }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(
            onClick = onBackPressed,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Text(
            text = "COGNITIVE PROFILE",
            fontSize = 32.sp,
            fontFamily = MonigueFont,
            fontWeight = FontWeight.Normal,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        QuestionComponent(
            question = questions[currentQuestionIndex],
            onOptionSelected = { profile ->
                answers[profile] = (answers[profile] ?: 0) + 1
                if (currentQuestionIndex < questions.size - 1) {
                    currentQuestionIndex++
                } else {
                    onComplete(answers)
                }
            }
        )

        LinearProgressIndicator(
            progress = (currentQuestionIndex + 1) / questions.size.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}

@Composable
private fun QuestionComponent(
    question: AssessmentQuestion,
    onOptionSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = question.question,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        question.options.forEach { option ->
            Button(
                onClick = { onOptionSelected(option.profile) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B4EFF)
                )
            ) {
                Text(
                    text = option.text,
                    fontSize = 14.sp
                )
            }
        }
    }
}

private val questions = listOf(
    AssessmentQuestion(
        "You're in a giant maze! How do you find your way out?",
        listOf(
            AssessmentOption("Remember which paths you already took and retrace your steps", "Memory Master"),
            AssessmentOption("Look for clues and details that might help", "Focus Finder"),
            AssessmentOption("Plan ahead and think of the best strategy before moving", "Problem Solver"),
            AssessmentOption("Notice patterns in the walls or floor that lead the way", "Perception Pro"),
            AssessmentOption("Ask for directions or read signs to guide you", "Language Leader"),
            AssessmentOption("Move around quickly and test different paths", "Active Learner")
        )
    ),
    AssessmentQuestion(
        "You need to learn a new dance. What's your best approach?",
        listOf(
            AssessmentOption("Watch it a few times and remember the moves", "Memory Master"),
            AssessmentOption("Focus on small details in the steps", "Focus Finder"),
            AssessmentOption("Break it down into easy parts and follow a plan", "Problem Solver"),
            AssessmentOption("Notice how the rhythm and shapes fit together", "Perception Pro"),
            AssessmentOption("Listen to the song's beat and count the steps", "Language Leader"),
            AssessmentOption("Try the moves yourself and adjust as you go", "Active Learner")
        )
    ),
    AssessmentQuestion(
        "You're solving a tricky puzzle. How do you figure it out?",
        listOf(
            AssessmentOption("Think back to a similar puzzle you solved before", "Memory Master"),
            AssessmentOption("Carefully look at every piece before making a move", "Focus Finder"),
            AssessmentOption("Plan a few steps ahead before making a choice", "Problem Solver"),
            AssessmentOption("Rotate and test pieces to see how they fit", "Perception Pro"),
            AssessmentOption("Read the instructions or get a hint", "Language Leader"),
            AssessmentOption("Try moving pieces around until you find the right fit", "Active Learner")
        )
    ),
    AssessmentQuestion(
        "You're listening to a new story. How do you understand it best?",
        listOf(
            AssessmentOption("Remember key parts as the story moves along", "Memory Master"),
            AssessmentOption("Pay attention to important details in the plot", "Focus Finder"),
            AssessmentOption("Think about how the events connect and predict the ending", "Problem Solver"),
            AssessmentOption("Visualize the characters and places in your head", "Perception Pro"),
            AssessmentOption("Repeat the story out loud or explain it to someone", "Language Leader"),
            AssessmentOption("Act it out or move around while listening", "Active Learner")
        )
    ),
    AssessmentQuestion(
        "How do you like to learn new things?",
        listOf(
            AssessmentOption("By remembering examples and past experiences", "Memory Master"),
            AssessmentOption("By paying close attention to details", "Focus Finder"),
            AssessmentOption("By breaking problems into steps and solving them logically", "Problem Solver"),
            AssessmentOption("By recognizing patterns, shapes, or movement", "Perception Pro"),
            AssessmentOption("By talking about it or reading a story", "Language Leader"),
            AssessmentOption("By doing it myself and testing it out", "Active Learner")
        )
    )
) 