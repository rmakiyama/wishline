package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.Item
import com.rmakiyama.wishline.domain.ItemRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

interface GetItemsStreamUseCase {
    operator fun invoke(): Flow<List<Item>>
}

@Inject
class GetItemsStream(
    private val itemRepository: ItemRepository,
) : GetItemsStreamUseCase {
    override operator fun invoke(): Flow<List<Item>> {
        return itemRepository.getItemsStream()
    }
}
