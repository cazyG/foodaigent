package org.xg.project.presentation.manualrecipeinput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.xg.project.data.remote.UploadService
import org.xg.project.data.remote.httpClient
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.xg.project.domain.model.MealType
import kotlin.time.Clock

class ManualRecipeInputViewModel(
    private val uploadService: UploadService = UploadService(httpClient)
) : ViewModel() {
    private val _state = MutableStateFlow(ManualRecipeInputState())
    val state: StateFlow<ManualRecipeInputState> = _state.asStateFlow()

    fun handleIntent(intent: ManualRecipeInputIntent) {
        when (intent) {
            is ManualRecipeInputIntent.UpdateRecipeName -> updateRecipeName(intent.value)
            is ManualRecipeInputIntent.UpdateIngredients -> updateIngredients(intent.value)
            is ManualRecipeInputIntent.UpdateSteps -> updateSteps(intent.value)
            is ManualRecipeInputIntent.UpdateDuration -> updateDuration(intent.value)
            is ManualRecipeInputIntent.UpdateDifficulty -> updateDifficulty(intent.value)
            is ManualRecipeInputIntent.UpdateTag -> updateTag(intent.value)
            is ManualRecipeInputIntent.SelectMealType -> selectMealType(intent.mealType)
            is ManualRecipeInputIntent.SelectImage -> {
                selectImage(intent.imagePath)
                // 选择图片后立即上传
                if (intent.imagePath != null) {
                    uploadImage()
                }
            }
            ManualRecipeInputIntent.UploadImage -> uploadImage()
            ManualRecipeInputIntent.SaveRecipe -> saveRecipe()
            ManualRecipeInputIntent.ResetForm -> resetForm()
        }
    }

    private fun updateRecipeName(value: String) {
        _state.value = _state.value.copy(recipeName = value)
    }

    private fun updateIngredients(value: String) {
        _state.value = _state.value.copy(ingredients = value)
    }

    private fun updateSteps(value: String) {
        _state.value = _state.value.copy(steps = value)
    }

    private fun updateDuration(value: String) {
        _state.value = _state.value.copy(duration = value)
    }

    private fun updateDifficulty(value: String) {
        _state.value = _state.value.copy(difficulty = value)
    }

    private fun updateTag(value: String) {
        _state.value = _state.value.copy(tag = value)
    }

    private fun selectMealType(mealType: MealType) {
        _state.value = _state.value.copy(selectedMealType = mealType)
    }

    private fun selectImage(imagePath: String?) {
        _state.value = _state.value.copy(selectedImagePath = imagePath)
    }

    private fun uploadImage() {
        viewModelScope.launch {
            if (_state.value.selectedImagePath != null) {
                _state.value = _state.value.copy(isUploading = true)
                try {
                    println("ManualRecipeInputViewModel: Starting image upload")
                    println("ManualRecipeInputViewModel: Selected image path: ${_state.value.selectedImagePath}")
                    
                    val timeZone = TimeZone.currentSystemDefault()
                    val filename = "${Clock.System.todayIn(timeZone).toString()}.jpg"
                    println("ManualRecipeInputViewModel: Generated filename: $filename")
                    
                    val presignedUrlResponse = uploadService.getPresignedUrl(filename)
                    println("ManualRecipeInputViewModel: Got presigned URL response")
                    
                    // 这里需要将图片文件转换为ByteArray，实际项目中需要根据ImagePickerKMP的API来实现
                    // 暂时使用空字节数组，实际项目中需要读取文件内容
                    val imageBytes = ByteArray(0) // 暂时使用空字节数组
                    println("ManualRecipeInputViewModel: Image bytes size: ${imageBytes.size}")
                    
                    val uploadResult = uploadService.uploadFile(
                        presignedUrlResponse.data.putUrl,
                        presignedUrlResponse.data.getUrl,
                        presignedUrlResponse.data.headers,
                        imageBytes
                    )
                    
                    if (uploadResult.success) {
                        println("ManualRecipeInputViewModel: Upload success, URL: ${uploadResult.url}")
                        _state.value = _state.value.copy(uploadedImageUrl = uploadResult.url)
                    } else {
                        println("ManualRecipeInputViewModel: Upload failed")
                        _state.value = _state.value.copy(
                            error = "图片上传失败: 服务器返回失败",
                            isUploading = false
                        )
                    }
                } catch (e: Exception) {
                    println("ManualRecipeInputViewModel: Upload error: ${e.message}")
                    e.printStackTrace()
                    _state.value = _state.value.copy(
                        error = "图片上传失败: ${e.message}",
                        isUploading = false
                    )
                } finally {
                    _state.value = _state.value.copy(isUploading = false)
                    println("ManualRecipeInputViewModel: Upload process completed")
                }
            } else {
                println("ManualRecipeInputViewModel: No image path selected, skipping upload")
            }
        }
    }

    private fun saveRecipe() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)
            try {
                // 验证表单
                if (_state.value.recipeName.isEmpty() || _state.value.ingredients.isEmpty() || _state.value.steps.isEmpty()) {
                    _state.value = _state.value.copy(
                        error = "食谱名称、原材料和制作过程不能为空",
                        isSaving = false
                    )
                    return@launch
                }

                // 保存食谱到数据库
                // 这里需要实现保存食谱的逻辑，实际项目中需要调用相应的Repository方法

                // 保存成功
                _state.value = _state.value.copy(
                    saveSuccess = true,
                    isSaving = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "保存失败: ${e.message}",
                    isSaving = false
                )
            }
        }
    }

    private fun resetForm() {
        _state.value = ManualRecipeInputState()
    }
}