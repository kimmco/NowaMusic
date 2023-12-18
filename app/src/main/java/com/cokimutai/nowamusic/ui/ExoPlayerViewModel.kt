package com.cokimutai.nowamusic.ui

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.cokimutai.nowamusic.ContextCover
import com.cokimutai.nowamusic.service.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExoPlayerViewModel @Inject constructor(
    val contextCover: ContextCover,
    val exoPlayer: Player,
): ViewModel() {

    private val context = contextCover.supplyCtx()
    private val treePathStack : ArrayDeque<MediaItem> = ArrayDeque()

    // Create a StateFlow to represent the MediaBrowser
    private val _mediaBrowserState = MutableStateFlow<MediaBrowser?>(null)

    // Expose a StateFlow as a read-only property
    val mediaBrowserState: StateFlow<MediaBrowser?> = _mediaBrowserState



    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    private val browser: MediaBrowser?
        get() = if (browserFuture.isDone && !browserFuture.isCancelled) browserFuture.get() else null


    private val _subItemMediaList =  MutableStateFlow(emptyList<MediaItem>())
    val subItemMediaList: StateFlow<List<MediaItem>> = _subItemMediaList.asStateFlow()
    private val folderList = mutableListOf<MediaItem>()

    private val _triggerState = MutableStateFlow(Unit)
    val triggerState: StateFlow<Unit> = _triggerState

    init {
        initializeBrowser()
        exoPlayer.prepare()
    }

    fun triggerFunction() {
        _triggerState.value = Unit
    }


    fun initializeBrowser(){
        browserFuture =
            MediaBrowser.Builder(
                context,
                SessionToken(this.context, ComponentName(context, PlaybackService::class.java))
            )
                .buildAsync()
        browserFuture.addListener( {pushRoot(context) }, ContextCompat.getMainExecutor(context))

    }

    private fun pushRoot(context: Context){
        if(!treePathStack.isEmpty()) {
            return
        }
        val browser = browser ?: return
        val rootFuture = browser.getLibraryRoot(null)
        rootFuture.addListener(
            {
                val result: LibraryResult<MediaItem> = rootFuture.get()!!
                val root: MediaItem = result.value!!

                pushPathStack(root)
            },
            ContextCompat.getMainExecutor(context)
        )
    }

     fun pushPathStack(mediaItem: MediaItem) {
        treePathStack.addLast(mediaItem)
        displayChildrenList(treePathStack.last() )
    }


    private fun displayChildrenList(mediaItem: MediaItem) {

        val sharedBrowser = this.browser ?: return

           val childrenFuture =
            sharedBrowser.getChildren(
                mediaItem.mediaId,
                0,
                Int.MAX_VALUE,
                null
            )

            folderList.clear()
        childrenFuture.addListener(
            {
                val result = childrenFuture.get()!!
                val children = result.value!!
                folderList.addAll(children)
                _subItemMediaList.value = folderList.toList()
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    // Function to set the MediaBrowser using a ListenableFuture
    @OptIn(DelicateCoroutinesApi::class)
    fun setMediaBrowserListenableFuture(listenableFuture: ListenableFuture<MediaBrowser>) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Use withContext to safely get the result of ListenableFuture
                val mediaBrowser = withContext(Dispatchers.IO) {
                    listenableFuture.get()
                }
                // Update the StateFlow with the result
                _mediaBrowserState.value = mediaBrowser
            } catch (e: Exception) {
                // Handle exceptions if needed
                e.printStackTrace()
            }
        }
    }

}