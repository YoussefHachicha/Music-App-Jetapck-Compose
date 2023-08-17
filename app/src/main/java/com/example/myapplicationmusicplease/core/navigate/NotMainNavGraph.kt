package com.example.myapplicationmusicplease.core.navigate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplicationmusicplease.song.presentation.bussines_logic.PlayerViewModel
import com.example.myapplicationmusicplease.song.presentation.ui.SongScreen

@Composable
fun NotMainNavGraph(
	navController: NavHostController
) {
	NavHost(
		navController = navController,
		startDestination = Routes.SongScreen.route,
	) {

		composable(
			route = Routes.SongScreen.route,
		) {

			val viewModel = viewModel { PlayerViewModel() }
			val state by viewModel.state.collectAsStateWithLifecycle()

			SongScreen(
				state = state,
				playerEvents = viewModel,
				onEvent = viewModel::onEvent,
			)
		}


//		composable(
//			route =Routes.HomeScreen.route,
//		) { backStackEntry ->
//
//
//
////			HomeScreen(
////				state = state,
////				onEvent = viewModel::onEvent,
////				childId = childId,
////				navigateToLesson = { lessonId ->
////					navController.navigate(Routes.Lesson.route + "/${childId}/${lessonId}") {
////						popUpTo(Routes.Home.route) {
////							inclusive = true
////						}
////					}
////				},
////				navigateBackToParentProfile = {
////					navController.navigate(Routes.ParentAccount.route) {
////						popUpTo(Routes.Home.route) {
////							inclusive = true
////						}
////					}
////				}
////			)
//		}
	}


}

