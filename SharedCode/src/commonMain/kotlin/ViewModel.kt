package com.mediapark.saco.mpp.mobile

abstract class ViewModel<T : State<T, E>, E>(initial: T) {
    val machine by lazy {
        StateMachine<T, E>(initial)
    }
    private lateinit var listener: (T?, T) -> Unit
    private val _listener = object : StateListener<T, E> {
        override fun onStateUpdated(oldState: T?, newState: T) {
            handleState(newState)
            listener(oldState, newState)
        }
    }

    abstract fun handleState(newState: T)
    fun setListener(onStateUpdated: (T?, T) -> Unit) {
        this.listener = onStateUpdated
    }

    fun onViewStarted() {
        _listener.onStateUpdated(null, machine.state)
        machine.addListener(_listener)
    }

    fun onViewStopped() {
        machine.removeListener(_listener)
    }

    fun transition(event: E) {
        machine.transition(event)
    }
}