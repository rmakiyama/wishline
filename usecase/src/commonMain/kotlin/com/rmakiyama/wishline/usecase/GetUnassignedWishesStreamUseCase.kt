package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.UnassignedWish
import com.rmakiyama.wishline.domain.WishQueries
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

/** The wishes the next card offers: planned, and on no open card. */
interface GetUnassignedWishesStreamUseCase {
    operator fun invoke(): Flow<List<UnassignedWish>>
}

@Inject
class GetUnassignedWishesStream(
    private val wishQueries: WishQueries,
) : GetUnassignedWishesStreamUseCase {
    override operator fun invoke(): Flow<List<UnassignedWish>> = wishQueries.getUnassignedWishesStream()
}
