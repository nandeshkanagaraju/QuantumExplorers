import android.media.AudioAttributes
import android.media.SoundPool
import kotlinx.coroutines.flow.MutableStateFlow

class AudioEngine {
    private val soundPool: SoundPool
    private val beatFlow = MutableStateFlow(0f)
    private var currentBPM = 128f
    
    init {
        soundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
        
        loadSounds()
        startBeatLoop()
    }
    
    private fun loadSounds() {
        // Load collision sounds
        collisionSounds = mapOf(
            "light" to soundPool.load(context, R.raw.light_collision, 1),
            "medium" to soundPool.load(context, R.raw.medium_collision, 1),
            "heavy" to soundPool.load(context, R.raw.heavy_collision, 1)
        )
        
        // Load background music
        backgroundTrack = soundPool.load(context, R.raw.electronic_beat, 1)
    }
    
    fun playCollisionSound(atom1: Atom, atom2: Atom) {
        val collisionEnergy = calculateCollisionEnergy(atom1, atom2)
        val sound = when {
            collisionEnergy < 50f -> collisionSounds["light"]
            collisionEnergy < 100f -> collisionSounds["medium"]
            else -> collisionSounds["heavy"]
        }
        
        soundPool.play(
            sound ?: return,
            1f,
            1f,
            1,
            0,
            1f + (collisionEnergy / 100f - 1f).coerceIn(-0.5f, 0.5f)
        )
    }
} 