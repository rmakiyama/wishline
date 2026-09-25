package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.BingoCardRepository
import dev.zacsweers.metro.Inject

/** A card with no label of its own shows its number, so a blank one is the same as none. */
interface ChangeBingoCardLabelUseCase {
    suspend operator fun invoke(id: BingoCardId, label: String?)
}

@Inject
class ChangeBingoCardLabel(
    private val bingoCardRepository: BingoCardRepository,
) : ChangeBingoCardLabelUseCase {
    override suspend operator fun invoke(id: BingoCardId, label: String?) {
        bingoCardRepository.changeLabel(id, label?.trim()?.takeIf { it.isNotEmpty() })
    }
}
