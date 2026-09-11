package com.rmakiyama.wishline.domain

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class BingoCardLayoutTest {

    @Test
    fun `given twenty five distinct planned wishes, when building a layout, then it is accepted`() {
        val wishes = plannedWishes(BingoCard.SLOT_COUNT)

        BingoCardLayout.of(wishes).wishes shouldBe wishes
    }

    @Test
    fun `given too few wishes, when building a layout, then it is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            BingoCardLayout.of(plannedWishes(BingoCard.SLOT_COUNT - 1))
        }
    }

    @Test
    fun `given the same wish twice, when building a layout, then it is rejected`() {
        val wishes = plannedWishes(BingoCard.SLOT_COUNT)

        assertFailsWith<IllegalArgumentException> {
            BingoCardLayout.of(wishes.dropLast(1) + wishes.first())
        }
    }

    @Test
    fun `given a wish that is already done, when building a layout, then it is rejected`() {
        val wishes = plannedWishes(BingoCard.SLOT_COUNT)

        assertFailsWith<IllegalArgumentException> {
            BingoCardLayout.of(wishes.dropLast(1) + wishes.last().copy(status = WishStatus.Done(at)))
        }
    }

    @Test
    fun `given a wish put off for someday, when building a layout, then it is rejected`() {
        val wishes = plannedWishes(BingoCard.SLOT_COUNT)

        assertFailsWith<IllegalArgumentException> {
            BingoCardLayout.of(wishes.dropLast(1) + wishes.last().copy(status = WishStatus.Someday(at)))
        }
    }

    private val at = Instant.fromEpochMilliseconds(0)

    private fun plannedWishes(count: Int): List<Wish> = (0 until count).map {
        Wish(
            id = WishId("wish-$it"),
            title = "wish $it",
            status = WishStatus.Planned(at),
            createdAt = at,
        )
    }
}
