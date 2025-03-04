package com.miquel.firebasetraining.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
fun localDateToDate(localDate: LocalDate): Date {
    val localDateTime = localDate.atTime(LocalTime.MIDNIGHT)
    val zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())
    val instant = zonedDateTime.toInstant()
    return Date.from(instant)
}

@RequiresApi(Build.VERSION_CODES.O)
fun localDateToTimestamp(localDate: LocalDate): Timestamp {
    val date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    return Timestamp(date)
}
