package com.cokimutai.nowamusic


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import com.cokimutai.nowamusic.ui.ExoPlayerViewModel
import com.cokimutai.nowamusic.ui.NowaMusicApp
import com.cokimutai.nowamusic.ui.TabbedLayout
import com.cokimutai.nowamusic.ui.theme.NowaMusicTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //private val viewModel: ExoPlayerViewModel by viewModels()


    //val viewModel by hiltViewModel<ExoPlayerViewModel>()
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            NowaMusicTheme {
             /*   Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                  */
                NowaMusicApp()

               // }

            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MyApp(viewModel: ExoPlayerViewModel = viewModel()) {
        val subItemMediaList by viewModel.subItemMediaList.collectAsState()

        if (subItemMediaList.isNotEmpty()){
            val rememberedList by remember { mutableStateOf(subItemMediaList) }
          //  TabbedLayout(rememberedList)
        }/* else {
            Button(onClick = { TabbedLayout(subItemMediaList) }) {
                Text("Increment Counter")
            }
        } */


    }

    @Composable
    fun DisplayMusicTitles(viewModel: ExoPlayerViewModel = viewModel()) {
     // val gameUiState by gameViewModel.uiState.collectAsState()
        val subItemMediaList by viewModel.subItemMediaList.collectAsState()
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ){
            items(items = subItemMediaList, key = { mediaItem -> mediaItem.mediaId}) { mediaItem ->
                Text(
                    text = mediaItem.mediaMetadata.title.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (mediaItem.mediaMetadata.isPlayable != true) {
                                viewModel.pushPathStack(mediaItem)
                            } else {
                                Log.d("ContextCover", mediaItem.mediaId)
                                setContent {
                                    //TabbedLayout()
                                }
                            }
                        }
                )
                Spacer(modifier = Modifier.height(18.dp))

            }

        }

    }

}
