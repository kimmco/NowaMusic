package com.cokimutai.nowamusic.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaBrowser
import com.cokimutai.nowamusic.ui.commons.DisplayFolders
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.launch



lateinit var browserFuture: ListenableFuture<MediaBrowser>
private val browser: MediaBrowser?
    get() = if (browserFuture.isDone && !browserFuture.isCancelled) browserFuture.get() else null



@ExperimentalFoundationApi
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabbedLayout(
    subItemMediaList: List<MediaItem>,
    viewModel: ExoPlayerViewModel,
    onFolderClicked: (String) -> Unit
   // mediaViewModel: MediaViewModel
    ) {

    //val viewModel: ExoPlayerViewModel = hiltNavGraphViewModel()
    //val mediaViewModel: MediaViewModel = viewModel()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isLoadMusic by remember { mutableStateOf(false) }
    val mediaList by viewModel.subItemMediaList.collectAsState()

  //  val musicMediaList by mediaViewModel.subItemMediaListState.collectAsState()
    var myList = mutableListOf<MediaItem>()


    val pagerState = rememberPagerState(subItemMediaList.size)

    LaunchedEffect(selectedTabIndex){
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress){
        if (!pagerState.isScrollInProgress){
           selectedTabIndex = pagerState.currentPage
        }

    }

    LaunchedEffect(key1 = true) {

   /*     mediaViewModel.viewModelScope.launch {
            viewModel.mediaBrowserState.collect { browser ->
                // Perform UI updates based on browser state
                // ...
            }
        }

       mediaViewModel.viewModelScope.launch {
            mediaViewModel.subItemMediaListState.collect { mySubItemMediaList ->
                // Update UI with subItemMediaList (e.g., update RecyclerView adapter)
                // ...
                myList = mySubItemMediaList.toMutableList()
            }
        }  */
    }

 //   if (isLoadMusic) LoadMusicItems(musicMediaList)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
           // if (musicClicked) LoadMusicItems()
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                   // .background(MaterialTheme.colorSc3heme.error)
                ,
               // backgroundColor = MaterialTheme.colorScheme.primary
            ) {
                subItemMediaList.forEachIndexed { index, mediaItem ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = {
                            selectedTabIndex = index
                            if (mediaItem.mediaMetadata.isPlayable != true) {
                                viewModel.pushPathStack(mediaItem)
                               // loadMusicItems(subItemMediaList)
                            }
                        },
                        text = { Text(mediaItem.mediaMetadata.title.toString()) }
                    )


                }
            }
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = pagerState,
                pageCount = subItemMediaList.size
            ) { _ ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        items(items = mediaList, key = { mediaItem -> mediaItem.mediaId}) { mediaItem ->

                            DisplayFolders(
                                mediaItem = mediaItem,
                                onClick = { onFolderClicked(mediaItem.mediaId) }
                            )


                        }

                    }
                }
                
            }
        }
    }
}


@Composable
fun LoadMusicItems(media: List<MediaItem>) {


}
