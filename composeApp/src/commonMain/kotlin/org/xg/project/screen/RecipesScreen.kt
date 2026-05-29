package org.xg.project.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.RecipeMenu
import org.xg.project.presentation.recipes.RecipesEffect
import org.xg.project.presentation.recipes.RecipesIntent
import org.xg.project.presentation.recipes.RecipesViewModel
import org.xg.project.screen.recipes.RecipesBreakpoints
import org.xg.project.screen.recipes.RecipesCompactContent
import org.xg.project.screen.recipes.RecipesLoadingState
import org.xg.project.screen.navigation.isTabletLandscape
import org.xg.project.screen.navigation.isDesktopWidth
import org.xg.project.screen.recipes.RecipesMediumContent
import org.xg.project.screen.recipes.RecipesTabletContent
import org.xg.project.screen.recipes.RecipesWideContent
import org.xg.project.screen.recipes.toRecipesContentUi

@Composable
fun RecipesScreen(
    isFromHome: Boolean = false,
    initialMealType: String? = null,
    viewModel: RecipesViewModel = koinViewModel(),
    refreshTrigger: Int = 0,
    onNavigateToManualInput: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val content = state.toRecipesContentUi()
    val saveButtonLabel =
        if (state.selectedRecipeIds.isNotEmpty()) "保存" else "+ 手动录入"

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RecipesEffect.SaveSuccess -> onSaveSuccess()
                is RecipesEffect.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short,
                    )
                }
            }
        }
    }

    LaunchedEffect(refreshTrigger) {
        viewModel.handleIntent(RecipesIntent.LoadRecipes)
    }

    LaunchedEffect(initialMealType) {
        if (initialMealType != null) {
            runCatching {
                MealType.valueOf(initialMealType)
            }.onSuccess { mealType ->
                viewModel.handleIntent(RecipesIntent.ChangeMealFilter(mealType))
            }
        }
    }

    val onSaveOrManual: () -> Unit = {
        if (state.selectedRecipeIds.isNotEmpty()) {
            viewModel.handleIntent(RecipesIntent.SaveSelections)
        } else {
            onNavigateToManualInput()
        }
    }

    val onRecipeOpen: (RecipeMenu) -> Unit = { recipe ->
        if (isFromHome) {
            viewModel.handleIntent(RecipesIntent.ToggleSelection(recipe.id))
        } else {
            onNavigateToDetail(recipe.id.toString())
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val layoutWidth = maxWidth
        val tabletLandscape = isTabletLandscape(maxWidth, maxHeight)

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> RecipesLoadingState()
                tabletLandscape && isDesktopWidth(layoutWidth) && !isFromHome -> {
                    RecipesWideContent(
                        content = content,
                        isFromHome = isFromHome,
                        onIntent = viewModel::handleIntent,
                        onNavigateToManualInput = onNavigateToManualInput,
                        onRecipeOpen = onRecipeOpen,
                        onSaveOrManual = onSaveOrManual,
                        saveButtonLabel = saveButtonLabel,
                    )
                }
                tabletLandscape && layoutWidth >= RecipesBreakpoints.CompactMax && !isFromHome -> {
                    RecipesTabletContent(
                        content = content,
                        isFromHome = isFromHome,
                        layoutWidth = layoutWidth,
                        onIntent = viewModel::handleIntent,
                        onNavigateToManualInput = onNavigateToManualInput,
                        onRecipeOpen = onRecipeOpen,
                        onSaveOrManual = onSaveOrManual,
                        saveButtonLabel = saveButtonLabel,
                    )
                }
                layoutWidth >= RecipesBreakpoints.CompactMax -> {
                    RecipesMediumContent(
                        content = content,
                        isFromHome = isFromHome,
                        layoutWidth = layoutWidth,
                        onIntent = viewModel::handleIntent,
                        onNavigateToManualInput = onNavigateToManualInput,
                        onRecipeOpen = onRecipeOpen,
                        onBack = onBack,
                        onSaveOrManual = onSaveOrManual,
                        saveButtonLabel = saveButtonLabel,
                    )
                }
                else -> {
                    RecipesCompactContent(
                        content = content,
                        isFromHome = isFromHome,
                        onIntent = viewModel::handleIntent,
                        onNavigateToManualInput = onNavigateToManualInput,
                        onRecipeOpen = onRecipeOpen,
                        onBack = onBack,
                        onSaveOrManual = onSaveOrManual,
                        saveButtonLabel = saveButtonLabel,
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
