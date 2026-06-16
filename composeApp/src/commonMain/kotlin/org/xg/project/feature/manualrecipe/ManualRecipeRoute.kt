package org.xg.project.feature.manualrecipe

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.core.navigation.isDesktopWidth

@Composable
fun ManualRecipeInputScreen(
    viewModel: ManualRecipeInputViewModel = koinViewModel(),
    onBack: () -> Unit,
    onSave: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    val content = state.toManualRecipeContentUi()

    val scope = rememberCoroutineScope()
    val imagePickerLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image,
    ) { file: PlatformFile? ->
        if (file == null) return@rememberFilePickerLauncher
        scope.launch {
            val imageBytes = runCatching { file.readBytes() }.getOrNull() ?: return@launch
            viewModel.onIntent(
                ManualRecipeInputIntent.UploadImage(
                    file.name,
                    imageBytes,
                ),
            )
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { event ->
            when (event) {
                ManualRecipeInputEffect.SaveSuccess -> onSave()
            }
        }
    }

    val resetAndBack = {
        viewModel.onIntent(ManualRecipeInputIntent.ResetForm)
        onBack()
    }
    val onSaveClick = {
        viewModel.onIntent(ManualRecipeInputIntent.SaveRecipe)
    }
    val onPickImage = {
        if (!state.isUploading) {
            imagePickerLauncher.launch()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val layoutWidth = maxWidth
        when {
            isDesktopWidth(layoutWidth) -> {
                ManualRecipeDesktopLayout(
                    content = content,
                    onIntent = viewModel::onIntent,
                    onPickImage = onPickImage,
                    onBack = resetAndBack,
                    onSave = onSaveClick,
                    userAccount = state.userAccount,
                    onProfileClick = onNavigateToProfile,
                )
            }
            layoutWidth >= ManualRecipeBreakpoints.CompactMax -> {
                ManualRecipeTabletLayout(
                    content = content,
                    layoutWidth = layoutWidth,
                    onIntent = viewModel::onIntent,
                    onPickImage = onPickImage,
                    onBack = resetAndBack,
                    onSave = onSaveClick,
                    userAccount = state.userAccount,
                    onProfileClick = onNavigateToProfile,
                )
            }
            else -> {
                ManualRecipeCompactLayout(
                    content = content,
                    onIntent = viewModel::onIntent,
                    onPickImage = onPickImage,
                    onBack = resetAndBack,
                    onSave = onSaveClick,
                )
            }
        }
    }
}
