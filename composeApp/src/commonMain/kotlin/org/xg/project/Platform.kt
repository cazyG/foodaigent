package org.xg.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform