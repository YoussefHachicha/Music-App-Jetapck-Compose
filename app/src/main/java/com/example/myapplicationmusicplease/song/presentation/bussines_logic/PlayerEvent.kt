package com.example.myapplicationmusicplease.song.presentation.bussines_logic

import com.example.myapplicationmusicplease.song.domain.model.SongModel

sealed class PlayerEvent {
    object InitPlayer : PlayerEvent()
    object OnPlayPauseClick : PlayerEvent()
    object OnPreviousClick : PlayerEvent()
    object OnNextClick : PlayerEvent()
    data class OnRepeatClick(val isRepeat: Boolean) : PlayerEvent()
    data class OnShuffleClick(val song: SongModel) : PlayerEvent()
    data class OnSongClick(val song: SongModel) : PlayerEvent()
    data class OnSeekBarPositionChanged(val position: Long) : PlayerEvent()
    data class OnMuteClick(val isMute: Boolean) : PlayerEvent()
    data class UpdatePlayerState(val playerState: PlayerState.PlayerStates) : PlayerEvent()
}