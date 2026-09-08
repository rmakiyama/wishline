package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishRepository
import dev.zacsweers.metro.Inject
import kotlin.time.Clock

interface ChangeWishTitleUseCase {
    suspend operator fun invoke(id: WishId, title: String)
}

@Inject
class ChangeWishTitle(
    private val wishRepository: WishRepository,
) : ChangeWishTitleUseCase {
    override suspend operator fun invoke(id: WishId, title: String) {
        wishRepository.changeTitle(id, title, Clock.System.now())
    }
}
