package com.rmakiyama.wishline.domain

import kotlin.time.Instant

data class Wish(
    val id: WishId,
    val title: String,
    val status: WishStatus,
    val createdAt: Instant,
)

/** [Planned] carries a time too: a wish returns to it when it is taken back from done or someday. */
sealed interface WishStatus {
    val at: Instant

    data class Planned(override val at: Instant) : WishStatus
    data class Done(override val at: Instant) : WishStatus
    data class Someday(override val at: Instant) : WishStatus
}
