package com.mediapark.saco.mpp.mobile

import kotlinx.coroutines.CoroutineScope

expect fun platformName(): String

expect fun getTimestamp(): Long

expect fun coroutineScope(): CoroutineScope

expect fun Throwable.printStackTrace()