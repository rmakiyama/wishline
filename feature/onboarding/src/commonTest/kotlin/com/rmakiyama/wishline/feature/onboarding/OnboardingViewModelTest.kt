package com.rmakiyama.wishline.feature.onboarding

import com.rmakiyama.wishline.usecase.AddItemUseCase
import com.rmakiyama.wishline.usecase.CompleteOnboardingUseCase
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verify.VerifyMode.Companion.not
import dev.mokkery.verifySuspend
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val addItem = mock<AddItemUseCase> {
        everySuspend { invoke(any<List<String>>()) } returns Unit
    }
    private val completeOnboarding = mock<CompleteOnboardingUseCase> {
        everySuspend { invoke() } returns Unit
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = OnboardingViewModel(addItem, completeOnboarding)

    @Test
    fun addItemAppendsTrimmedInputAndClearsIt() {
        val vm = viewModel()
        vm.onInputChange("  富士山に登る ")
        vm.onAddItem()

        vm.uiState.value.items shouldContainExactly listOf("富士山に登る")
        vm.uiState.value.input shouldBe ""
    }

    @Test
    fun addItemIgnoresBlankInput() {
        val vm = viewModel()
        vm.onInputChange("   ")
        vm.onAddItem()

        vm.uiState.value.items shouldBe emptyList()
    }

    @Test
    fun canStartRequiresAnItem() {
        val vm = viewModel()
        vm.uiState.value.canStart.shouldBeFalse()

        vm.onInputChange("A"); vm.onAddItem()
        vm.uiState.value.canStart.shouldBeTrue()
    }

    @Test
    fun finishSavesItemsInOneCallThenCompletes() = runTest {
        val vm = viewModel()
        vm.onInputChange("A"); vm.onAddItem()
        vm.onInputChange("B"); vm.onAddItem()

        vm.onFinish()

        verifySuspend(exactly(1)) { addItem.invoke(listOf("A", "B")) }
        verifySuspend(exactly(1)) { completeOnboarding.invoke() }
        vm.uiState.value.isCompleted.shouldBeTrue()
    }

    @Test
    fun finishWithoutItemsOnlyCompletes() = runTest {
        val vm = viewModel()

        vm.onFinish()

        verifySuspend(not) { addItem.invoke(any<List<String>>()) }
        verifySuspend(exactly(1)) { completeOnboarding.invoke() }
        vm.uiState.value.isCompleted.shouldBeTrue()
    }

    @Test
    fun finishFailureReleasesSubmittingSoUserCanRetry() = runTest {
        everySuspend { addItem.invoke(any<List<String>>()) } throws IllegalStateException("disk full")
        val vm = viewModel()
        vm.onInputChange("A"); vm.onAddItem()

        vm.onFinish()

        vm.uiState.value.isSubmitting.shouldBeFalse()
        vm.uiState.value.isCompleted.shouldBeFalse()
        verifySuspend(not) { completeOnboarding.invoke() }
    }

    @Test
    fun removeItemDropsOnlyThatIndex() {
        val vm = viewModel()
        vm.onInputChange("A"); vm.onAddItem()
        vm.onInputChange("B"); vm.onAddItem()

        vm.onRemoveItem(0)

        vm.uiState.value.items shouldContainExactly listOf("B")
    }
}
