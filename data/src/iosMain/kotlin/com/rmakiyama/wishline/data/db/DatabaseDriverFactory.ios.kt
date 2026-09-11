package com.rmakiyama.wishline.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = WishlineDatabase.Schema,
            name = "wishline.db",
            onConfiguration = { configuration ->
                // SQLite leaves foreign keys unenforced unless every connection asks for them.
                configuration.copy(
                    extendedConfig = configuration.extendedConfig.copy(foreignKeyConstraints = true),
                )
            },
        )
    }
}
