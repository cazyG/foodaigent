package org.xg.project.feature.manualrecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.xg.project.core.navigation.BottomTabRoute
import org.xg.project.data.session.UserAccount
import org.xg.project.core.navigation.AppDesktopSidebar

@Composable
fun ManualRecipeCompactLayout(
    content: ManualRecipeContentUi,
    onIntent: (ManualRecipeInputIntent) -> Unit,
    onPickImage: () -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ManualRecipeColors.PageBackground)
            .statusBarsPadding()
            .imePadding(),
    ) {
        ManualRecipeMobileTopBar(onBack = onBack, onSave = onSave, isSaving = content.isSaving)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            content.error?.let { ManualRecipeErrorBanner(it) }
            ManualRecipeFormContent(
                content = content,
                onIntent = onIntent,
                onPickImage = onPickImage,
                layout = ManualRecipeFormLayout.Compact,
            )
        }
    }
}

@Composable
fun ManualRecipeDesktopLayout(
    content: ManualRecipeContentUi,
    onIntent: (ManualRecipeInputIntent) -> Unit,
    onPickImage: () -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    userAccount: UserAccount? = null,
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(ManualRecipeColors.PageBackground)
            .imePadding(),
    ) {
        AppDesktopSidebar(
            activeTab = BottomTabRoute.Recipes,
            onTabClick = {},
            onProfileClick = onProfileClick,
            userAccount = userAccount,
            modifier = Modifier.fillMaxHeight(),
            footerTop = {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ManualRecipeColors.BrandBrown),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("+ 手动录入", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.size(20.dp))
            },
        )
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            ManualRecipeDesktopTopBar(onBack = onBack, onSave = onSave, isSaving = content.isSaving)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                content.error?.let { ManualRecipeErrorBanner(it) }
                ManualRecipeFormContent(
                    content = content,
                    onIntent = onIntent,
                    onPickImage = onPickImage,
                    layout = ManualRecipeFormLayout.Desktop,
                )
            }
        }
    }
}

@Composable
private fun ManualRecipeMobileTopBar(
    onBack: () -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ManualRecipeColors.CardWhite)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回", tint = ManualRecipeColors.TextPrimary)
        }
        Text(
            text = "手动录入食谱",
            fontSize = ManualRecipeFonts.pageTitle,
            fontWeight = FontWeight.Bold,
            color = ManualRecipeColors.TextPrimary,
        )
        Button(
            onClick = onSave,
            enabled = !isSaving,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ManualRecipeColors.BrandOrange),
        ) {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("保存", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ManualRecipeDesktopTopBar(
    onBack: () -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ManualRecipeColors.CardWhite)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回", tint = ManualRecipeColors.TextPrimary)
            }
            Text(
                text = "手动录入食谱",
                fontSize = ManualRecipeFonts.pageTitle,
                fontWeight = FontWeight.Bold,
                color = ManualRecipeColors.TextPrimary,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.CloudDone, contentDescription = null, tint = ManualRecipeColors.TextSecondary, modifier = Modifier.size(18.dp))
                Text("Auto-saved", fontSize = ManualRecipeFonts.helper, color = ManualRecipeColors.TextSecondary)
            }
            Button(
                onClick = onSave,
                enabled = !isSaving,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ManualRecipeColors.BrandBrown),
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.size(8.dp))
                }
                Text("保存", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
