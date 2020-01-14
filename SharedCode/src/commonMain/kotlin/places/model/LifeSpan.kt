package com.mediapark.saco.mpp.mobile.places.model

import kotlinx.serialization.Serializable

@Serializable
class LifeSpan(val ended: Boolean? = null, val begin: String? = null) {
    fun getBeginYear(): Int? {
        return if (begin == null) {
            null
        } else {
            if (begin.contains("-")) {
                begin.split("-")[0].toInt()
            } else {
                begin.toInt()
            }
        }
    }
}