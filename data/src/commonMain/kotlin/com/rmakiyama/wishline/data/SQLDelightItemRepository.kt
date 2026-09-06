package com.rmakiyama.wishline.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.rmakiyama.wishline.data.db.WishlineDatabase
import com.rmakiyama.wishline.domain.Item
import com.rmakiyama.wishline.domain.ItemId
import com.rmakiyama.wishline.domain.ItemRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Instant

@Inject
class SQLDelightItemRepository(
    private val database: WishlineDatabase,
) : ItemRepository {

    private val itemQueries = database.itemQueries

    override fun getItemsStream(): Flow<List<Item>> {
        return itemQueries.selectAll().asFlow().mapToList(Dispatchers.IO).map { items ->
            items.map { item ->
                Item(
                    id = ItemId(item.id),
                    title = item.title,
                    description = item.description,
                    createdAt = Instant.fromEpochMilliseconds(item.created_at),
                )
            }
        }
    }

    override suspend fun save(item: Item) {
        withContext(Dispatchers.IO) {
            insert(item)
        }
    }

    override suspend fun saveAll(items: List<Item>) {
        withContext(Dispatchers.IO) {
            itemQueries.transaction {
                items.forEach(::insert)
            }
        }
    }

    private fun insert(item: Item) {
        itemQueries.insert(
            id = item.id.value,
            title = item.title,
            description = item.description,
            created_at = item.createdAt.toEpochMilliseconds(),
        )
    }
}
