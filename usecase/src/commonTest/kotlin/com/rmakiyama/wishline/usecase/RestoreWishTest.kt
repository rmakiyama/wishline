package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishRepository
import com.rmakiyama.wishline.domain.WishStatus
import dev.mokkery.MockMode
import dev.mokkery.matcher.ofType
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RestoreWishTest {

    private val wishRepository = mock<WishRepository>(MockMode.autoUnit)

    @Test
    fun `when a wish is restored, then it moves back to planned`() = runTest {
        RestoreWish(wishRepository).invoke(WishId("w1"))

        verifySuspend(exactly(1)) {
            wishRepository.changeStatus(WishId("w1"), ofType<WishStatus.Planned>())
        }
    }
}
