package com.example.doctorlisting.ui.component

import com.adamglin.PhosphorIcons
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doctorlisting.data.model.Appointment

@Composable
fun ScheduleTimeline(appointments: List<Appointment>) {
    Column {
        appointments.forEach {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(it.time, modifier = Modifier.width(60.dp), color = Color.Gray)
                Divider(color = Color.Blue, modifier = Modifier.width(10.dp).height(1.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Card(
                    shape = RoundedCornerShape(10.dp),
                    backgroundColor = Color(0xFFE6F0FB),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Column {
                            Text(it.doctorName, fontWeight = FontWeight.Bold)
                            Text(
                                it.status,
                                color = Color.Blue,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .background(Color(0xFFD0E7FF), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
