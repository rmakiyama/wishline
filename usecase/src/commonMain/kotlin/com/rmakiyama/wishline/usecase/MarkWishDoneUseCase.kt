package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.BingoCardRepository
import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishQueries
import com.rmakiyama.wishline.domain.WishRepository
import com.rmakiyama.wishline.domain.WishStatus
import dev.zacsweers.metro.Inject
import kotlin.time.Clock

/** Marking the last slot of a card closes it: nothing on it is left to decide. */
interface MarkWishDoneUseCase {
    suspend operator fun invoke(id: WishId)
}

@Inject
class MarkWishDone(
    private val wishRepository: WishRepository,
    private val wishQueries: WishQueries,
    private val bingoCardRepository: BingoCardRepository,
) : MarkWishDoneUseCase {
    override suspend operator fun invoke(id: WishId) {
        val at = Clock.System.now()
        wishRepository.changeStatus(id, WishStatus.Done(at))
        wishQueries.fullyMarkedOpenCardId(id)?.let { bingoCardRepository.close(it, at) }
    }
}
