package com.rmakiyama.wishline.feature.onboarding

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rmakiyama.wishline.designsystem.theme.WlTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private const val PAGE_COUNT = 3
private const val PAGE_INPUT = 2

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) onFinished()
    }

    OnboardingScreen(
        uiState = uiState,
        onInputChange = viewModel::onInputChange,
        onAddWish = viewModel::onAddWish,
        onRemoveWish = viewModel::onRemoveWish,
        onFinish = viewModel::onFinish,
        modifier = modifier,
    )
}

@Composable
private fun OnboardingScreen(
    uiState: OnboardingUiState,
    onInputChange: (String) -> Unit,
    onAddWish: () -> Unit,
    onRemoveWish: (Int) -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })
    val scope = rememberCoroutineScope()

    Scaffold(modifier = modifier, containerColor = WlTheme.colorScheme.surface) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                when (page) {
                    0 -> ConceptPage(
                        illustration = { NotesIllustration() },
                        headline = stringResource(Res.string.onboarding_page1_headline),
                        body = stringResource(Res.string.onboarding_page1_body),
                    )
                    1 -> ConceptPage(
                        illustration = { CardIllustration() },
                        headline = stringResource(Res.string.onboarding_page2_headline),
                        body = stringResource(Res.string.onboarding_page2_body),
                    )
                    else -> InputPage(
                        uiState = uiState,
                        onInputChange = onInputChange,
                        onAddWish = onAddWish,
                        onRemoveWish = onRemoveWish,
                    )
                }
            }
            val page = pagerState.currentPage
            Footer(
                page = page,
                uiState = uiState,
                onNext = { scope.launch { pagerState.animateScrollToPage(page + 1) } },
                onFinish = onFinish,
            )
        }
    }
}

@Composable
private fun ConceptPage(
    illustration: @Composable () -> Unit,
    headline: String,
    body: String,
) {
    val spacing = WlTheme.spacing
    // Bottom-aligned when the content fits, scrollable when the screen is too short for it.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.l),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center,
            ) {
                illustration()
            }
            Spacer(Modifier.height(40.dp))
            Text(
                text = headline,
                style = WlTheme.typography.displaySmall,
                color = WlTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(spacing.s + spacing.xs))
            Text(
                text = body,
                style = WlTheme.typography.bodyLarge,
                color = WlTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(spacing.xl))
        }
    }
}

@Composable
private fun InputPage(
    uiState: OnboardingUiState,
    onInputChange: (String) -> Unit,
    onAddWish: () -> Unit,
    onRemoveWish: (Int) -> Unit,
) {
    val spacing = WlTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.l)
            .padding(top = spacing.l),
    ) {
        Text(
            text = stringResource(Res.string.onboarding_page3_headline),
            style = WlTheme.typography.headlineMedium,
            color = WlTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(spacing.s))
        Text(
            text = stringResource(Res.string.onboarding_page3_body),
            style = WlTheme.typography.bodyMedium,
            color = WlTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(spacing.l))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .animateContentSize(),
        ) {
            itemsIndexed(uiState.wishes) { index, title ->
                PendingWishRow(title = title, onRemove = { onRemoveWish(index) })
                HorizontalDivider(color = WlTheme.colorScheme.surfaceContainerHigh)
            }
        }
        Spacer(Modifier.height(spacing.m))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            OutlinedTextField(
                value = uiState.input,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(Res.string.onboarding_input_placeholder)) },
                singleLine = true,
                shape = WlTheme.shapes.large,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAddWish() }),
            )
            FilledIconButton(
                onClick = onAddWish,
                enabled = uiState.input.isNotBlank(),
                modifier = Modifier.size(56.dp),
                shape = WlTheme.shapes.large,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = WlTheme.colorScheme.primaryContainer,
                    contentColor = WlTheme.colorScheme.onPrimaryContainer,
                ),
            ) {
                Icon(ArrowUpIcon, contentDescription = stringResource(Res.string.onboarding_add))
            }
        }
        Spacer(Modifier.height(spacing.m))
    }
}

