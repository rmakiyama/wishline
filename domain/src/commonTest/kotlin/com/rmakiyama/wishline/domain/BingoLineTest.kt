package com.rmakiyama.wishline.domain

import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class BingoLineTest {

    @Test
    fun `given a five by five grid, then there are twelve winning lines`() {
        BingoLine.All.size shouldBe 12
    }

    @Test
    fun `given a five by five grid, then every line covers five positions`() {
        BingoLine.All.forEach { it.positions.size shouldBe BingoLine.SIZE }
    }

    @Test
    fun `given a five by five grid, then both diagonals are included`() {
        BingoLine.All.map { it.positions } shouldContainAll listOf(
            listOf(0, 6, 12, 18, 24),
            listOf(4, 8, 12, 16, 20),
        )
    }
}
