package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.BingoCardRepository
import dev.zacsweers.metro.Inject

interface ChangeBingoCardLabelUseCase {
    suspend operator fun invoke(id: BingoCardId, label: String?)
}

@Inject
class ChangeBingoCardLabel(
    private val bingoCardRepository: BingoCardRepository,
) : ChangeBingoCardLabelUseCase {
    override suspend operator fun invoke(id: BingoCardId, label: String?) {
        bingoCardRepository.changeLabel(id, label?.takeIf { it.isNotBlank() })
    }
}
