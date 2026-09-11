package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.BingoCard
import com.rmakiyama.wishline.domain.BingoCardRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

interface GetClosedBingoCardsStreamUseCase {
    operator fun invoke(): Flow<List<BingoCard>>
}

@Inject
class GetClosedBingoCardsStream(
    private val bingoCardRepository: BingoCardRepository,
) : GetClosedBingoCardsStreamUseCase {
    override operator fun invoke(): Flow<List<BingoCard>> = bingoCardRepository.getClosedCardsStream()
}
