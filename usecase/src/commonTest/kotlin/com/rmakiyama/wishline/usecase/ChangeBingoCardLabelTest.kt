package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.BingoCardRepository
import dev.mokkery.MockMode
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ChangeBingoCardLabelTest {

    private val bingoCardRepository = mock<BingoCardRepository>(MockMode.autoUnit)
    private val changeLabel = ChangeBingoCardLabel(bingoCardRepository)

    @Test
    fun `given a label, when it is changed, then it is stored as written`() = runTest {
        changeLabel(BingoCardId("c1"), "2026年の夏")

        verifySuspend(exactly(1)) { bingoCardRepository.changeLabel(BingoCardId("c1"), "2026年の夏") }
    }

    @Test
    fun `given a blank label, when it is changed, then the card falls back to no label`() = runTest {
        changeLabel(BingoCardId("c1"), "   ")

        verifySuspend(exactly(1)) { bingoCardRepository.changeLabel(BingoCardId("c1"), null) }
    }
}
