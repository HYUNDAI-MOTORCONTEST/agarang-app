package com.hmgchallenge.app.departure

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.hmgchallenge.app.R
import com.hmgchallenge.app.care.CareActionTile
import com.hmgchallenge.app.care.CareButton
import com.hmgchallenge.app.care.CareCard
import com.hmgchallenge.app.care.CareText
import com.hmgchallenge.app.ui.components.CareGlyph
import com.hmgchallenge.app.ui.components.CareSymbol
import com.hmgchallenge.app.ui.components.ForecastBadge
import com.hmgchallenge.app.ui.components.ForecastLevel
import com.hmgchallenge.app.ui.theme.PleosAccentTone
import com.hmgchallenge.app.ui.theme.PleosRoles.accentColor
import com.hmgchallenge.app.ui.theme.PleosRoles.cardBorder
import com.hmgchallenge.app.ui.theme.PleosRoles.cardContainer
import com.hmgchallenge.app.ui.theme.PleosRoles.heroBorder
import com.hmgchallenge.app.ui.theme.PleosRoles.heroContainer
import com.hmgchallenge.app.ui.theme.PleosRoles.nestedOnHeroContainer
import com.hmgchallenge.app.ui.theme.PleosTheme
import com.hmgchallenge.shared.care.CareEventType
import com.hmgchallenge.shared.care.CareJournal
import com.hmgchallenge.shared.departure.DepartureChoice
import com.hmgchallenge.shared.departure.DepartureStoreStatus
import com.hmgchallenge.shared.departure.SupplyKind
import com.hmgchallenge.shared.forecast.CareNeed
import com.hmgchallenge.shared.forecast.ForecastReasonCode
import com.hmgchallenge.shared.forecast.ForecastStage

/** Large quiet surfaces; evidence and uncertainty remain visible beside the headline. */
@Composable
internal fun DepartureHome(
    state: DepartureUiState,
    onChoice: (DepartureChoice) -> Unit,
    onProfile: () -> Unit,
    onRecord: (CareEventType) -> Unit,
    onUndo: () -> Unit,
    onTogglePacking: (String) -> Unit,
    responseEntry: @Composable () -> Unit,
) {
    val dimens = PleosTheme.dimens
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val compact = maxWidth < dimens.breakpointTwoColumn
        val hero: @Composable () -> Unit = { ForecastHero(state, compact, onProfile) }
        val choices: @Composable () -> Unit = { DepartureChoices(state, onChoice) }
        val packing: @Composable () -> Unit = { PackingPreview(state, onTogglePacking) }
        if (!compact) {
            Row(horizontalArrangement = Arrangement.spacedBy(dimens.sectionSpacing)) {
                Column(Modifier.weight(1.2f), verticalArrangement = Arrangement.spacedBy(dimens.sectionSpacing)) {
                    hero()
                    responseEntry()
                    QuickCareRow(state, onRecord, onUndo)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(dimens.sectionSpacing)) { choices(); packing() }
            }
        } else Column(verticalArrangement = Arrangement.spacedBy(dimens.itemSpacing)) {
            hero(); responseEntry(); choices(); packing(); QuickCareRow(state, onRecord, onUndo)
        }
    }
}

