package com.rmakiyama.wishline.domain

/** One of the twelve winning lines on a bingo card, as slot positions. */
data class BingoLine(val positions: List<Int>) {

    companion object {
        const val SIZE: Int = 5

        val All: List<BingoLine> = buildList {
            (0 until SIZE).mapTo(this) { row -> BingoLine((0 until SIZE).map { row * SIZE + it }) }
            (0 until SIZE).mapTo(this) { column -> BingoLine((0 until SIZE).map { it * SIZE + column }) }
            add(BingoLine((0 until SIZE).map { it * (SIZE + 1) }))
            add(BingoLine((1..SIZE).map { it * (SIZE - 1) }))
        }
    }
}
