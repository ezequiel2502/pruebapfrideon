package com.example.gps_test.ui.map

import android.graphics.Color
import com.tomtom.quantity.Distance
import com.tomtom.quantity.Speed
import com.tomtom.sdk.location.GeoLocation
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.Place
import com.tomtom.sdk.location.simulation.SimulationLocationProvider
import com.tomtom.sdk.location.simulation.strategy.InterpolationStrategy
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.common.WidthByZoom
import com.tomtom.sdk.map.display.route.Instruction
import com.tomtom.sdk.map.display.route.RouteOptions
import com.tomtom.sdk.map.display.route.RouteSection
import com.tomtom.sdk.map.display.route.RouteSectionType
import com.tomtom.sdk.routing.options.Itinerary
import com.tomtom.sdk.routing.options.ItineraryPoint
import com.tomtom.sdk.routing.options.RouteInformationMode
import com.tomtom.sdk.routing.options.RoutePlanningOptions
import com.tomtom.sdk.routing.options.description.SectionType
import com.tomtom.sdk.routing.options.guidance.AnnouncementPoints
import com.tomtom.sdk.routing.options.guidance.ExtendedSections
import com.tomtom.sdk.routing.options.guidance.GuidanceOptions
import com.tomtom.sdk.routing.options.guidance.InstructionPhoneticsType
import com.tomtom.sdk.routing.options.guidance.InstructionType
import com.tomtom.sdk.routing.options.guidance.ProgressPoints
import com.tomtom.sdk.routing.options.guidance.RoadShieldReferences
import com.tomtom.sdk.routing.route.Route
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class Routing_Variables {

    fun setRoute(positions: List<GeoPoint>) : RouteOptions {
        val routeOptions = RouteOptions(

                geometry = positions,

                color = Color.BLUE,

                outlineWidth = 3.0,

                widths = listOf(WidthByZoom(5.0)),

                progress = Distance.meters(100.0),

                /*instructions = listOf(

                        Instruction(

                                routeOffset = Distance.meters(1000.0),

                                combineWithNext = false

                        ),

                        Instruction(

                                routeOffset = Distance.meters(2000.0),

                                combineWithNext = true

                        ),

                        Instruction(routeOffset = Distance.meters(3000.0))

                ),*/

                tag = "Extra information about the route",

                departureMarkerVisible = true,

                destinationMarkerVisible = true

        )
        return routeOptions
    }

    fun setRouteAlt(positions: List<GeoPoint>) : RouteOptions {
        val routeOptions = RouteOptions(

                geometry = positions,

                color = Color.RED,

                outlineWidth = 3.0,

                widths = listOf(WidthByZoom(5.0)),

                //progress = Distance.meters(0.0),

                /*instructions = listOf(

                        Instruction(

                                routeOffset = Distance.meters(1000.0),

                                combineWithNext = false

                        ),

                        Instruction(

                                routeOffset = Distance.meters(2000.0),

                                combineWithNext = true

                        ),

                        Instruction(routeOffset = Distance.meters(3000.0))

                ),*/

                tag = "Extra information about the route",

                departureMarkerVisible = true,

                destinationMarkerVisible = true

        )
        return routeOptions
    }

    fun setRoute2(positions: List<GeoPoint>, plan_sections: List<RouteSection>) : RouteOptions {
        val routeOptions = RouteOptions(

                geometry = positions,

                color = Color.BLUE,

                outlineWidth = 3.0,

                widths = listOf(WidthByZoom(5.0)),

                progress = Distance.meters(100.0),

                /*instructions = listOf(

                        Instruction(

                                routeOffset = Distance.meters(1000.0),

                                combineWithNext = false

                        ),

                        Instruction(

                                routeOffset = Distance.meters(2000.0),

                                combineWithNext = true

                        ),

                        Instruction(routeOffset = Distance.meters(3000.0))

                ),*/

                tag = "Extra information about the route",

                departureMarkerVisible = true,

                destinationMarkerVisible = true,

                sections = plan_sections
        )

        return routeOptions
    }

    fun setOnlineRoute(positions: List<GeoPoint>) : RoutePlanningOptions {

        val list = mutableListOf<ItineraryPoint>()
        for (position in positions) {
            list.add(ItineraryPoint(Place(position)))
        }

        val origin = list.first()
        val destination = list.last()
        list.remove(origin)
        list.remove(destination)

        val itinerary = Itinerary(
                origin = origin,
                destination = destination,
                waypoints = list
        )
        val routeOptions = RoutePlanningOptions(
                itinerary = itinerary,
                mode = RouteInformationMode.Complete,
                guidanceOptions = GuidanceOptions(
                        instructionType = InstructionType.Tagged,
                        language = Locale.ENGLISH)
        )

        return routeOptions
    }

    fun setInstruction(distance: Double, arrow_Lenght: Double ) : Instruction {

        val ins = Instruction(
                routeOffset = Distance.meters(distance),

                length = Distance.meters(arrow_Lenght),

                combineWithNext = true
        )

        return ins
    }

    @OptIn(ExperimentalTime::class)
    fun simulateRoute(route: Route) : LocationProvider {
        val routeGeoLocations = route.geometry.map { GeoLocation(it) }
        val locationProvider = SimulationLocationProvider.create(strategy = InterpolationStrategy(routeGeoLocations, currentSpeed = Speed.metersPerSecond(10), broadcastDelay = 0.seconds, startDelay = 0.seconds))
        return locationProvider
    }

}