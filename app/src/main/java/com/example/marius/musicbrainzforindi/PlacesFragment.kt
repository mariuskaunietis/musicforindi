package com.example.marius.musicbrainzforindi

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.marius.musicbrainzforindi.base.BaseFragment
import com.example.marius.musicbrainzforindi.base.ViewModelAware
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxbinding2.widget.RxTextView
import com.mediapark.saco.mpp.mobile.ViewModel
import com.mediapark.saco.mpp.mobile.places.PlacesState
import com.mediapark.saco.mpp.mobile.places.PlacesViewModel
import com.mediapark.saco.mpp.mobile.places.model.Coordinates
import io.reactivex.Observable
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
        map_view.onCreate(savedInstanceState)

        vm.setListener { boundState, newState ->
            when (val command = newState.command) {
                is PlacesState.Command.ZoomToMarkers -> {
                    doOnGoogleMap { googleMap ->
                        zoomGoogleMap(googleMap, command.coordinates)
                    }
                }
            }
            if (boundState?.markers != newState.markers) {
                doOnGoogleMap { googleMap ->
                    googleMap.clear()
                    newState.markers.forEach {
                        val marker = MarkerOptions()
                            .position(it.coordinates.latLng())
                            .title(it.name)
                        googleMap.addMarker(marker)
                    }
                }
            }
            if (boundState?.isLoading != newState.isLoading) {
                doOnUI {
                    if (newState.isLoading) {
                        progress_overlay.visibility = View.VISIBLE
                        hideKeyboard()
                    } else {
                        progress_overlay.visibility = View.GONE
                    }

                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(search.windowToken, 0)
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
        map_view.onStart()

        Observable
            .interval(1, TimeUnit.SECONDS)
            .subscribe {
                vm.transition(PlacesState.Event.TickedClock)
            }
            .addTo(disposable)


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
        map_view.onResume()
    }

    override fun onPause() {
        map_view.onPause()
        super.onPause()
    }

    override fun onStop() {
        map_view.onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        map_view.onDestroy()
        super.onDestroyView()
    }

    override fun onLowMemory() {
        map_view.onLowMemory()
        super.onLowMemory()
    }

    private fun doOnUI(callback: () -> Unit) {
        requireActivity().runOnUiThread {
            callback()
        }
    }

    private fun doOnGoogleMap(callback: (GoogleMap) -> Unit) {
        doOnUI {
            map_view.getMapAsync { callback(it) }
        }
    }
}