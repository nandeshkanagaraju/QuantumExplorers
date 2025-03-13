package fm.mrc.quantumexplorers.utils

import android.speech.tts.TextToSpeech
import android.content.Context
import java.util.Locale
import javax.inject.Inject

class AudioSupport @Inject constructor(
    context: Context
) {
    private var textToSpeech: TextToSpeech? = null
    
    init {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.getDefault()
            }
        }
    }

    fun speak(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown() {
        textToSpeech?.shutdown()
    }
} 