package org.xg.project.screen.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.xg.project.Routes.BottomTabRoute
import org.xg.project.domain.model.RecipeMenu
import org.xg.project.presentation.recipes.RecipesIntent

@Composable
fun RecipesCompactContent(
    content: RecipesContentUi,
    isFromHome: Boolean,
    onIntent: (RecipesIntent) -> Unit,
    onNavigateToManualInput: () -> Unit,
    onRecipeOpen: (RecipeMenu) -> Unit,
    onBack: () -> Unit,
    onSaveOrManual: () -> Unit,
    saveButtonLabel: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RecipesColors.PageBackground),
    ) {
        RecipesCompactTopBar(
            isFromHome = isFromHome,
            saveButtonLabel = saveButtonLabel,
            onBack = onBack,
            onSaveOrManual = onSaveOrManual,
            onNavigateToManualInput = onNavigateToManualInput,
        )
        RecipesContentBody(
            content = content,
            isFromHome = isFromHome,
            gridColumns = 1,
            onIntent = onIntent,
            onRecipeOpen = onRecipeOpen,
            showPageHeader = false,
            showPageSubtitle = true,
        )
    }
}

@Composable
private fun RecipesCompactTopBar(
    isFromHome: Boolean,
    saveButtonLabel: String,
    onBack: () -> Unit,
    onSaveOrManual: () -> Unit,
    onNavigateToManualInput: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RecipesColors.CardWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isFromHome) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = RecipesColors.TextPrimary,
                    )
                }
            }
            Text(
                text = "食谱灵感库",
                fontSize = RecipesFonts.pageTitle,
                fontWeight = FontWeight.Bold,
                color = RecipesColors.TextPrimary,
            )
        }
        Button(
            onClick = {
                if (isFromHome) onSaveOrManual() else onNavigateToManualInput()
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RecipesColors.BrandOrange.copy(alpha = 0.15f),
                contentColor = RecipesColors.BrandBrown,
            ),
        ) {
            Text(
                text = if (isFromHome) saveButtonLabel else "+ 手动录入",
                fontSize = RecipesFonts.actionButton,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun RecipesMediumContent(
    content: RecipesContentUi,
    isFromHome: Boolean,
    layoutWidth: androidx.compose.ui.unit.Dp,
    onIntent: (RecipesIntent) -> Unit,
    onNavigateToManualInput: () -> Unit,
    onRecipeOpen: (RecipeMenu) -> Unit,
    onBack: () -> Unit,
    onSaveOrManual: () -> Unit,
    saveButtonLabel: String,
    modifier: Modifier = Modifier,
) {
    val columns = when {
        layoutWidth >= 960.dp -> 3
        else -> 2
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RecipesColors.PageBackground),
    ) {
        RecipesMediumHeader(
            isFromHome = isFromHome,
            recommendedCount = content.recommendedCount,
            saveButtonLabel = saveButtonLabel,
            onBack = onBack,
            onSaveOrManual = onSaveOrManual,
            onNavigateToManualInput = onNavigateToManualInput,
        )
        RecipesContentBody(
            content = content,
            isFromHome = isFromHome,
            gridColumns = columns,
            onIntent = onIntent,
            onRecipeOpen = onRecipeOpen,
            modifier = Modifier.weight(1f),
            showPageHeader = false,
        )
    }
}

@Composable
private fun RecipesMediumHeader(
    isFromHome: Boolean,
    recommendedCount: Int,
    saveButtonLabel: String,
    onBack: () -> Unit,
    onSaveOrManual: () -> Unit,
    onNavigateToManualInput: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RecipesColors.CardWhite)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isFromHome) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = RecipesColors.TextPrimary,
                    )
                }
            }
            Column {
                Text(
                    text = "食谱灵感库",
                    fontSize = RecipesFonts.pageTitle,
                    fontWeight = FontWeight.Bold,
                    color = RecipesColors.TextPrimary,
                )
                Text(
                    text = "发现您的下一次美味创作，今日推荐 $recommendedCount 道精品菜谱",
                    fontSize = RecipesFonts.pageSubtitle,
                    color = RecipesColors.TextSecondary,
                )
            }
        }
        Button(
            onClick = {
                if (isFromHome) onSaveOrManual() else onNavigateToManualInput()
            },
            colors = ButtonDefaults.buttonColors(containerColor = RecipesColors.BrandBrown),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(
                text = if (isFromHome) saveButtonLabel else "+ 手动录入",
                color = Color.White,
                fontSize = RecipesFonts.actionButton,
            )
        }
    }
}

@Composable
fun RecipesWideContent(
    content: RecipesContentUi,
    isFromHome: Boolean,
    onIntent: (RecipesIntent) -> Unit,
    onNavigateToManualInput: () -> Unit,
    onRecipeOpen: (RecipeMenu) -> Unit,
    onSaveOrManual: () -> Unit,
    saveButtonLabel: String,
    modifier: Modifier = Modifier,
) {
    RecipesDesktopLayout(
        content = content,
        isFromHome = isFromHome,
        activeTab = BottomTabRoute.Recipes,
        onTabClick = {},
        onIntent = onIntent,
        onNavigateToManualInput = onNavigateToManualInput,
        onRecipeOpen = onRecipeOpen,
        onSaveOrManual = onSaveOrManual,
        saveButtonLabel = saveButtonLabel,
        showSidebar = false,
        gridColumns = 4,
        modifier = modifier,
    )
}

@Composable
fun RecipesTabletContent(
    content: RecipesContentUi,
    isFromHome: Boolean,
    layoutWidth: androidx.compose.ui.unit.Dp,
    onIntent: (RecipesIntent) -> Unit,
    onNavigateToManualInput: () -> Unit,
    onRecipeOpen: (RecipeMenu) -> Unit,
    onSaveOrManual: () -> Unit,
    saveButtonLabel: String,
    modifier: Modifier = Modifier,
) {
    val columns = when {
        layoutWidth >= 960.dp -> 3
        else -> 2
    }
    RecipesDesktopLayout(
        content = content,
        isFromHome = isFromHome,
        activeTab = BottomTabRoute.Recipes,
        onTabClick = {},
        onIntent = onIntent,
        onNavigateToManualInput = onNavigateToManualInput,
        onRecipeOpen = onRecipeOpen,
        onSaveOrManual = onSaveOrManual,
        saveButtonLabel = saveButtonLabel,
        showSidebar = false,
        gridColumns = columns,
        modifier = modifier,
    )
}
