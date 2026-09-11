package com.rmakiyama.wishline.feature.home

import com.rmakiyama.wishline.domain.BingoCard
import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishStatus
import com.rmakiyama.wishline.usecase.AddWishUseCase
import com.rmakiyama.wishline.usecase.CreateBingoCardUseCase
import com.rmakiyama.wishline.usecase.GetOpenBingoCardsStreamUseCase
import com.rmakiyama.wishline.usecase.GetUnassignedWishesStreamUseCase
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verify.VerifyMode.Companion.not
import dev.mokkery.verifySuspend
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val openCards = MutableStateFlow<List<BingoCard>>(emptyList())
    private val unassigned = MutableStateFlow<List<Wish>>(emptyList())

    private val getOpenCards = mock<GetOpenBingoCardsStreamUseCase> {
        every { invoke() } returns openCards
    }
    private val getUnassigned = mock<GetUnassignedWishesStreamUseCase> {
        every { invoke() } returns unassigned
    }
    private val addWish = mock<AddWishUseCase> {
        everySuspend { invoke(any<String>()) } returns Unit
    }
    private val createCard = mock<CreateBingoCardUseCase> {
        everySuspend { invoke(any()) } returns BingoCardId("card-1")
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given fewer than 25 wishes, then the next card is still filling`() = runTest(dispatcher) {
        unassigned.value = wishes(24)
        val vm = viewModel()

        vm.uiState.value.nextCard.readiness shouldBe NextCardReadiness.Filling
    }

    @Test
    fun `given exactly 25 wishes, then the next card is ready`() = runTest(dispatcher) {
        unassigned.value = wishes(25)
        val vm = viewModel()

        vm.uiState.value.nextCard.readiness shouldBe NextCardReadiness.Ready
    }

    @Test
    fun `given more than 25 wishes, then the next card is overflowing`() = runTest(dispatcher) {
        unassigned.value = wishes(26)
        val vm = viewModel()

        vm.uiState.value.nextCard.readiness shouldBe NextCardReadiness.Overflowing
    }

    @Test
    fun `given input with spaces around it, when it is added, then it is saved trimmed`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.onInputChange("  富士山に登る ")
        vm.onAddWish()

        verifySuspend(exactly(1)) { addWish.invoke("富士山に登る") }
    }

    @Test
    fun `when the input is added, then the field is cleared`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.onInputChange("富士山に登る")
        vm.onAddWish()

        vm.uiState.value.nextCard.input shouldBe ""
    }

    @Test
    fun `given blank input, when it is added, then nothing is saved`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.onInputChange("   ")
        vm.onAddWish()

        verifySuspend(not) { addWish.invoke(any<String>()) }
    }

    @Test
    fun `given 25 wishes, when a card is created, then those wishes make the card`() = runTest(dispatcher) {
        val wishes = wishes(25)
        unassigned.value = wishes
        val vm = viewModel()

        vm.onCreateCard()

        verifySuspend(exactly(1)) { createCard.invoke(wishes) }
    }

    @Test
    fun `given 25 wishes, when a card is created, then the screen is told which card to show`() = runTest(dispatcher) {
        unassigned.value = wishes(25)
        val vm = viewModel()

        vm.onCreateCard()

        vm.uiState.value.createdCardId shouldBe BingoCardId("card-1")
    }

    @Test
    fun `given 26 wishes, when a card is created, then nothing happens`() = runTest(dispatcher) {
        unassigned.value = wishes(26)
        val vm = viewModel()

        vm.onCreateCard()

        verifySuspend(not) { createCard.invoke(any()) }
    }

    @Test
    fun `given creation fails, when a card is created, then the next card can be used again`() = runTest(dispatcher) {
        everySuspend { createCard.invoke(any()) } throws IllegalStateException("disk full")
        unassigned.value = wishes(25)
        val vm = viewModel()

        vm.onCreateCard()

        vm.uiState.value.nextCard.isCreating.shouldBeFalse()
        vm.uiState.value.createdCardId.shouldBeNull()
    }

    @Test
    fun `given a created card, when the screen has shown it, then it is not asked to show it again`() = runTest(dispatcher) {
        unassigned.value = wishes(25)
        val vm = viewModel()
        vm.onCreateCard()

        vm.onCreatedCardShown()

        vm.uiState.value.createdCardId.shouldBeNull()
    }

    /** `uiState` only runs while collected, so every test keeps a collector alive in the background. */
    private fun TestScope.viewModel(): HomeViewModel {
        val vm = HomeViewModel(getOpenCards, getUnassigned, addWish, createCard)
        backgroundScope.launch { vm.uiState.collect {} }
        return vm
    }

    private fun wishes(count: Int): List<Wish> = List(count) { index ->
        Wish(
            id = WishId("w$index"),
            title = "wish $index",
            status = WishStatus.Planned(Instant.fromEpochMilliseconds(index.toLong())),
            createdAt = Instant.fromEpochMilliseconds(index.toLong()),
        )
    }
}
