package org.xg.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun httpPlatform(): HttpPlatform = JsHttpPlatform

private object JsHttpPlatform : HttpPlatform {
    override fun createHttpClient(): HttpClient {
        return HttpClient(Js) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }
}
