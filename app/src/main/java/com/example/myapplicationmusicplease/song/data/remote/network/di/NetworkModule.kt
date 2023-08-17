package com.example.myapplicationmusicplease.song.data.remote.network.di

import com.example.myapplicationmusicplease.core.utils.createNoAuthHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module


val networkModule = module {
	single(named("NoAuth")) { createNoAuthHttpClient() }
}//Todo change it place later