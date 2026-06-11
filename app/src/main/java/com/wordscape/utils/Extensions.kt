package com.wordscape.utils

import android.text.format.DateUtils
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Color.withGlow(glowColor: Color = this, radius: Float = 20f): Color {
    return this.copy(alpha = 0.8f)
}

fun Long.toRelativeTimeString(): String {
    return DateUtils.getRelativeTimeSpanString(
        this,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()
}

fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

fun String.toPhonetic(): String {
    return when (this.uppercase()) {
        "CAT" -> "/kæt/"
        "DOG" -> "/dɒɡ/"
        "SUN" -> "/sʌn/"
        "BEE" -> "/biː/"
        "OWL" -> "/aʊl/"
        "FOX" -> "/fɒks/"
        "APPLE" -> "/ˈæp.l̩/"
        "HOUSE" -> "/haʊs/"
        "WATER" -> "/ˈwɔː.tər/"
        "FLOWER" -> "/ˈflaʊ.ər/"
        "CLOUD" -> "/klaʊd/"
        "RIVER" -> "/ˈrɪv.ər/"
        else -> "/${this.lowercase()}/"
    }
}

fun Offset.distanceTo(other: Offset): Float {
    return kotlin.math.hypot(this.x - other.x, this.y - other.y)
}

fun Offset.lerp(target: Offset, fraction: Float): Offset {
    val x = this.x + (target.x - this.x) * fraction
    val y = this.y + (target.y - this.y) * fraction
    return Offset(x, y)
}

fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Good Morning, Explorer ✨"
        in 12..16 -> "Good Afternoon, Explorer ✨"
        else -> "Good Evening, Explorer ✨"
    }
}
