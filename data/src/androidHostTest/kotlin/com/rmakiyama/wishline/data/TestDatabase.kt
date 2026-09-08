package com.rmakiyama.wishline.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.rmakiyama.wishline.data.db.WishlineDatabase
import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishStatus
import kotlin.time.Instant

internal fun inMemoryDatabase(): WishlineDatabase {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    WishlineDatabase.Schema.create(driver)
    return WishlineDatabase(driver)
}

internal fun at(day: Int): Instant = Instant.fromEpochMilliseconds(day * 86_400_000L)

internal fun cardId(value: String): BingoCardId = BingoCardId(value)

internal fun wish(id: String, title: String, at: Instant = at(1)): Wish = Wish(
    id = WishId(id),
    title = title,
    status = WishStatus.Planned(at),
    createdAt = at,
)

internal fun wishes(count: Int, at: Instant = at(1)): List<Wish> =
    (0 until count).map { wish("w$it", "wish $it", at) }
