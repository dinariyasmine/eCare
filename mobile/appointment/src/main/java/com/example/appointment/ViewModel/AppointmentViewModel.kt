package com.example.appointment.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Availability
import com.example.data.repository.AvailabilityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AppointmentViewModel(private val repository: AvailabilityRepository) : ViewModel() {
    private val _availabilities = MutableStateFlow<List<Availability>>(emptyList())
    val availabilities: StateFlow<List<Availability>> = _availabilities.asStateFlow()

    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> = _selectedDate.asStateFlow()

    private val _selectedDoctorId = MutableStateFlow<Int?>(null)
    val selectedDoctorId: StateFlow<Int?> = _selectedDoctorId.asStateFlow()

    // Set selected date
    fun setSelectedDate(date: Date) {
        _selectedDate.value = date
        loadAvailabilitiesForDate(date)
    }

    // Set selected doctor
    fun setSelectedDoctor(doctorId: Int) {
        _selectedDoctorId.value = doctorId
        _selectedDate.value?.let { loadAvailabilitiesForDate(it) }
    }

    // Load availabilities for a specific date
    private fun loadAvailabilitiesForDate(date: Date) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val startDate = calendar.time

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val endDate = calendar.time

            val availabilitiesForDate = repository.getAvailabilitiesByDateRange(startDate, endDate)

            // Filter by doctor if selected
            val filteredAvailabilities = _selectedDoctorId.value?.let { doctorId ->
                availabilitiesForDate.filter { it.doctor_id == doctorId }
            } ?: availabilitiesForDate

            _availabilities.value = filteredAvailabilities
        }
    }

    // Get all available time slots for the selected date
    fun getAvailableTimeSlots(): List<String> {
        val timeSlots = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("hh:mm a")

        _availabilities.value.forEach { availability ->
            // Create 30-minute slots within each availability period
            val calendar = Calendar.getInstance()
            calendar.time = availability.start_time

            while (calendar.time < availability.end_time) {
                timeSlots.add(dateFormat.format(calendar.time))
                calendar.add(Calendar.MINUTE, 30) // Add 30 minutes for each slot
            }
        }

        return timeSlots.sorted()
    }

    // Check if a time slot is available
    fun isTimeSlotAvailable(timeSlot: String): Boolean {
        val dateFormat = SimpleDateFormat("hh:mm a")
        val slotTime = dateFormat.parse(timeSlot) ?: return false

        // Compare only hours and minutes
        val calendar = Calendar.getInstance()

        for (availability in _availabilities.value) {
            calendar.time = availability.start_time
            val startHour = calendar.get(Calendar.HOUR_OF_DAY)
            val startMinute = calendar.get(Calendar.MINUTE)

            calendar.time = availability.end_time
            val endHour = calendar.get(Calendar.HOUR_OF_DAY)
            val endMinute = calendar.get(Calendar.MINUTE)

            calendar.time = slotTime
            val slotHour = calendar.get(Calendar.HOUR_OF_DAY)
            val slotMinute = calendar.get(Calendar.MINUTE)

            // Check if the slot is within the availability
            if ((slotHour > startHour || (slotHour == startHour && slotMinute >= startMinute)) &&
                (slotHour < endHour || (slotHour == endHour && slotMinute <= endMinute))) {
                return true
            }
        }

        return false
    }
}