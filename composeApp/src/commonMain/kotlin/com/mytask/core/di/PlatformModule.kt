package com.mytask.core.di

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformModule() : Module

fun networkModule() = module {

    factory {
        HttpClient(get()) {
            install(HttpTimeout) {
                socketTimeoutMillis = 120_000  // 2 minutes
                requestTimeoutMillis = 120_000 // 2 minutes
                connectTimeoutMillis = 60_000  // 30 seconds
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 1)
                exponentialDelay()
                modifyRequest { request ->
                    request.headers.append("x-retry-count", retryCount.toString())
                }
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        Logger.d("ktor client external") {
                            message
                        }
                    }
                }
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            // Don't automatically throw exceptions for HTTP error status codes
            // This allows our BaseService.handleResponse() to handle errors properly
            expectSuccess = false
        }
    }

}