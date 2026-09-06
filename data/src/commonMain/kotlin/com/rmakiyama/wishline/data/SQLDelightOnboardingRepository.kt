package com.rmakiyama.wishline.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.rmakiyama.wishline.data.db.WishlineDatabase
import com.rmakiyama.wishline.domain.OnboardingRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Inject
class SQLDelightOnboardingRepository(
    private val database: WishlineDatabase,
) : OnboardingRepository {

    private val queries = database.appPreferenceQueries

    override fun isCompletedStream(): Flow<Boolean> {
        return queries.selectByKey(KEY_ONBOARDING_COMPLETED)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it == VALUE_TRUE }
    }

    override suspend fun markCompleted() {
        withContext(Dispatchers.IO) {
            queries.upsert(pref_key = KEY_ONBOARDING_COMPLETED, pref_value = VALUE_TRUE)
        }
    }

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        const val VALUE_TRUE = "true"
    }
}
