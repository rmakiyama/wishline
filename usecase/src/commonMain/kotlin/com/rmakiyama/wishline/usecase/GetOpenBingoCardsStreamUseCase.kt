package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.BingoCard
import com.rmakiyama.wishline.domain.BingoCardRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

interface GetOpenBingoCardsStreamUseCase {
    operator fun invoke(): Flow<List<BingoCard>>
}

@Inject
class GetOpenBingoCardsStream(
    private val bingoCardRepository: BingoCardRepository,
) : GetOpenBingoCardsStreamUseCase {
    override operator fun invoke(): Flow<List<BingoCard>> = bingoCardRepository.getOpenCardsStream()
}
