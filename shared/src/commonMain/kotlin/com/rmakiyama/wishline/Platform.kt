package com.rmakiyama.wishline

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
