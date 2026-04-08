package org.xg.project.data.remote

import io.ktor.client.HttpClient

interface HttpPlatform {
    fun createHttpClient(): HttpClient
}

expect fun httpPlatform(): HttpPlatform
