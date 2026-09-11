package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.BingoCard
import com.rmakiyama.wishline.domain.BingoCardLayout
import com.rmakiyama.wishline.domain.BingoCardRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class CreateBingoCardTest {

    private val layout = Capture.slot<BingoCardLayout>()
    private val bingoCardRepository = mock<BingoCardRepository> {
        everySuspend { create(any(), capture(layout), any()) } returns Unit
    }
    private val createBingoCard = CreateBingoCard(bingoCardRepository)

    @Test
    fun `given twenty five planned wishes, when a card is created, then the layout holds all of them`() = runTest {
        val wishes = plannedWishes(BingoCard.SLOT_COUNT)

        createBingoCard(wishes)

        layout.get().wishes shouldContainExactlyInAnyOrder wishes
    }

    @Test
    fun `given twenty five planned wishes, when a card is created, then the stored id is returned`() = runTest {
        val returned = createBingoCard(plannedWishes(BingoCard.SLOT_COUNT))

        verifySuspend(exactly(1)) { bingoCardRepository.create(returned, any(), any()) }
    }
}
