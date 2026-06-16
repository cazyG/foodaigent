package org.xg.project.feature.recipes

import androidx.compose.foundation.layout.Box
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
import org.xg.project.core.navigation.AppAdaptiveLayout
import org.xg.project.core.navigation.currentAppAdaptiveLayout
import org.xg.project.core.navigation.isMediumOrExpanded

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
    val state by viewModel.uiState.collectAsState()
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
        viewModel.onIntent(RecipesIntent.LoadRecipes)
    }

    LaunchedEffect(initialMealType) {
        if (initialMealType != null) {
            runCatching {
                MealType.valueOf(initialMealType)
            }.onSuccess { mealType ->
                viewModel.onIntent(RecipesIntent.ChangeMealFilter(mealType))
            }
        }
    }

    val onSaveOrManual: () -> Unit = {
        if (state.selectedRecipeIds.isNotEmpty()) {
            viewModel.onIntent(RecipesIntent.SaveSelections)
        } else {
            onNavigateToManualInput()
        }
    }

    val onRecipeOpen: (RecipeMenu) -> Unit = { recipe ->
        if (isFromHome) {
            viewModel.onIntent(RecipesIntent.ToggleSelection(recipe.id))
        } else {
            onNavigateToDetail(recipe.id.toString())
        }
    }
    val adaptiveLayout = currentAppAdaptiveLayout()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> RecipesLoadingState()
            adaptiveLayout == AppAdaptiveLayout.Expanded && !isFromHome -> {
                RecipesWideContent(
                    content = content,
                    isFromHome = isFromHome,
                    onIntent = viewModel::onIntent,
                    onNavigateToManualInput = onNavigateToManualInput,
                    onRecipeOpen = onRecipeOpen,
                    onSaveOrManual = onSaveOrManual,
                    saveButtonLabel = saveButtonLabel,
                )
            }
            adaptiveLayout.isMediumOrExpanded -> {
                RecipesMediumContent(
                    content = content,
                    isFromHome = isFromHome,
                    onIntent = viewModel::onIntent,
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
                    onIntent = viewModel::onIntent,
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
