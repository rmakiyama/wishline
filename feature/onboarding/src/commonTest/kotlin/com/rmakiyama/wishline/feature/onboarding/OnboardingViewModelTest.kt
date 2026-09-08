package com.rmakiyama.wishline.feature.onboarding

import com.rmakiyama.wishline.usecase.AddWishUseCase
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

    private val addWish = mock<AddWishUseCase> {
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

    @Test
    fun `given input with spaces around it, when it is added, then it is kept trimmed`() {
        val vm = viewModel()

        vm.onInputChange("  富士山に登る ")
        vm.onAddWish()

        vm.uiState.value.wishes shouldContainExactly listOf("富士山に登る")
    }

    @Test
    fun `when the input is added, then the field is cleared`() {
        val vm = viewModel()

        vm.onInputChange("富士山に登る")
        vm.onAddWish()

        vm.uiState.value.input shouldBe ""
    }

    @Test
    fun `given blank input, when it is added, then nothing is kept`() {
        val vm = viewModel()

        vm.onInputChange("   ")
        vm.onAddWish()

        vm.uiState.value.wishes shouldBe emptyList()
    }

    @Test
    fun `given nothing has been written, then starting is not allowed`() {
        viewModel().uiState.value.canStart.shouldBeFalse()
    }

    @Test
    fun `given one wish has been written, then starting is allowed`() {
        val vm = viewModel()

        vm.onInputChange("A")
        vm.onAddWish()

        vm.uiState.value.canStart.shouldBeTrue()
    }

    @Test
    fun `given two wishes, when finishing, then both are saved in one call`() = runTest {
        val vm = viewModel().withWishes("A", "B")

        vm.onFinish()

        verifySuspend(exactly(1)) { addWish.invoke(listOf("A", "B")) }
    }

    @Test
    fun `given two wishes, when finishing, then onboarding is completed`() = runTest {
        val vm = viewModel().withWishes("A", "B")

        vm.onFinish()

        verifySuspend(exactly(1)) { completeOnboarding.invoke() }
        vm.uiState.value.isCompleted.shouldBeTrue()
    }

    @Test
    fun `given no wish, when finishing, then nothing is saved`() = runTest {
        val vm = viewModel()

        vm.onFinish()

        verifySuspend(not) { addWish.invoke(any<List<String>>()) }
    }

    @Test
    fun `given no wish, when finishing, then onboarding is completed`() = runTest {
        val vm = viewModel()

        vm.onFinish()

        verifySuspend(exactly(1)) { completeOnboarding.invoke() }
        vm.uiState.value.isCompleted.shouldBeTrue()
    }

    @Test
    fun `given saving fails, when finishing, then the screen can be used again`() = runTest {
        everySuspend { addWish.invoke(any<List<String>>()) } throws IllegalStateException("disk full")
        val vm = viewModel().withWishes("A")

        vm.onFinish()

        vm.uiState.value.isSubmitting.shouldBeFalse()
    }

    @Test
    fun `given saving fails, when finishing, then onboarding is not completed`() = runTest {
        everySuspend { addWish.invoke(any<List<String>>()) } throws IllegalStateException("disk full")
        val vm = viewModel().withWishes("A")

        vm.onFinish()

        vm.uiState.value.isCompleted.shouldBeFalse()
        verifySuspend(not) { completeOnboarding.invoke() }
    }

    @Test
    fun `given two wishes, when one is removed, then only that one is dropped`() {
        val vm = viewModel().withWishes("A", "B")

        vm.onRemoveWish(0)

        vm.uiState.value.wishes shouldContainExactly listOf("B")
    }

    private fun viewModel() = OnboardingViewModel(addWish, completeOnboarding)

    private fun OnboardingViewModel.withWishes(vararg titles: String) = apply {
        titles.forEach {
            onInputChange(it)
            onAddWish()
        }
    }
}
