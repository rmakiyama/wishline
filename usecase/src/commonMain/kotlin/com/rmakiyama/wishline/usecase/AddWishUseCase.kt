package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishRepository
import com.rmakiyama.wishline.domain.WishStatus
import dev.zacsweers.metro.Inject
import kotlin.time.Clock
import kotlin.uuid.Uuid

interface AddWishUseCase {
    suspend operator fun invoke(title: String) = invoke(listOf(title))

    /** Adds every title as a new wish. All succeed or none is stored. */
    suspend operator fun invoke(titles: List<String>)
}

@Inject
class AddWish(
    private val wishRepository: WishRepository,
) : AddWishUseCase {
    override suspend operator fun invoke(titles: List<String>) {
        val now = Clock.System.now()
        wishRepository.add(
            titles.map { title ->
                Wish(
                    id = WishId(Uuid.random().toString()),
                    title = title,
                    status = WishStatus.Planned(now),
                    createdAt = now,
                )
            },
        )
    }
}