@Composable
private fun ForecastHero(state: DepartureUiState, compact: Boolean, onProfile: () -> Unit) {
    val type = PleosTheme.typography
    val colors = PleosTheme.colors
    val dimens = PleosTheme.dimens
    val shapes = PleosTheme.shapes
    val record = state.care.record
    var showBasis by rememberSaveable(state.isDemo) { mutableStateOf(false) }
    val forecast = state.evaluation?.forecast
    val feed = forecast?.needs?.firstOrNull { it.need == CareNeed.FEED }
    val accentTone = when (forecast?.stage) {
        ForecastStage.IMMINENT -> PleosAccentTone.WARNING
        ForecastStage.CAUTION -> PleosAccentTone.WARNING
        ForecastStage.RELAXED -> PleosAccentTone.ACTIVE
        ForecastStage.UNKNOWN, null -> PleosAccentTone.NEUTRAL
    }
    val headline = when {
        record == null -> R.string.departure_first_heading
        record.events.isEmpty() -> R.string.departure_first_day_heading
        forecast?.stage == ForecastStage.IMMINENT -> R.string.departure_care_first_heading
        forecast?.journey?.careBeforeArrival?.isNotEmpty() == true -> R.string.departure_stop_heading
        forecast?.estimates?.isEmpty() != false -> R.string.departure_need_unknown_heading
        forecast?.journey?.estimatedArrivalAtEpochMillis == null -> R.string.departure_route_heading
        else -> R.string.departure_arrival_heading
    }
    Column(
        Modifier
            .fillMaxWidth()
            .testTag("departure-forecast")
            .clip(shapes.hero)
            .background(colors.heroContainer)
            .border(dimens.borderThin, colors.heroBorder, shapes.hero)
            .padding(if (compact) dimens.cardPaddingCompact else dimens.cardPadding),
        verticalArrangement = Arrangement.spacedBy(dimens.itemSpacing),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(dimens.tightSpacing)) {
            Box(
                Modifier
                    .width(dimens.accentBarWidth)
                    .height(dimens.accentBarHeight)
                    .clip(shapes.pillBar)
                    .background(colors.accentColor(accentTone)),
            )
            DepartureText(stringResource(R.string.departure_forecast_title), secondary = true, modifier = Modifier.weight(1f, fill = false))
            // 기획의 예보 3단계(여유 → 주의 → 임박)를 글자 배지로 보인다. 색은 보조이고 단계 이름이 같은 뜻을 전한다.
            // 기록이 없거나 판단 근거가 부족한 UNKNOWN은 배지를 붙이지 않는다(단정하지 않음).
            forecast?.stage?.toForecastLevel()?.let { ForecastBadge(it) }
        }
        DepartureText(stringResource(headline), style = if (compact) type.titleLarge else type.headlineSmall)
        val detail = when {
            record == null -> R.string.departure_first_detail
            feed?.reasons?.contains(ForecastReasonCode.STALE_CARE_RECORD) == true -> R.string.departure_stale_care_detail
            feed?.reasons?.contains(ForecastReasonCode.FUTURE_CARE_RECORD) == true -> R.string.departure_future_care_detail
            forecast?.reasons?.contains(ForecastReasonCode.NO_CARE_RECORDS) == true -> R.string.departure_first_day_detail
            feed?.estimate == null -> R.string.departure_need_unknown_detail
            forecast?.journey?.careBeforeArrival?.isNotEmpty() == true -> R.string.departure_stop_detail
            forecast?.journey?.estimatedArrivalAtEpochMillis == null -> R.string.departure_route_detail
            else -> R.string.departure_forecast_detail
        }
        // Missing/uncertain data is always visible; the longer explanation is a parked disclosure.
        if (record == null || feed?.estimate == null || showBasis) DepartureText(stringResource(detail), secondary = true)
        if (record != null) BoxWithConstraints(Modifier.fillMaxWidth()) {
            val metrics: @Composable (Modifier) -> Unit = { metricModifier ->
                ForecastMetric(stringResource(R.string.departure_next_feed), estimateText(feed?.estimate), basisText(feed?.basis), CareSymbol.FEED, metricModifier)
                ForecastMetric(stringResource(R.string.departure_arrival), timeText(forecast?.journey?.estimatedArrivalAtEpochMillis),
                    state.journey.readyValue?.destinationLabel ?: stringResource(R.string.departure_route_missing), CareSymbol.ROUTE, metricModifier)
            }
            if (maxWidth >= dimens.breakpointMinuteGrid) Row(horizontalArrangement = Arrangement.spacedBy(dimens.itemSpacing)) { metrics(Modifier.weight(1f)) }
            else Column(verticalArrangement = Arrangement.spacedBy(dimens.itemSpacing)) { metrics(Modifier.fillMaxWidth()) }
        }
        val basisNote = stringResource(if (forecast?.isLimited != false) R.string.departure_limited else R.string.departure_record_based)
        if (record != null && feed?.estimate == null) DepartureText(basisNote, secondary = true)
        if (record == null) CareButton(stringResource(R.string.departure_profile_action), onProfile,
            Modifier.fillMaxWidth(), enabled = state.care.canWrite, tag = "departure-profile", leadingIcon = CareSymbol.BABY)
        else if (feed?.estimate == null) CareButton(stringResource(R.string.ux_check_records), onProfile,
            Modifier.fillMaxWidth(), primary = false, tag = "departure-check-records", leadingIcon = CareSymbol.CHECKLIST)
        // 근거 보기는 보조 조작이라 전체 폭 버튼 대신 근거 문구 옆 작은 버튼으로 둔다(정차 중 홈의 같은 무게 버튼 줄이기).
        else Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimens.itemSpacing), verticalAlignment = Alignment.CenterVertically) {
            DepartureText(basisNote, Modifier.weight(1f), secondary = true, maxLines = 2)
            CareButton(stringResource(if (showBasis) R.string.ux_hide_basis else R.string.ux_show_basis), { showBasis = !showBasis },
                primary = false, selected = showBasis, compact = true, tag = "departure-forecast-basis", selectionRole = Role.Button)
        }
    }
}