@Composable
private fun PendingWishRow(title: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = WlTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(WlTheme.spacing.s + WlTheme.spacing.xs),
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(WlTheme.colorScheme.surfaceContainerHighest),
        )
        Text(
            text = title,
            style = WlTheme.typography.bodyLarge,
            color = WlTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onRemove) {
            Icon(
                CloseIcon,
                contentDescription = null,
                tint = WlTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Footer(
    page: Int,
    uiState: OnboardingUiState,
    onNext: () -> Unit,
    onFinish: () -> Unit,
) {
    val spacing = WlTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.l)
            .padding(bottom = spacing.l),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.m),
    ) {
        PageIndicator(page = page)
        if (page < PAGE_INPUT) {
            PrimaryButton(text = stringResource(Res.string.onboarding_next), onClick = onNext)
        } else {
            PrimaryButton(
                text = stringResource(Res.string.onboarding_start),
                onClick = onFinish,
                enabled = uiState.canStart,
            )
            TextButton(onClick = onFinish, enabled = !uiState.isSubmitting) {
                Text(
                    text = stringResource(Res.string.onboarding_later),
                    style = WlTheme.typography.labelLarge,
                    color = WlTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        contentPadding = ButtonDefaults.ContentPadding,
    ) {
        Text(text = text, style = WlTheme.typography.titleMedium)
    }
}

@Composable
private fun PageIndicator(page: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(PAGE_COUNT) { index ->
            val active = index == page
            Box(
                modifier = Modifier
                    .animateContentSize()
                    .height(6.dp)
                    .width(if (active) 18.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (active) WlTheme.colorScheme.primary else WlTheme.colorScheme.outlineVariant,
                    ),
            )
        }
    }
}

/** Three tilted note cards: "write wishes down, one at a time". */
@Composable
private fun NotesIllustration() {
    val samples = listOf(
        stringResource(Res.string.onboarding_sample_1),
        stringResource(Res.string.onboarding_sample_2),
        stringResource(Res.string.onboarding_sample_3),
    )
    Box(modifier = Modifier.fillMaxSize()) {
        NoteCard(samples[0], rotation = -6f, x = 12.dp, y = 30.dp, color = WlTheme.colorScheme.surfaceContainerLowest)
        NoteCard(samples[1], rotation = 4f, x = 92.dp, y = 100.dp, color = WlTheme.extendedColors.fill)
        NoteCard(samples[2], rotation = -3f, x = 32.dp, y = 180.dp, color = WlTheme.colorScheme.surfaceContainerLowest)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = 10.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(WlTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(PlusIcon, contentDescription = null, tint = WlTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
private fun NoteCard(text: String, rotation: Float, x: Dp, y: Dp, color: Color) {
    Box(
        modifier = Modifier
            .offset(x = x, y = y)
            .rotate(rotation)
            .width(200.dp)
            .height(72.dp)
            .clip(WlTheme.shapes.large)
            .background(color)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            style = WlTheme.typography.titleMedium,
            color = WlTheme.colorScheme.onSurface,
        )
    }
}

/** A 5×5 card with a few fulfilled cells and one completed line wrapped in a capsule. */
@Composable
private fun CardIllustration() {
    val fulfilled = setOf(0, 5, 10, 15, 20, 16, 17, 18, 19, 2, 8, 24)
    val cell = 40.dp
    val gap = 6.dp
    Box(
        modifier = Modifier
            .clip(WlTheme.shapes.extraLarge)
            .background(WlTheme.colorScheme.surfaceContainerLow)
            .padding(20.dp),
    ) {
        // Capsule behind the first column.
        Box(
            modifier = Modifier
                .offset(x = -5.dp, y = -5.dp)
                .width(cell + 10.dp)
                .height(cell * 5 + gap * 4 + 10.dp)
                .clip(CircleShape)
                .background(WlTheme.extendedColors.line),
        )
        Column(verticalArrangement = Arrangement.spacedBy(gap)) {
            repeat(5) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    repeat(5) { col ->
                        val index = row * 5 + col
                        val done = index in fulfilled
                        Box(
                            modifier = Modifier
                                .size(cell)
                                .clip(if (done) CircleShape else WlTheme.shapes.medium)
                                .background(
                                    if (done) WlTheme.extendedColors.fill
                                    else WlTheme.colorScheme.surfaceContainerHigh,
                                ),
                        )
                    }
                }
            }
        }
    }
}

// Stroke icons drawn inline so the module does not depend on material-icons.
private fun strokeIcon(name: String, builder: PathBuilder.() -> Unit): ImageVector =
    ImageVector.Builder(name = name, defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f)
        .path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathBuilder = builder,
        )
        .build()

private val PlusIcon: ImageVector = strokeIcon("Plus") {
    moveTo(12f, 5f); lineTo(12f, 19f); moveTo(5f, 12f); lineTo(19f, 12f)
}
private val ArrowUpIcon: ImageVector = strokeIcon("ArrowUp") {
    moveTo(12f, 19f); lineTo(12f, 5f); moveTo(6f, 11f); lineTo(12f, 5f); lineTo(18f, 11f)
}
private val CloseIcon: ImageVector = strokeIcon("Close") {
    moveTo(6f, 6f); lineTo(18f, 18f); moveTo(18f, 6f); lineTo(6f, 18f)
}
