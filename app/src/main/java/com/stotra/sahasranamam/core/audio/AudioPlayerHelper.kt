package com.stotra.sahasranamam.core.audio

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayerHelper @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener {

    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var onPlaybackStateChanged: ((Boolean) -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())
    private var watchdogRunnable: Runnable? = null
    private var isTtsSpeakingActive = false

    init {
        initTts()
    }

    private fun initTts() {
        try {
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        tts = TextToSpeech(context, this)
        isTtsReady = false
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("hi", "IN"))
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsReady = true
            } else {
                tts?.setLanguage(Locale.getDefault())
                isTtsReady = true
            }

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    isTtsSpeakingActive = true
                    watchdogRunnable?.let { handler.removeCallbacks(it) }
                    onPlaybackStateChanged?.invoke(true)
                }

                override fun onDone(utteranceId: String?) {
                    isTtsSpeakingActive = false
                    onPlaybackStateChanged?.invoke(false)
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    isTtsSpeakingActive = false
                    onPlaybackStateChanged?.invoke(false)
                }
            })
        }
    }

    fun playVerse(
        assetPath: String?,
        sanskritText: String,
        audioStartMs: Long,
        audioEndMs: Long,
        speed: Float,
        onStateChange: (Boolean) -> Unit
    ) {
        this.onPlaybackStateChanged = onStateChange
        stop()

        // Check if custom audio asset file exists in assets
        val hasAsset = !assetPath.isNullOrBlank() && try {
            context.assets.open(assetPath).close()
            true
        } catch (e: Exception) {
            false
        }

        if (hasAsset) {
            try {
                val afd = context.assets.openFd(assetPath!!)
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    afd.close()
                    prepare()
                    try {
                        playbackParams = playbackParams.setSpeed(speed)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (audioStartMs > 0) {
                        seekTo(audioStartMs.toInt())
                    }
                    start()

                    setOnCompletionListener {
                        onStateChange(false)
                        stop()
                    }
                }
                onStateChange(true)
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // TTS audio voice synthesis for Devanagari Sanskrit text
        if (isTtsReady && tts != null) {
            tts?.setSpeechRate(speed)
            val cleanText = sanskritText
                .replace(Regex("[^\\u0900-\\u097F\\s]"), " ")
                .replace(Regex("[\\u0951\\u0952\\u0953\\u0954]"), "")
                .replace(Regex("\\s+"), " ")
                .trim()

            if (cleanText.isNotEmpty()) {
                isTtsSpeakingActive = false
                val result = tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "shloka_tts")
                if (result == TextToSpeech.SUCCESS) {
                    onStateChange(true)
                    // Watchdog: If onStart is not called within 1 second, the TTS service likely died silently
                    watchdogRunnable?.let { handler.removeCallbacks(it) }
                    watchdogRunnable = Runnable {
                        if (!isTtsSpeakingActive) {
                            initTts()
                            onStateChange(false)
                        }
                    }
                    handler.postDelayed(watchdogRunnable!!, 1000)
                } else {
                    onStateChange(false)
                    initTts()
                }
            } else {
                onStateChange(false)
            }
        } else {
            onStateChange(false)
        }
    }

    fun stop() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
            mediaPlayer = null

            if (tts?.isSpeaking == true) {
                tts?.stop()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        onPlaybackStateChanged?.invoke(false)
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
