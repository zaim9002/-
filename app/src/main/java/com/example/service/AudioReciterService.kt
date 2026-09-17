package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class AudioReciterService(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPlayingId = MutableStateFlow<Int?>(null)
    val currentPlayingId: StateFlow<Int?> = _currentPlayingId.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val arabic = Locale("ar")
                val result = tts?.setLanguage(arabic)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setSpeechRate(0.88f)
                    isInitialized = true
                }
            }
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isPlaying.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isPlaying.value = false
                _currentPlayingId.value = null
            }

            override fun onError(utteranceId: String?) {
                _isPlaying.value = false
                _currentPlayingId.value = null
            }
        })
    }

    fun speak(dhikrId: Int, text: String) {
        if (!isInitialized && tts == null) return
        if (_isPlaying.value && _currentPlayingId.value == dhikrId) {
            stop()
            return
        }
        stop()
        _currentPlayingId.value = dhikrId
        _isPlaying.value = true
        val utteranceId = "Dhikr_$dhikrId"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _isPlaying.value = false
        _currentPlayingId.value = null
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
