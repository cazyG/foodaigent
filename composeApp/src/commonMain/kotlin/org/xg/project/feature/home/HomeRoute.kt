package org.xg.project.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.domain.model.DailyMenuRecord
import org.xg.project.domain.model.MealType
import org.xg.project.core.navigation.AppAdaptiveLayout
import org.xg.project.core.navigation.currentAppAdaptiveLayout

@Composable
fun HomeScreen(
    onAddPlan: (MealType) -> Unit,
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val today = remember {
        Clock.System.todayIn(TimeZone.currentSystemDefault())
    }
    val emptyRecord = remember(today) {
        DailyMenuRecord(
            date = today.formatChineseDateOnly(),
            breakfast = emptyList(),
            lunch = emptyList(),
            dinner = emptyList(),
            snack = emptyList(),
        )
    }
    val content = state.toHomeContentUi(today, emptyRecord)
    val adaptiveLayout = currentAppAdaptiveLayout()
    val pageBackground =
        if (adaptiveLayout == AppAdaptiveLayout.Expanded) {
            HomeColors.CardWhite
        } else {
            HomeColors.PageBackground
        }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(pageBackground),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = HomeColors.BrandOrange)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(pageBackground),
                ) {
                    when (adaptiveLayout) {
                        AppAdaptiveLayout.Compact -> {
                            HomeCompactContent(
                                content = content,
                                onAddPlan = onAddPlan,
                                onProfileClick = onNavigateToProfile,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        AppAdaptiveLayout.Expanded -> {
                            HomeWideContent(
                                content = content,
                                onAddPlan = onAddPlan,
                                onProfileClick = onNavigateToProfile,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        AppAdaptiveLayout.Medium -> {
                            HomeMediumContent(
                                content = content,
                                onAddPlan = onAddPlan,
                                onProfileClick = onNavigateToProfile,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}
