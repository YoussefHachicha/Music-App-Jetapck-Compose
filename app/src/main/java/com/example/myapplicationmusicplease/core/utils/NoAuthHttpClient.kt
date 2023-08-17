package com.example.myapplicationmusicplease.core.utils

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


fun createNoAuthHttpClient(): HttpClient {
	return createPlatformHttpClient()
		.config {
			defaultRequest {
				url(BaseUrl)
				contentType(ContentType.Application.Json)
			}

			install(ContentNegotiation) {
				json(Json {
					ignoreUnknownKeys = true
					isLenient = true
					encodeDefaults = true}
				)
			}

			install(Logging) {
				logger = Logger.SIMPLE
				level = LogLevel.ALL
			}
		}
}

fun createPlatformHttpClient(): HttpClient {
	return HttpClient() //Todo add android here later
}