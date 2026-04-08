package org.xg.project.presentation.index

import org.xg.project.domain.model.DailyMenuRecord

data class IndexState(
    val isLoading: Boolean = false,
    val todayRecord: DailyMenuRecord? = null,
    val canReviewBreakfast: Boolean = false,
    val canReviewLunch: Boolean = false,
    val canReviewDinner: Boolean = false,
    val canReviewSnack: Boolean = false,
    val error: String? = null
)

sealed class IndexIntent {
    object LoadTodayMenu : IndexIntent()
}