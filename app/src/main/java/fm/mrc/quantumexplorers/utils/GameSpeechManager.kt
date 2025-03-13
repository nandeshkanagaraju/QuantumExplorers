package fm.mrc.quantumexplorers.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

interface TextToSpeechManager {
    fun initialize(context: Context)
    fun speak(text: String)
    fun stop()
    fun shutdown()
    fun isInitialized(): Boolean
}

@Singleton
class GameSpeechManager @Inject constructor() : TextToSpeechManager {
    private var textToSpeech: TextToSpeech? = null
    private var isReady = false

    override fun initialize(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            isReady = status == TextToSpeech.SUCCESS
            textToSpeech?.let { tts ->
                if (isReady) {
                    val result = tts.setLanguage(Locale.US)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        isReady = false
                    }
                }
            }
        }

        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {}
            override fun onError(utteranceId: String?) {}
        })
    }

    override fun speak(text: String) {
        if (isReady) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
        }
    }

    override fun stop() {
        textToSpeech?.stop()
    }

    override fun shutdown() {
        textToSpeech?.shutdown()
        textToSpeech = null
        isReady = false
    }

    override fun isInitialized(): Boolean = isReady
} 