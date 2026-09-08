package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.BingoCardRepository
import dev.zacsweers.metro.Inject
import kotlin.time.Clock

/** The wishes still planned return to the next card, so it can pick them up again. */
interface CloseBingoCardUseCase {
    suspend operator fun invoke(id: BingoCardId)
}

@Inject
class CloseBingoCard(
    private val bingoCardRepository: BingoCardRepository,
) : CloseBingoCardUseCase {
    override suspend operator fun invoke(id: BingoCardId) {
        bingoCardRepository.close(id, Clock.System.now())
    }
}
