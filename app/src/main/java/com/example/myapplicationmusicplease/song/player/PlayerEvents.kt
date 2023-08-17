package com.example.myapplicationmusicplease.song.player

import com.example.myapplicationmusicplease.song.domain.model.SongModel

interface PlayerEvents {
	fun onPlayPauseClick() //Todo remove the suspend keyword
	fun onPreviousClick()//Todo remove the suspend keyword
	fun onNextClick()//Todo remove the suspend keyword
	fun onRepeatClick(isRepeat: Boolean)
	fun onShuffleClick(song: SongModel)
	fun onSongClick(song: SongModel)
	fun onSeekBarPositionChanged(position: Long)
}