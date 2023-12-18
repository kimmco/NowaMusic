package com.cokimutai.nowamusic.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.cokimutai.nowamusic.R
import com.cokimutai.nowamusic.ui.commons.ShowMediaItems

@OptIn(UnstableApi::class) @Composable
fun PlayerScreen(
    mediaViewModel: MediaViewModel,
    onMusicClick: (Int) -> Unit = {},
    playViewModel: PlayerViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val folderMediaItems by mediaViewModel.subItemMediaListState.collectAsState()
    val musicPlayerView = playViewModel.playerView
    //by playViewModel.player.collectAsState()


    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier
                 .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

                AndroidView(
                    factory = { context ->
                        musicPlayerView.also {

                            it.player = musicPlayerView.player
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f)
                )

            Divider(
                thickness = 2.dp,
                modifier = Modifier.height(2.dp)
            )

            Row( modifier = Modifier.weight(1f, false)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        items(folderMediaItems.size){ index ->
                            val mediaItem = folderMediaItems[index]
                            ShowMediaItems(
                                index = index,
                                mediaItem = mediaItem,
                                onTrackClicked = onMusicClick
                            )
                        }
                    }
                }
            }

        }
    }
}