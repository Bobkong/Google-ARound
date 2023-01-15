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
import android.widget.LinearLayout
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
import com.google.ar.core.codelabs.arlocalizer.consts.Configs
import com.google.ar.core.codelabs.arlocalizer.utils.PixelUtil
import com.google.ar.core.codelabs.arlocalizer.widget.ProgressView
import com.google.ar.core.examples.java.common.helpers.SnackbarHelper


/** Contains UI elements for Hello Geo. */
class LocalizeView(val activity: LocalizeActivity, val mode: Int) : DefaultLifecycleObserver {
    val TAG = "LocalizeView"
    val root = View.inflate(activity, R.layout.activity_localize, null)
    val surfaceView = root.findViewById<GLSurfaceView>(R.id.surfaceview)
    val distanceText = root.findViewById<TextView>(R.id.distance_text)
    val movePhoneAnimation = root.findViewById<LottieAnimationView>(R.id.move_phone_animation)
    val movePhoneRl = root.findViewById<RelativeLayout>(R.id.move_phone_rl)
    var isShowingNavigateAnim: Boolean? = null
    val back = root.findViewById<ImageView>(R.id.back)
    val navigateAnimation = root.findViewById<ImageView>(R.id.navigate_animation)
    val distanceRl = root.findViewById<RelativeLayout>(R.id.distance_rl)
    val loadingLl = root.findViewById<LinearLayout>(R.id.loading_ll)
    val loadingText = root.findViewById<TextView>(R.id.loading_text)
    val lookAroundAnimation = root.findViewById<LottieAnimationView>(R.id.look_around_animation)
    val progressAnimation = root.findViewById<ProgressView>(R.id.rotateloading)

    val session
        get() = activity.arCoreSessionHelper.session

    val snackbarHelper = SnackbarHelper()

    var mapView: ChatMapView? = null
    val mapTouchWrapper = root.findViewById<MapTouchWrapper>(R.id.map_wrapper).apply {
        setup { screenLocation ->
//            val latLng: LatLng =
//                mapView?.googleMap?.projection?.fromScreenLocation(screenLocation) ?: return@setup
//            activity.renderer.onMapClick(latLng)
        }
    }
    val mapFragment =
        (activity.supportFragmentManager.findFragmentById(R.id.map)!! as SupportMapFragment).also {
            it.getMapAsync { googleMap -> mapView = if (mode == Configs.chat_mode) ChatMapView(activity, googleMap)
            else StaticMapView(activity, googleMap)}
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
        back.setOnClickListener {
            activity.finish()
        }

        val rlParams = navigateAnimation.layoutParams as ConstraintLayout.LayoutParams

        val wm = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val height = wm.defaultDisplay.height
        rlParams.height = (height - PixelUtil.convertDpToPixel(430f)).toInt()

        navigateAnimation.setLayoutParams(rlParams)
    }

    fun updateDistanceText(distance: String) {
        distanceRl.visibility = View.VISIBLE
        distanceText.text = distance
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

    fun showLoading(hint: String) {
        activity.runOnUiThread {
            loadingText.text = hint
            lookAroundAnimation.visibility = View.GONE
            progressAnimation.visibility = View.VISIBLE
            loadingLl.visibility = View.VISIBLE
        }
    }

    var hasShownLookAroundAnim = false
    fun showLookAround(hint: String) {
        if (hasShownLookAroundAnim) {
            return
        }
        hasShownLookAroundAnim = true
        activity.runOnUiThread {
            loadingText.text = hint
            progressAnimation.visibility = View.GONE
            lookAroundAnimation.visibility = View.VISIBLE
            loadingLl.visibility = View.VISIBLE

            if (!lookAroundAnimation.isAnimating) {
                lookAroundAnimation.setAnimation("look_around.json")
                lookAroundAnimation.loop(true)
                lookAroundAnimation.playAnimation()
            }

            Handler(Looper.getMainLooper()).postDelayed({
                lookAroundAnimation.pauseAnimation()
                loadingLl.visibility = View.GONE
            }, 5000)

        }
    }

    fun dismissLoadingLl() {
        activity.runOnUiThread {
            loadingLl.visibility = View.GONE
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
    }

}
