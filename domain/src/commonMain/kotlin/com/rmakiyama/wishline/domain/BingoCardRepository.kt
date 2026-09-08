package com.rmakiyama.wishline.domain

import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

interface BingoCardRepository {
    fun getOpenCardsStream(): Flow<List<BingoCard>>
    fun getClosedCardsStream(): Flow<List<BingoCard>>

    /** Places the layout's wishes in their given order, one per slot, so the caller owns the shuffle. */
    suspend fun create(id: BingoCardId, layout: BingoCardLayout, at: Instant)
    suspend fun close(id: BingoCardId, at: Instant)
    suspend fun changeLabel(id: BingoCardId, label: String?)
}
