package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishStatus
import kotlin.time.Instant

internal fun plannedWishes(count: Int): List<Wish> {
    val at = Instant.fromEpochMilliseconds(0)
    return (0 until count).map {
        Wish(
            id = WishId("wish-$it"),
            title = "wish $it",
            status = WishStatus.Planned(at),
            createdAt = at,
        )
    }
}
