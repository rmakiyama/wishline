package com.rmakiyama.wishline.feature.home

import com.rmakiyama.wishline.domain.BingoCard
import com.rmakiyama.wishline.domain.BingoCardId
import com.rmakiyama.wishline.domain.BingoSlot
import com.rmakiyama.wishline.domain.SlotStatus
import com.rmakiyama.wishline.domain.UnassignedWish
import com.rmakiyama.wishline.domain.Wish
import com.rmakiyama.wishline.domain.WishId
import com.rmakiyama.wishline.domain.WishStatus
import com.rmakiyama.wishline.core.ui.component.WishAction
import com.rmakiyama.wishline.usecase.AddWishUseCase
import com.rmakiyama.wishline.usecase.ChangeBingoCardLabelUseCase
import com.rmakiyama.wishline.usecase.CloseBingoCardUseCase
import com.rmakiyama.wishline.usecase.ChangeWishTitleUseCase
import com.rmakiyama.wishline.usecase.DeleteWishUseCase
import com.rmakiyama.wishline.usecase.MarkWishDoneUseCase
import com.rmakiyama.wishline.usecase.MarkWishSomedayUseCase
import com.rmakiyama.wishline.usecase.RestoreWishUseCase
import com.rmakiyama.wishline.usecase.CreateBingoCardUseCase
import com.rmakiyama.wishline.usecase.GetOpenBingoCardsStreamUseCase
import com.rmakiyama.wishline.usecase.GetUnassignedWishesStreamUseCase
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.MockMode
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verify.VerifyMode.Companion.not
import dev.mokkery.verifySuspend
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
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
    private val unassigned = MutableStateFlow<List<UnassignedWish>>(emptyList())

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
    private val changeTitle = mock<ChangeWishTitleUseCase>(MockMode.autoUnit)
    private val markDone = mock<MarkWishDoneUseCase>(MockMode.autoUnit)
    private val markSomeday = mock<MarkWishSomedayUseCase>(MockMode.autoUnit)
    private val restore = mock<RestoreWishUseCase>(MockMode.autoUnit)
    private val delete = mock<DeleteWishUseCase>(MockMode.autoUnit)
    private val closeCard = mock<CloseBingoCardUseCase>(MockMode.autoUnit)
    private val changeLabel = mock<ChangeBingoCardLabelUseCase>(MockMode.autoUnit)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given only the cards have been emitted, then the screen is not loaded`() = runTest(dispatcher) {
        every { getUnassigned.invoke() } returns emptyFlow()
        val vm = viewModel()

        vm.uiState.value.isLoaded.shouldBeFalse()
    }

    @Test
    fun `given both streams have emitted, then the screen is loaded`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.uiState.value.isLoaded.shouldBeTrue()
    }

    @Test
    fun `given open cards, then they are shown in stream order`() = runTest(dispatcher) {
        val cards = listOf(card("c1"), card("c2"))
        openCards.value = cards
        val vm = viewModel()

        vm.uiState.value.cards shouldBe cards
    }

    @Test
    fun `given fewer than 25 wishes, then the next card is still filling`() = runTest(dispatcher) {
        unassigned.value = unassigned(24)
        val vm = viewModel()

        vm.uiState.value.nextCard.readiness shouldBe NextCardReadiness.Filling
    }

    @Test
    fun `given exactly 25 wishes, then the next card is ready`() = runTest(dispatcher) {
        unassigned.value = unassigned(25)
        val vm = viewModel()

        vm.uiState.value.nextCard.readiness shouldBe NextCardReadiness.Ready
    }

    @Test
    fun `given more than 25 wishes, then the next card is overflowing`() = runTest(dispatcher) {
        unassigned.value = unassigned(26)
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
        unassigned.value = wishes.map { UnassignedWish(it, hasBeenOnCard = false) }
        val vm = viewModel()

        vm.onCreateCard()

        verifySuspend(exactly(1)) { createCard.invoke(wishes) }
    }

    @Test
    fun `given 25 wishes, when a card is created, then the screen is told which card to show`() = runTest(dispatcher) {
        unassigned.value = unassigned(25)
        val vm = viewModel()

        vm.onCreateCard()

        vm.uiState.value.createdCardId shouldBe BingoCardId("card-1")
    }

    @Test
    fun `given 24 wishes, when a card is created, then nothing happens`() = runTest(dispatcher) {
        unassigned.value = unassigned(24)
        val vm = viewModel()

        vm.onCreateCard()

        verifySuspend(not) { createCard.invoke(any()) }
    }

    @Test
    fun `given 26 wishes, when a card is created, then nothing happens`() = runTest(dispatcher) {
        unassigned.value = unassigned(26)
        val vm = viewModel()

        vm.onCreateCard()

        verifySuspend(not) { createCard.invoke(any()) }
    }

    @Test
    fun `given creation fails, when a card is created, then the next card can be used again`() = runTest(dispatcher) {
        everySuspend { createCard.invoke(any()) } throws IllegalStateException("disk full")
        unassigned.value = unassigned(25)
        val vm = viewModel()

        vm.onCreateCard()

        vm.uiState.value.nextCard.isCreating.shouldBeFalse()
        vm.uiState.value.createdCardId.shouldBeNull()
    }

    @Test
    fun `given a created card, when the screen has shown it, then it is not asked to show it again`() = runTest(dispatcher) {
        unassigned.value = unassigned(25)
        val vm = viewModel()
        vm.onCreateCard()

        vm.onCreatedCardShown()

        vm.uiState.value.createdCardId.shouldBeNull()
    }

    @Test
    fun `given a card, when it is flipped twice, then it shows its front again`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.onFlipCard(BingoCardId("c1"))
        vm.onFlipCard(BingoCardId("c1"))

        vm.uiState.value.flippedCardIds shouldBe emptySet()
    }

    @Test
    fun `given a wish on the next card, when it is opened, then it offers achieve, someday and delete`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.onWishClick(wishes(1).first(), WishPlace.NextCard(hasBeenOnCard = false))

        vm.uiState.value.sheet?.actions shouldBe listOf(WishAction.Achieve, WishAction.Someday, WishAction.Delete)
    }

    @Test
    fun `given a wish on the next card that has been on a card, when it is opened, then it offers achieve and someday`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.onWishClick(wishes(1).first(), WishPlace.NextCard(hasBeenOnCard = true))

        vm.uiState.value.sheet?.actions shouldBe listOf(WishAction.Achieve, WishAction.Someday)
    }

    @Test
    fun `given a wish on the next card that has been on a card, when delete is chosen, then nothing happens`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.onWishClick(wishes(1).first(), WishPlace.NextCard(hasBeenOnCard = true))

        vm.onWishAction(WishAction.Delete)

        verifySuspend(not) { delete.invoke(any()) }
    }

    @Test
    fun `given a planned wish on a card, when it is opened, then it offers achieve and someday`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.onWishClick(wishes(1).first(), onCard)

        vm.uiState.value.sheet?.actions shouldBe listOf(WishAction.Achieve, WishAction.Someday)
    }

    @Test
    fun `given a done wish on a card, when it is opened, then it only offers undo`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.onWishClick(wish(WishStatus.Done(now)), onCard)

        vm.uiState.value.sheet?.actions shouldBe listOf(WishAction.UndoAchieve)
    }

    @Test
    fun `given a someday wish on a card, when it is opened, then it only offers restore`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.onWishClick(wish(WishStatus.Someday(now)), onCard)

        vm.uiState.value.sheet?.actions shouldBe listOf(WishAction.Restore)
    }

    @Test
    fun `given an open sheet, when achieve is chosen, then the wish is marked done`() = runTest(dispatcher) {
        val wish = wishes(1).first()
        val vm = viewModel()
        vm.onWishClick(wish, onCard)

        vm.onWishAction(WishAction.Achieve)

        verifySuspend(exactly(1)) { markDone.invoke(wish.id) }
    }

    @Test
    fun `given an open sheet, when an action is chosen, then the sheet closes`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.onWishClick(wishes(1).first(), onCard)

        vm.onWishAction(WishAction.Achieve)

        vm.uiState.value.sheet.shouldBeNull()
    }

    @Test
    fun `given a done wish on a card, when delete is chosen, then nothing happens`() = runTest(dispatcher) {
        val wish = wish(WishStatus.Done(now))
        val vm = viewModel()
        vm.onWishClick(wish, onCard)

        vm.onWishAction(WishAction.Delete)

        verifySuspend(not) { delete.invoke(any()) }
    }

    @Test
    fun `given an open sheet, when someday is chosen, then the wish is marked someday`() = runTest(dispatcher) {
        val wish = wishes(1).first()
        val vm = viewModel()
        vm.onWishClick(wish, onCard)

        vm.onWishAction(WishAction.Someday)

        verifySuspend(exactly(1)) { markSomeday.invoke(wish.id) }
    }

    @Test
    fun `given a someday wish, when restore is chosen, then the wish returns to planned`() = runTest(dispatcher) {
        val wish = wish(WishStatus.Someday(now))
        val vm = viewModel()
        vm.onWishClick(wish, onCard)

        vm.onWishAction(WishAction.Restore)

        verifySuspend(exactly(1)) { restore.invoke(wish.id) }
    }

    @Test
    fun `given a wish on the next card, when delete is chosen, then the wish is deleted`() = runTest(dispatcher) {
        val wish = wishes(1).first()
        val vm = viewModel()
        vm.onWishClick(wish, WishPlace.NextCard(hasBeenOnCard = false))

        vm.onWishAction(WishAction.Delete)

        verifySuspend(exactly(1)) { delete.invoke(wish.id) }
    }

    @Test
    fun `given a planned wish on a card, when delete is chosen, then nothing happens`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.onWishClick(wishes(1).first(), onCard)

        vm.onWishAction(WishAction.Delete)

        verifySuspend(not) { delete.invoke(any()) }
    }

    @Test
    fun `given deleting fails, when delete is chosen, then the screen keeps working`() = runTest(dispatcher) {
        everySuspend { delete.invoke(any()) } throws IllegalStateException("still on a card")
        val vm = viewModel()
        vm.onWishClick(wishes(1).first(), WishPlace.NextCard(hasBeenOnCard = false))

        vm.onWishAction(WishAction.Delete)

        vm.uiState.value.sheet.shouldBeNull()
    }

    @Test
    fun `given a done wish, when undo is chosen, then the wish returns to planned`() = runTest(dispatcher) {
        val wish = wish(WishStatus.Done(now))
        val vm = viewModel()
        vm.onWishClick(wish, onCard)

        vm.onWishAction(WishAction.UndoAchieve)

        verifySuspend(exactly(1)) { restore.invoke(wish.id) }
    }

    @Test
    fun `given editing has started, then the input holds the current title`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.onWishClick(wishes(1).first(), onCard)

        vm.onStartEditTitle()

        vm.uiState.value.sheet?.titleInput shouldBe "wish 0"
    }

    @Test
    fun `given a new title with spaces around it, when it is saved, then it is stored trimmed`() = runTest(dispatcher) {
        val wish = wishes(1).first()
        val vm = viewModel()
        vm.onWishClick(wish, onCard)
        vm.onStartEditTitle()
        vm.onTitleInputChange("  富士山に登る ")

        vm.onSaveTitle()

        verifySuspend(exactly(1)) { changeTitle.invoke(wish.id, "富士山に登る") }
    }

    @Test
    fun `given a blank title, when it is saved, then nothing is stored`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.onWishClick(wishes(1).first(), onCard)
        vm.onStartEditTitle()
        vm.onTitleInputChange("   ")

        vm.onSaveTitle()

        verifySuspend(not) { changeTitle.invoke(any(), any()) }
    }

    private val now = Instant.fromEpochMilliseconds(0)
    private val onCard = WishPlace.Card(BingoCardId("c1"), number = 1)

    @Test
    fun `given a card, when close is chosen, then the confirmation shows how many wishes go back`() = runTest(dispatcher) {
        val card = card("c1")
        openCards.value = listOf(card)
        val vm = viewModel()

        vm.onCloseCardClick(card)

        (vm.uiState.value.cardDialog as CardDialog.CloseConfirm).plannedCount shouldBe 25
    }

    @Test
    fun `given the close confirmation, when it is confirmed, then the card is closed`() = runTest(dispatcher) {
        val card = card("c1")
        val vm = viewModel()
        vm.onCloseCardClick(card)

        vm.onConfirmClose()

        verifySuspend(exactly(1)) { closeCard.invoke(card.id) }
        vm.uiState.value.cardDialog.shouldBeNull()
    }

    @Test
    fun `given the close confirmation, when it is dismissed, then the card stays open`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.onCloseCardClick(card("c1"))

        vm.onDismissCardDialog()

        verifySuspend(not) { closeCard.invoke(any()) }
    }

    @Test
    fun `given a card, when the label is saved, then the card gets that label`() = runTest(dispatcher) {
        val card = card("c1")
        val vm = viewModel()
        vm.onEditLabelClick(card)

        vm.onLabelInputChange("2026 夏")
        vm.onSaveLabel()

        verifySuspend(exactly(1)) { changeLabel.invoke(card.id, "2026 夏") }
    }

    @Test
    fun `given a card with one slot left, when that wish is achieved, then the card closes on its own`() = runTest(dispatcher) {
        val card = card("c1", "w0")
        openCards.value = listOf(card)
        val vm = viewModel()
        vm.onWishClick(card.slots.first { it.wish.id == WishId("w0") }.wish, WishPlace.Card(card.id, card.number))

        vm.onWishAction(WishAction.Achieve)

        verifySuspend(exactly(1)) { closeCard.invoke(card.id) }
    }

    @Test
    fun `given a card with two slots left, when one wish is achieved, then the card stays open`() = runTest(dispatcher) {
        val card = card("c1", "w0", "w1")
        openCards.value = listOf(card)
        val vm = viewModel()
        vm.onWishClick(card.slots.first { it.wish.id == WishId("w0") }.wish, WishPlace.Card(card.id, card.number))

        vm.onWishAction(WishAction.Achieve)

        verifySuspend(not) { closeCard.invoke(any()) }
    }

    private fun viewModel() = HomeViewModel(
        getOpenCards,
        getUnassigned,
        addWish,
        createCard,
        changeTitle,
        markDone,
        markSomeday,
        restore,
        delete,
        closeCard,
        changeLabel,
    )

    private fun wish(status: WishStatus): Wish = wishes(1).first().copy(status = status)

    /** With [markedExcept] given, every slot is marked but those wishes. */
    private fun card(id: String, vararg markedExcept: String): BingoCard = BingoCard(
        id = BingoCardId(id),
        number = 1,
        label = null,
        createdAt = Instant.fromEpochMilliseconds(0),
        closedAt = null,
        slots = wishes(BingoCard.SLOT_COUNT).mapIndexed { position, wish ->
            val marked = markedExcept.isNotEmpty() && wish.id.value !in markedExcept
            BingoSlot(
                position = position,
                wish = wish,
                status = if (marked) SlotStatus.Marked(now) else SlotStatus.Unmarked,
            )
        },
    )

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
