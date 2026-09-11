package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishRepository
import dev.zacsweers.metro.Inject

interface DeleteWishUseCase {
    suspend operator fun invoke(id: WishId)
}

@Inject
class DeleteWish(
    private val wishRepository: WishRepository,
) : DeleteWishUseCase {
    override suspend operator fun invoke(id: WishId) {
        wishRepository.delete(id)
    }
}
