package com.cokimutai.nowamusic.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import com.cokimutai.nowamusic.ui.commons.DisplayFolders
import com.cokimutai.nowamusic.ui.commons.ShowMediaItems

@Composable
fun TrackListScreen(
   // mediaId: String,
    mediaViewModel: MediaViewModel,
    onTrackClicked: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) { 
    val folderMediaItems by mediaViewModel.subItemMediaListState.collectAsState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Rock")
        Divider(
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Box(
            modifier = Modifier
            .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ){
                items(folderMediaItems.size){ index ->
                    val mediaItem = folderMediaItems[index]
                    ShowMediaItems(
                        index = index,
                        mediaItem = mediaItem,
                        onTrackClicked = onTrackClicked
                    )
                }

            }

        }
    }

}