package com.example.myapplicationmusicplease.song.presentation.bussines_logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.example.myapplicationmusicplease.song.domain.model.SongModel
import com.example.myapplicationmusicplease.song.domain.repository.SongRepository
import com.example.myapplicationmusicplease.song.player.MyPlayer
import com.example.myapplicationmusicplease.song.player.PlayerEvents
import com.example.myapplicationmusicplease.song.player.PlayerStates
import com.example.myapplicationmusicplease.core.utils.UnauthorizedException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayerViewModel() : KoinComponent, PlayerEvents, ViewModel() {
	private val repository: SongRepository by inject()
	private val myPlayer: MyPlayer by inject()

	private val coroutineScope = viewModelScope

	//	    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)
	private val _state = MutableStateFlow(PlayerState())
	val state: StateFlow<PlayerState>
		get() = _state.stateIn(
			scope = coroutineScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = PlayerState()
		)

	private var playbackStateJob: Job? = null

	init {
		getLesson()

	}

	private fun getLesson() {
		println("getLesson() called")
		if (_state.value.getSongs.isSuccess || _state.value.getSongs.isLoading) return

		_state.value = _state.value.copy(
			getSongs = _state.value.getSongs.copy(
				isLoading = true,
				isFailure = false
			),
		)

		coroutineScope.launch {
			println("API call started")
			repository.getSongs()
				.onSuccess { songs ->
					println("API call success")
					val newSongs = songs.toMutableList()
					_state.value = _state.value.copy(
						getSongs = _state.value.getSongs.copy(
							isSuccess = true,
							isLoading = false,//Bruh
						),
						songs = newSongs
					)
				}.onFailure { e ->
					println("API call failure")
					_state.value = _state.value.copy(
						getSongs = _state.value.getSongs.copy(
							isLoading = false,
							isFailure = true,
							isUnauthorized = e is UnauthorizedException,
						),
						errorMessage = e.message ?: "",
					)
				}
		}
	}


	fun onEvent(
		event: PlayerEvent,
	) {
		when (event) {
			is PlayerEvent.UpdateSelectedSong -> {

				onSongSelected(event.selectedSongIndex)
			}

			is PlayerEvent.UpdatePlaybackState -> {
				_state.value = _state.value.copy(
					playbackState = _state.value.playbackState.copy(
						currentPlaybackPosition = event.currentPlaybackPosition,
						currentTrackDuration = event.currentTrackDuration
					)
				)
			}

			is PlayerEvent.InitPlayer -> {
				coroutineScope.launch{
					if (_state.value.getSongs.isSuccess && _state.value.songs.isNotEmpty()) {
						val mediaItems = _state.value.songs.map { MediaItem.fromUri(it.url) }.toMutableList()
						println("Number of media items: ${mediaItems.size}")
						myPlayer.iniPlayer(mediaItems)
						observePlayerState()
					}
				}
			}


			is PlayerEvent.SetToRepeat -> {
				_state.value = _state.value.copy(isRepeat = event.isRepeat)
			}

			else -> {}
		}
	}

	private fun onSongSelected(index: Int) {
		if (_state.value.selectedSongIndex == -1) _state.value =
			_state.value.copy(isSongPlay = true)
		if (_state.value.selectedSongIndex == -1 || _state.value.selectedSongIndex != index) {

			val newSongs = _state.value.songs.map {
				it.copy(isSelected = false, state = PlayerStates.STATE_IDLE)
			}.toMutableList()

			_state.value = _state.value.copy(
				songs = newSongs,
				selectedSongIndex = index
			)
			setUpSong()
		}
	}

	private fun setUpSong() {
		if (!_state.value.isAutoSetOnPlay) myPlayer.setUpSong(
			index = _state.value.selectedSongIndex,
			isSongPlay = _state.value.isSongPlay,
		)
		_state.value = _state.value.copy(isAutoSetOnPlay = false)
	}


	private fun updateState(state: PlayerStates) {
		if (_state.value.selectedSongIndex != -1) {

			_state.value = _state.value.copy(
				isSongPlay = state == PlayerStates.STATE_PLAYING,
				songs = _state.value.songs.apply {
					this[_state.value.selectedSongIndex] =
						_state.value.songs[_state.value.selectedSongIndex].copy(
							state = state,
							isSelected = true
						)
				},
				selectedSongModel = null
			)

			_state.value = _state.value.copy(
				selectedSongModel = _state.value.songs[_state.value.selectedSongIndex]
			)

			updatePlaybackState(state)
			if (state == PlayerStates.STATE_NEXT_TRACK) {
				_state.value = _state.value.copy(isAutoSetOnPlay = true)
				viewModelScope.launch {
					onNextClick()
				}
			}
			if (state == PlayerStates.STATE_END) onSongSelected(0)
		}
	}

	private fun observePlayerState() {
		coroutineScope.launch {
			myPlayer.playerState.collect {
				updateState(it)//This updates the state of the player
			}
		}
	}

	private fun updatePlaybackState(state: PlayerStates) {
		playbackStateJob?.cancel()

		playbackStateJob = coroutineScope.launch {
			do {
				_state.value = _state.value.copy(
					playbackState = _state.value.playbackState.copy(
						currentPlaybackPosition = myPlayer.currentPlaybackPosition,
						currentTrackDuration = myPlayer.currentSongDuration
					)
				)
				delay(200)
			} while (state == PlayerStates.STATE_PLAYING && isActive)

		}
	}

	override  fun onPlayPauseClick() {
		myPlayer.playPause()
	}

	override  fun onPreviousClick() {
		if (_state.value.selectedSongIndex > 0) onSongSelected(_state.value.selectedSongIndex - 1)
	}

	override  fun onNextClick() {
		if (_state.value.selectedSongIndex < _state.value.songs.size - 1) onSongSelected(_state.value.selectedSongIndex + 1)
	}


	override fun onRepeatClick(isRepeat: Boolean) {
		myPlayer.repeat(isRepeat)
	}

	override fun onShuffleClick(song: SongModel) {
		val songsList = _state.value.songs.toMutableList()
		val currentIndex = songsList.indexOf(song)

		songsList.removeAt(currentIndex)
		songsList.shuffle()
		songsList.add(currentIndex, song)

		_state.value = _state.value.copy(songs = songsList)

		val mediaItems = _state.value.songs.map { MediaItem.fromUri(it.url) }.toMutableList()


		myPlayer.updatePlayerMediaItems(mediaItems)
	}
	override fun onSongClick(song: SongModel) {
		onSongSelected(_state.value.songs.indexOf(song))
	}

	override fun onSeekBarPositionChanged(position: Long) {
		coroutineScope.launch { myPlayer.seekToPosition(position) }
	}

	override fun onCleared() {
		super.onCleared()
		myPlayer.releasePlayer()
	}

}


