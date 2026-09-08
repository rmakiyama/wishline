package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.BingoCardLayout
import com.rmakiyama.wishline.domain.BingoCardRepository
import com.rmakiyama.wishline.domain.Wish
import dev.zacsweers.metro.Inject
import kotlin.time.Clock
import kotlin.uuid.Uuid

interface CreateBingoCardUseCase {
    suspend operator fun invoke(wishes: List<Wish>): BingoCardId
}

@Inject
class CreateBingoCard(
    private val bingoCardRepository: BingoCardRepository,
) : CreateBingoCardUseCase {
    /** The one shuffle a card ever gets: from here on its layout is fixed. */
    override suspend operator fun invoke(wishes: List<Wish>): BingoCardId {
        val id = BingoCardId(Uuid.random().toString())
        bingoCardRepository.create(id, BingoCardLayout.of(wishes.shuffled()), Clock.System.now())
        return id
    }
}
