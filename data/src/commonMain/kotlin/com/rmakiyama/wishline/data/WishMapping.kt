package com.rmakiyama.wishline.data

import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishStatus
import kotlin.time.Instant

internal const val STATUS_PLANNED = "PLANNED"
internal const val STATUS_DONE = "DONE"
internal const val STATUS_SOMEDAY = "SOMEDAY"

internal fun WishStatus.column(): String = when (this) {
    is WishStatus.Planned -> STATUS_PLANNED
    is WishStatus.Done -> STATUS_DONE
    is WishStatus.Someday -> STATUS_SOMEDAY
}

internal fun wishStatusOf(status: String, statusAt: Long): WishStatus {
    val at = Instant.fromEpochMilliseconds(statusAt)
    return when (status) {
        STATUS_PLANNED -> WishStatus.Planned(at)
        STATUS_DONE -> WishStatus.Done(at)
        STATUS_SOMEDAY -> WishStatus.Someday(at)
        else -> error("Unknown wish status: $status")
    }
}

internal fun toWish(
    id: String,
    title: String,
    status: String,
    statusAt: Long,
    createdAt: Long,
): Wish = Wish(
    id = WishId(id),
    title = title,
    status = wishStatusOf(status, statusAt),
    createdAt = Instant.fromEpochMilliseconds(createdAt),
)
