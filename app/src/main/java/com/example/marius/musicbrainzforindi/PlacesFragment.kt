package com.example.marius.musicbrainzforindi

import android.os.Bundle
import android.view.View
import com.example.marius.musicbrainzforindi.base.BaseFragment
import com.example.marius.musicbrainzforindi.base.ViewModelAware
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.jakewharton.rxbinding2.widget.RxTextView
import com.mediapark.saco.mpp.mobile.ViewModel
import com.mediapark.saco.mpp.mobile.places.PlacesState
import com.mediapark.saco.mpp.mobile.places.PlacesViewModel
import com.mediapark.saco.mpp.mobile.places.model.Coordinates
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_maps.*
import java.util.concurrent.TimeUnit

class PlacesFragment : BaseFragment(), ViewModelAware<PlacesState, PlacesState.Event> {
    override val layoutId = R.layout.fragment_maps

    private val vm by lazy {
        PlacesViewModel()
    }

    override fun viewModel(): ViewModel<PlacesState, PlacesState.Event> {
        return vm
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)

        vm.setListener { _, newState ->
            when (val command = newState.command) {
                is PlacesState.Command.ZoomToMarkers -> {
                    mapView.getMapAsync { googleMap ->
                        zoomGoogleMap(googleMap, command.coordinates)
                    }
                }
            }
        }
    }

    private fun zoomGoogleMap(googleMap: GoogleMap, coordinates: List<Coordinates>) {
        if (coordinates.isEmpty()) return
        val cameraUpdate = if (coordinates.size == 1) {
            CameraUpdateFactory.newLatLngZoom(coordinates.first().latLng(), 15f)
        } else {
            val boundsBuilder = LatLngBounds.builder()
            coordinates.forEach { boundsBuilder.include(it.latLng()) }
            CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 0)
        }
        googleMap.animateCamera(cameraUpdate)
    }

    private fun Coordinates.latLng(): LatLng {
        return LatLng(latitude, longitude)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()

        RxTextView.textChanges(search)
            .skipInitialValue()
            .debounce(1, TimeUnit.SECONDS)
            .subscribe { newQuery ->
                vm.transition(PlacesState.Event.TypedText(newQuery.toString()))
            }
            .addTo(disposable)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        mapView.onDestroy()
        super.onDestroyView()
    }

    override fun onLowMemory() {
        mapView.onLowMemory()
        super.onLowMemory()
    }
}