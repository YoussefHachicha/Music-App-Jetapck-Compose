package com.example.myapplicationmusicplease.core.di

import com.example.myapplicationmusicplease.song.data.remote.network.di.networkModule
import com.example.myapplicationmusicplease.song.di.songModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration


fun initKoin(appDeclaration: KoinAppDeclaration) = run {
	startKoin {
		appDeclaration()
		modules(
			networkModule,
			songModule
		)
	}
}