package com.example.marius.musicbrainzforindi.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.example.marius.musicbrainzforindi.appService
import com.example.marius.musicbrainzforindi.state.PlacesState
import com.example.marius.musicbrainzforindi.utils.scheduleAsNetworkCall
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class PlacesViewModel(application: Application) : AndroidViewModel(application) {

  private val appService = application.appService

  private val _state = BehaviorSubject.create<PlacesState>()
  val state: Observable<PlacesState> = _state.hide()


  fun textQueryChanged(newQuery: CharSequence) {
    if (newQuery.isBlank()) {
//todo hmmz??
    } else {
      appService
        .getPlaces(query = newQuery.toString())
        .scheduleAsNetworkCall()
        .subscribe({}, Throwable::printStackTrace)
    }
  }


}