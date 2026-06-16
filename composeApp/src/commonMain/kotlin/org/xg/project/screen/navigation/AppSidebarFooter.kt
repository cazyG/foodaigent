package org.xg.project.screen.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.xg.project.Routes.BottomTabRoute
import org.xg.project.screen.home.HomeColors
import org.xg.project.screen.recipes.RecipesColors
import org.xg.project.screen.recipes.RecipesFonts

@Composable
internal fun AppSidebarFooterTop(
    selectedTab: BottomTabRoute,
    onNavigateToManualInput: () -> Unit,
) {
    when (selectedTab) {
        BottomTabRoute.Home -> {
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HomeColors.BrandBrown),
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("上传新菜谱", color = Color.White)
            }
        }
        BottomTabRoute.Recipes -> {
            Button(
                onClick = onNavigateToManualInput,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RecipesColors.BrandBrown),
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("+ 手动录入", fontSize = RecipesFonts.actionButton, color = Color.White)
            }
        }
        else -> Unit
    }
}
