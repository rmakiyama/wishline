package com.rmakiyama.wishline.data

import app.cash.turbine.test
import com.rmakiyama.wishline.domain.BingoCardLayout
import com.rmakiyama.wishline.domain.SlotStatus
import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishStatus
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SQLDelightBingoCardRepositoryTest {

    private val database = inMemoryDatabase()
    private val wishRepository = SQLDelightWishRepository(database)
    private val wishQueries = SQLDelightWishQueries(database)
    private val cardRepository = SQLDelightBingoCardRepository(database)

    @Test
    fun `given a layout, when a card is created, then its slots follow that order`() = runTest {
        val placed = givenWishes(25)

        cardRepository.create(cardId("c1"), BingoCardLayout.of(placed), at(2))

        val card = cardRepository.getOpenCardsStream().first().single()
        card.slots.map { it.position } shouldContainExactly (0 until 25).toList()
        card.slots.map { it.wish.id.value } shouldContainExactly placed.map { it.id.value }
    }

    @Test
    fun `when the first card is created, then it is numbered one`() = runTest {
        val placed = givenWishes(25)

        cardRepository.create(cardId("c1"), BingoCardLayout.of(placed), at(2))

        cardRepository.getOpenCardsStream().first().single().number shouldBe 1
    }

    @Test
    fun `given a closed card, when the next one is created, then it takes the following number`() = runTest {
        val placed = givenWishes(25)
        givenAnOpenCard("c1", placed)
        cardRepository.close(cardId("c1"), at(3))

        cardRepository.create(cardId("c2"), BingoCardLayout.of(placed), at(4))

        cardRepository.getOpenCardsStream().first().single().number shouldBe 2
    }

    @Test
    fun `given a wish on an open card, when it is marked done, then its slot is marked`() = runTest {
        givenAnOpenCard("c1", givenWishes(25))

        wishRepository.changeStatus(WishId("w0"), WishStatus.Done(at(3)))

        cardRepository.getOpenCardsStream().first().single()
            .slots.first().status shouldBe SlotStatus.Marked(at(3))
    }

    @Test
    fun `given a wish on an open card, when it is marked done, then the event records that card`() = runTest {
        givenAnOpenCard("c1", givenWishes(25))

        wishRepository.changeStatus(WishId("w0"), WishStatus.Done(at(3)))

        database.wishStatusChangeQueries.selectByWish("w0").executeAsList()
            .last().bingo_card_id shouldBe "c1"
    }

    @Test
    fun `when a card is closed, then only its planned wishes return to the next card`() = runTest {
        givenAnOpenCard("c1", givenWishes(25))
        wishRepository.changeStatus(WishId("w0"), WishStatus.Done(at(3)))
        wishRepository.changeStatus(WishId("w1"), WishStatus.Someday(at(3)))

        cardRepository.close(cardId("c1"), at(4))

        wishQueries.getUnassignedWishesStream().first().map { it.wish.id.value } shouldContainExactly
            (2 until 25).map { "w$it" }
    }

    @Test
    fun `when a card is closed, then it leaves the open cards`() = runTest {
        givenAnOpenCard("c1", givenWishes(25))

        cardRepository.close(cardId("c1"), at(4))

        cardRepository.getOpenCardsStream().first() shouldBe emptyList()
    }

    @Test
    fun `given a closed card, when one of its wishes is marked done later, then its slot stays unmarked`() = runTest {
        givenAnOpenCard("c1", givenWishes(25))
        cardRepository.close(cardId("c1"), at(3))

        wishRepository.changeStatus(WishId("w0"), WishStatus.Done(at(4)))

        cardRepository.getClosedCardsStream().first().single()
            .slots.first().status shouldBe SlotStatus.Unmarked
    }

    @Test
    fun `given a closed card, then its slots show the wish as it is today`() = runTest {
        givenAnOpenCard("c1", givenWishes(25))
        cardRepository.close(cardId("c1"), at(3))

        wishRepository.changeStatus(WishId("w0"), WishStatus.Done(at(4)))

        cardRepository.getClosedCardsStream().first().single()
            .slots.first().wish.status shouldBe WishStatus.Done(at(4))
    }

    @Test
    fun `given a wish on an open card, when another card is created with it, then the create fails`() = runTest {
        val placed = givenWishes(26)
        givenAnOpenCard("c1", placed.take(25))

        assertFailsWith<IllegalStateException> {
            cardRepository.create(cardId("c2"), BingoCardLayout.of(placed.drop(1)), at(3))
        }
    }

    @Test
    fun `given a wish on an open card, when the create fails, then no card is added`() = runTest {
        val placed = givenWishes(26)
        givenAnOpenCard("c1", placed.take(25))

        runCatching {
            cardRepository.create(cardId("c2"), BingoCardLayout.of(placed.drop(1)), at(3))
        }

        cardRepository.getOpenCardsStream().first().map { it.id.value } shouldContainExactly listOf("c1")
    }

    @Test
    fun `given the open cards are observed, when a wish is marked done, then they emit again`() = runTest {
        givenAnOpenCard("c1", givenWishes(25))

        cardRepository.getOpenCardsStream().test {
            awaitItem().single().slots.first().status shouldBe SlotStatus.Unmarked

            wishRepository.changeStatus(WishId("w0"), WishStatus.Done(at(3)))

            awaitItem().single().slots.first().status shouldBe SlotStatus.Marked(at(3))
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun givenWishes(count: Int): List<Wish> =
        wishes(count).also { wishRepository.add(it) }

    private suspend fun givenAnOpenCard(id: String, placed: List<Wish>) {
        cardRepository.create(cardId(id), BingoCardLayout.of(placed), at(2))
    }
}
