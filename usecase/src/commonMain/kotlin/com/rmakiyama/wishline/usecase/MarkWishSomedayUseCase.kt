package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishRepository
import com.rmakiyama.wishline.domain.WishStatus
import dev.zacsweers.metro.Inject
import kotlin.time.Clock

interface MarkWishSomedayUseCase {
    suspend operator fun invoke(id: WishId)
}

@Inject
class MarkWishSomeday(
    private val wishRepository: WishRepository,
) : MarkWishSomedayUseCase {
    override suspend operator fun invoke(id: WishId) {
        wishRepository.changeStatus(id, WishStatus.Someday(Clock.System.now()))
    }
}
