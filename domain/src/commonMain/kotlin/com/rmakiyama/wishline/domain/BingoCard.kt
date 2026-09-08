package com.rmakiyama.wishline.domain

import kotlin.time.Instant

data class BingoCard(
    val id: BingoCardId,
    val number: Int,
    val label: String?,
    val createdAt: Instant,
    val closedAt: Instant?,
    val slots: List<BingoSlot>,
) {
    val isOpen: Boolean get() = closedAt == null

    /** Lines whose five slots are all marked. A slot holding a someday wish is never marked. */
    fun completedLines(): List<BingoLine> {
        val marked = slots.filter { it.status is SlotStatus.Marked }.mapTo(mutableSetOf()) { it.position }
        return BingoLine.All.filter { marked.containsAll(it.positions) }
    }

    companion object {
        const val SLOT_COUNT: Int = BingoLine.SIZE * BingoLine.SIZE
    }
}

/**
 * A slot keeps its position and wish for the life of the card. Only [status] moves, and only while
 * the card is open, which is what leaves a closed card exactly as it was on the day it closed.
 */
data class BingoSlot(
    val position: Int,
    val wish: Wish,
    val status: SlotStatus,
)

sealed interface SlotStatus {
    data object Unmarked : SlotStatus
    data class Marked(val at: Instant) : SlotStatus
}
