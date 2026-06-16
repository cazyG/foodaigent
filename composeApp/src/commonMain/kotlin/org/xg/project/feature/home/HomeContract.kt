package org.xg.project.feature.home

import org.xg.project.domain.model.DailyMenuRecord

data class HomeState(
    val isLoading: Boolean = false,
    val todayRecord: DailyMenuRecord? = null,
    val canReviewBreakfast: Boolean = false,
    val canReviewLunch: Boolean = false,
    val canReviewDinner: Boolean = false,
    val canReviewSnack: Boolean = false,
    val error: String? = null
)

sealed class HomeIntent {
    object LoadTodayMenu : HomeIntent()
}
