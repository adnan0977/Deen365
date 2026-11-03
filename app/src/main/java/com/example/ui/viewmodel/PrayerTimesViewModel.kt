package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DeenDatabase
import com.example.data.local.PrayerTimeEntity
import com.example.data.repository.PrayerTimesRepository
import com.example.domain.CalculationMethod
import com.example.domain.CityPreset
import com.example.domain.PrayerSchedule
import com.example.domain.PrayerTimeCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

data class PrayerTimesUiState(
    val latitude: Double = 21.4225, // Makkah default
    val longitude: Double = 39.8262,
    val cityName: String = "Makkah",
    val timezone: Double = 3.0,
    val calculationMethod: CalculationMethod = CalculationMethod.KARACHI,
    val schedule: PrayerSchedule? = null,
    val savedRecord: PrayerTimeEntity? = null,
    val savedHistory: List<PrayerTimeEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PrayerTimesViewModel(application: Application) : AndroidViewModel(application) {

    private val database = DeenDatabase.getDatabase(application)
    val repository = PrayerTimesRepository(database.prayerTimeDao())

    private val _uiState = MutableStateFlow(PrayerTimesUiState())
    val uiState: StateFlow<PrayerTimesUiState> = _uiState.asStateFlow()

    init {
        // Calculate initial times for default coordinates and observe saved history
        calculateAndStore()
        observeSavedHistory()
    }

    private fun observeSavedHistory() {
        viewModelScope.launch {
            repository.getAllSavedPrayerTimes().collect { list ->
                _uiState.update { it.copy(savedHistory = list) }
            }
        }
    }

    fun updateCoordinates(
        latitude: Double,
        longitude: Double,
        cityName: String = "Custom Location",
        timezone: Double = (TimeZone.getDefault().rawOffset / 3600000.0)
    ) {
        _uiState.update {
            it.copy(
                latitude = latitude,
                longitude = longitude,
                cityName = cityName,
                timezone = timezone
            )
        }
        calculateAndStore()
    }

    fun selectCity(city: CityPreset) {
        updateCoordinates(
            latitude = city.latitude,
            longitude = city.longitude,
            cityName = city.name,
            timezone = city.timezone
        )
    }

    fun updateCalculationMethod(method: CalculationMethod) {
        _uiState.update { it.copy(calculationMethod = method) }
        calculateAndStore()
    }

    fun calculateAndStore() {
        val current = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val cal = Calendar.getInstance()
                val schedule = repository.calculateAndStorePrayerTimes(
                    latitude = current.latitude,
                    longitude = current.longitude,
                    timezone = current.timezone,
                    cityName = current.cityName,
                    method = current.calculationMethod,
                    calendar = cal
                )

                val dateStr = "%04d-%02d-%02d".format(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH)
                )

                val savedRecord = repository.getSavedPrayerTimesSync(dateStr)

                _uiState.update {
                    it.copy(
                        schedule = schedule,
                        savedRecord = savedRecord,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to calculate prayer times"
                    )
                }
            }
        }
    }
}
