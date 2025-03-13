package fm.mrc.quantumexplorers.ui.screens

import android.content.Intent
import android.provider.Settings
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fm.mrc.quantumexplorers.accessibility.AccessibilityManager
import fm.mrc.quantumexplorers.accessibility.TTSManager

@Composable
fun TTSSetupScreen(
    accessibilityManager: AccessibilityManager,
    onSetupComplete: () -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    val steps = listOf(
        "Check TTS Engine",
        "Install Language Data",
        "Configure Settings",
        "Test Voice"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Text-to-Speech Setup",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Progress indicator
        LinearProgressIndicator(
            progress = { (currentStep + 1) / steps.size.toFloat() },
            modifier = Modifier.fillMaxWidth()
        )

        // Current step title
        Text(
            text = "Step ${currentStep + 1}: ${steps[currentStep]}",
            style = MaterialTheme.typography.titleLarge
        )

        // Step content
        when (currentStep) {
            0 -> CheckTTSEngineStep(
                onNext = { currentStep++ }
            )
            1 -> InstallLanguageStep(
                onNext = { currentStep++ }
            )
            2 -> ConfigureSettingsStep(
                onNext = { currentStep++ }
            )
            3 -> TestVoiceStep(
                accessibilityManager = accessibilityManager,
                onComplete = onSetupComplete
            )
        }
    }
}

@Composable
private fun CheckTTSEngineStep(
    onNext: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "First, let's check if you have a Text-to-Speech engine installed.",
            style = MaterialTheme.typography.bodyLarge
        )

        Button(
            onClick = {
                val intent = Intent()
                intent.action = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Check TTS Engine")
        }

        Button(
            onClick = {
                val intent = Intent()
                intent.action = "com.android.settings.TTS_SETTINGS"
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Settings, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open TTS Settings")
        }

        OutlinedButton(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("I've installed a TTS Engine")
        }
    }
}

@Composable
private fun InstallLanguageStep(
    onNext: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Now, let's make sure you have the required language data installed.",
            style = MaterialTheme.typography.bodyLarge
        )

        Button(
            onClick = {
                val intent = Intent()
                intent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Download, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Install Language Data")
        }

        OutlinedButton(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Language Data is Installed")
        }
    }
}

@Composable
private fun ConfigureSettingsStep(
    onNext: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Let's configure your TTS settings for the best experience.",
            style = MaterialTheme.typography.bodyLarge
        )

        // Settings instructions
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Recommended Settings:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("1. Set your preferred TTS engine as default")
                Text("2. Set speech rate to Normal or Slower")
                Text("3. Set pitch to Normal")
                Text("4. Test the voice output")
            }
        }

        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Settings, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open Accessibility Settings")
        }

        OutlinedButton(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Settings Configured")
        }
    }
}

@Composable
private fun TestVoiceStep(
    accessibilityManager: AccessibilityManager,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Finally, let's test if everything is working correctly.",
            style = MaterialTheme.typography.bodyLarge
        )

        Button(
            onClick = {
                accessibilityManager.testTTSVoice("Hello! This is a test of the Text-to-Speech system.")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.VolumeUp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Test Voice")
        }

        // Success section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Did you hear the test message?",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onComplete,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Yes, it works!")
                    }

                    OutlinedButton(
                        onClick = { /* Reset to first step */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("No, start over")
                    }
                }
            }
        }
    }
} 