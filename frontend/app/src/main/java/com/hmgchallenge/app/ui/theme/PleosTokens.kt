package com.hmgchallenge.app.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Pleos Connect design tokens for spacing, sizing, responsive breakpoints, shapes, motion, and elevation.
 *
 * Guidelines and token origins:
 * - Pleos Connect UI specs (48dp icons, 8px baseline grid, 3-level shadow, INTER cubic-bezier & durations, 792dp toast):
 *   Pleos Connect UI design guide (foundations, components)
 * - Touch targets (64dp min, 72dp primary action, 96dp hero action):
 *   Pleos has no touch-size number; Android for Cars quality guideline UX-1 is the fallback
 */
@Immutable
data class PleosDimens(
    // Spacing & Padding
    val screenPadding: Dp = 24.dp,
    val shellGap: Dp = 16.dp,
    val contentInset: Dp = 8.dp,
    val cardPadding: Dp = 24.dp,
    val cardPaddingCompact: Dp = 20.dp,
    val sectionSpacing: Dp = 24.dp,
    val cardContentSpacing: Dp = 24.dp,
    val itemSpacing: Dp = 16.dp,
    val tightSpacing: Dp = 12.dp,
    val microSpacing: Dp = 8.dp,
    val nanoSpacing: Dp = 4.dp,
    val mainScreenVerticalPadding: Dp = 40.dp,
    val infoCardVerticalPadding: Dp = 28.dp,
    val bannerHorizontalPadding: Dp = 28.dp,
    val bannerVerticalPadding: Dp = 20.dp,
    val buttonHorizontalPadding: Dp = 16.dp,
    val buttonHorizontalPaddingCompact: Dp = 12.dp,
    val buttonVerticalPadding: Dp = 16.dp,
    val buttonVerticalPaddingCompact: Dp = 12.dp,

    // Touch Targets & Component Sizing
    val touchMinSize: Dp = 64.dp,
    val buttonHeight: Dp = 72.dp,
    val heroButtonHeight: Dp = 96.dp,
    val rowMinHeight: Dp = 88.dp,
    val headerBackWidth: Dp = 96.dp,
    val stepperButtonWidth: Dp = 72.dp,
    val quantityMinWidth: Dp = 64.dp,
    val iconSize: Dp = 48.dp,
    val iconStroke: Dp = 3.dp,
    val actionTileHeight: Dp = 112.dp,
    val actionChoiceHeight: Dp = 224.dp,
    /** 주행 중 화면의 유일한 주요 조작(울음 대응). 팀 판단: 빈 화면을 남기지 않고 한눈에 찾게 크게 둔다. */
    val drivingActionHeight: Dp = 160.dp,
    val progressTrackHeight: Dp = 8.dp,
    val progressSize: Dp = 32.dp,
    val checkboxSize: Dp = 32.dp,
    val tabIndicatorWidth: Dp = 6.dp,
    val tabIndicatorHeight: Dp = 24.dp,

    // Borders & Strokes
    val accentBarWidth: Dp = 6.dp,
    val accentBarHeight: Dp = 28.dp,
    val errorBarWidth: Dp = 8.dp,
    val borderThin: Dp = 1.5.dp,
    val borderMedium: Dp = 2.dp,
    val strokeCheck: Dp = 3.dp,
    val strokeCheckMark: Dp = 4.dp,

    // Overlay / Toast Specs (Pleos UI design guide, toast)
    val toastMaxWidth: Dp = 792.dp,
    val toastTextMaxWidth: Dp = 712.dp,
    val toastMargin: Dp = 24.dp,

    // Responsive Window Breakpoints (width-driven for Pleos 1-window portrait & 2-window landscape)
    val breakpointTwoColumn: Dp = 1_000.dp,
    val breakpointWideCard: Dp = 850.dp,
    val breakpointThreeGrid: Dp = 700.dp,
    val breakpointHorizontalChoices: Dp = 650.dp,
    val breakpointCareWide: Dp = 600.dp,
    val breakpointMinuteGrid: Dp = 540.dp,
    val breakpointTwoGrid: Dp = 360.dp,
    val breakpointQuickRecordTwoCol: Dp = 244.dp,
    /** 아이콘이 붙은 큰 기록 버튼(출발 준비 안 돌봄 기록 탭)을 2열로 둘 최소 폭. 버튼 하나가 "수면 시작"을 한 줄로 담는 폭(약 200dp) × 2 + 간격. */
    val breakpointQuickRecordTwoColLarge: Dp = 440.dp,
    /** 출발 준비 안 돌봄 기록 탭에서 기록 버튼과 최근 기록을 나란히 둘 최소 폭. 반쪽 폭이 위 2열 기준(440dp)을 넘도록 440 × 2 + 간격 24 + 여백 16. */
    val breakpointCareWideShell: Dp = 920.dp,
)

@Immutable
data class PleosShapes(
    val hero: RoundedCornerShape = RoundedCornerShape(28.dp),
    val card: RoundedCornerShape = RoundedCornerShape(24.dp),
    val container: RoundedCornerShape = RoundedCornerShape(20.dp),
    val control: RoundedCornerShape = RoundedCornerShape(16.dp),
    val badge: RoundedCornerShape = RoundedCornerShape(12.dp),
    val checkboxRadius: Dp = 8.dp,
    val checkbox: RoundedCornerShape = RoundedCornerShape(8.dp),
    val pillBar: RoundedCornerShape = RoundedCornerShape(4.dp),
)

/**
 * Pleos motion & interaction tokens (Pleos UI design guide, motion).
 * Keep touch press shrink subtle; avoid decorative transition motion when driving restrictions apply.
 */
object PleosMotion {
    val easingStandard = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
    const val short1 = 100
    const val short2 = 150
    const val medium1 = 250
    const val medium2 = 300
    const val long1 = 400
    const val long2 = 450
    const val pressedScale = 0.95f
}

/**
 * Pleos 3-level elevation/shadow specifications (Pleos UI design guide, shadow).
 */
object PleosElevation {
    @Immutable
    data class ShadowSpec(
        val offsetY: Dp,
        val blur: Dp,
        val alpha: Float,
        val secondaryOffsetY: Dp? = null,
        val secondaryBlur: Dp? = null,
        val secondaryAlpha: Float? = null,
    )

    val level1 = ShadowSpec(offsetY = 6.dp, blur = 24.dp, alpha = 0.08f)
    val level2 = ShadowSpec(
        offsetY = 8.dp,
        blur = 32.dp,
        alpha = 0.16f,
        secondaryOffsetY = 2.dp,
        secondaryBlur = 2.dp,
        secondaryAlpha = 0.08f,
    )
    val level3 = ShadowSpec(
        offsetY = 20.dp,
        blur = 40.dp,
        alpha = 0.24f,
        secondaryOffsetY = 2.dp,
        secondaryBlur = 4.dp,
        secondaryAlpha = 0.20f,
    )
}

val LocalPleosDimens = staticCompositionLocalOf { PleosDimens() }
val LocalPleosShapes = staticCompositionLocalOf { PleosShapes() }

/**
 * Unified accessor for all Pleos design tokens inside composable functions.
 */
object PleosTheme {
    val colors: PleosColors
        @Composable
        @ReadOnlyComposable
        get() = LocalPleosColors.current

    val typography: PleosTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalPleosTypography.current

    val dimens: PleosDimens
        @Composable
        @ReadOnlyComposable
        get() = LocalPleosDimens.current

    val shapes: PleosShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalPleosShapes.current
}
