package com.google.ar.core.codelabs.arlocalizer

import kotlin.math.atan2
import kotlin.math.sqrt

class GeoCoordinate {
    var latitude: Double
    var longitude: Double
    var altitude: Double

    constructor(latitude: Double, longitude: Double, altitude: Double) {
        this.altitude = altitude
        this.latitude = latitude
        this.longitude = longitude
    }


    /// <summary>
    ///     Returns the distance between the latitude and longitude coordinates that are specified by this GeoCoordinate and
    ///     another specified GeoCoordinate.
    /// </summary>
    /// <returns>
    ///     The distance between the two coordinates, in meters.
    /// </returns>
    /// <param name="other">The GeoCoordinate for the location to calculate the distance to.</param
    fun calculateDistanceTo(other: GeoCoordinate): Int {

        var d1 = latitude * (Math.PI / 180.0);
        var num1 = longitude * (Math.PI / 180.0);
        var d2 = other.latitude * (Math.PI / 180.0);
        var num2 = other.longitude * (Math.PI / 180.0) - num1;
        var d3 = Math.pow(Math.sin((d2 - d1) / 2.0), 2.0) +
                Math.cos(d1) * Math.cos(d2) * Math.pow(Math.sin(num2 / 2.0), 2.0);

        return (6376500.0 * (2.0 * atan2(sqrt(d3), sqrt(1.0 - d3)))).toInt();
    }



}