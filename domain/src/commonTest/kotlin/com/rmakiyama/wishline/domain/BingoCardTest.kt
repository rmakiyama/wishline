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
    fun `given a card with two planned wishes, when it closes, then both return`() {
        val card = card(marked = (2 until BingoCard.SLOT_COUNT).toSet())

        card.wishesReturningOnClose().map { it.id } shouldContainExactly listOf(WishId("wish-0"), WishId("wish-1"))
    }

    @Test
    fun `given a someday wish, when the card closes, then it stays on the card`() {
        val card = card(marked = (2 until BingoCard.SLOT_COUNT).toSet(), someday = setOf(1))

        card.wishesReturningOnClose().map { it.id } shouldContainExactly listOf(WishId("wish-0"))
    }

    @Test
    fun `given every wish done, when the card closes, then nothing returns`() {
        val card = card(marked = (0 until BingoCard.SLOT_COUNT).toSet())

        card.wishesReturningOnClose() shouldBe emptyList()
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
