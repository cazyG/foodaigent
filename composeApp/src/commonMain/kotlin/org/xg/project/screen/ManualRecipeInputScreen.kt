package org.xg.project.screen

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import org.xg.project.data.session.UserSessionRepository
import io.github.ismoy.imagepickerkmp.domain.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.domain.extensions.loadBytes
import io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.ImagePickerResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP
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

    val pickerConfig = remember {
        ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                allowMultiple = false,
                selectionLimit = 1,
            ),
        )
    }
    val picker = rememberImagePickerKMP(config = pickerConfig)
    val result = picker.result

    LaunchedEffect(result) {
        when (result) {
            is ImagePickerResult.Success -> {
                val file = result.photos.firstOrNull()
                val imageBytes = file?.loadBytes()
                if (imageBytes != null) {
                    viewModel.handleIntent(
                        ManualRecipeInputIntent.UploadImage(
                            file?.fileName ?: "",
                            imageBytes,
                        ),
                    )
                }
            }
            is ImagePickerResult.Error -> {
                println("图片选择错误: ${result.exception.message}")
            }
            else -> Unit
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
            picker.launchGallery()
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
