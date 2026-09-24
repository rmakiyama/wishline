package com.rmakiyama.wishline.data

import app.cash.turbine.test
import com.rmakiyama.wishline.domain.BingoCardLayout
import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishStatus
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SQLDelightWishQueriesTest {

    private val database = inMemoryDatabase()
    private val wishRepository = SQLDelightWishRepository(database)
    private val cardRepository = SQLDelightBingoCardRepository(database)
    private val queries = SQLDelightWishQueries(database)

    @Test
    fun `given wishes added in order, when listing the next card, then they keep that order`() = runTest {
        givenWishes(3)

        queries.getUnassignedWishesStream().first().map { it.wish.id.value } shouldContainExactly
            listOf("w0", "w1", "w2")
    }

    @Test
    fun `given a wish on an open card, when listing the next card, then it is not offered`() = runTest {
        val placed = givenWishes(26)

        givenAnOpenCard("c1", placed.take(25))

        queries.getUnassignedWishesStream().first().map { it.wish.id.value } shouldContainExactly
            listOf("w25")
    }

    @Test
    fun `given a wish that was never placed, when listing the next card, then it has not been on a card`() = runTest {
        givenWishes(1)

        queries.getUnassignedWishesStream().first().single().hasBeenOnCard.shouldBeFalse()
    }

    @Test
    fun `given wishes back from a closed card next to a new one, when listing the next card, then only the returned ones have been on a card`() = runTest {
        val placed = givenWishes(26)
        givenAnOpenCard("c1", placed.take(25))
        cardRepository.close(cardId("c1"), at(3))

        val onCard = queries.getUnassignedWishesStream().first()
            .filter { it.hasBeenOnCard }
            .map { it.wish.id.value }

        onCard shouldContainExactly placed.take(25).map { it.id.value }
    }

    @Test
    fun `given the next card is observed, when a card is closed, then it emits again`() = runTest {
        val placed = givenWishes(25)
        givenAnOpenCard("c1", placed)

        queries.getUnassignedWishesStream().test {
            awaitItem() shouldBe emptyList()

            cardRepository.close(cardId("c1"), at(3))

            awaitItem().map { it.wish.id.value } shouldContainExactly placed.map { it.id.value }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given a card about to be closed, then what it says it hands back is what comes back`() = runTest {
        val placed = givenWishes(25)
        givenAnOpenCard("c1", placed)
        wishRepository.changeStatus(placed[0].id, WishStatus.Done(at(3)))
        wishRepository.changeStatus(placed[1].id, WishStatus.Someday(at(3)))
        val card = cardRepository.getOpenCardsStream().first().single()

        cardRepository.close(cardId("c1"), at(4))

        queries.getUnassignedWishesStream().first().map { it.wish.id } shouldContainExactly
            card.wishesReturningOnClose().map { it.id }
    }

    @Test
    fun `given one slot still unmarked, when asking for the fully marked card, then there is none`() = runTest {
        val placed = givenWishes(25)
        givenAnOpenCard("c1", placed)
        placed.drop(1).forEach { wishRepository.changeStatus(it.id, WishStatus.Done(at(3))) }

        queries.fullyMarkedOpenCardId(placed.first().id).shouldBeNull()
    }

    @Test
    fun `given every slot marked, when asking for the fully marked card, then it is that card`() = runTest {
        val placed = givenWishes(25)
        givenAnOpenCard("c1", placed)
        placed.forEach { wishRepository.changeStatus(it.id, WishStatus.Done(at(3))) }

        queries.fullyMarkedOpenCardId(placed.first().id) shouldBe cardId("c1")
    }

    @Test
    fun `given a someday wish among done ones, when asking for the fully marked card, then there is none`() = runTest {
        val placed = givenWishes(25)
        givenAnOpenCard("c1", placed)
        placed.drop(1).forEach { wishRepository.changeStatus(it.id, WishStatus.Done(at(3))) }
        wishRepository.changeStatus(placed.first().id, WishStatus.Someday(at(3)))

        queries.fullyMarkedOpenCardId(placed.first().id).shouldBeNull()
    }

    @Test
    fun `given a closed card, when asking for the fully marked card, then there is none`() = runTest {
        val placed = givenWishes(25)
        givenAnOpenCard("c1", placed)
        placed.forEach { wishRepository.changeStatus(it.id, WishStatus.Done(at(3))) }
        cardRepository.close(cardId("c1"), at(4))

        queries.fullyMarkedOpenCardId(placed.first().id).shouldBeNull()
    }

    private suspend fun givenWishes(count: Int): List<Wish> =
        wishes(count).also { wishRepository.add(it) }

    private suspend fun givenAnOpenCard(id: String, placed: List<Wish>) {
        cardRepository.create(cardId(id), BingoCardLayout.of(placed), at(2))
    }
}
