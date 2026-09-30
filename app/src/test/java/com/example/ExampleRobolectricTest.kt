package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.service.GeofenceState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Darshan Live", appName)
  }

  @Test
  fun `geofence threshold check`() {
    val insideState = GeofenceState(currentDistanceMeters = 68f, thresholdMeters = 200f)
    assertTrue(insideState.currentDistanceMeters <= insideState.thresholdMeters)
  }
}
