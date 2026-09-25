package com.rmakiyama.wishline.domain

import kotlinx.coroutines.flow.Flow

/** Reads whose answer is not a [Wish]; writes go through [WishRepository]. */
interface WishQueries {
    fun getUnassignedWishesStream(): Flow<List<UnassignedWish>>

    /** A someday slot is never marked, so a card keeping one is never answered here. */
    suspend fun fullyMarkedOpenCardId(id: WishId): BingoCardId?
}
