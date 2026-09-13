package com.rmakiyama.wishline.domain

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.time.Instant

class BingoCardTest {

    @Test
    fun `given a fully marked row, when counting lines, then that row is complete`() {
        val card = card(marked = setOf(0, 1, 2, 3, 4))

        card.completedLines().map { it.positions } shouldContainExactly listOf(listOf(0, 1, 2, 3, 4))
    }

    @Test
    fun `given a marked row and a marked column, when counting lines, then both are complete`() {
        val card = card(marked = setOf(0, 1, 2, 3, 4, 5, 10, 15, 20))

        card.completedLines().map { it.positions } shouldContainExactlyInAnyOrder listOf(
            listOf(0, 1, 2, 3, 4),
            listOf(0, 5, 10, 15, 20),
        )
    }

    @Test
    fun `given a row holding a someday wish, when counting lines, then that row is not complete`() {
        val card = card(marked = setOf(0, 1, 2, 3), someday = setOf(4))

        card.completedLines() shouldBe emptyList()
    }

    @Test
    fun `given every slot marked, when counting lines, then all twelve lines are complete`() {
        val card = card(marked = (0 until BingoCard.SLOT_COUNT).toSet())

        card.completedLines().size shouldBe BingoLine.All.size
    }

    @Test
    fun `given one unmarked slot, when its wish would be marked, then the card is completed`() {
        val card = card(marked = (1 until BingoCard.SLOT_COUNT).toSet())

        card.isCompletedByMarking(WishId("wish-0")) shouldBe true
    }

    @Test
    fun `given two unmarked slots, when one wish would be marked, then the card is not completed`() {
        val card = card(marked = (2 until BingoCard.SLOT_COUNT).toSet())

        card.isCompletedByMarking(WishId("wish-0")) shouldBe false
    }

    @Test
    fun `given a someday slot beside the last unmarked one, when that wish would be marked, then the card is not completed`() {
        val card = card(marked = (2 until BingoCard.SLOT_COUNT).toSet(), someday = setOf(1))

        card.isCompletedByMarking(WishId("wish-0")) shouldBe false
    }

    private fun card(marked: Set<Int>, someday: Set<Int> = emptySet()): BingoCard {
        val at = Instant.fromEpochMilliseconds(0)
        return BingoCard(
            id = BingoCardId("card"),
            number = 1,
            label = null,
            createdAt = at,
            closedAt = null,
            slots = (0 until BingoCard.SLOT_COUNT).map { position ->
                BingoSlot(
                    position = position,
                    wish = Wish(
                        id = WishId("wish-$position"),
                        title = "wish $position",
                        status = when (position) {
                            in someday -> WishStatus.Someday(at)
                            in marked -> WishStatus.Done(at)
                            else -> WishStatus.Planned(at)
                        },
                        createdAt = at,
                    ),
                    status = if (position in marked) SlotStatus.Marked(at) else SlotStatus.Unmarked,
                )
            },
        )
    }
}
