package fm.mrc.quantumexplorers.accessibility

import android.content.Context
import android.content.Intent
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.view.HapticFeedbackConstants
import android.view.View
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessibilityManager @Inject constructor(
    private val context: Context,
    private val ttsManager: TTSManager
) {
    private val _isAccessibilityEnabled = MutableStateFlow(false)
    val isAccessibilityEnabled: StateFlow<Boolean> = _isAccessibilityEnabled

    private val _isVoiceFeedbackEnabled = MutableStateFlow(false)
    val isVoiceFeedbackEnabled: StateFlow<Boolean> = _isVoiceFeedbackEnabled

    private val _isHighContrastEnabled = MutableStateFlow(false)
    val isHighContrastEnabled: StateFlow<Boolean> = _isHighContrastEnabled

    private val _isEnhancedTouchEnabled = MutableStateFlow(false)
    val isEnhancedTouchEnabled: StateFlow<Boolean> = _isEnhancedTouchEnabled

    private val _showTTSSetup = MutableStateFlow(false)
    val showTTSSetup: StateFlow<Boolean> = _showTTSSetup

    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun toggleAccessibility() {
        _isAccessibilityEnabled.value = !_isAccessibilityEnabled.value
        if (_isAccessibilityEnabled.value) {
            _isVoiceFeedbackEnabled.value = true
            _isHighContrastEnabled.value = true
            _isEnhancedTouchEnabled.value = true
            speak("Accessibility features enabled", true)
        } else {
            _isVoiceFeedbackEnabled.value = false
            _isHighContrastEnabled.value = false
            _isEnhancedTouchEnabled.value = false
            speak("Accessibility features disabled", true)
        }
    }

    fun toggleVoiceFeedback() {
        if (!ttsManager.isReady.value) {
            ttsManager.checkAndRequestTTS(context)
            return
        }
        
        _isVoiceFeedbackEnabled.value = !_isVoiceFeedbackEnabled.value
        if (_isVoiceFeedbackEnabled.value) {
            speak("Voice feedback enabled", true)
        }
    }

    fun speak(text: String, override: Boolean = false) {
        if (_isVoiceFeedbackEnabled.value) {
            ttsManager.speak(text, override)
        }
    }

    fun toggleHighContrast() {
        _isHighContrastEnabled.value = !_isHighContrastEnabled.value
        speak(
            if (_isHighContrastEnabled.value) "High contrast mode enabled"
            else "High contrast mode disabled"
        )
    }

    fun toggleEnhancedTouch() {
        _isEnhancedTouchEnabled.value = !_isEnhancedTouchEnabled.value
        speak(
            if (_isEnhancedTouchEnabled.value) "Enhanced touch mode enabled"
            else "Enhanced touch mode disabled"
        )
        if (_isEnhancedTouchEnabled.value) {
            vibrate()
        }
    }

    fun performHapticFeedback(view: View) {
        if (_isEnhancedTouchEnabled.value) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            vibrate()
        }
    }

    private fun vibrate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }

    fun cleanup() {
        ttsManager.shutdown()
    }

    fun startTTSSetup() {
        _showTTSSetup.value = true
    }

    fun completeTTSSetup() {
        _showTTSSetup.value = false
        _isVoiceFeedbackEnabled.value = true
        speak("Text-to-Speech setup complete. Voice feedback is now enabled.", true)
    }

    fun testTTSVoice(text: String) {
        ttsManager.speak(text, true)
    }

    fun handleTTSCheck(requestCode: Int, resultCode: Int) {
        when (resultCode) {
            TextToSpeech.Engine.CHECK_VOICE_DATA_PASS -> {
                speak("Text to speech is ready", true)
            }
            else -> {
                val intent = Intent()
                intent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }
}
