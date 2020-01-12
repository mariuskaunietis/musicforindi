package com.example.marius.musicbrainzforindi

import com.mediapark.saco.mpp.mobile.places.model.ExpiringPlace
import com.example.marius.musicbrainzforindi.state.PlacesState
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Test

class PlacesStateTest {
  @Test
  fun showProgressEvent() {
    val state = PlacesState()
    val progress10 = state.reduce(PlacesState.Event.ShowProgress(10))
    assertEquals(10, progress10.progressCount)
    val progress0 = progress10.reduce(PlacesState.Event.HideProgress)
    assertEquals(null, progress0.progressCount)
  }

  @Test
  fun setMarkers() {
    val state = PlacesState()
    val withMockMarkers = state.reduce(PlacesState.Event.MarkersChanged(getMockMarker()))
    assertEquals(getMockMarker()[0].name, withMockMarkers.markers[0].name)
  }

  @Test
  fun removeMarkerAfterOneSecond() {
    val mockMarkers = getMockMarker()
    Thread.sleep(1500)
    val filteredMarkers = mockMarkers.filter { !it.isExpired() }
    assertEquals(0, filteredMarkers.size)
  }


  @Test
  fun zoomToLatLng() {
    val state = PlacesState()
    val effectLatLng = state.reduce(PlacesState.Event.ZoomEvent(getMockMarker()))
    assert(effectLatLng.effect?.getContentIfNotHandled() is PlacesState.Effect.ZoomToLatLng)
  }

  @Test
  fun zoomToLatLngBounds() {
    val state = PlacesState()
    val effectBounds = state.reduce(PlacesState.Event.ZoomEvent(get2MockMarkers()))
    assert(effectBounds.effect?.getContentIfNotHandled() is PlacesState.Effect.ZoomToLatLngBounds)
  }


  private fun getMockMarker(): List<ExpiringPlace> {
    val markers = mutableListOf<ExpiringPlace>()
    markers.add(
      ExpiringPlace(
        name = "first",
        coordinates = LatLng(20.0, 54.0),
        expiresAt = System.currentTimeMillis() + 1000
      )
    )
    return markers
  }

  private fun get2MockMarkers(): List<ExpiringPlace> {
    val markers = mutableListOf<ExpiringPlace>()
    markers.add(
      ExpiringPlace(
        name = "first",
        coordinates = LatLng(20.0, 54.0),
        expiresAt = System.currentTimeMillis() + 1000
      )
    )
    markers.add(
      ExpiringPlace(
        name = "second",
        coordinates = LatLng(24.0, 51.0),
        expiresAt = System.currentTimeMillis() + 2000
      )
    )
    return markers
  }
}