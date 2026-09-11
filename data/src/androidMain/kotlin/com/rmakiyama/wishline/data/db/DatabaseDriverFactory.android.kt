package com.rmakiyama.wishline.data.db

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(
    private val context: Context,
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = WishlineDatabase.Schema,
            context = context,
            name = "wishline.db",
            callback = ForeignKeysCallback,
        )
    }
}

/** SQLite leaves foreign keys unenforced unless every connection asks for them. */
private object ForeignKeysCallback : AndroidSqliteDriver.Callback(WishlineDatabase.Schema) {
    override fun onOpen(db: SupportSQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }
}
