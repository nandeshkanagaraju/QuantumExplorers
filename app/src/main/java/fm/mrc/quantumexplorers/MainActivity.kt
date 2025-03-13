package fm.mrc.quantumexplorers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import fm.mrc.quantumexplorers.navigation.NavGraph
import fm.mrc.quantumexplorers.ui.theme.QuantumExplorersTheme
import fm.mrc.quantumexplorers.viewmodel.LearningProgressViewModel
import fm.mrc.quantumexplorers.accessibility.AccessibilityManager
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.content.Intent
import android.media.AudioManager
import android.content.Context

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var accessibilityManager: AccessibilityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize volume controls
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = AudioManager.MODE_NORMAL
        
        enableEdgeToEdge()
        setContent {
            QuantumExplorersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    val viewModel = hiltViewModel<LearningProgressViewModel>()
                    NavGraph(
                        navController = navController,
                        viewModel = viewModel,
                        accessibilityManager = accessibilityManager
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        accessibilityManager.handleTTSCheck(requestCode, resultCode)
    }

    override fun onDestroy() {
        super.onDestroy()
        accessibilityManager.cleanup()
    }
}