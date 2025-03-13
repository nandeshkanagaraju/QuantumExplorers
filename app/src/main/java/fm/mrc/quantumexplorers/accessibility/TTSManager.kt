package fm.mrc.quantumexplorers.accessibility

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TTSManager @Inject constructor(
    private val context: Context
) {
    private var tts: TextToSpeech? = null
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    init {
        initializeTTS()
    }

    private fun initializeTTS() {
        try {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale.getDefault())
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTSManager", "Language not supported")
                        _isReady.value = false
                        return@TextToSpeech
                    }
                    
                    // Configure TTS settings
                    tts?.setPitch(1.0f)
                    tts?.setSpeechRate(0.8f)
                    _isReady.value = true
                    
                    // Test TTS
                    speak("Text to speech initialized", true)
                } else {
                    Log.e("TTSManager", "TTS Initialization failed")
                    _isReady.value = false
                }
            }
        } catch (e: Exception) {
            Log.e("TTSManager", "Error initializing TTS", e)
            _isReady.value = false
        }
    }

    fun speak(text: String, override: Boolean = false) {
        try {
            // Ensure audio is properly configured
            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.isSpeakerphoneOn = true
            
            // Set volume to audible level if too low
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (currentVolume < 3) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2,
                    0
                )
            }

            val queueMode = if (override) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            val params = Bundle()
            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
            
            tts?.speak(text, queueMode, params, "MessageId_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("TTSManager", "Error speaking text", e)
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("TTSManager", "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        try {
            tts?.shutdown()
            tts = null
            _isReady.value = false
        } catch (e: Exception) {
            Log.e("TTSManager", "Error shutting down TTS", e)
        }
    }

    fun checkAndRequestTTS(context: Context) {
        val checkIntent = Intent()
        checkIntent.action = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
        checkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        
        try {
            context.startActivity(checkIntent)
        } catch (e: Exception) {
            Log.e("TTSManager", "Error checking TTS data", e)
            // Try to install TTS data
            val installIntent = Intent()
            installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(installIntent)
            } catch (e: Exception) {
                Log.e("TTSManager", "Error installing TTS data", e)
            }
        }
    }
} 