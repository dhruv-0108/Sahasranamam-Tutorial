import re

with open("app/src/main/java/com/stotra/sahasranamam/core/audio/AudioPlayerHelper.kt", "r") as f:
    content = f.read()

# Add imports for Handler
if "import android.os.Handler" not in content:
    content = content.replace("import android.media.MediaPlayer", "import android.media.MediaPlayer\nimport android.os.Handler\nimport android.os.Looper")

# Add watchdog variable
old_init_vars = """    private var isTtsReady = false
    private var onPlaybackStateChanged: ((Boolean) -> Unit)? = null

    init {"""
new_init_vars = """    private var isTtsReady = false
    private var onPlaybackStateChanged: ((Boolean) -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())
    private var watchdogRunnable: Runnable? = null
    private var isTtsSpeakingActive = false

    init {"""
content = content.replace(old_init_vars, new_init_vars)

# Update UtteranceProgressListener
old_listener = """            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    onPlaybackStateChanged?.invoke(true)
                }

                override fun onDone(utteranceId: String?) {
                    onPlaybackStateChanged?.invoke(false)
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    onPlaybackStateChanged?.invoke(false)
                }
            })"""
new_listener = """            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
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
            })"""
content = content.replace(old_listener, new_listener)

# Update speak logic
old_speak = """            if (cleanText.isNotEmpty()) {
                val result = tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "shloka_tts")
                if (result == TextToSpeech.SUCCESS) {
                    onStateChange(true)
                } else {
                    onStateChange(false)
                    initTts()
                }
            } else {"""
new_speak = """            if (cleanText.isNotEmpty()) {
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
            } else {"""
content = content.replace(old_speak, new_speak)

with open("app/src/main/java/com/stotra/sahasranamam/core/audio/AudioPlayerHelper.kt", "w") as f:
    f.write(content)

