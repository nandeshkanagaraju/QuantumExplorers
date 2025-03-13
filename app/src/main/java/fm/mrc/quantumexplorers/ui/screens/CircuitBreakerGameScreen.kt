package fm.mrc.quantumexplorers.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fm.mrc.quantumexplorers.ui.components.CircuitBreakerSimulation

@Composable
fun CircuitBreakerGameScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPoints by remember { mutableStateOf(0) }
    var showTutorial by remember { mutableStateOf(true) }
    var tutorialStep by remember { mutableStateOf(1) }
    var hasCompletedTutorial by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Circuit Breaker Simulation - let it handle the entire UI
        CircuitBreakerSimulation(
            modifier = Modifier.fillMaxSize(),
            onLevelComplete = { levelId -> 
                // Update points based on level completion
                currentPoints += (levelId * 10)
                
                // Show tutorial for next level if needed
                if (levelId == 1 && !hasCompletedTutorial) {
                    hasCompletedTutorial = true
                    showTutorial = true
                }
            }
        )
        
        // Tutorial overlay
        if (showTutorial) {
            val tutorialContent = when (tutorialStep) {
                1 -> "Welcome to Circuit Breaker! In this game, you'll learn how electrical circuits work by building them yourself."
                2 -> "This is your component palette. Tap on a component to select it, then place it on the grid."
                3 -> "Connect components by dragging wires between them. A complete circuit needs a power source (battery) and a load (like a light bulb)."
                4 -> "Use switches to control when current flows. Resistors limit current, and circuit breakers protect against overloads."
                5 -> "Complete each level's objective to earn points and advance to more complex circuits!"
                else -> ""
            }
            
            GameTutorialOverlay(
                step = tutorialStep,
                totalSteps = 5,
                content = tutorialContent,
                onNext = {
                    if (tutorialStep < 5) {
                        tutorialStep++
                    } else {
                        showTutorial = false
                        tutorialStep = 1
                    }
                },
                onSkip = {
                    showTutorial = false
                    tutorialStep = 1
                }
            )
        }
    }
}

@Composable
private fun GameTutorialOverlay(
    step: Int,
    totalSteps: Int,
    content: String,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Semi-transparent background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(enabled = false) { }
        )
        
        // Tutorial card
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Step $step of $totalSteps",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF6B4EFF)
                )
                
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onSkip) {
                        Text("Skip Tutorial")
                    }
                    
                    Button(onClick = onNext) {
                        Text(if (step < totalSteps) "Next" else "Finish")
                    }
                }
            }
        }
    }
} 