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

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.SupportMapFragment
import com.google.ar.core.Earth
import com.google.ar.core.GeospatialPose
import com.google.ar.core.codelabs.arlocalizer.R
import com.google.ar.core.codelabs.arlocalizer.activity.LocalizeActivity
import com.google.ar.core.codelabs.arlocalizer.utils.PixelUtil
import com.google.ar.core.codelabs.arlocalizer.widgets.WaitingDialog
import com.google.ar.core.examples.java.common.helpers.SnackbarHelper


/** Contains UI elements for Hello Geo. */
class LocalizeView(val activity: LocalizeActivity) : DefaultLifecycleObserver {
    val TAG = "LocalizeView"
    val root = View.inflate(activity, R.layout.activity_localize, null)
    val surfaceView = root.findViewById<GLSurfaceView>(R.id.surfaceview)
    val instructionText = root.findViewById<TextView>(R.id.instruction)
    val movePhoneAnimation = root.findViewById<LottieAnimationView>(R.id.move_phone_animation)
    val movePhoneRl = root.findViewById<RelativeLayout>(R.id.move_phone_rl)
    var isShowingNavigateAnim: Boolean? = null
    val back = root.findViewById<ImageView>(R.id.back)
    val navigateAnimation = root.findViewById<ImageView>(R.id.navigate_animation)

    val session
        get() = activity.arCoreSessionHelper.session

    val snackbarHelper = SnackbarHelper()

    var mapView: MapView? = null
    val mapTouchWrapper = root.findViewById<MapTouchWrapper>(R.id.map_wrapper).apply {
        setup { screenLocation ->
//            val latLng: LatLng =
//                mapView?.googleMap?.projection?.fromScreenLocation(screenLocation) ?: return@setup
//            activity.renderer.onMapClick(latLng)
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

    init {
        WaitingDialog.show(activity)
        instructionText.text = "Starting Navigation Service..."
        back.setOnClickListener {
            WaitingDialog.dismiss()
            activity.renderer.stopUpdateNavigation()
            activity.finish()
        }

        val rlParams = navigateAnimation.layoutParams as ConstraintLayout.LayoutParams

        val wm = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val height = wm.defaultDisplay.height
        rlParams.height = (height - PixelUtil.convertDpToPixel(430f)).toInt()

        navigateAnimation.setLayoutParams(rlParams)
    }

    fun updateDistanceText(distance: String) {
        instructionText.text = distance
    }

    fun startMovePhoneAnim() {
        activity.runOnUiThread {
            if (isShowingNavigateAnim == false) {
                return@runOnUiThread
            }
            // hide navigate anim
            stopNavigateAnim()

            isShowingNavigateAnim = false
            movePhoneRl.visibility = View.VISIBLE
            movePhoneAnimation.setAnimation("movephone.json")
            movePhoneAnimation.loop(true)
            movePhoneAnimation.playAnimation()
        }

    }

    fun stopMovePhoneAnim() {
        activity.runOnUiThread {
            movePhoneAnimation.pauseAnimation()
            movePhoneRl.visibility = View.GONE
        }
    }

    fun startNavigateAnim() {

        activity.runOnUiThread {
            if (isShowingNavigateAnim == true) {
                return@runOnUiThread
            }

            // hide move phone anim
            stopMovePhoneAnim()
            isShowingNavigateAnim = true
            Glide.with(activity)
                .load(R.raw.navigation)
                .into(navigateAnimation)
            navigateAnimation.visibility = View.VISIBLE

        }

    }

    fun stopNavigateAnim() {
        activity.runOnUiThread {
            navigateAnimation.visibility = View.GONE
        }

    }

    fun setInstruction(hint: String) {
        activity.runOnUiThread {
            instructionText.text = hint
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        surfaceView.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        surfaceView.onPause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        WaitingDialog.dismiss()
    }

}