@Composable
private fun ForecastMetric(title: String, value: String, basis: String, icon: CareSymbol, modifier: Modifier) {
    val dimens = PleosTheme.dimens
    val shapes = PleosTheme.shapes
    val colors = PleosTheme.colors
    Column(
        modifier
            .clip(shapes.container)
            .background(colors.nestedOnHeroContainer)
            .padding(dimens.itemSpacing),
        verticalArrangement = Arrangement.spacedBy(dimens.microSpacing),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(dimens.microSpacing)) {
            CareGlyph(icon)
            DepartureText(title, Modifier.weight(1f), secondary = true, maxLines = 2)
        }
        DepartureText(value, style = PleosTheme.typography.titleSmall, maxLines = 2)
        DepartureText(basis, secondary = true, maxLines = 1)
    }
}

@Composable
private fun DepartureChoices(state: DepartureUiState, onChoice: (DepartureChoice) -> Unit) {
    val dimens = PleosTheme.dimens
    val decision = state.evaluation?.validDecision
    val forecast = state.evaluation?.forecast
    // 아직 고르지 않았으면 예보 제목과 같은 기준으로 출발 방법 하나를 진하게 추천한다(정차 중 첫 조작을 분명히).
    // 돌봄 기록이 없거나, 근거가 부족하거나(UNKNOWN), 주의 단계라도 도착 전 겹침이 없으면 추천하지 않는다.
    // "지금 출발"은 도착 예정 시각과 돌봄 예상이 모두 있어 도착 전 겹침을 계산한 여유 단계에서만 추천한다.
    // 목적지가 없어 겹침을 확인하지 못했으면 출발을 권하지 않는다.
    // 수유 예상이 없으면 예보 카드가 "돌봄 기록 확인"을 권하므로 "지금 출발"도 추천하지 않는다(두 카드 기준을 맞춤).
    // 돌봄 먼저·중간 정차는 더 조심하는 쪽이라 수유 예상이 없어도 그대로 추천한다.
    // 저장소가 준비되지 않았으면(불러오는 중·다시 읽기 필요) 추천하지 않는다. 저장 중 잠깐 비활성되는 동안에는 문구를 유지해 깜빡이지 않게 한다.
    val hasRecord = state.care.record?.events?.isNotEmpty() == true
    val feedKnown = forecast?.needs?.firstOrNull { it.need == CareNeed.FEED }?.estimate != null
    val overlapChecked = forecast?.estimates?.isNotEmpty() == true && forecast.journey?.estimatedArrivalAtEpochMillis != null
    val recommended = if (decision != null || !hasRecord || state.preparation.status != DepartureStoreStatus.READY) null else when {
        forecast?.stage == ForecastStage.IMMINENT -> DepartureChoice.AFTER_CARE
        forecast?.journey?.careBeforeArrival?.isNotEmpty() == true -> DepartureChoice.STOP_ON_WAY
        forecast?.stage == ForecastStage.RELAXED && overlapChecked && feedKnown -> DepartureChoice.NOW
        else -> null
    }
    CareCard(stringResource(R.string.departure_choose_title)) {
        if (decision != null) DepartureText(stringResource(R.string.departure_selected, choiceText(decision.choice)))
        if (decision?.facilityId != null) {
            val candidate = (state.stops?.fullCare?.candidates.orEmpty() + state.stops?.nearestSafeStop?.candidates.orEmpty())
                .firstOrNull { it.facility.id == decision.facilityId }
            DepartureText(candidate?.facility?.name ?: stringResource(R.string.departure_selected_stop_unknown), secondary = true)
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val horizontal = maxWidth >= dimens.breakpointHorizontalChoices
            val buttons: @Composable (Modifier) -> Unit = { modifier ->
                DepartureChoice.entries.forEach { choice ->
                    val detail = stringResource(when (choice) {
                        DepartureChoice.NOW -> R.string.ux_choice_now_detail
                        DepartureChoice.AFTER_CARE -> R.string.ux_choice_care_detail
                        DepartureChoice.STOP_ON_WAY -> R.string.ux_choice_stop_detail
                    })
                    val isRecommended = choice == recommended
                    CareActionTile(stringResource(when (choice) {
                        DepartureChoice.NOW -> R.string.departure_now
                        DepartureChoice.AFTER_CARE -> R.string.ux_choice_care
                        DepartureChoice.STOP_ON_WAY -> R.string.ux_choice_stop
                    }), if (isRecommended) stringResource(R.string.ux_choice_recommended) else detail, when (choice) {
                        DepartureChoice.NOW -> CareSymbol.START
                        DepartureChoice.AFTER_CARE -> CareSymbol.CARE
                        DepartureChoice.STOP_ON_WAY -> CareSymbol.STOP
                    }, { onChoice(choice) }, modifier,
                        enabled = state.preparation.canWrite, selected = decision?.choice == choice, stacked = horizontal,
                        tag = "departure-choice-${choice.name}", selectionRole = Role.Button, primary = isRecommended)
                }
            }
            if (horizontal) Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(dimens.sectionSpacing)) { buttons(Modifier.weight(1f)) }
            else Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(dimens.sectionSpacing)) { buttons(Modifier.fillMaxWidth()) }
        }
    }
}

