package com.rmakiyama.wishline.domain

import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface WishRepository {
    fun getWishesStream(): Flow<List<Wish>>

    /** Planned wishes that sit on no open card. These are the ones the next card offers. */
    fun getUnassignedWishesStream(): Flow<List<Wish>>

    suspend fun add(wishes: List<Wish>)
    suspend fun changeTitle(id: WishId, title: String, at: Instant)
    suspend fun changeStatus(id: WishId, status: WishStatus)

    /** Only a wish that has never been placed on a card can be deleted; the rest are history. */
    suspend fun delete(id: WishId)
}
