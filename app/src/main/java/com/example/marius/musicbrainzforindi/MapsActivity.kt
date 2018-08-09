package com.example.marius.musicbrainzforindi

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.example.marius.musicbrainzforindi.state.PlacesState
import com.example.marius.musicbrainzforindi.utils.px
import com.example.marius.musicbrainzforindi.viewmodel.PlacesViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxbinding2.widget.RxTextView
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
      .debounce(3, TimeUnit.SECONDS)
      .subscribe { newQuery ->
        placesViewModel.textQueryChanged(newQuery)
      }
      .addTo(disposable)

    placesViewModel.state.subscribe { placesState ->
      handleState(placesState)
    }
      .addTo(disposable)
  }

  private fun handleState(placesState: PlacesState) {
    if (placesState.progressMessage == null) {
      hideProgress()
    } else {
      showProgress(placesState.progressMessage)
    }
    googleMap?.let { googleMap ->
      googleMap.clear()


      if (placesState.markers.size > 1) {
        val boundsBuilder = LatLngBounds.builder()
        placesState.markers.forEach { markerPlace ->
          googleMap.addMarker(MarkerOptions().position(markerPlace.coordinates).title(markerPlace.name))
          boundsBuilder.include(markerPlace.coordinates)
        }
        val newCameraPosition =
          CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 20.px.toInt())

        googleMap.animateCamera(newCameraPosition)
      } else if (placesState.markers.size == 1) {
        //this branch exists because google map would crash if there's only one location in bounds
        val markerPlace = placesState.markers[0]
        googleMap.addMarker(MarkerOptions().position(markerPlace.coordinates).title(markerPlace.name))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPlace.coordinates, 15f))
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

    // Add a marker in Sydney and move the camera
    val sydney = LatLng(-34.0, 151.0)
    aGoogleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
    aGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
  }


}
