package com.example.sensor

import android.content.Context
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.sqrt

data class QiblaSensorData(
    val headingDegrees: Float = 0f,          // Smooth heading [0, 360)
    val magneticHeading: Float = 0f,         // Raw magnetic heading
    val trueHeading: Float = 0f,             // True North heading compensated for declination
    val declination: Float = 0f,             // Magnetic declination in degrees (+ East, - West)
    val isSensorAvailable: Boolean = true,
    val accuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_HIGH,
    val pitch: Float = 0f,                   // Tilt forward/backward in degrees (-90 to +90)
    val roll: Float = 0f,                    // Tilt left/right in degrees (-180 to +180)
    val isFlat: Boolean = true,              // True if held horizontally within ±18 degrees
    val magneticFieldStrength: Float = 45f,  // Magnitude in microteslas (µT)
    val isMagneticInterference: Boolean = false, // True if field strength is abnormal
    val sensorTypeUsed: String = "Rotation Vector"
)

class QiblaSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val rotationVectorSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private val _sensorData = MutableStateFlow(
        QiblaSensorData(
            isSensorAvailable = (accelerometer != null && magnetometer != null) || rotationVectorSensor != null
        )
    )
    val sensorData: StateFlow<QiblaSensorData> = _sensorData.asStateFlow()

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var hasAccelerometerData = false
    private var hasMagnetometerData = false
    private var currentHeading = 0f
    private var currentDeclination = 0f
    private var lastMagneticFieldStrength = 45f
    private var isListening = false
    private var activeSensorType = if (rotationVectorSensor != null) "Rotation Vector" else "Accelerometer + Magnetometer"

    // Exponential smoothing factor for low-pass filter
    private val alpha = 0.20f

    fun setLocation(latitude: Double, longitude: Double, altitude: Double = 0.0) {
        try {
            val gmf = GeomagneticField(
                latitude.toFloat(),
                longitude.toFloat(),
                altitude.toFloat(),
                System.currentTimeMillis()
            )
            currentDeclination = gmf.declination
        } catch (_: Exception) {
            currentDeclination = 0f
        }
    }

    fun startListening() {
        if (isListening) return

        if (rotationVectorSensor != null) {
            sensorManager.registerListener(
                this,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_UI
            )
            // Also register magnetometer for field strength interference detection
            magnetometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            activeSensorType = "Hardware Rotation Vector (Fused)"
        } else {
            accelerometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            magnetometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            activeSensorType = "Accelerometer + Magnetometer"
        }
        isListening = true
    }

    fun stopListening() {
        if (!isListening) return
        sensorManager.unregisterListener(this)
        hasAccelerometerData = false
        hasMagnetometerData = false
        isListening = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                calculateOrientation(event.accuracy, "Hardware Rotation Vector (Fused)")
            }

            Sensor.TYPE_ACCELEROMETER -> {
                // Low-pass filter for gravity vector
                accelerometerReading[0] = alpha * event.values[0] + (1 - alpha) * accelerometerReading[0]
                accelerometerReading[1] = alpha * event.values[1] + (1 - alpha) * accelerometerReading[1]
                accelerometerReading[2] = alpha * event.values[2] + (1 - alpha) * accelerometerReading[2]
                hasAccelerometerData = true
                if (hasMagnetometerData) {
                    processSensors(event.accuracy)
                }
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                // Compute magnetic field magnitude in microteslas (µT)
                val bx = event.values[0]
                val by = event.values[1]
                val bz = event.values[2]
                lastMagneticFieldStrength = sqrt(bx * bx + by * by + bz * bz)

                // Low-pass filter for magnetic field vector
                magnetometerReading[0] = alpha * event.values[0] + (1 - alpha) * magnetometerReading[0]
                magnetometerReading[1] = alpha * event.values[1] + (1 - alpha) * magnetometerReading[1]
                magnetometerReading[2] = alpha * event.values[2] + (1 - alpha) * magnetometerReading[2]
                hasMagnetometerData = true
                if (hasAccelerometerData && rotationVectorSensor == null) {
                    processSensors(event.accuracy)
                }
            }
        }
    }

    private fun processSensors(accuracy: Int) {
        val success = SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )
        if (success) {
            calculateOrientation(accuracy, "Accelerometer + Magnetometer")
        }
    }

    private fun calculateOrientation(accuracy: Int, sensorType: String) {
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        // azimuth is in orientationAngles[0], pitch in [1], roll in [2]
        val azimuthRad = orientationAngles[0]
        val pitchRad = orientationAngles[1]
        val rollRad = orientationAngles[2]

        val targetHeading = ((Math.toDegrees(azimuthRad.toDouble()) + 360.0) % 360.0).toFloat()

        // Dynamic exponential smoothing: responsive when moving fast, stable when steady
        var diff = targetHeading - currentHeading
        while (diff < -180f) diff += 360f
        while (diff > 180f) diff -= 360f

        val dynamicSmoothing = if (abs(diff) > 12f) 0.35f else 0.16f
        currentHeading = ((currentHeading + diff * dynamicSmoothing + 360f) % 360f)

        val pitchDeg = Math.toDegrees(pitchRad.toDouble()).toFloat()
        val rollDeg = Math.toDegrees(rollRad.toDouble()).toFloat()

        // Level horizontal threshold: within ±18 degrees on both axes
        val isFlat = abs(pitchDeg) < 18f && abs(rollDeg) < 18f

        // True North heading compensated for magnetic declination
        val trueHeading = ((currentHeading + currentDeclination + 360f) % 360f)

        // Earth's natural magnetic field range is usually ~25 µT to ~65 µT
        val isInterference = lastMagneticFieldStrength < 20f || lastMagneticFieldStrength > 75f

        _sensorData.value = QiblaSensorData(
            headingDegrees = currentHeading,
            magneticHeading = targetHeading,
            trueHeading = trueHeading,
            declination = currentDeclination,
            isSensorAvailable = true,
            accuracy = accuracy,
            pitch = pitchDeg,
            roll = rollDeg,
            isFlat = isFlat,
            magneticFieldStrength = lastMagneticFieldStrength,
            isMagneticInterference = isInterference,
            sensorTypeUsed = sensorType
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _sensorData.value = _sensorData.value.copy(accuracy = accuracy)
    }
}
