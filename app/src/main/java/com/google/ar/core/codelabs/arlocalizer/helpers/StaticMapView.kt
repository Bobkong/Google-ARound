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

import android.graphics.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.ar.core.codelabs.arlocalizer.activity.LocalizeActivity
import com.google.ar.core.codelabs.arlocalizer.R
import com.google.ar.core.codelabs.arlocalizer.utils.PreferenceUtils

class StaticMapView(activity: LocalizeActivity, googleMap: GoogleMap):
  ChatMapView(activity, googleMap){

  override fun createColoredMarkerBitmap(isSelf: Boolean): Bitmap {
    val username = PreferenceUtils.getNickname()
    var resource: Int?
    if (username == "Liz") {
      resource = if (isSelf) R.drawable.female_marker else R.drawable.static_marker
    } else {
      resource = if (isSelf) R.drawable.male_marker else R.drawable.static_marker
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
}