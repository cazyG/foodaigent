package org.xg.project.screen

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.domain.model.DailyMenuRecord
import org.xg.project.domain.model.MealType
import org.xg.project.presentation.index.IndexViewModel
import org.xg.project.screen.home.HomeBreakpoints
import org.xg.project.screen.navigation.isDesktopWidth
import org.xg.project.screen.navigation.isTabletLandscape
import org.xg.project.screen.home.HomeColors
import org.xg.project.screen.home.HomeCompactContent
import org.xg.project.screen.home.HomeMediumContent
import org.xg.project.screen.home.HomeWideContent
import org.xg.project.screen.home.formatChineseDateOnly
import org.xg.project.screen.home.toHomeContentUi

@Composable
fun HomeScreen(
    onAddPlan: (MealType) -> Unit,
    viewModel: IndexViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
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
                                modifier = Modifier.weight(1f),
                            )
                        }
                        isDesktopWidth(layoutWidth) -> {
                            HomeWideContent(
                                content = content,
                                onAddPlan = onAddPlan,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        else -> {
                            HomeMediumContent(
                                content = content,
                                onAddPlan = onAddPlan,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}
