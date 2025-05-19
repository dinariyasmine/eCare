<<<<<<<< HEAD:mobile/appointment/src/androidTest/java/com/example/appointment/ExampleInstrumentedTest.kt
package com.example.appointment
========
package com.example.pop_ups_confirmations_template
>>>>>>>> backend/appointments:mobile/pop-ups-confirmations-template/src/androidTest/java/com/example/pop_ups_confirmations_template/ExampleInstrumentedTest.kt

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
<<<<<<<< HEAD:mobile/appointment/src/androidTest/java/com/example/appointment/ExampleInstrumentedTest.kt
        assertEquals("com.example.appointment", appContext.packageName)
========
        assertEquals("com.example.pop_ups_confirmations_template.test", appContext.packageName)
>>>>>>>> backend/appointments:mobile/pop-ups-confirmations-template/src/androidTest/java/com/example/pop_ups_confirmations_template/ExampleInstrumentedTest.kt
    }
}