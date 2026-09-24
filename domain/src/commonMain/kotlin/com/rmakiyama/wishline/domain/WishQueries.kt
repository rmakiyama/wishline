package com.rmakiyama.wishline.domain

import kotlinx.coroutines.flow.Flow

/** Reads whose answer is not a [Wish]; writes go through [WishRepository]. */
interface WishQueries {
    fun getUnassignedWishesStream(): Flow<List<UnassignedWish>>

    /** The open card holding this wish, once none of its slots is left unmarked. */
    suspend fun fullyMarkedOpenCardId(id: WishId): BingoCardId?
}
