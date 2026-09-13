package com.rmakiyama.wishline.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.rmakiyama.wishline.data.db.WishlineDatabase
import com.rmakiyama.wishline.domain.UnassignedWish
import com.rmakiyama.wishline.domain.WishQueries
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

@Inject
class SQLDelightWishQueries(
    database: WishlineDatabase,
) : WishQueries {

    private val wishQueries = database.wishQueries

    override fun getUnassignedWishesStream(): Flow<List<UnassignedWish>> {
        return wishQueries
            .selectUnassigned { id, title, status, statusAt, createdAt, hasBeenOnCard ->
                UnassignedWish(
                    wish = toWish(id, title, status, statusAt, createdAt),
                    hasBeenOnCard = hasBeenOnCard,
                )
            }
            .asFlow()
            .mapToList(Dispatchers.IO)
    }
}
