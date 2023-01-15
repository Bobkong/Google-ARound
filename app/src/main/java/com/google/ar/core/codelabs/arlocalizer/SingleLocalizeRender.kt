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
package com.google.ar.core.codelabs.arlocalizer

import android.opengl.Matrix
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.Anchor
import com.google.ar.core.TrackingState
import com.google.ar.core.codelabs.arlocalizer.activity.LocalizeActivity
import com.google.ar.core.codelabs.arlocalizer.model.CloudAnchor
import com.google.ar.core.examples.java.common.helpers.DisplayRotationHelper
import com.google.ar.core.examples.java.common.helpers.TrackingStateHelper
import com.google.ar.core.examples.java.common.samplerender.*
import com.google.ar.core.examples.java.common.samplerender.arcore.BackgroundRenderer
import com.google.ar.core.exceptions.CameraNotAvailableException
import java.io.IOException
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt


class SingleLocalizeRender(val activity: LocalizeActivity) :
  SampleRender.Renderer, DefaultLifecycleObserver {
  //<editor-fold desc="ARCore initialization" defaultstate="collapsed">
  companion object {
    val TAG = "SingleLocalizeRender"

    private val Z_NEAR = 0.1f
    private val Z_FAR = 1000f
  }

  lateinit var backgroundRenderer: BackgroundRenderer
  lateinit var virtualSceneFramebuffer: Framebuffer
  var hasSetTextureNames = false

  // Virtual object (ARCore pawn)
  lateinit var virtualObjectMesh: Mesh
  lateinit var virtualObjectShader: Shader
  lateinit var virtualObjectTexture: Texture

  // Temporary matrix allocated here to reduce number of allocations for each frame.
  val modelMatrix = FloatArray(16)
  val viewMatrix = FloatArray(16)
  val projectionMatrix = FloatArray(16)
  val modelViewMatrix = FloatArray(16) // view x model

  val modelViewProjectionMatrix = FloatArray(16) // projection x view x model

  val session
    get() = activity.arCoreSessionHelper.session

  var cloudAnchorManager: CloudAnchorManager? = null

  val displayRotationHelper = DisplayRotationHelper(activity)
  val trackingStateHelper = TrackingStateHelper(activity)

  var currentAnchor: Anchor? = null
  private val anchorLock = Any()
  private var hasStartedWaiting = false

  override fun onResume(owner: LifecycleOwner) {
    displayRotationHelper.onResume()
    hasSetTextureNames = false
  }

  override fun onPause(owner: LifecycleOwner) {
    displayRotationHelper.onPause()
  }

  override fun onSurfaceCreated(render: SampleRender) {
    // Prepare the rendering objects.
    // This involves reading shaders and 3D model files, so may throw an IOException.
    try {
      backgroundRenderer = BackgroundRenderer(render)
      virtualSceneFramebuffer = Framebuffer(render, /*width=*/ 1, /*height=*/ 1)

      // Virtual object to render (Geospatial Marker)
      virtualObjectTexture =
        Texture.createFromAsset(
          render,
          "models/dest_texture.png",
          Texture.WrapMode.CLAMP_TO_EDGE,
          Texture.ColorFormat.SRGB
        )

      virtualObjectMesh = Mesh.createFromAsset(render, "models/geospatial_marker.obj")
      virtualObjectShader =
        Shader.createFromAssets(
          render,
          "shaders/ar_unlit_object.vert",
          "shaders/ar_unlit_object.frag",
          /*defines=*/ null)
          .setTexture("u_Texture", virtualObjectTexture)


      backgroundRenderer.setUseDepthVisualization(render, false)
      backgroundRenderer.setUseOcclusion(render, false)
    } catch (e: IOException) {
      Log.e(TAG, "Failed to read a required asset file", e)
      showError("Failed to read a required asset file: $e")
    }
  }

  override fun onSurfaceChanged(render: SampleRender, width: Int, height: Int) {
    displayRotationHelper.onSurfaceChanged(width, height)
    virtualSceneFramebuffer.resize(width, height)
  }
  //</editor-fold>

  override fun onDrawFrame(render: SampleRender) {
    val session = session ?: return

    //<editor-fold desc="ARCore frame boilerplate" defaultstate="collapsed">
    // Texture names should only be set once on a GL thread unless they change. This is done during
    // onDrawFrame rather than onSurfaceCreated since the session is not guaranteed to have been
    // initialized during the execution of onSurfaceCreated.
    if (!hasSetTextureNames) {
      session.setCameraTextureNames(intArrayOf(backgroundRenderer.cameraColorTexture.textureId))
      hasSetTextureNames = true
    }

    // -- Update per-frame state

    // Notify ARCore session that the view size changed so that the perspective matrix and
    // the video background can be properly adjusted.
    displayRotationHelper.updateSessionIfNeeded(session)

    // Obtain the current frame from ARSession. When the configuration is set to
    // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
    // camera framerate.
    val frame =
      try {
        session.update()
      } catch (e: CameraNotAvailableException) {
        Log.e(TAG, "Camera not available during onDrawFrame", e)
        showError("Camera not available. Try restarting the app.")
        return
      }

    val camera = frame.camera

    // BackgroundRenderer.updateDisplayGeometry must be called every frame to update the coordinates
    // used to draw the background camera image.
    backgroundRenderer.updateDisplayGeometry(frame)

    // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
    trackingStateHelper.updateKeepScreenOnFlag(camera.trackingState)

    // -- Draw background
    if (frame.timestamp != 0L) {
      // Suppress rendering if the camera did not produce the first frame yet. This is to avoid
      // drawing possible leftover data from previous sessions if the texture is reused.
      backgroundRenderer.drawBackground(render)
    }

    // If not tracking, don't draw 3D objects.
    if (camera.trackingState == TrackingState.PAUSED) {
      return
    }

    // Get projection matrix.
    camera.getProjectionMatrix(projectionMatrix, 0, Z_NEAR, Z_FAR)

    // Get camera matrix and draw.
    camera.getViewMatrix(viewMatrix, 0)

    render.clear(virtualSceneFramebuffer, 0f, 0f, 0f, 0f)
    //</editor-fold>

    if (cloudAnchorManager == null) {
      cloudAnchorManager = CloudAnchorManager(session)
    }
    // Notify the cloudAnchorManager of all the updates.
    cloudAnchorManager?.onUpdate()

    val earth = session.earth
    if (earth?.trackingState == TrackingState.TRACKING) {
      val cameraGeospatialPose = earth.cameraGeospatialPose
      activity.view.mapView?.updateMapPosition(
        latitude = cameraGeospatialPose.latitude,
        longitude = cameraGeospatialPose.longitude,
        heading = cameraGeospatialPose.heading
      )

    }

    updateNavigation()

    // Draw the placed anchor, if it exists.
    destinationAnchor?.let {
      render.renderCompassAtAnchor(it)
    }


//    navigationAnchor?.let {
//      if (distance != null && distance!! > 5) {
//        render.renderNavigationAtAnchor(it)
//      }
//    }

    activity.runOnUiThread {
      startNavigation()
    }

    navigationHeading?.let {



      if (distance != null && distance!! < 5) {
        // reaching the destination, don't show animation
        activity.view.stopNavigateAnim()
        activity.view.stopMovePhoneAnim()
        // show success slogan
        activity.view.showLookAround("Congratulations, the destination is next to you! Look around and find it!")
      } else if (abs(earth!!.cameraGeospatialPose.heading.minus(it)) < 20) {
        //hide loading layout
        activity.view.dismissLoadingLl()
        // show navigate animation
        activity.view.startNavigateAnim()
      } else {
        //hide loading layout
        activity.view.dismissLoadingLl()
        // show move animation
        activity.view.startMovePhoneAnim()
      }

    }

    // Compose the virtual scene with the backgroundwo.
    backgroundRenderer.drawVirtualScene(render, virtualSceneFramebuffer, Z_NEAR, Z_FAR)
  }

  var timer = Timer()
  // wait friend to join
  private fun updateNavigation() {

    val earth = session?.earth

    if (earth == null || earth.trackingState != TrackingState.TRACKING || hasStartedWaiting) {
      return
    }

    hasStartedWaiting = true
    // create a static anchor around user
    val pose = earth.cameraGeospatialPose
    val anchor = CloudAnchor("",  ((pose.latitude * 10000f).roundToInt() / 10000f).toDouble(),
      ((pose.longitude * 10000f).roundToInt() / 10000f).toDouble(), pose.altitude)

    placeFriendAnchor(anchor)
  }

  var destinationAnchor: Anchor? = null
  var destinationCoordinate: GeoCoordinate? = null

  private fun placeFriendAnchor(cloudAnchor: CloudAnchor) {
    val earth = session?.earth ?: return
    if (earth.trackingState != TrackingState.TRACKING) {
      return
    }

    // Place the earth anchor at the same altitude as that of the camera to make it easier to view.
    // The rotation quaternion of the anchor in the East-Up-South (EUS) coordinate system.
    val qx = 0f
    val qy = 0f
    val qz = 0f
    val qw = 0f
    destinationCoordinate = GeoCoordinate(cloudAnchor.latitude, cloudAnchor.longitude, earth.cameraGeospatialPose.altitude)

    destinationAnchor =
      earth.createAnchor(cloudAnchor.latitude, cloudAnchor.longitude, earth.cameraGeospatialPose.altitude, qx, qy, qz, qw)


    activity.view.mapView?.earthMarker?.apply {
      activity.runOnUiThread {
        position = LatLng(cloudAnchor.latitude, cloudAnchor.longitude)
        isVisible = true
      }
    }

  }

  var navigationAnchor: Anchor? = null
  var navigationHeading : Double? = null

  fun startNavigation() {

    if (destinationAnchor == null) {
      return
    }

    val earth = session?.earth ?: return
    if (earth.trackingState != TrackingState.TRACKING) {
      return
    }

    navigationAnchor?.detach()

    navigationHeading = activity.view.mapView?.courseAngle()

    if (navigationHeading == null) {
      return
    }

    // Place the earth anchor at the same altitude as that of the camera to make it easier to view.
    val altitude = earth.cameraGeospatialPose.altitude - 1.3
    // The rotation quaternion of the anchor in the East-Up-South (EUS) coordinate system.
    val currentCoordinate = GeoCoordinate(earth.cameraGeospatialPose.latitude, earth.cameraGeospatialPose.longitude, earth.cameraGeospatialPose.altitude)
//    val geoCoordinateAhead = Mercator.calculateDerivedPosition(currentCoordinate, 3.5, navigationHeading!!)
//    val qx = 0f
//    val qy = 0f
//    val qz = 0f
//    val qw = 1f
//    navigationAnchor = earth.createAnchor(geoCoordinateAhead.latitude, geoCoordinateAhead.longitude, altitude, qx, qy, qz, qw)

    updateDistance(currentCoordinate)

  }

  var distance: Int? = null
  private fun updateDistance(currentCoordinate: GeoCoordinate) {
    destinationCoordinate?.let {
      distance = currentCoordinate.calculateDistanceTo(it)
      activity.view.updateDistanceText(distance.toString().plus("m"))
    }
  }

  private fun SampleRender.renderCompassAtAnchor(anchor: Anchor) {
    // Get the current pose of the Anchor in world space. The Anchor pose is updated
    // during calls to session.update() as ARCore refines its estimate of the world.
    anchor.pose.toMatrix(modelMatrix, 0)

    // Calculate model/view/projection matrices
    Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
    Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)
    Matrix.scaleM(modelViewProjectionMatrix, 0, 0.6f, 0.6f, 0.6f)

    // Update shader properties and draw
    virtualObjectShader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix)
    draw(virtualObjectMesh, virtualObjectShader, virtualSceneFramebuffer)
  }

  private fun showError(errorMessage: String) =
    activity.view.snackbarHelper.showError(activity, errorMessage)

}
