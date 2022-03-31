package com.radyopilipinomediagroup.radyo_now.utils

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer

class AudioPlayer(var context: Context) {

    var audioPlayer = MediaPlayer()
    //Interface Instance
    var callback: MediaPlayerCallBack? = null
    var isReady = false

    fun initMediaType(streamUrl: String?) {
        audioPlayer.reset()
        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        audioPlayer.setDataSource(streamUrl)
        audioPlayer.prepareAsync()
        initMediaPlayer()
    }

    fun startAudioPlayer() {
        audioPlayer.start()
    }

    fun pauseAudioPlayer() {
        audioPlayer.pause()
    }

    fun resetAudioPlayer() {
        audioPlayer.reset()
    }

    fun getDuration() : Int {
        return audioPlayer.duration
    }

    fun getCurrentPosition() : Int {
        return audioPlayer.currentPosition
    }

    fun seekToPosition(progress: Int) {
        audioPlayer.seekTo(progress)
    }

    private fun initMediaPlayer() {
        try {
               audioPlayer.setOnErrorListener { mp, _, _ ->
                mp.reset()
                false
            }

            audioPlayer.setOnPreparedListener {
                try {
                    callback?.onPrepared()
                } catch (e: Exception) {
                    print(e.message)
                }
            }

            audioPlayer.setOnBufferingUpdateListener { _: MediaPlayer, progress: Int ->
                callback?.onBuffered(progress)
            }

            audioPlayer.setOnCompletionListener {
                callback?.onCompleted()
            }
        } catch (e: Exception) {
            log(e.message.toString())
        }
    }

    fun stopMediaPlayer() {
        try {
            if (audioPlayer.isPlaying) {
                audioPlayer.stop()
            }
        } catch (e: Exception) {}
    }

    interface MediaPlayerCallBack {
        fun onPrepared()
        fun onBuffered(progress: Int)
        fun onCompleted()
    }
}