package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.Item
import com.rmakiyama.wishline.domain.ItemId
import com.rmakiyama.wishline.domain.ItemRepository
import dev.zacsweers.metro.Inject
import kotlin.time.Clock
import kotlin.uuid.Uuid

interface AddItemUseCase {
    suspend operator fun invoke(title: String) = invoke(listOf(title))

    /** Adds every title as a new item. All succeed or none is stored. */
    suspend operator fun invoke(titles: List<String>)
}

@Inject
class AddItem(
    private val itemRepository: ItemRepository,
) : AddItemUseCase {
    override suspend operator fun invoke(titles: List<String>) {
        val now = Clock.System.now()
        itemRepository.saveAll(
            titles.map { title ->
                Item(
                    id = ItemId(Uuid.random().toString()),
                    title = title,
                    description = null,
                    createdAt = now,
                )
            },
        )
    }
}
