package com.example.appointment.ui.screen.components.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HorizontalCalendar(
    initialDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit = {}
) {
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }

    // Get all days for the current month
    val currentMonth = YearMonth.from(initialDate)
    val dates = remember {
        generateDatesForMonth(currentMonth)
    }

    // Find the index of today in the list
    val todayIndex = dates.indexOfFirst { it == today }

    // State for the LazyRow
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to today's date when first composed
    LaunchedEffect(Unit) {
        if (todayIndex >= 0) {
            // First scroll to the item so it gets laid out
            scrollState.animateScrollToItem(todayIndex)
            // Scroll to position the current day at the center
            coroutineScope.launch {
                val itemInfo = scrollState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == todayIndex }
                if (itemInfo != null) {
                    val center = scrollState.layoutInfo.viewportEndOffset / 2
                    val childCenter = itemInfo.offset + itemInfo.size / 2
                    scrollState.animateScrollBy((childCenter - center).toFloat())
                } else {
                    scrollState.animateScrollToItem(todayIndex)
                }
            }
        }
    }

    LaunchedEffect(selectedDate) {
        onDateSelected(selectedDate)
    }

    Column(modifier = Modifier.fillMaxWidth()) {

        LazyRow(
            state = scrollState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(dates) { date ->
                val isSelected = date == selectedDate
                val isToday = date == today

                DateItem(
                    date = date,
                    isSelected = isSelected,
                    isToday = isToday,
                    onClick = {
                        selectedDate = date
                        onDateSelected(date)
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val dayOfMonth = date.dayOfMonth.toString()

    val backgroundColor = when {
        isToday -> Color(0xFFEFF6FF)
        isSelected -> Color(0xFF1E88E5)
        else -> Color.Transparent
    }

    val textColor = when {
        isToday -> Color(0xFF1E88E5)
        isSelected -> MaterialTheme.colorScheme.inverseSurface
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .width(48.dp)
            .height(70.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .then(
                if (isToday && !isSelected)
                    Modifier.border(1.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
                else Modifier
            )
            .clickable(onClick = onClick)
    ) {
        Text(
            text = dayOfMonth,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )
        Text(
            text = dayOfWeek,
            fontSize = 12.sp,
            color = if(isToday || isSelected) textColor else MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Normal
        )
    }
}

// Helper function to generate a list of all dates for a specific month
@RequiresApi(Build.VERSION_CODES.O)
private fun generateDatesForMonth(yearMonth: YearMonth): List<LocalDate> {
    val dateList = mutableListOf<LocalDate>()
    val daysInMonth = yearMonth.lengthOfMonth()

    for (day in 1..daysInMonth) {
        dateList.add(yearMonth.atDay(day))
    }

    return dateList
}