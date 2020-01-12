package com.mediapark.saco.mpp.mobile

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import platform.UIKit.UIDevice
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.CoroutineContext

actual fun platformName(): String {

    return UIDevice.currentDevice.systemName() +
        " " +
        UIDevice.currentDevice.systemVersion
}

private class MainDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatch_get_main_queue()) { block.run() }
    }
}

private class IosScope : CoroutineScope {
    private val dispatcher = MainDispatcher()
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = dispatcher + job
}

actual fun coroutineScope(): CoroutineScope {
    return IosScope()
}