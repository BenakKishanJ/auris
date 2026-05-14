package com.example.auris // Make sure this matches your actual package name!

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.k2fsa.sherpa.onnx.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class TtsManager(private val context: Context) {
    private var tts: OfflineTts? = null
    private var audioTrack: AudioTrack? = null
    private var lastGeneratedAudio: GeneratedAudio? = null

    // 1. Initialize the Engine and copy files
    // 1. Initialize the Engine and copy files
    suspend fun initEngine() = withContext(Dispatchers.IO) {
        if (tts != null) return@withContext // Already initialized

        // Extract files from APK to physical device storage
        copyAssets("kokoro")

        val baseDir = File(context.filesDir, "kokoro").absolutePath

        // Configure Kokoro inside Sherpa-ONNX
        val config = OfflineTtsConfig(
            model = OfflineTtsModelConfig(
                kokoro = OfflineTtsKokoroModelConfig(
                    model = "$baseDir/model.onnx",
                    voices = "$baseDir/voices.bin",
                    tokens = "$baseDir/tokens.txt",
                    dataDir = "$baseDir/espeak-ng-data",
                    lengthScale = 1.0f, // Normal speed
                    dictDir = "",
                    lexicon = "" // Explicitly added for 1.13.1 API compatibility
                ),
                numThreads = 2, // Keeps it lightweight on mobile
                debug = true,   // FIXED: Changed from 1 to true
                provider = "cpu"
            )
        )

        // FIXED: OfflineTts now expects an AssetManager as the first parameter
        tts = OfflineTts(assetManager = null, config = config)
    }

    // 2. Generate the Audio Bytes
    suspend fun generateAudio(text: String, voiceId: Int = 0) = withContext(Dispatchers.IO) {
        val engine = tts ?: throw IllegalStateException("TTS Engine not initialized")
        // Voice ID 0 is the default American Female voice
        lastGeneratedAudio = engine.generate(text, sid = voiceId, speed = 1.0f)
    }

    // 3. Play the Audio Bytes
    suspend fun playAudio() = withContext(Dispatchers.IO) {
        val audio = lastGeneratedAudio ?: return@withContext

        // Reset the audio track if it's already playing
        audioTrack?.release()

        // Android's native audio streaming API
        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT) // Kokoro outputs Float arrays
                    .setSampleRate(audio.sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()
        audioTrack?.write(audio.samples, 0, audio.samples.size, AudioTrack.WRITE_BLOCKING)
    }

    // Recursive function to pull files out of the Assets folder
    private fun copyAssets(path: String) {
        val assets = context.assets.list(path) ?: return
        if (assets.isEmpty()) {
            val file = File(context.filesDir, path)
            if (file.exists()) return
            context.assets.open(path).use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
        } else {
            val dir = File(context.filesDir, path)
            if (!dir.exists()) dir.mkdirs()
            for (asset in assets) {
                copyAssets("$path/$asset")
            }
        }
    }
}