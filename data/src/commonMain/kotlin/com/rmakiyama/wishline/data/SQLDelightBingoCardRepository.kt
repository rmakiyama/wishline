package com.rmakiyama.wishline.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.rmakiyama.wishline.data.db.WishlineDatabase
import com.rmakiyama.wishline.domain.BingoCard
import com.rmakiyama.wishline.domain.BingoCardLayout
import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.BingoCardRepository
import com.rmakiyama.wishline.domain.BingoSlot
import com.rmakiyama.wishline.domain.SlotStatus
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Instant

@Inject
class SQLDelightBingoCardRepository(
    private val database: WishlineDatabase,
) : BingoCardRepository {

    private val cardQueries = database.bingoCardQueries
    private val slotQueries = database.bingoSlotQueries

    override fun getOpenCardsStream(): Flow<List<BingoCard>> {
        return cardQueries.selectOpenSlots(::CardSlotRow)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.toCards() }
    }

    override fun getClosedCardsStream(): Flow<List<BingoCard>> {
        return cardQueries.selectClosedSlots(::CardSlotRow)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.toCards() }
    }

    override suspend fun create(id: BingoCardId, layout: BingoCardLayout, at: Instant) {
        val wishIds = layout.wishes.map { it.id }
        withContext(Dispatchers.IO) {
            database.transaction {
                val onOpenCard = slotQueries
                    .countOpenSlotsForWishes(wishIds.map { it.value })
                    .executeAsOne()
                check(onOpenCard == 0L) { "Some of the wishes are already on an open card" }
                cardQueries.insert(
                    id = id.value,
                    number = cardQueries.selectNextNumber().executeAsOne(),
                    label = null,
                    created_at = at.toEpochMilliseconds(),
                )
                wishIds.forEachIndexed { position, wishId ->
                    slotQueries.insert(
                        bingo_card_id = id.value,
                        position = position.toLong(),
                        wish_id = wishId.value,
                    )
                }
            }
        }
    }

    override suspend fun close(id: BingoCardId, at: Instant) {
        withContext(Dispatchers.IO) {
            cardQueries.close(closed_at = at.toEpochMilliseconds(), id = id.value)
        }
    }

    override suspend fun changeLabel(id: BingoCardId, label: String?) {
        withContext(Dispatchers.IO) {
            cardQueries.updateLabel(label = label, id = id.value)
        }
    }
}

/** Shared shape of `selectOpenSlots` and `selectClosedSlots`, which differ only in their filter. */
internal data class CardSlotRow(
    val cardId: String,
    val cardNumber: Long,
    val cardLabel: String?,
    val cardCreatedAt: Long,
    val cardClosedAt: Long?,
    val slotPosition: Long,
    val slotMarkedAt: Long?,
    val wishId: String,
    val wishTitle: String,
    val wishStatus: String,
    val wishStatusAt: Long,
    val wishCreatedAt: Long,
)

private fun List<CardSlotRow>.toCards(): List<BingoCard> {
    return groupBy { it.cardId }.map { (_, rows) ->
        val head = rows.first()
        BingoCard(
            id = BingoCardId(head.cardId),
            number = head.cardNumber.toInt(),
            label = head.cardLabel,
            createdAt = Instant.fromEpochMilliseconds(head.cardCreatedAt),
            closedAt = head.cardClosedAt?.let(Instant::fromEpochMilliseconds),
            slots = rows.map(CardSlotRow::toSlot),
        )
    }
}

private fun CardSlotRow.toSlot(): BingoSlot = BingoSlot(
    position = slotPosition.toInt(),
    wish = toWish(wishId, wishTitle, wishStatus, wishStatusAt, wishCreatedAt),
    status = slotMarkedAt
        ?.let { SlotStatus.Marked(Instant.fromEpochMilliseconds(it)) }
        ?: SlotStatus.Unmarked,
)
