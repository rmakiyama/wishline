package com.rmakiyama.wishline.data

import com.rmakiyama.wishline.domain.BingoCardLayout
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
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
        wishRepository.add(wishes(3))

        queries.getUnassignedWishesStream().first().map { it.wish.id.value } shouldContainExactly
            listOf("w0", "w1", "w2")
    }

    @Test
    fun `given a wish that was never placed, when listing the next card, then it has not been on a card`() = runTest {
        wishRepository.add(wishes(1))

        queries.getUnassignedWishesStream().first().single().hasBeenOnCard.shouldBeFalse()
    }

    @Test
    fun `given a wish returned from a closed card, when listing the next card, then it has been on a card`() = runTest {
        val placed = wishes(25)
        wishRepository.add(placed)
        cardRepository.create(cardId("c1"), BingoCardLayout.of(placed), at(2))
        cardRepository.close(cardId("c1"), at(3))

        queries.getUnassignedWishesStream().first().all { it.hasBeenOnCard }.shouldBeTrue()
    }
}
