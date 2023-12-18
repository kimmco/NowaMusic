package com.cokimutai.nowamusic.ui

import android.content.ComponentName
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.cokimutai.nowamusic.ContextCover
import com.cokimutai.nowamusic.service.PlaybackService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val contextCover: ContextCover,
   // val exoPlayer: Player,
) : ViewModel() {

    private val context = contextCover.supplyCtx()
    private val _mediaBrowserState = MutableStateFlow<MediaBrowser?>(null)
    val mediaBrowserState: StateFlow<MediaBrowser?> get() = _mediaBrowserState

    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    private val browser: MediaBrowser?
        get() = if (browserFuture.isDone) browserFuture.get() else null

    private val _subItemMediaListState = MutableStateFlow<List<MediaItem>>(emptyList())
    val subItemMediaListState: StateFlow<List<MediaItem>> get() = _subItemMediaListState
    private val folderList = mutableListOf<MediaItem>()



    init {
      //  exoPlayer.prepare()
    }

    fun initializeBrowser(selectedItemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            browserFuture =
                MediaBrowser.Builder(
                    context,
                    SessionToken(context, ComponentName(context, PlaybackService::class.java))
                )
                    .buildAsync()
          //  _mediaBrowserState.value = browser
            browserFuture.addListener({ displayFolder(selectedItemId) }, ContextCompat.getMainExecutor(context))

           // displayFolder(selectedItemId)
        }
    }

    fun playMusic(position : Int){
        val browser = this.browser ?: return
        browser.setMediaItems(
            _subItemMediaListState.value,
        /* startIndex= */ position,
        /* startPositionMs= */ C.TIME_UNSET
            )
        browser.shuffleModeEnabled = false
        browser.prepare()
        browser.play()
        browser.sessionActivity?.send()
    }

    private fun displayFolder(id: String) {
        val mbrowser = browser ?: return

        val mediaItemFuture = mbrowser!!.getItem(id)
        val childrenFuture =
            mbrowser.getChildren(id, /* page= */ 0, /* pageSize= */ Int.MAX_VALUE, /* params= */ null)

        viewModelScope.launch(Dispatchers.Main) {
            try {
                mediaItemFuture.addListener(
                    {
                    val result  = mediaItemFuture.get()!!
                },
                    ContextCompat.getMainExecutor(context)
            )

                childrenFuture.addListener(
                    {
                        folderList.clear()
                        val childrenResult = childrenFuture.get()!!
                        val children = childrenResult.value!!
                        folderList.addAll(children)

                        _subItemMediaListState.value = folderList.toList()
                },
                    ContextCompat.getMainExecutor(context   )
                )

            } catch (e: Exception) {
                // Handle exceptions
                e.printStackTrace()
            }
        }
    }
}