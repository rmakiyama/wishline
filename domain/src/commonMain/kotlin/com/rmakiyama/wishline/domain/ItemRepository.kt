package com.rmakiyama.wishline.domain

import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getItemsStream(): Flow<List<Item>>
    suspend fun save(item: Item)

    /** Saves all items in one transaction: either every item is stored or none is. */
    suspend fun saveAll(items: List<Item>)
}
