package com.rmakiyama.wishline.domain

import kotlin.jvm.JvmInline

/**
 * The wishes of a new card, in slot order. Whether a wish already sits on another open card is not
 * checked here: a wish does not know its cards, so that rule belongs to [BingoCardRepository].
 */
@JvmInline
value class BingoCardLayout private constructor(val wishes: List<Wish>) {

    companion object {
        fun of(wishes: List<Wish>): BingoCardLayout {
            require(wishes.size == BingoCard.SLOT_COUNT) {
                "A bingo card takes exactly ${BingoCard.SLOT_COUNT} wishes, was ${wishes.size}"
            }
            require(wishes.distinctBy { it.id }.size == wishes.size) {
                "A wish cannot take two slots on the same card"
            }
            require(wishes.all { it.status is WishStatus.Planned }) {
                "Only a planned wish can be placed on a bingo card"
            }
            return BingoCardLayout(wishes)
        }
    }
}
