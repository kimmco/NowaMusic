package com.cokimutai.nowamusic.ui

import android.content.ComponentName
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C.TRACK_TYPE_TEXT
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.cokimutai.nowamusic.ContextCover
import com.cokimutai.nowamusic.service.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val contextCover: ContextCover,
    val playerView: PlayerView
    ): ViewModel() {

    private val context = contextCover.supplyCtx()
    private lateinit var controllerFuture: ListenableFuture<MediaController>

    private val controller: MediaController?
        get() = if(controllerFuture.isDone) controllerFuture.get() else null

    private val _musicItemMediaListState = MutableStateFlow<List<MediaItem>>(emptyList())
    val musicItemMediaListState: StateFlow<List<MediaItem>> get() = _musicItemMediaListState
    private val musicList = mutableListOf<MediaItem>()

   // private val _player = MutableStateFlow<PlayerView?>(null)
    //val player: StateFlow<PlayerView?> = _player

        @UnstableApi
        fun initializeController() {
            controllerFuture =
                MediaController.Builder(
                    context.applicationContext,
                    SessionToken(
                        context.applicationContext,
                        ComponentName(
                            context.applicationContext,
                            PlaybackService::class.java
                        )
                    )
                )
                    .buildAsync()
            controllerFuture.addListener( { setController() }, MoreExecutors.directExecutor()   )
        }

    @UnstableApi
    private fun setController() {
        val controller = this.controller ?: return

        playerView.player = controller

        updateCurrentPlaylistUI()
        updateMediaMetadataUI(controller.mediaMetadata)
        playerView.setShowSubtitleButton(controller.currentTracks.isTypeSupported(TRACK_TYPE_TEXT))

        controller.addListener(
            object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    updateMediaMetadataUI(mediaItem?.mediaMetadata ?: MediaMetadata.EMPTY)
                }

                override fun onTracksChanged(tracks: Tracks) {
                    playerView.setShowSubtitleButton(tracks.isTypeSupported(TRACK_TYPE_TEXT))
                }
            }
        )
    }

    private fun updateCurrentPlaylistUI(){
        val controller = this.controller ?: return
        musicList.clear()
        for (i in 0 until controller.mediaItemCount) {
            musicList.add(controller.getMediaItemAt(i))
        }
        _musicItemMediaListState.value = musicList.toList()
    }

    private fun updateMediaMetadataUI(mediaMetadata: MediaMetadata) {
        val title: CharSequence = mediaMetadata.title ?: ""
    }

    @UnstableApi
    fun setPlay(position: Int) {
        val controller = this.controller ?: return
        Log.d("AJAB", "EMPTY")
        if (controller.currentMediaItemIndex == position) {
             controller.playWhenReady = !controller.playWhenReady
             if (controller.playWhenReady) {
                  playerView.hideController()
                }
            } else {
                controller.seekToDefaultPosition(/* mediaItemIndex= */ position)
            }
        }

}