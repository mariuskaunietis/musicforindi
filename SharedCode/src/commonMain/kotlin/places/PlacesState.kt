package com.mediapark.saco.mpp.mobile.places

import com.mediapark.saco.mpp.mobile.State
import com.mediapark.saco.mpp.mobile.api.Constants
import com.mediapark.saco.mpp.mobile.getTimestamp
import com.mediapark.saco.mpp.mobile.places.model.Coordinates
import com.mediapark.saco.mpp.mobile.places.model.ExpiringPlace
import com.mediapark.saco.mpp.mobile.places.model.PlacesResponse

data class PlacesState(
    private val markers: List<ExpiringPlace> = emptyList(),
    private val page: Int = 0,
    private val query: String = "",
    val command: Command? = null,
    val request: Request? = null
) : State<PlacesState, PlacesState.Event> {

    sealed class Event {
        data class TypedText(val newText: String) : Event()
        object RequestFailed : Event()
        data class FetchedPage(val places: PlacesResponse) : Event()
    }

    sealed class Command {
        data class ZoomToMarkers(val coordinates: List<Coordinates>) : Command()
    }

    sealed class Request {
        data class LoadPage(val page: Int, val query: String) : Request()
    }

    override fun reduce(event: Event): PlacesState {
        return when (event) {
            is Event.TypedText -> copy(
                page = 0,
                query = event.newText,
                request = Request.LoadPage(0, event.newText)
            )
            Event.RequestFailed -> {
                this
            }
            is Event.FetchedPage -> {

                val nextPage = page + 1
                val newMarkers = if (page == 0) {
                    event.places.markerPlaces(getTimestamp())
                } else {
                    markers + event.places.markerPlaces(getTimestamp())
                }

                val totalPages = event.places.count / Constants.PER_PAGE

                val willLoadMore = page < totalPages

                val nextRequest: Request?
                val nextCommand: Command?

                if (willLoadMore) {
                    nextRequest = Request.LoadPage(nextPage, query)
                    nextCommand = null
                } else {
                    nextRequest = null
                    nextCommand = Command.ZoomToMarkers(newMarkers.map { it.coordinates })
                }
                copy(
                    page = page + 1,
                    request = nextRequest,
                    command = nextCommand,
                    markers = newMarkers
                )
            }
        }
    }

    override fun clearCommandAndRequest() = copy(command = null, request = null)
}

// data class PlacesState(
//     val progressCount: Int? = null,
//     val markers: List<MarkerPlace> = emptyList(),
//     val effect: UiEffect<Effect>? = null
// ) {
//
//     sealed class Event {
//         class ShowProgress(val count: Int) : Event()
//         object HideProgress : Event()
//         class MarkersChanged(val markers: List<MarkerPlace>) : Event()
//         class ZoomEvent(val markers: List<MarkerPlace>) : Event()
//     }
//
//
//     sealed class Effect {
//         class ZoomToLatLng(val latLng: LatLng, val zoom: Float = 15f) : Effect()
//         class ZoomToLatLngBounds(val bounds: LatLngBounds) : Effect()
//     }
//
//     fun reduce(event: Event): PlacesState {
//         return when (event) {
//             is Event.ShowProgress -> copy(progressCount = event.count)
//             Event.HideProgress -> copy(progressCount = null)
//             is Event.MarkersChanged -> copy(markers = event.markers)
//             is Event.ZoomEvent -> {
//                 when {
//                     event.markers.size > 1 -> {
//                         val bounds = LatLngBounds.builder()
//                         event.markers.forEach { bounds.include(it.coordinates) }
//                         copy(effect = UiEffect(Effect.ZoomToLatLngBounds(bounds.build())))
//                     }
//                     event.markers.size == 1 -> {
//                         val latLng = event.markers[0].coordinates
//                         copy(effect = UiEffect(Effect.ZoomToLatLng(latLng)))
//                     }
//                     else -> this
//                 }
//             }
//         }
//     }
//
// }
//
