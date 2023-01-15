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
package com.google.ar.core.codelabs.arlocalizer.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.codelabs.arlocalizer.ChatLocalizeRenderer
import com.google.ar.core.codelabs.arlocalizer.SingleLocalizeRender
import com.google.ar.core.codelabs.arlocalizer.consts.Configs
import com.google.ar.core.codelabs.arlocalizer.helpers.ARCoreSessionLifecycleHelper
import com.google.ar.core.codelabs.arlocalizer.helpers.GeoPermissionsHelper
import com.google.ar.core.codelabs.arlocalizer.helpers.LocalizeView
import com.google.ar.core.codelabs.arlocalizer.helpers.StaticMapView
import com.google.ar.core.examples.java.common.helpers.FullScreenHelper
import com.google.ar.core.examples.java.common.samplerender.SampleRender
import com.google.ar.core.examples.java.common.samplerender.SampleRender.Renderer
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException

class LocalizeActivity : AppCompatActivity() {
  companion object {
    private const val TAG = "HelloGeoActivity"
  }

  lateinit var arCoreSessionHelper: ARCoreSessionLifecycleHelper
  lateinit var view: LocalizeView
  lateinit var chatRenderer: ChatLocalizeRenderer
  lateinit var singleRenderer: SingleLocalizeRender

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val localizeMode = intent.getIntExtra("mode", Configs.static_mode)
    // Setup ARCore session lifecycle helper and configuration.
    arCoreSessionHelper = ARCoreSessionLifecycleHelper(this)
    // If Session creation or Session.resume() fails, display a message and log detailed
    // information.
    arCoreSessionHelper.exceptionCallback =
      { exception ->
        val message =
          when (exception) {
            is UnavailableUserDeclinedInstallationException ->
              "Please install Google Play Services for AR"
            is UnavailableApkTooOldException -> "Please update ARCore"
            is UnavailableSdkTooOldException -> "Please update this app"
            is UnavailableDeviceNotCompatibleException -> "This device does not support AR"
            is CameraNotAvailableException -> "Camera not available. Try restarting the app."
            else -> "Failed to create AR session: $exception"
          }
        Log.e(TAG, "ARCore threw an exception", exception)
        view.snackbarHelper.showError(this, message)
      }

    // Configure session features.
    arCoreSessionHelper.beforeSessionResume = ::configureSession
    lifecycle.addObserver(arCoreSessionHelper)


    // Set up Hello AR UI.
    view = LocalizeView(this, localizeMode)
    lifecycle.addObserver(view)
    setContentView(view.root)

    if (localizeMode == Configs.chat_mode) {
      // Set up the Hello AR renderer.
      chatRenderer = ChatLocalizeRenderer(this)
      lifecycle.addObserver(chatRenderer)

      // Sets up an example renderer using our HelloGeoRenderer.
      SampleRender(view.surfaceView, chatRenderer, assets)
    } else {
      // Set up the Hello AR renderer.
      singleRenderer = SingleLocalizeRender(this)
      lifecycle.addObserver(singleRenderer)

      // Sets up an example renderer using our HelloGeoRenderer.
      SampleRender(view.surfaceView, singleRenderer, assets)
    }


  }

  // Configure the session, setting the desired options according to your usecase.
  fun configureSession(session: Session) {
    session.configure(
      session.config.apply {
        cloudAnchorMode = Config.CloudAnchorMode.ENABLED
        geospatialMode = Config.GeospatialMode.ENABLED
      }
    )
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    results: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, results)
    if (!GeoPermissionsHelper.hasGeoPermissions(this)) {
      // Use toast instead of snackbar here since the activity will exit.
      Toast.makeText(this, "Camera and location permissions are needed to run this application", Toast.LENGTH_LONG)
        .show()
      if (!GeoPermissionsHelper.shouldShowRequestPermissionRationale(this)) {
        // Permission denied with checking "Do not ask again".
        GeoPermissionsHelper.launchPermissionSettings(this)
      }
      finish()
    }
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
  }
}
