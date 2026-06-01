package org.xg.project.screen.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.xg.project.Routes.BottomTabRoute
import org.xg.project.domain.model.RecipeMenu
import org.xg.project.presentation.recipes.RecipesIntent
import org.xg.project.screen.home.HomeColors
import org.xg.project.screen.navigation.AppDesktopSidebar

@Composable
fun RecipesDesktopLayout(
    content: RecipesContentUi,
    isFromHome: Boolean,
    activeTab: BottomTabRoute,
    onTabClick: (BottomTabRoute) -> Unit,
    onIntent: (RecipesIntent) -> Unit,
    onNavigateToManualInput: () -> Unit,
    onRecipeOpen: (RecipeMenu) -> Unit,
    onSaveOrManual: () -> Unit,
    saveButtonLabel: String,
    showSidebar: Boolean = true,
    gridColumns: Int = 4,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(RecipesColors.CardWhite),
    ) {
        if (showSidebar) {
            RecipesDesktopSidebar(
                activeTab = activeTab,
                onTabClick = onTabClick,
                onManualEntry = onNavigateToManualInput,
                modifier = Modifier.fillMaxHeight(),
            )
        }
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            RecipesDesktopTopBar(
                query = content.searchQuery,
                onQueryChange = { onIntent(RecipesIntent.UpdateSearchQuery(it)) },
            )
            if (isFromHome) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Button(
                        onClick = onSaveOrManual,
                        colors = ButtonDefaults.buttonColors(containerColor = RecipesColors.BrandBrown),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text(saveButtonLabel, color = Color.White, fontSize = RecipesFonts.actionButton)
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(RecipesColors.PageBackground)
                    .padding(horizontal = 28.dp, vertical = 20.dp),
            ) {
                RecipesPageHeader(recommendedCount = content.recommendedCount)
                Spacer(modifier = Modifier.height(16.dp))
                RecipesFilterChipsRow(
                    selectedFilter = content.selectedMealFilter,
                    onFilterSelected = { onIntent(RecipesIntent.ChangeMealFilter(it)) },
                )
                Spacer(modifier = Modifier.height(20.dp))
                if (content.error != null && content.recipes.isEmpty()) {
                    RecipesErrorState(
                        message = content.error,
                        onRetry = { onIntent(RecipesIntent.LoadRecipes) },
                    )
                } else {
                    RecipesRecipeGrid(
                        recipes = content.recipes,
                        gridColumns = gridColumns,
                        isFromHome = isFromHome,
                        selectedRecipeIds = content.selectedRecipeIds,
                        onRecipeClick = onRecipeOpen,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipesDesktopSidebar(
    activeTab: BottomTabRoute,
    onTabClick: (BottomTabRoute) -> Unit,
    onManualEntry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppDesktopSidebar(
        activeTab = activeTab,
        onTabClick = onTabClick,
        modifier = modifier,
        subtitle = "Kitchen Master",
        footer = {
            Column {
                Button(
                    onClick = onManualEntry,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RecipesColors.BrandBrown),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ 手动录入", color = Color.White, fontSize = RecipesFonts.actionButton)
                }
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(HomeColors.BrandOrange.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = RecipesColors.BrandBrown)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Chef Huang", fontSize = RecipesFonts.profileName, fontWeight = FontWeight.SemiBold)
                        Text("Gold Member", fontSize = RecipesFonts.profileBadge, color = RecipesColors.TextSecondary)
                    }
                }
            }
        },
    )
}

@Composable
private fun RecipesDesktopTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RecipesColors.CardWhite)
            .padding(horizontal = 28.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        RecipesSearchField(
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier.weight(1f),
        )
        Icon(Icons.Default.Notifications, contentDescription = null, tint = RecipesColors.TextSecondary)
        Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = RecipesColors.TextSecondary)
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(RecipesColors.BrandOrange.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = RecipesColors.BrandBrown)
        }
    }
}
