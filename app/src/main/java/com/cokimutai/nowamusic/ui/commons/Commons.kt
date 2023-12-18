package com.cokimutai.nowamusic.ui.commons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem

@Composable
fun DisplayFolders(
    mediaItem: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Text(
        text = AnnotatedString(mediaItem.mediaMetadata.title.toString()),
        modifier = Modifier
            .fillMaxWidth()
            .clickable (
                onClick = onClick
            )   /*{
                if (mediaItem.mediaMetadata.isPlayable == true) {
                 //   mediaViewModel.initializeBrowser(mediaItem.mediaId)

                  //  isLoadMusic = true

                } else {

                    // musicClicked = true
                }
            } */
    )
    Spacer(modifier = Modifier.height(18.dp))

}

@Composable
fun ShowMediaItems(
    index: Int,
    mediaItem: MediaItem,
    onTrackClicked: (Int) -> Unit = {}
) {
    Text(
        text = AnnotatedString(mediaItem.mediaMetadata.title.toString()),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onTrackClicked(index) }
            )
    )
    Spacer(modifier = Modifier.height(18.dp))
    Divider(
        thickness = 1.dp,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}
