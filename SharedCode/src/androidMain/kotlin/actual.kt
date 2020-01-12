package com.mediapark.saco.mpp.mobile

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

actual fun platformName(): String {
    return "Android"
}

actual fun coroutineScope(): CoroutineScope {
    return MainScope()
}

actual fun getTimestamp(): Long {
    return System.currentTimeMillis() / 1000L
}