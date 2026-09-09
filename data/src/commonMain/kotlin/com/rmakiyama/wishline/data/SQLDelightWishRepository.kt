package com.rmakiyama.wishline.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.rmakiyama.wishline.data.db.WishlineDatabase
import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishRepository
import com.rmakiyama.wishline.domain.WishStatus
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.time.Instant

@Inject
class SQLDelightWishRepository(
    private val database: WishlineDatabase,
) : WishRepository {

    private val wishQueries = database.wishQueries
    private val slotQueries = database.bingoSlotQueries
    private val statusChangeQueries = database.wishStatusChangeQueries
    private val titleChangeQueries = database.wishTitleChangeQueries

    override fun getWishesStream(): Flow<List<Wish>> {
        return wishQueries.selectAll(::toWish).asFlow().mapToList(Dispatchers.IO)
    }

    override fun getUnassignedWishesStream(): Flow<List<Wish>> {
        return wishQueries.selectUnassigned(::toWish).asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun add(wishes: List<Wish>) {
        withContext(Dispatchers.IO) {
            database.transaction {
                wishes.forEach(::insert)
            }
        }
    }

    override suspend fun changeTitle(id: WishId, title: String, at: Instant) {
        withContext(Dispatchers.IO) {
            database.transaction {
                wishQueries.updateTitle(title = title, id = id.value)
                titleChangeQueries.insert(
                    wish_id = id.value,
                    title = title,
                    changed_at = at.toEpochMilliseconds(),
                )
            }
        }
    }

    /** The slot only moves while its card is open, so a closed card keeps the marks it ended with. */
    override suspend fun changeStatus(id: WishId, status: WishStatus) {
        withContext(Dispatchers.IO) {
            val changedAt = status.at.toEpochMilliseconds()
            database.transaction {
                val openCardId = slotQueries.selectOpenCardIdForWish(id.value).executeAsOneOrNull()
                statusChangeQueries.insert(
                    wish_id = id.value,
                    status = status.column(),
                    bingo_card_id = openCardId,
                    changed_at = changedAt,
                )
                wishQueries.updateStatus(
                    status = status.column(),
                    status_at = changedAt,
                    id = id.value,
                )
                slotQueries.updateMarkedAtOnOpenCard(
                    marked_at = (status as? WishStatus.Done)?.at?.toEpochMilliseconds(),
                    wish_id = id.value,
                )
            }
        }
    }

    override suspend fun delete(id: WishId) {
        withContext(Dispatchers.IO) {
            database.transaction {
                val placed = slotQueries.countSlotsForWish(id.value).executeAsOne()
                check(placed == 0L) {
                    "Wish ${id.value} has been on a bingo card and cannot be deleted"
                }
                statusChangeQueries.deleteByWish(id.value)
                titleChangeQueries.deleteByWish(id.value)
                wishQueries.delete(id.value)
            }
        }
    }

    private fun insert(wish: Wish) {
        val createdAt = wish.createdAt.toEpochMilliseconds()
        val statusAt = wish.status.at.toEpochMilliseconds()
        wishQueries.insert(
            id = wish.id.value,
            title = wish.title,
            status = wish.status.column(),
            status_at = statusAt,
            created_at = createdAt,
        )
        titleChangeQueries.insert(
            wish_id = wish.id.value,
            title = wish.title,
            changed_at = createdAt,
        )
        statusChangeQueries.insert(
            wish_id = wish.id.value,
            status = wish.status.column(),
            bingo_card_id = null,
            changed_at = statusAt,
        )
    }
}
