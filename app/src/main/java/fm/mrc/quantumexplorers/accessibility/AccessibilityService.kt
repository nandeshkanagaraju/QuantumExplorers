package fm.mrc.quantumexplorers.accessibility

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessibilityService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var textToSpeech: TextToSpeech? = null
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    private val _isAccessibilityEnabled = MutableStateFlow(false)
    val isAccessibilityEnabled: StateFlow<Boolean> = _isAccessibilityEnabled

    init {
        initializeTextToSpeech()
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.getDefault()
                textToSpeech?.setSpeechRate(0.8f) // Slightly slower for better comprehension
                
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                })
            }
        }
    }

    fun speak(text: String, priority: AccessibilityPriority = AccessibilityPriority.NORMAL) {
        if (!isAccessibilityEnabled.value) return

        val utteranceId = UUID.randomUUID().toString()
        val queueMode = when (priority) {
            AccessibilityPriority.HIGH -> TextToSpeech.QUEUE_FLUSH
            AccessibilityPriority.NORMAL -> TextToSpeech.QUEUE_ADD
        }

        textToSpeech?.speak(text, queueMode, null, utteranceId)
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        _isSpeaking.value = false
    }

    fun toggleAccessibility() {
        _isAccessibilityEnabled.value = !_isAccessibilityEnabled.value
    }

    fun cleanup() {
        textToSpeech?.shutdown()
    }
}
