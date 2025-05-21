package com.example.appointment.ui.screen.components.appoint

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePicker(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val currentMonth = remember { YearMonth.now() }

    MonthYearSelector(
        month = selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
        year = selectedDate.year
    ) {
        // Handle month/year selection if needed
    }

    DaySelector(
        selectedDate = selectedDate,
        currentMonth = currentMonth,
        onDaySelected = onDateSelected
    )
}

@Composable
fun MonthYearSelector(month: String, year: Int, onDateClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDateClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$month, $year",
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Select Month",
            modifier = Modifier.padding(8.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DaySelector(
    selectedDate: LocalDate,
    currentMonth: YearMonth,
    onDaySelected: (LocalDate) -> Unit
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1)

    // Create a list of all dates in the month
    val datesInMonth = List(daysInMonth) { i -> firstDayOfMonth.plusDays(i.toLong()) }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(datesInMonth) { date ->
            val day = date.dayOfMonth
            val weekday = date.dayOfWeek
                .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                .uppercase()

            val isSelected = date == selectedDate
            val isCurrentMonth = YearMonth.from(date) == currentMonth

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(56.dp)
                    .height(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) Color(0xFF4285F4)
                        else if (isCurrentMonth) Color.LightGray.copy(alpha = 0.3f)
                        else Color.LightGray.copy(alpha = 0.1f)
                    )
                    .clickable { onDaySelected(date) }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = day.toString(),
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isSelected -> Color.White
                            isCurrentMonth -> Color.Black
                            else -> Color.Gray
                        }
                    )
                    Text(
                        text = weekday,
                        color = when {
                            isSelected -> Color.White
                            isCurrentMonth -> Color.Gray
                            else -> Color.LightGray
                        },
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}