package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishRepository
import com.rmakiyama.wishline.domain.WishStatus
import dev.zacsweers.metro.Inject
import kotlin.time.Clock

interface MarkWishDoneUseCase {
    suspend operator fun invoke(id: WishId)
}

@Inject
class MarkWishDone(
    private val wishRepository: WishRepository,
) : MarkWishDoneUseCase {
    override suspend operator fun invoke(id: WishId) {
        wishRepository.changeStatus(id, WishStatus.Done(Clock.System.now()))
    }
}
