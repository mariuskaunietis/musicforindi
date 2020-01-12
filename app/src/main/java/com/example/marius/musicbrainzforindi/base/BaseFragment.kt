package com.example.marius.musicbrainzforindi.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment : androidx.fragment.app.Fragment() {

    @get:LayoutRes
    abstract val layoutId: Int
    val disposable by lazy {
        CompositeDisposable()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutId, container, false)

    override fun onStop() {
        super.onStop()
        if (this is ViewModelAware<*, *>) {
            viewModel().onViewStopped()
        }
        disposable.clear()
    }

    override fun onStart() {
        super.onStart()
        if (this is ViewModelAware<*, *>) {
            viewModel().onViewStarted()
        }
    }
}
