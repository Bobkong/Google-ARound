/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.core.codelabs.arlocalizer.helpers

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.ar.core.codelabs.arlocalizer.R
import com.google.ar.core.codelabs.arlocalizer.activity.LocalizeActivity
import com.google.ar.core.codelabs.arlocalizer.model.MapData
import com.google.ar.core.codelabs.arlocalizer.utils.PreferenceUtils
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request


open class ChatMapView(val activity: LocalizeActivity, val googleMap: GoogleMap) {

  var setInitialCameraPosition = false
  val cameraMarker = createMarker(true)
  var cameraIdle = true

  val earthMarker = createMarker(false)

  init {
    googleMap.uiSettings.apply {
      isMapToolbarEnabled = false
      isIndoorLevelPickerEnabled = false
      isZoomControlsEnabled = false
      isTiltGesturesEnabled = false
      isScrollGesturesEnabled = false
    }

    googleMap.setOnMarkerClickListener { unused -> false }

    // Add listeners to keep track of when the GoogleMap camera is moving.
    googleMap.setOnCameraMoveListener { cameraIdle = false }
    googleMap.setOnCameraIdleListener { cameraIdle = true }

  }

  fun updateMapPosition(latitude: Double, longitude: Double, heading: Double) {
    val position = LatLng(latitude, longitude)
    activity.runOnUiThread {
      // If the map is already in the process of a camera update, then don't move it.
      if (!cameraIdle) {
        return@runOnUiThread
      }
      cameraMarker.isVisible = true
      cameraMarker.position = position
      cameraMarker.rotation = heading.toFloat()



      if (earthMarker.position.latitude != 0.0 || earthMarker.position.longitude != 0.0) {
        val builder = LatLngBounds.Builder()
        builder.include(cameraMarker.position)
        builder.include(earthMarker.position)
        val bounds = builder.build()
        val padding = 64 // offset from edges of the map in pixels

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        googleMap.moveCamera(cu)
      } else {
        val cameraPositionBuilder: CameraPosition.Builder = if (!setInitialCameraPosition) {
          // Set the camera position with an initial default zoom level.
          setInitialCameraPosition = true
          CameraPosition.Builder().zoom(21f).target(position)
        } else {
          // Set the camera position and keep the same zoom level.
          CameraPosition.Builder()
            .zoom(googleMap.cameraPosition.zoom)
            .target(position)
        }
        googleMap.moveCamera(
          CameraUpdateFactory.newCameraPosition(cameraPositionBuilder.build()))

      }

    }

  }

  fun courseAngle(): Double {
    val lng_a = cameraMarker.position.longitude
    val lat_a = cameraMarker.position.latitude
    val lng_b = earthMarker.position.longitude
    val lat_b = earthMarker.position.latitude
    return Math.atan2((lng_b - lng_a), (lat_b - lat_a)) * 180 / Math.PI

  }

  /** Creates and adds a 2D anchor marker on the 2D map view.  */
  private fun createMarker(
    isSelf: Boolean
  ): Marker {
    val markersOptions = MarkerOptions()
      .position(LatLng(0.0,0.0))
      .draggable(false)
      .anchor(0.5f, 0.5f)
      .flat(true)
      .visible(false)
      .icon(BitmapDescriptorFactory.fromBitmap(createColoredMarkerBitmap(isSelf)))
    return googleMap.addMarker(markersOptions)!!
  }

  open fun createColoredMarkerBitmap(isSelf: Boolean): Bitmap {
    val username = PreferenceUtils.getNickname()
    var resource: Int?
    if (username == "Liz") {
      resource = if (isSelf) R.drawable.female_marker else R.drawable.male_marker
    } else {
      resource = if (isSelf) R.drawable.male_marker else R.drawable.female_marker
    }

    val opt = BitmapFactory.Options()
    opt.inMutable = true
    val navigationIcon =
      BitmapFactory.decodeResource(activity.resources, resource, opt)
    val p = Paint()
    val canvas = Canvas(navigationIcon)
    canvas.drawBitmap(navigationIcon,  /* left= */0f,  /* top= */0f, p)
    return navigationIcon
  }


  public fun showRoute(origin: LatLng, dest: LatLng) {
    val url = getDirectionURL(origin, dest, "AIzaSyBHWMoxz8l5prXUSRnjRCIBGh_veejxFNM")
    GetDirection(url).execute()

  }

  private fun getDirectionURL(origin:LatLng, dest:LatLng, secret: String) : String{
    return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
            "&destination=${dest.latitude},${dest.longitude}" +
            "&sensor=false" +
            "&mode=driving" +
            "&key=$secret"
  }

  fun decodePolyline(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0
    while (index < len) {
      var b: Int
      var shift = 0
      var result = 0
      do {
        b = encoded[index++].code - 63
        result = result or (b and 0x1f shl shift)
        shift += 5
      } while (b >= 0x20)
      val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
      lat += dlat
      shift = 0
      result = 0
      do {
        b = encoded[index++].code - 63
        result = result or (b and 0x1f shl shift)
        shift += 5
      } while (b >= 0x20)
      val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
      lng += dlng
      val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
      poly.add(latLng)
    }
    return poly
  }



  @SuppressLint("StaticFieldLeak")
  private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
    override fun doInBackground(vararg params: Void?): List<List<LatLng>> {

      val client = OkHttpClient()
      val request = Request.Builder().url(url).build()
      val response = client.newCall(request).execute()
      val data = response.body()!!.string()

      val result =  ArrayList<List<LatLng>>()
      try{
        val respObj = Gson().fromJson(data, MapData::class.java)
        val path =  ArrayList<LatLng>()
        for (i in 0 until respObj.routes[0].legs[0].steps.size){
          path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
        }
        result.add(path)
      }catch (e:Exception){
        e.printStackTrace()
      }
      return result
    }

    override fun onPostExecute(result: List<List<LatLng>>) {
      val lineoption = PolylineOptions()
      for (i in result.indices){
        lineoption.addAll(result[i])
        lineoption.width(12f)
        lineoption.color(activity.getColor(R.color.theme_color))
        lineoption.geodesic(true)
      }
      googleMap.addPolyline(lineoption)
    }
  }



}