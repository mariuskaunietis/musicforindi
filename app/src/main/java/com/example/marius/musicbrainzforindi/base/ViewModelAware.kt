package com.example.marius.musicbrainzforindi.base

import com.mediapark.saco.mpp.mobile.State
import com.mediapark.saco.mpp.mobile.ViewModel

interface ViewModelAware<T : State<T, E>, E> {
    fun viewModel(): ViewModel<T, E>
}
