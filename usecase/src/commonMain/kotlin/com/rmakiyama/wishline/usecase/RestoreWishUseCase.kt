package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishRepository
import com.rmakiyama.wishline.domain.WishStatus
import dev.zacsweers.metro.Inject
import kotlin.time.Clock

/** Backs both "取り消し" and "戻す": either way the wish goes back to planned. */
interface RestoreWishUseCase {
    suspend operator fun invoke(id: WishId)
}

@Inject
class RestoreWish(
    private val wishRepository: WishRepository,
) : RestoreWishUseCase {
    override suspend operator fun invoke(id: WishId) {
        wishRepository.changeStatus(id, WishStatus.Planned(Clock.System.now()))
    }
}
