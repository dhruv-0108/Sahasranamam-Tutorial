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
            // Try Sanskrit first for correct pronunciation rules (no Schwa deletion)
            val saLocale = Locale("sa", "IN")
            var result = tts?.setLanguage(saLocale)
            
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                // Explicitly set Sanskrit voice if available on the device
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    try {
                        val voices = tts?.voices
                        val saVoice = voices?.find { voice -> 
                            voice.locale.language.equals("sa", ignoreCase = true)
                        }
                        if (saVoice != null) {
                            tts?.setVoice(saVoice)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                isTtsReady = true
            } else {
                // Fallback to Hindi
                result = tts?.setLanguage(Locale("hi", "IN"))
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true
                } else {
                    tts?.setLanguage(Locale.getDefault())
                    isTtsReady = true
                }
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
            val cleanText = preprocessSanskritForTts(sanskritText)

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

    private fun preprocessSanskritForTts(text: String): String {
        // 1. Replace Sanskrit punctuation with speech pauses
        var result = text
            .replace("॥", ".")
            .replace("।", ",")

        // 2. Clean up digits and other non-Devanagari characters (except space, period, comma)
        result = result
            .replace(Regex("[०-९0-9]"), " ")
            .replace(Regex("[^\\u0900-\\u097F\\s.,]"), " ")
            .replace(Regex("[\\u0951\\u0952\\u0953\\u0954]"), "") // remove Vedic accents
            .replace(Regex("\\s+"), " ")
            .trim()

        // 3. Expand Anusvara (ं) to homorganic nasals based on the following consonant
        result = result.replace(Regex("ं(?=\\s*[कखगघ])"), "ङ्")
        result = result.replace(Regex("ं(?=\\s*[चछजझ])"), "ञ्")
        result = result.replace(Regex("ं(?=\\s*[टठडढ])"), "ण्")
        result = result.replace(Regex("ं(?=\\s*[तथदध])"), "न्")
        result = result.replace(Regex("ं(?=\\s*[पफबभम])"), "म्")
        result = result.replace(Regex("ं(?=\\s|$|[.,])"), "म्")

        // 4. Expand Visarga (ः) to vocalized echo of the preceding vowel
        result = result
            .replace("ाः", "ाहा")
            .replace("िः", "िहि")
            .replace("ीः", "ीही")
            .replace("ुः", "ुहु")
            .replace("ूः", "ूहू")
            .replace("ेः", "ेहे")
            .replace("ोः", "ोहो")
            .replace("ैः", "ैहै")
            .replace("ौः", "ौहौ")
            .replace("ः", "ह")

        // 5. Apply Sanskrit Syllabification to force clear akshara-level pronunciation and prevent slurring
        result = syllabifySanskrit(result)

        // 6. Clean up consecutive periods/commas and spaces
        result = result.replace(Regex("\\s+"), " ")
        result = result.replace(Regex("\\s+\\."), ".")
        result = result.replace(Regex("\\s+,"), ",")
        result = result.replace(Regex("\\.+"), ".")
        result = result.replace(Regex(",+"), ",")
        return result.replace(Regex("\\s+"), " ").trim()
    }

    private fun syllabifySanskrit(word: String): String {
        val consonants = "कखगघङचछजझञटठडढणतथदधनपफबभमयरलवशषसह"
        val matras = "ािीुूृॄेैोौ"
        val halant = '्'
        val anusvara = 'ं'
        val visarga = 'ः'

        val tokens = mutableListOf<Pair<Char, String>>()
        val chars = word.toCharArray()
        val n = chars.size
        var i = 0
        while (i < n) {
            val char = chars[i]
            if (consonants.contains(char)) {
                // Check if followed by halant
                if (i + 1 < n && chars[i + 1] == halant) {
                    tokens.add(Pair('C', char.toString() + halant))
                    i += 2
                } else {
                    var valStr = char.toString()
                    i++
                    while (i < n && (matras.contains(chars[i]) || chars[i] == anusvara || chars[i] == visarga)) {
                        valStr += chars[i]
                        i++
                    }
                    tokens.add(Pair('V', valStr))
                }
            } else if (matras.contains(char) || char == anusvara || char == visarga) {
                tokens.add(Pair('V', char.toString()))
                i++
            } else {
                tokens.add(Pair('P', char.toString()))
                i++
            }
        }

        val syllables = mutableListOf<String>()
        val tempC = mutableListOf<String>()

        for (token in tokens) {
            val type = token.first
            val value = token.second
            when (type) {
                'P' -> {
                    if (tempC.isNotEmpty()) {
                        syllables.add(tempC.joinToString(""))
                        tempC.clear()
                    }
                    syllables.add(value)
                }
                'V' -> {
                    if (tempC.isNotEmpty()) {
                        syllables.add(tempC.joinToString("") + value)
                        tempC.clear()
                    } else {
                        syllables.add(value)
                    }
                }
                'C' -> {
                    // Halant consonant
                    val lastIndex = syllables.lastIndex
                    if (lastIndex >= 0 && !listOf(" ", ",", ".", "।", "॥", "\n", "\t").contains(syllables[lastIndex])) {
                        syllables[lastIndex] = syllables[lastIndex] + value
                    } else {
                        tempC.add(value)
                    }
                }
            }
        }

        if (tempC.isNotEmpty()) {
            syllables.add(tempC.joinToString(""))
        }

        val result = mutableListOf<String>()
        val pendingWord = mutableListOf<String>()

        for (s in syllables) {
            if (s.isEmpty()) continue
            if (s.contains(Regex("[\\s.,।॥]"))) {
                if (pendingWord.isNotEmpty()) {
                    result.add(pendingWord.joinToString("-"))
                    pendingWord.clear()
                }
                result.add(s)
            } else {
                pendingWord.add(s)
            }
        }
        if (pendingWord.isNotEmpty()) {
            result.add(pendingWord.joinToString("-"))
        }

        return result.joinToString("")
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