/** 홈의 준비물 요약. 전체 목록은 아래 "준비물" 탭이 열므로 같은 일을 하는 버튼을 따로 두지 않는다. */
@Composable
internal fun PackingPreview(state: DepartureUiState, onToggle: (String) -> Unit) {
    val dimens = PleosTheme.dimens
    val evaluation = state.evaluation
    val plan = evaluation?.packing
    CareCard(stringResource(R.string.packing_preview_title, evaluation?.checkedItems ?: 0, evaluation?.totalItems ?: 0)) {
        PackingProgress(evaluation?.checkedItems ?: 0, evaluation?.totalItems ?: 0)
        val items = state.preparation.record?.items.orEmpty().filter { it.kind == SupplyKind.FEEDING || it.kind == SupplyKind.DIAPERS }
        if (plan != null && evaluation != null && items.isNotEmpty()) BoxWithConstraints(Modifier.fillMaxWidth()) {
            val checks: @Composable (Modifier) -> Unit = { itemModifier ->
                items.forEach { item ->
                    PackingCheckRow(item, plan, plan.isChecked(item, evaluation.evaluatedAtEpochMillis),
                        state.preparation.canWrite, { onToggle(item.id) }, itemModifier)
                }
            }
            if (maxWidth >= dimens.breakpointMinuteGrid) Row(horizontalArrangement = Arrangement.spacedBy(dimens.sectionSpacing)) { checks(Modifier.weight(1f)) }
            else Column(verticalArrangement = Arrangement.spacedBy(dimens.sectionSpacing)) { checks(Modifier.fillMaxWidth()) }
        }
    }
}

@Composable
internal fun QuickCareRow(state: DepartureUiState, onRecord: (CareEventType) -> Unit, onUndo: () -> Unit) {
    val colors = PleosTheme.colors
    val dimens = PleosTheme.dimens
    val shapes = PleosTheme.shapes
    val type = PleosTheme.typography
    val sleeping = state.care.record?.let(CareJournal::isSleeping) == true
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shapes.card)
            .background(colors.cardContainer)
            .border(dimens.borderThin, colors.cardBorder, shapes.card)
            .padding(dimens.cardPaddingCompact),
        verticalArrangement = Arrangement.spacedBy(dimens.sectionSpacing),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimens.itemSpacing), verticalAlignment = Alignment.CenterVertically) {
            CareText(stringResource(R.string.departure_quick_record), Modifier.weight(1f), type.titleSmall)
            CareButton(stringResource(R.string.departure_undo), onUndo, enabled = state.care.canWrite && state.care.record?.events?.isNotEmpty() == true,
                primary = false, compact = true, tag = "departure-undo", leadingIcon = CareSymbol.UNDO)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(dimens.sectionSpacing)) {
            val types = listOf(CareEventType.FEED, CareEventType.DIAPER, if (sleeping) CareEventType.SLEEP_END else CareEventType.SLEEP_START)
            types.forEach { kind ->
                CareButton(stringResource(when (kind) {
                    CareEventType.FEED -> R.string.departure_record_feed
                    CareEventType.DIAPER -> R.string.departure_record_diaper
                    CareEventType.SLEEP_START -> R.string.departure_record_sleep
                    CareEventType.SLEEP_END -> R.string.departure_record_awake
                }), { onRecord(kind) }, Modifier.weight(1f), enabled = state.care.canWrite && state.care.record != null,
                    primary = false, compact = true, tag = "departure-record-${kind.name}", leadingIcon = when (kind) {
                        CareEventType.FEED -> CareSymbol.FEED
                        CareEventType.DIAPER -> CareSymbol.DIAPER
                        else -> CareSymbol.SLEEP
                    })
            }
        }
    }
}

internal fun ForecastStage.toForecastLevel(): ForecastLevel? = when (this) {
    ForecastStage.RELAXED -> ForecastLevel.Relaxed
    ForecastStage.CAUTION -> ForecastLevel.Caution
    ForecastStage.IMMINENT -> ForecastLevel.Imminent
    ForecastStage.UNKNOWN -> null
}
