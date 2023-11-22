package com.example.myapplicationmusicplease.song.presentation.bussines_logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.myapplicationmusicplease.song.domain.model.SongModel
import com.example.myapplicationmusicplease.song.domain.repository.SongRepository
import com.example.myapplicationmusicplease.song.presentation.bussines_logic.PlayerState.PlayerStates
import com.example.myapplicationmusicplease.core.utils.UnauthorizedException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayerViewModel() : KoinComponent, Player.Listener,  ViewModel() {
	private val repository: SongRepository by inject()
	private val player: ExoPlayer by inject()

	private val coroutineScope = viewModelScope

	private val _state = MutableStateFlow(PlayerState())

	private val currentPlaybackPosition: Long
		get() = if (player.currentPosition > 0) player.currentPosition else 0L

	private val currentSongDuration: Long
		get() = if (player.duration > 0) player.duration else 0L

	val state: StateFlow<PlayerState>
		get() = _state.stateIn(
			scope = coroutineScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = PlayerState()
		)

	private var playbackStateJob: Job? = null

	init {
		getSongs()
		_state.update {
			it.copy(
				playbackState = PlayerState.PlaybackState(currentPlaybackPosition,currentSongDuration)
			)
		}
	}


	private fun iniPlayer(songList: List<MediaItem>) {
		player.addListener(this)
		player.setMediaItems(songList)
		player.prepare()
	}

	private fun updatePlayerMediaItems(mediaItems: List<MediaItem>) {
		player.setMediaItems(mediaItems, false)
	}

	private fun setUpSong(index: Int, isSongPlay: Boolean) {
		if (player.playbackState == Player.STATE_IDLE) player.prepare()
		player.seekTo(index, 0)
		if (isSongPlay) player.playWhenReady = true
	}

	private fun playPause() {
		if (player.playbackState == Player.STATE_IDLE) player.prepare()
		player.playWhenReady = !player.playWhenReady
	}

	private fun repeat(isRepeat: Boolean) {
		if (isRepeat) {
			player.repeatMode = Player.REPEAT_MODE_ONE
		} else {
			player.repeatMode = Player.REPEAT_MODE_OFF
		}
	}

	private fun mute(isMute : Boolean) {
		if (isMute) {
			player.volume = 0f
		} else {
			player.volume = 1f
		}
	}

	fun releasePlayer() {
		player.release()
	}

	fun seekToPosition(position: Long) {
		player.seekTo(position)
	}

	override fun onPlayerError(error: PlaybackException) {
		super.onPlayerError(error)
		_state.update {
			it.copy(
				playerState = PlayerStates.STATE_ERROR
			)
		}
	}

	override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
		if (player.playbackState == Player.STATE_READY) {
			if (playWhenReady) {
				_state.update {
					it.copy(
						playerState = PlayerStates.STATE_PLAYING
					)
				}
			} else {
				_state.update {
					it.copy(
						playerState = PlayerStates.STATE_PAUSE
					)
				}
			}
		}
	}
	override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
		super.onMediaItemTransition(mediaItem, reason)
		when(reason){
			Player.MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
				_state.update {
					it.copy(
						playerState = PlayerState.PlayerStates.STATE_NEXT_TRACK
					)
				}
				_state.update {
					it.copy(
						playerState = PlayerStates.STATE_PLAYING
					)
				}
			}
			Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT -> {
				_state.update {
					it.copy(
						playerState = PlayerStates.STATE_REPEAT_MODE_ALL
					)
				}
				_state.update {
					it.copy(
						playerState = PlayerStates.STATE_PLAYING
					)
				}
			}
		}
	}

	override fun onPlaybackStateChanged(playbackState: Int) {
		when (playbackState) {
			Player.STATE_IDLE -> {
				_state.update {
					it.copy(
						playerState = PlayerStates.STATE_IDLE
					)
				}
			}
			Player.STATE_BUFFERING -> {
				_state.update {
					it.copy(
						playerState = PlayerStates.STATE_BUFFERING
					)
				}
			}
			Player.STATE_READY -> {
				_state.update {
					it.copy(
						playerState = PlayerStates.STATE_READY
					)
				}
				if (player.playWhenReady) {
					_state.update {
						it.copy(
							playerState = PlayerStates.STATE_PLAYING
						)
					}
				} else {
					_state.update {
						it.copy(
							playerState = PlayerStates.STATE_PAUSE
						)
					}
				}
			}
			Player.STATE_ENDED -> {
				_state.update {
					it.copy(
						playerState = PlayerStates.STATE_END
					)
				}
			}
			Player.REPEAT_MODE_ALL -> {
				_state.update {
					it.copy(
						playerState = PlayerStates.STATE_REPEAT_MODE_ALL
					)
				}
			}
		}
	}

	private fun getSongs() {
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
			is PlayerEvent.InitPlayer -> initPlayer()
			is PlayerEvent.OnPlayPauseClick -> playPause()
			is PlayerEvent.OnPreviousClick -> if (_state.value.selectedSongIndex > 0) onSongSelected(_state.value.selectedSongIndex - 1)
			is PlayerEvent.OnNextClick -> if (_state.value.selectedSongIndex < _state.value.songs.size - 1) onSongSelected(_state.value.selectedSongIndex + 1)
			is PlayerEvent.OnRepeatClick -> repeat(event.isRepeat)
			is PlayerEvent.OnShuffleClick -> onShuffleClicked(event.song)
			is PlayerEvent.OnSongClick -> onSongSelected(_state.value.songs.indexOf(event.song))
			is PlayerEvent.OnSeekBarPositionChanged -> coroutineScope.launch { seekToPosition(event.position) }
			is PlayerEvent.OnMuteClick -> mute(event.isMute)
			is PlayerEvent.UpdatePlayerState -> updateState(event.playerState)
		}
	}

	private fun initPlayer() {
		coroutineScope.launch {
			if (_state.value.getSongs.isSuccess && _state.value.songs.isNotEmpty()) {
				val mediaItems = _state.value.songs.map { MediaItem.fromUri(it.url) }.toMutableList()
				iniPlayer(mediaItems)
			}
		}
	}
	private fun onShuffleClicked(song: SongModel) {
		val songsList = _state.value.songs.toMutableList()
		val currentIndex = songsList.indexOf(song)

		songsList.removeAt(currentIndex)
		songsList.shuffle()
		songsList.add(currentIndex, song)

		_state.value = _state.value.copy(songs = songsList)

		val mediaItems = _state.value.songs.map { MediaItem.fromUri(it.url) }.toMutableList()

		updatePlayerMediaItems(mediaItems)
	}

	private fun onSongSelected(index: Int) {
		if (_state.value.selectedSongIndex == -1) _state.update { it.copy(isSongPlay = true) }
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
		if (!_state.value.isAutoSetOnPlay) {
			setUpSong(
				index = _state.value.selectedSongIndex,
				isSongPlay = _state.value.isSongPlay,
			)
		}
		_state.value = _state.value.copy(isAutoSetOnPlay = false)
	}

	private fun updateState(state: PlayerStates) {
		if (_state.value.selectedSongIndex != -1) {

			_state.update {
				it.copy(
					isSongPlay = state == PlayerStates.STATE_PLAYING,
					songs = it.songs.toMutableList().apply {
						this[it.selectedSongIndex] =
							it.songs[it.selectedSongIndex].copy(
								state = state,
								isSelected = true
							)
					},
					selectedSongModel = null
				)
			}

			_state.update {
				it.copy(
					selectedSongModel = it.songs[it.selectedSongIndex]
				)
			}

			updatePlaybackState(state)
			if (state == PlayerStates.STATE_NEXT_TRACK) {
				_state.update { it.copy(isAutoSetOnPlay = true) }
				coroutineScope.launch {
					PlayerEvent.OnNextClick
				}
			}
			if (state == PlayerStates.STATE_END) onSongSelected(0)
		}
	}

	private fun updatePlaybackState(state: PlayerStates) {
		playbackStateJob?.cancel()

		playbackStateJob = coroutineScope.launch {
			do {
				_state.value = _state.value.copy(
					playbackState = _state.value.playbackState.copy(
						currentPlaybackPosition = currentPlaybackPosition,
						currentTrackDuration = currentSongDuration
					)
				)
				delay(200)
			} while (state == PlayerStates.STATE_PLAYING && isActive)

		}
	}

//	override fun onCleared() {
//		super.onCleared()
//		releasePlayer()
//	}

}


