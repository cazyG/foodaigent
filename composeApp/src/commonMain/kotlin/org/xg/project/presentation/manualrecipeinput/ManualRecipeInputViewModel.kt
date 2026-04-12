package org.xg.project.presentation.manualrecipeinput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.xg.project.data.remote.UploadService
import org.xg.project.data.remote.httpClient
import org.xg.project.data.repository.FoodRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.xg.project.domain.model.MealType
import kotlin.time.Clock

class ManualRecipeInputViewModel(
    private val uploadService: UploadService = UploadService(httpClient),
    private val foodRepository: FoodRepository = FoodRepository()
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
            is ManualRecipeInputIntent.SelectImage -> selectImage(intent.uri)
            is ManualRecipeInputIntent.UploadImage -> uploadImage(intent.imageBytes)
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

    private fun selectImage(uri: String?) {
        _state.value = _state.value.copy(uploadedImageUrl = uri)
    }

    private fun uploadImage(imageBytes: ByteArray?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isUploading = true)
            try {

                val timeZone = TimeZone.currentSystemDefault()
                val filename = "${Clock.System.todayIn(timeZone).toString()}.jpg"
                val presignedUrlResponse = uploadService.getPresignedUrl(filename)

                val uploadResult = uploadService.uploadFile(
                    presignedUrlResponse.data.putUrl,
                    presignedUrlResponse.data.getUrl,
                    presignedUrlResponse.data.headers,
                    imageBytes ?: ByteArray(0)
                )

                if (uploadResult.success) {
                    _state.value = _state.value.copy(uploadedImageUrl = uploadResult.url)
                } else {
                    _state.value = _state.value.copy(
                        error = "图片上传失败: 服务器返回失败",
                        isUploading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(
                    error = "图片上传失败: ${e.message}",
                    isUploading = false
                )
            } finally {
                _state.value = _state.value.copy(isUploading = false)
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

                // 如果有选中的图片但尚未上传，则先上传图片
                if ( _state.value.uploadedImageUrl == null) {
                    // 等待上传完成
                    while (_state.value.isUploading) {
                        kotlinx.coroutines.delay(100)
                    }
                    // 如果上传失败，直接返回
                    if (_state.value.error != null) {
                        _state.value = _state.value.copy(isSaving = false)
                        return@launch
                    }
                }

                // 保存食谱到服务器
                val response = foodRepository.createRecipe(
                    name = _state.value.recipeName,
                    ingredients = _state.value.ingredients,
                    steps = _state.value.steps,
                    duration = _state.value.duration,
                    difficulty = _state.value.difficulty,
                    tag = _state.value.tag,
                    mealType = _state.value.selectedMealType,
                    imageUrl = _state.value.uploadedImageUrl
                )

                if (response.success) {
                    // 保存成功
                    _state.value = _state.value.copy(
                        saveSuccess = true,
                        isSaving = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        error = "保存失败: ${response.message}",
                        isSaving = false
                    )
                }
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