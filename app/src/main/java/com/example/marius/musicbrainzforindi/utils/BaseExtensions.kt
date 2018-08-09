package com.example.marius.musicbrainzforindi.utils

import android.content.res.Resources
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

val Int.dp: Int
  get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Float
  get() = (this * Resources.getSystem().displayMetrics.density)


fun <T> Observable<T>.scheduleAsNetworkCall(): Observable<T> {
  return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}
