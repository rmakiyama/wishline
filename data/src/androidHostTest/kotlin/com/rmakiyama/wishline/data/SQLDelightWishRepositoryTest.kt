package com.rmakiyama.wishline.data

import com.rmakiyama.wishline.domain.BingoCardLayout
import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishStatus
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.sql.SQLException
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SQLDelightWishRepositoryTest {

    private val database = inMemoryDatabase()
    private val repository = SQLDelightWishRepository(database)

    @Test
    fun `when a wish is added, then it is stored as planned`() = runTest {
        repository.add(listOf(wish("w1", "京都ひとり旅")))

        val stored = repository.getWishesStream().first().single()
        stored.title shouldBe "京都ひとり旅"
        stored.status shouldBe WishStatus.Planned(at(1))
    }

    @Test
    fun `when a wish is added, then its title opens the title history`() = runTest {
        repository.add(listOf(wish("w1", "京都ひとり旅")))

        titleHistory("w1") shouldContainExactly listOf("京都ひとり旅")
    }

    @Test
    fun `when a wish is added, then planned opens the status history`() = runTest {
        repository.add(listOf(wish("w1", "京都ひとり旅")))

        statusHistory("w1") shouldContainExactly listOf("PLANNED")
    }

    @Test
    fun `given a batch holding a duplicate, when it is added, then none of it is written`() = runTest {
        repository.add(listOf(wish("w1", "already there")))

        assertFailsWith<SQLException> {
            repository.add(listOf(wish("w2", "new"), wish("w1", "duplicate")))
        }

        repository.getWishesStream().first().map { it.id.value } shouldContainExactly listOf("w1")
        titleHistory("w2") shouldBe emptyList()
    }

    @Test
    fun `when the title is changed, then the wish shows the new title`() = runTest {
        repository.add(listOf(wish("w1", "京都")))

        repository.changeTitle(WishId("w1"), "京都ひとり旅", at(5))

        repository.getWishesStream().first().single().title shouldBe "京都ひとり旅"
    }

    @Test
    fun `when the title is changed, then the title history keeps both`() = runTest {
        repository.add(listOf(wish("w1", "京都")))

        repository.changeTitle(WishId("w1"), "京都ひとり旅", at(5))

        titleHistory("w1") shouldContainExactly listOf("京都", "京都ひとり旅")
    }

    @Test
    fun `when the status changes repeatedly, then the history keeps every change in order`() = runTest {
        repository.add(listOf(wish("w1", "a")))

        changeStatusThreeTimes()

        statusHistory("w1") shouldContainExactly listOf("PLANNED", "DONE", "PLANNED", "SOMEDAY")
    }

    @Test
    fun `when the status changes repeatedly, then the wish shows the newest one`() = runTest {
        repository.add(listOf(wish("w1", "a")))

        changeStatusThreeTimes()

        repository.getWishesStream().first().single().status shouldBe WishStatus.Someday(at(4))
    }

    @Test
    fun `given a wish that was never placed, when it is deleted, then it is gone`() = runTest {
        repository.add(listOf(wish("w1", "a")))

        repository.delete(WishId("w1"))

        repository.getWishesStream().first() shouldBe emptyList()
    }

    @Test
    fun `given a wish that was never placed, when it is deleted, then its history is gone`() = runTest {
        repository.add(listOf(wish("w1", "a")))

        repository.delete(WishId("w1"))

        titleHistory("w1") shouldBe emptyList()
        statusHistory("w1") shouldBe emptyList()
    }

    @Test
    fun `given a wish that has been on a card, when it is deleted, then the delete fails`() = runTest {
        placeEveryWishOnAClosedCard()

        assertFailsWith<IllegalStateException> { repository.delete(WishId("w0")) }
    }

    @Test
    fun `given a wish that has been on a card, when the delete fails, then nothing is removed`() = runTest {
        placeEveryWishOnAClosedCard()

        runCatching { repository.delete(WishId("w0")) }

        repository.getWishesStream().first().size shouldBe 25
        statusHistory("w0") shouldContainExactly listOf("PLANNED")
    }

    private suspend fun changeStatusThreeTimes() {
        repository.changeStatus(WishId("w1"), WishStatus.Done(at(2)))
        repository.changeStatus(WishId("w1"), WishStatus.Planned(at(3)))
        repository.changeStatus(WishId("w1"), WishStatus.Someday(at(4)))
    }

    private suspend fun placeEveryWishOnAClosedCard() {
        val cards = SQLDelightBingoCardRepository(database)
        val placed = wishes(25)
        repository.add(placed)
        cards.create(cardId("c1"), BingoCardLayout.of(placed), at(2))
        cards.close(cardId("c1"), at(3))
    }

    private fun titleHistory(wishId: String) =
        database.wishTitleChangeQueries.selectByWish(wishId).executeAsList().map { it.title }

    private fun statusHistory(wishId: String) =
        database.wishStatusChangeQueries.selectByWish(wishId).executeAsList().map { it.status }
}
