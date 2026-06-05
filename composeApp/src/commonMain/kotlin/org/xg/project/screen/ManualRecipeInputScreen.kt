package org.xg.project.screen

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
import org.koin.compose.koinInject
import org.xg.project.data.session.UserSessionRepository
import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.presentation.manualrecipeinput.ManualRecipeInputIntent
import org.xg.project.presentation.manualrecipeinput.ManualRecipeInputUiEvent
import org.xg.project.presentation.manualrecipeinput.ManualRecipeInputViewModel
import org.xg.project.screen.manualrecipe.ManualRecipeBreakpoints
import org.xg.project.screen.manualrecipe.ManualRecipeCompactLayout
import org.xg.project.screen.manualrecipe.ManualRecipeDesktopLayout
import org.xg.project.screen.manualrecipe.ManualRecipeTabletLayout
import org.xg.project.screen.manualrecipe.toManualRecipeContentUi
import org.xg.project.screen.navigation.isDesktopWidth

@Composable
fun ManualRecipeInputScreen(
    viewModel: ManualRecipeInputViewModel = koinViewModel(),
    onBack: () -> Unit,
    onSave: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
) {
    val userSessionRepository = koinInject<UserSessionRepository>()
    val currentUser by userSessionRepository.currentUser.collectAsState()
    val state by viewModel.state.collectAsState()
    val content = state.toManualRecipeContentUi()

    val scope = rememberCoroutineScope()
    val imagePickerLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image,
    ) { file: PlatformFile? ->
        if (file == null) return@rememberFilePickerLauncher
        scope.launch {
            try {
                val imageBytes = file.readBytes()
                viewModel.handleIntent(
                    ManualRecipeInputIntent.UploadImage(
                        file.name,
                        imageBytes,
                    ),
                )
            } catch (e: Exception) {
                println("图片选择错误: ${e.message}")
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                ManualRecipeInputUiEvent.SaveSuccess -> onSave()
            }
        }
    }

    val resetAndBack = {
        viewModel.handleIntent(ManualRecipeInputIntent.ResetForm)
        onBack()
    }
    val onSaveClick = {
        viewModel.handleIntent(ManualRecipeInputIntent.SaveRecipe)
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
                    onIntent = viewModel::handleIntent,
                    onPickImage = onPickImage,
                    onBack = resetAndBack,
                    onSave = onSaveClick,
                    userAccount = currentUser,
                    onProfileClick = onNavigateToProfile,
                )
            }
            layoutWidth >= ManualRecipeBreakpoints.CompactMax -> {
                ManualRecipeTabletLayout(
                    content = content,
                    layoutWidth = layoutWidth,
                    onIntent = viewModel::handleIntent,
                    onPickImage = onPickImage,
                    onBack = resetAndBack,
                    onSave = onSaveClick,
                    userAccount = currentUser,
                    onProfileClick = onNavigateToProfile,
                )
            }
            else -> {
                ManualRecipeCompactLayout(
                    content = content,
                    onIntent = viewModel::handleIntent,
                    onPickImage = onPickImage,
                    onBack = resetAndBack,
                    onSave = onSaveClick,
                )
            }
        }
    }
}
