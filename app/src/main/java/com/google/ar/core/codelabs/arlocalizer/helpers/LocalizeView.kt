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

import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.Earth
import com.google.ar.core.GeospatialPose
import com.google.ar.core.codelabs.arlocalizer.activity.LocalizeActivity
import com.google.ar.core.codelabs.arlocalizer.R
import com.google.ar.core.examples.java.common.helpers.SnackbarHelper


/** Contains UI elements for Hello Geo. */
class LocalizeView(val activity: LocalizeActivity) : DefaultLifecycleObserver {
    val root = View.inflate(activity, R.layout.activity_localize, null)
    val surfaceView = root.findViewById<GLSurfaceView>(R.id.surfaceview)
    val distanceView = root.findViewById<TextView>(R.id.distance)
    val distanceRl = root.findViewById<RelativeLayout>(R.id.distanceRl)
    val movePhoneAnimation = root.findViewById<LottieAnimationView>(R.id.move_phone_animation)
    val movePhoneRl = root.findViewById<RelativeLayout>(R.id.move_phone_rl)
    var hasShownMoveAnim = false

    val session
        get() = activity.arCoreSessionHelper.session

    val snackbarHelper = SnackbarHelper()

    var mapView: MapView? = null
    val mapTouchWrapper = root.findViewById<MapTouchWrapper>(R.id.map_wrapper).apply {
        setup { screenLocation ->
            val latLng: LatLng =
                mapView?.googleMap?.projection?.fromScreenLocation(screenLocation) ?: return@setup
            activity.renderer.onMapClick(latLng)
        }
    }
    val mapFragment =
        (activity.supportFragmentManager.findFragmentById(R.id.map)!! as SupportMapFragment).also {
            it.getMapAsync { googleMap -> mapView = MapView(activity, googleMap) }
        }

    val statusText = root.findViewById<TextView>(R.id.statusText)
    fun updateStatusText(earth: Earth, cameraGeospatialPose: GeospatialPose?) {
        activity.runOnUiThread {
            val poseText = if (cameraGeospatialPose == null) "" else
                activity.getString(
                    R.string.geospatial_pose,
                    cameraGeospatialPose.latitude,
                    cameraGeospatialPose.longitude,
                    cameraGeospatialPose.horizontalAccuracy,
                    cameraGeospatialPose.altitude,
                    cameraGeospatialPose.verticalAccuracy,
                    cameraGeospatialPose.heading,
                    cameraGeospatialPose.headingAccuracy
                )
            statusText.text = activity.resources.getString(
                R.string.earth_state,
                earth.earthState.toString(),
                earth.trackingState.toString(),
                poseText
            )
        }
    }

    fun updateDistanceText(distance: String) {
        distanceView.text = distance
        distanceRl.visibility = View.VISIBLE
    }

    fun startMovePhoneAnim() {
        if (hasShownMoveAnim) {
            return
        }
        hasShownMoveAnim = true
        movePhoneRl.visibility = View.VISIBLE
        movePhoneAnimation.setAnimation("movephone.json")
        movePhoneAnimation.loop(true)
        movePhoneAnimation.playAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            movePhoneAnimation.pauseAnimation()
            movePhoneRl.visibility = View.GONE
        }, 4000)
    }

    override fun onResume(owner: LifecycleOwner) {
        surfaceView.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        surfaceView.onPause()
    }

}
