package com.rmakiyama.wishline.domain

/**
 * A planned wish that sits on no open card. [hasBeenOnCard] can only be true through a closed
 * card, and is what [WishRepository.delete] turns on.
 */
data class UnassignedWish(
    val wish: Wish,
    val hasBeenOnCard: Boolean,
)
