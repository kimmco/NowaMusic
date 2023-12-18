package com.cokimutai.nowamusic.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cokimutai.nowamusic.R

enum class HomeScreen() {
    NowaTabs(),
    Folders(),
    NowaMusic()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowaAppBar(
    currentScreen: HomeScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier

) {
    TopAppBar(
        title = { /* */ },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack){
                IconButton(onClick = navigateUp) { 
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button)
                    )
                    
                }
            }
        }
    )
}

@androidx.annotation.OptIn(UnstableApi::class) @OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun NowaMusicApp(
    exoViewModel: ExoPlayerViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = HomeScreen.valueOf(
        backStackEntry?.destination?.route ?: HomeScreen.NowaTabs.name
    )

    Scaffold(
        topBar = {
            NowaAppBar(
               // modifier = ,
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) {innerPadding ->
        val subItemMediaList by exoViewModel.subItemMediaList.collectAsState()
        if (subItemMediaList.isNotEmpty()) {
            val rememberedList by remember { mutableStateOf(subItemMediaList) }
            //  TabbedLayout(rememberedList)
            NavHost(
                navController = navController,
                startDestination = HomeScreen.NowaTabs.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = HomeScreen.NowaTabs.name) {
                    val parentEntry = remember(it) {
                        navController.getBackStackEntry("NowaTabs")
                    }
                    val parentViewModel = hiltViewModel<ExoPlayerViewModel>(parentEntry)
                    val mediaViewModel = hiltViewModel<MediaViewModel>(parentEntry)
                    TabbedLayout(
                        subItemMediaList,
                        parentViewModel,
                        onFolderClicked = {
                            mediaViewModel.initializeBrowser(it)
                            navController.navigate(HomeScreen.Folders.name)
                        }
                    )
                }
                composable(route = HomeScreen.Folders.name) {
                    val parentEntry = remember(it) {
                        navController.getBackStackEntry("NowaTabs")
                    }
                    val mediaViewModel = hiltViewModel<MediaViewModel>(parentEntry)
                    val playViewModel = hiltViewModel<PlayerViewModel>(parentEntry)
                    TrackListScreen(
                        mediaViewModel,
                        onTrackClicked = {
                            mediaViewModel.playMusic(it)
                            playViewModel.initializeController()
                          navController.navigate(HomeScreen.NowaMusic.name)
                        }
                    )
                }
                composable(route = HomeScreen.NowaMusic.name) { navBackStackEntry ->
                    val parentEntry = remember(navBackStackEntry) {
                        navController.getBackStackEntry("NowaTabs")
                    }
                    val mediaViewModel = hiltViewModel<MediaViewModel>(parentEntry)
                    val playerViewModel = hiltViewModel<PlayerViewModel>(parentEntry)
                    val folderMediaItems by mediaViewModel.subItemMediaListState.collectAsState()
                    PlayerScreen(
                        mediaViewModel,
                        onMusicClick = {
                            playerViewModel.setPlay(it)
                        }
                    )
                }
            }
        }
    }
}