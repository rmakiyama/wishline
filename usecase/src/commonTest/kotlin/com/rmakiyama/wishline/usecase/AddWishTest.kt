package com.rmakiyama.wishline.usecase

import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishRepository
import com.rmakiyama.wishline.domain.WishStatus
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class AddWishTest {

    private val added = Capture.slot<List<Wish>>()
    private val wishRepository = mock<WishRepository> {
        everySuspend { add(capture(added)) } returns Unit
    }
    private val addWish = AddWish(wishRepository)

    @Test
    fun `given two titles, when they are added, then both titles are kept`() = runTest {
        addWish(listOf("A", "B"))

        added.get().map { it.title } shouldContainExactly listOf("A", "B")
    }

    @Test
    fun `given two titles, when they are added, then both wishes start planned`() = runTest {
        addWish(listOf("A", "B"))

        added.get().forEach { it.status.shouldBeInstanceOf<WishStatus.Planned>() }
    }

    @Test
    fun `given two titles, when they are added, then each wish gets its own id`() = runTest {
        addWish(listOf("A", "B"))

        added.get().map { it.id }.toSet().size shouldBe 2
    }

    @Test
    fun `given two titles, when they are added, then they share one moment`() = runTest {
        addWish(listOf("A", "B"))

        val moments = added.get().flatMap { listOf(it.createdAt, it.status.at) }
        moments.toSet().size shouldBe 1
    }
}
