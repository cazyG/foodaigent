package org.xg.project.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import org.xg.project.core.navigation.isDesktopWidth
import org.xg.project.core.navigation.isTabletLandscape

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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val layoutWidth = maxWidth
        val tabletLandscape = isTabletLandscape(maxWidth, maxHeight)
        val pageBackground =
            if (tabletLandscape && isDesktopWidth(layoutWidth)) {
                HomeColors.CardWhite
            } else {
                HomeColors.PageBackground
            }
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
                    when {
                        !tabletLandscape || layoutWidth < HomeBreakpoints.CompactMax -> {
                            HomeCompactContent(
                                content = content,
                                onAddPlan = onAddPlan,
                                onProfileClick = onNavigateToProfile,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        isDesktopWidth(layoutWidth) -> {
                            HomeWideContent(
                                content = content,
                                onAddPlan = onAddPlan,
                                onProfileClick = onNavigateToProfile,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        else -> {
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
