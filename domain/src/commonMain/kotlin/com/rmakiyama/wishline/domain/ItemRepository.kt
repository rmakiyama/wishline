package com.rmakiyama.wishline.domain

import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getItemsStream(): Flow<List<Item>>
    suspend fun save(item: Item)
}
