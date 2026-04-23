package org.xg.project.presentation.manualrecipeinput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.xg.project.data.remote.UploadService
import org.xg.project.data.remote.httpClient
import org.xg.project.data.repository.FoodRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.xg.project.domain.model.Ingredient
import kotlin.time.Clock
import kotlin.random.Random

class ManualRecipeInputViewModel(
    private val uploadService: UploadService = UploadService(httpClient),
    private val foodRepository: FoodRepository = FoodRepository()
) : ViewModel() {
    private val _state = MutableStateFlow(ManualRecipeInputState())
    val state: StateFlow<ManualRecipeInputState> = _state.asStateFlow()
    private val _uiEvent = MutableSharedFlow<ManualRecipeInputUiEvent>()
    val uiEvent: SharedFlow<ManualRecipeInputUiEvent> = _uiEvent.asSharedFlow()

    fun handleIntent(intent: ManualRecipeInputIntent) {
        when (intent) {
            is ManualRecipeInputIntent.UpdateRecipeName -> updateRecipeName(intent.value)
            is ManualRecipeInputIntent.AddIngredient -> addIngredient(intent.ingredient)
            is ManualRecipeInputIntent.UpdateIngredient -> updateIngredient(intent.ingredient)
            is ManualRecipeInputIntent.RemoveIngredient -> removeIngredient(intent.ingredient)
            ManualRecipeInputIntent.AddStep -> addStep()
            is ManualRecipeInputIntent.UpdateStep -> updateStep(intent.index, intent.value)
            is ManualRecipeInputIntent.RemoveStep -> removeStep(intent.index)
            is ManualRecipeInputIntent.UpdateDuration -> updateDuration(intent.minutes)
            is ManualRecipeInputIntent.UpdateDifficulty -> updateDifficulty(intent.stars)
            is ManualRecipeInputIntent.UpdateTag -> updateTag(intent.value)
            is ManualRecipeInputIntent.SelectMealType -> selectMealType(intent.mealType)
            is ManualRecipeInputIntent.UploadImage -> uploadImage(intent.fileName,intent.imageBytes)
            ManualRecipeInputIntent.SaveRecipe -> saveRecipe()
            ManualRecipeInputIntent.ResetForm -> resetForm()
        }
    }

    private fun updateRecipeName(value: String) {
        _state.value = _state.value.copy(recipeName = value)
    }

    private fun addIngredient(ingredient: Ingredient) {
        val ingredientWithUniqueId = ingredient.copy(
            id = "${Clock.System.now().toEpochMilliseconds()}-${Random.nextInt()}"
        )
        _state.value = _state.value.copy(ingredients = _state.value.ingredients + ingredientWithUniqueId)
    }

    private fun updateIngredient(ingredient: Ingredient) {
        _state.value = _state.value.copy(
            ingredients = _state.value.ingredients.map {
                if (it.id == ingredient.id) ingredient else it
            }
        )
    }

    private fun removeIngredient(ingredient: Ingredient) {
        _state.value = _state.value.copy(ingredients = _state.value.ingredients.filter { it.id != ingredient.id })
    }

    private fun addStep() {
        _state.value = _state.value.copy(steps = _state.value.steps + "")
    }

    private fun updateStep(index: Int, value: String) {
        _state.value = _state.value.copy(
            steps = _state.value.steps.mapIndexed { i, step ->
                if (i == index) value else step
            }
        )
    }

    private fun removeStep(index: Int) {
        _state.value = _state.value.copy(
            steps = _state.value.steps.filterIndexed { i, _ -> i != index }
        )
    }

    private fun updateDuration(minutes: Int) {
        _state.value = _state.value.copy(durationMinutes = minutes.coerceIn(5, 180))
    }

    private fun updateDifficulty(stars: Int) {
        _state.value = _state.value.copy(difficultyStars = stars.coerceIn(1, 5))
    }

    private fun updateTag(value: String) {
        _state.value = _state.value.copy(tag = value)
    }

    private fun selectMealType(mealType: String) {
        _state.value = _state.value.copy(selectedMealType = mealType)
    }

    private fun uploadImage(fileName: String, imageBytes: ByteArray?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isUploading = true)
            when (val result = uploadService.uploadImage(fileName, imageBytes ?: ByteArray(0))) {
                is org.xg.project.domain.Result.Success -> {
                    if (result.data.success) {
                        _state.value = _state.value.copy(
                            uploadedImageUrl = result.data.url,
                            isUploading = false
                        )
                    } else {
                        _state.value = _state.value.copy(
                            error = "图片上传失败: 服务器返回失败",
                            isUploading = false
                        )
                    }
                }
                is org.xg.project.domain.Result.Error -> {
                    _state.value = _state.value.copy(
                        error = "图片上传失败: ${result.message}",
                        isUploading = false
                    )
                }
            }
        }
    }

    private fun saveRecipe() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)
            
            // 验证表单
            val normalizedSteps = _state.value.steps
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            if (_state.value.recipeName.isEmpty() || _state.value.ingredients.isEmpty() || normalizedSteps.isEmpty()) {
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

            val recipeDraft = _state.value.copy(steps = normalizedSteps).toRecipeDraft()

            // 保存食谱到服务器
            when (val response = foodRepository.createRecipe(recipeDraft)) {
                is org.xg.project.domain.Result.Success -> {
                    if (response.data.success) {
                        // 保存成功
                        _state.value = ManualRecipeInputState()
                        _uiEvent.emit(ManualRecipeInputUiEvent.SaveSuccess)
                    } else {
                        _state.value = _state.value.copy(
                            error = "保存失败: ${response.data.message}",
                            isSaving = false
                        )
                    }
                }
                is org.xg.project.domain.Result.Error -> {
                    _state.value = _state.value.copy(
                        error = "保存失败: ${response.message}",
                        isSaving = false
                    )
                }
            }
        }
    }

    private fun resetForm() {
        _state.value = ManualRecipeInputState()
    }
}
