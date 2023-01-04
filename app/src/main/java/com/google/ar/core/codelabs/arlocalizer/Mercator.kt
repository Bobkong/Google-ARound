package com.google.ar.core.codelabs.arlocalizer

import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object Mercator {

    val degreesToRadians = (Math.PI/180.0)
    val radiansToDegrees = (180.0/ Math.PI)
    val earthRadius = 6378137.0

    fun calculateDerivedPosition(coordinate: GeoCoordinate, range: Double, heading: Double): GeoCoordinate {
        val latA = coordinate.latitude * degreesToRadians
        val lonA = coordinate.longitude * degreesToRadians
        val angularDistance = range / earthRadius
        val trueCourse = heading * degreesToRadians

        val lat = asin(
            sin(latA) * cos(angularDistance) +
                    cos(latA) * sin(angularDistance) * cos(trueCourse)
        )

        val dlon = atan2(
            sin(trueCourse) * sin(angularDistance) * cos(latA),
            cos(angularDistance) - sin(latA) * sin(lat)
        )

        val lon: Double = (lonA + dlon + Math.PI) % (Math.PI * 2) - Math.PI

        return GeoCoordinate(
            lat * radiansToDegrees,
            lon * radiansToDegrees,
            coordinate.altitude
        )
    }

}