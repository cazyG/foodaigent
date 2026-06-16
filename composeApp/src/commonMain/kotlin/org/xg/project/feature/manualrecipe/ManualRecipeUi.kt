package org.xg.project.feature.manualrecipe

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

object ManualRecipeColors {
    val PageBackground = Color(0xFFF5F5F5)
    val CardWhite = Color.White
    val BrandBrown = Color(0xFF7D4A05)
    val BrandOrange = Color(0xFFF0883A)
    val TextPrimary = Color(0xFF1F2937)
    val TextSecondary = Color(0xFF6B7280)
    val FieldBackground = Color(0xFFF3F4F6)
    val FieldBorder = Color(0xFFE5E7EB)
    val ChipInactive = Color(0xFFF9FAFB)
    val ErrorBackground = Color(0xFFFEE2E2)
    val ErrorText = Color(0xFFB91C1C)
    val StarActive = Color(0xFFF59E0B)
    val UploadBorder = Color(0xFFD1D5DB)
}

object ManualRecipeFonts {
    val pageTitle = 20.sp
    val sectionTitle = 16.sp
    val body = 14.sp
    val helper = 12.sp
    val durationValue = 18.sp
}

data class ManualRecipeContentUi(
    val recipeName: String,
    val tag: String,
    val selectedMealType: String,
    val durationMinutes: Int,
    val difficultyStars: Int,
    val ingredients: List<org.xg.project.domain.model.Ingredient>,
    val steps: List<String>,
    val uploadedImageUrl: String?,
    val isUploading: Boolean,
    val isSaving: Boolean,
    val error: String?,
)

fun ManualRecipeInputState.toManualRecipeContentUi(): ManualRecipeContentUi =
    ManualRecipeContentUi(
        recipeName = recipeName,
        tag = tag,
        selectedMealType = selectedMealType,
        durationMinutes = durationMinutes,
        difficultyStars = difficultyStars,
        ingredients = ingredients,
        steps = steps,
        uploadedImageUrl = uploadedImageUrl,
        isUploading = isUploading,
        isSaving = isSaving,
        error = error,
    )
