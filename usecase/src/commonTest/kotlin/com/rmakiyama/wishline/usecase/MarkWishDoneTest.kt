package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.BingoCardRepository
import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishQueries
import com.rmakiyama.wishline.domain.WishRepository
import com.rmakiyama.wishline.domain.WishStatus
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.ofType
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verify.VerifyMode.Companion.not
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MarkWishDoneTest {

    private val wishRepository = mock<WishRepository>(MockMode.autoUnit)
    private val wishQueries = mock<WishQueries>(MockMode.autoUnit)
    private val bingoCardRepository = mock<BingoCardRepository>(MockMode.autoUnit)
    private val markDone = MarkWishDone(wishRepository, wishQueries, bingoCardRepository)

    @Test
    fun `when a wish is marked done, then it moves to done`() = runTest {
        everySuspend { wishQueries.fullyMarkedOpenCardId(any()) } returns null

        markDone(WishId("w1"))

        verifySuspend(exactly(1)) {
            wishRepository.changeStatus(WishId("w1"), ofType<WishStatus.Done>())
        }
    }

    @Test
    fun `given the wish was the last unmarked slot, when it is marked done, then its card closes`() = runTest {
        everySuspend { wishQueries.fullyMarkedOpenCardId(WishId("w1")) } returns BingoCardId("c1")

        markDone(WishId("w1"))

        verifySuspend(exactly(1)) { bingoCardRepository.close(BingoCardId("c1"), any()) }
    }

    @Test
    fun `given a slot is still unmarked, when a wish is marked done, then its card stays open`() = runTest {
        everySuspend { wishQueries.fullyMarkedOpenCardId(WishId("w1")) } returns null

        markDone(WishId("w1"))

        verifySuspend(not) { bingoCardRepository.close(any(), any()) }
    }
}
