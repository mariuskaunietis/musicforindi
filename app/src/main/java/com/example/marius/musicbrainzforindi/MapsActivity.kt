package com.example.marius.musicbrainzforindi

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.example.marius.musicbrainzforindi.state.PlacesState
import com.example.marius.musicbrainzforindi.viewmodel.PlacesViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.concurrent.TimeUnit


class MapsActivity : BaseActivity(), OnMapReadyCallback {
  private var googleMap: GoogleMap? = null


  private val disposable by lazy {
    CompositeDisposable()
  }

  private val placesViewModel by lazy {
    ViewModelProviders
      .of(this, ViewModelProvider.AndroidViewModelFactory(application))
      .get(PlacesViewModel::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_maps)
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    val mapFragment = supportFragmentManager
      .findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)
  }

  override fun onStart() {
    super.onStart()

    RxTextView.textChanges(search)
      .debounce(1, TimeUnit.SECONDS)
      .subscribe { newQuery ->
        placesViewModel.textQueryChanged(newQuery)
      }
      .addTo(disposable)

    placesViewModel
      .state
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe { placesState ->
        handleState(placesState)
      }
      .addTo(disposable)
  }


  private fun handleState(placesState: PlacesState) {
    if (placesState.progressCount == null) {
      hideProgress()
    } else {
      showProgress(placesState.progressCount)
    }
    googleMap?.let { googleMap ->
      googleMap.clear()
      placesState.markers.forEach { markerPlace ->
        googleMap.addMarker(MarkerOptions().position(markerPlace.coordinates).title(markerPlace.name))
      }
    }
  }

  override fun onStop() {
    super.onStop()
    disposable.clear()
  }

  //todo convert to rx so we can merge this together with subscribtion to viewmodel
  override fun onMapReady(aGoogleMap: GoogleMap) {
    googleMap = aGoogleMap
  }


}
