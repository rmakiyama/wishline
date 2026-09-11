package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

/** The wishes the next card offers: planned, and on no open card. */
interface GetUnassignedWishesStreamUseCase {
    operator fun invoke(): Flow<List<Wish>>
}

@Inject
class GetUnassignedWishesStream(
    private val wishRepository: WishRepository,
) : GetUnassignedWishesStreamUseCase {
    override operator fun invoke(): Flow<List<Wish>> = wishRepository.getUnassignedWishesStream()
}
