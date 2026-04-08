package org.xg.project.presentation.history

import org.xg.project.domain.model.DailyMenuRecord

data class HistoryState(
    val isLoading: Boolean = false,
    val tasteRadarData: Map<String, Float> = emptyMap(),
    val dailyRecords: List<DailyMenuRecord> = emptyList(),
    val error: String? = null
)

sealed class HistoryIntent {
    object LoadHistory : HistoryIntent()
}