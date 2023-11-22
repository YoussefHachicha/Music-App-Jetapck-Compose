package com.example.myapplicationmusicplease.song.di

import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import com.example.myapplicationmusicplease.song.data.remote.SongClient
import com.example.myapplicationmusicplease.song.data.repository.SongRepositoryImpl
import com.example.myapplicationmusicplease.song.domain.repository.SongRepository
import com.example.myapplicationmusicplease.song.presentation.bussines_logic.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val songModule = module {
    single { SongClient() }
    single<SongRepository> { SongRepositoryImpl() }
    single {
        val exoPlayer = ExoPlayer
            .Builder(get())
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NONE)
            .build()

        println("ExoPlayer instance created: $exoPlayer")
        exoPlayer
    }
    viewModel { PlayerViewModel() }

}