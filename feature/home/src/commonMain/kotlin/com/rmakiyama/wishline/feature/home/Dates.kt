package com.rmakiyama.wishline.feature.home

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

internal fun Instant.toLocalDate(): LocalDate = toLocalDateTime(TimeZone.currentSystemDefault()).date

/** 2026/9/12 */
internal fun Instant.toDateText(): String {
    val date = toLocalDate()
    return "${date.year}/${date.month.number}/${date.day}"
}

/** 9/12 */
internal fun Instant.toMonthDayText(): String {
    val date = toLocalDate()
    return "${date.month.number}/${date.day}"
}
