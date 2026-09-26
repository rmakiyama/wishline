package com.rmakiyama.wishline.feature.home

import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.UnassignedWish
import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishStatus
import com.rmakiyama.wishline.usecase.CreateBingoCardUseCase
import com.rmakiyama.wishline.usecase.GetUnassignedWishesStreamUseCase
import dev.mokkery.answering.calls
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
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class SelectionViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val unassigned = MutableStateFlow(unassigned(27))

    private val getUnassigned = mock<GetUnassignedWishesStreamUseCase> {
        every { invoke() } returns unassigned
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
    fun `given the screen opens, then nothing is selected`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.uiState.value.selectedCount shouldBe 0
    }

    @Test
    fun `given a selected wish, when it is toggled, then it is no longer selected`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.onToggle(WishId("w0"))

        vm.onToggle(WishId("w0"))

        vm.uiState.value.selectedCount shouldBe 0
    }

    @Test
    fun `given nothing is selected, when all are toggled, then every wish is selected`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.onToggleAll()

        vm.uiState.value.selectedCount shouldBe 27
    }

    @Test
    fun `given some are selected, when all are toggled, then every wish is selected`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.onToggle(WishId("w0"))

        vm.onToggleAll()

        vm.uiState.value.isAllSelected.shouldBeTrue()
    }

    @Test
    fun `given every wish is selected, when all are toggled, then nothing is selected`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.onToggleAll()

        vm.onToggleAll()

        vm.uiState.value.selectedCount shouldBe 0
    }

    @Test
    fun `given a selected wish leaves the list, then it is not counted`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.onToggle(WishId("w26"))

        unassigned.value = unassigned(26)

        vm.uiState.value.selectedCount shouldBe 0
    }

    @Test
    fun `given 24 are selected, then a card cannot be created`() = runTest(dispatcher) {
        val vm = viewModel()
        select(vm, 24)

        vm.uiState.value.canCreate.shouldBeFalse()
    }

    @Test
    fun `given 26 are selected, then a card cannot be created`() = runTest(dispatcher) {
        val vm = viewModel()
        select(vm, 26)

        vm.uiState.value.canCreate.shouldBeFalse()
    }

    @Test
    fun `given 26 are selected, when create is tapped, then nothing is saved`() = runTest(dispatcher) {
        val vm = viewModel()
        select(vm, 26)

        vm.onCreate()

        verifySuspend(not) { createCard.invoke(any()) }
    }

    @Test
    fun `given 25 are selected, when a card is created, then exactly those wishes make the card`() = runTest(dispatcher) {
        val vm = viewModel()
        select(vm, 25)

        vm.onCreate()

        verifySuspend(exactly(1)) { createCard.invoke(wishes(25)) }
    }

    @Test
    fun `given 25 are selected, when a card is created, then the screen is told to leave`() = runTest(dispatcher) {
        val vm = viewModel()
        select(vm, 25)

        vm.onCreate()

        vm.uiState.value.isCreated.shouldBeTrue()
    }

    @Test
    fun `given a card has been created, when create is tapped again, then no second card is made`() = runTest(dispatcher) {
        val vm = viewModel()
        select(vm, 25)
        vm.onCreate()

        vm.onCreate()

        verifySuspend(exactly(1)) { createCard.invoke(any()) }
    }

    @Test
    fun `given a card is being created, when create is tapped again, then no second card is made`() = runTest(dispatcher) {
        val pending = CompletableDeferred<BingoCardId>()
        everySuspend { createCard.invoke(any()) } calls { pending.await() }
        val vm = viewModel()
        select(vm, 25)
        vm.onCreate()

        vm.onCreate()

        verifySuspend(exactly(1)) { createCard.invoke(any()) }
    }

    @Test
    fun `given creation fails, when a card is created, then the screen stays to try again`() = runTest(dispatcher) {
        everySuspend { createCard.invoke(any()) } throws IllegalStateException("disk full")
        val vm = viewModel()
        select(vm, 25)

        vm.onCreate()

        vm.uiState.value.isCreated.shouldBeFalse()
        vm.uiState.value.canCreate.shouldBeTrue()
    }

    private fun viewModel() = SelectionViewModel(getUnassigned, createCard)

    private fun select(vm: SelectionViewModel, count: Int) {
        repeat(count) { vm.onToggle(WishId("w$it")) }
    }

    private fun unassigned(count: Int): List<UnassignedWish> =
        wishes(count).map { UnassignedWish(it, hasBeenOnCard = false) }

    private fun wishes(count: Int): List<Wish> = List(count) { index ->
        Wish(
            id = WishId("w$index"),
            title = "wish $index",
            status = WishStatus.Planned(Instant.fromEpochMilliseconds(index.toLong())),
            createdAt = Instant.fromEpochMilliseconds(index.toLong()),
        )
    }
}
