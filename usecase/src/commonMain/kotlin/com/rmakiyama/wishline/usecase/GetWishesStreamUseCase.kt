package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

interface GetWishesStreamUseCase {
    operator fun invoke(): Flow<List<Wish>>
}

@Inject
class GetWishesStream(
    private val wishRepository: WishRepository,
) : GetWishesStreamUseCase {
    override operator fun invoke(): Flow<List<Wish>> = wishRepository.getWishesStream()
}
