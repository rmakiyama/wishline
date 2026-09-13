package com.rmakiyama.wishline.domain

import kotlinx.coroutines.flow.Flow

/** Reads whose answer is not a [Wish]; writes go through [WishRepository]. */
interface WishQueries {
    fun getUnassignedWishesStream(): Flow<List<UnassignedWish>>
}
