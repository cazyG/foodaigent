package org.xg.project.screen.manualrecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.xg.project.domain.model.Ingredient
import org.xg.project.domain.model.MealType
import org.xg.project.presentation.manualrecipeinput.ManualRecipeInputIntent

private val FieldShape = RoundedCornerShape(12.dp)
private val CardShape = RoundedCornerShape(16.dp)

enum class ManualRecipeFormLayout { Compact, Desktop }

@Composable
fun ManualRecipeErrorBanner(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ManualRecipeColors.ErrorBackground, RoundedCornerShape(12.dp))
            .padding(14.dp),
    ) {
        Text(text = message, color = ManualRecipeColors.ErrorText, fontSize = ManualRecipeFonts.body)
    }
}

@Composable
fun ManualRecipeFormContent(
    content: ManualRecipeContentUi,
    onIntent: (ManualRecipeInputIntent) -> Unit,
    onPickImage: () -> Unit,
    layout: ManualRecipeFormLayout,
    modifier: Modifier = Modifier,
) {
    when (layout) {
        ManualRecipeFormLayout.Compact -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ManualRecipeBasicFields(content, onIntent)
                ManualRecipeMealTypeSection(content, onIntent, columns = 2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ManualRecipeDurationCard(
                        minutes = content.durationMinutes,
                        onIntent = onIntent,
                        modifier = Modifier.weight(1f),
                    )
                    ManualRecipeDifficultyCard(
                        stars = content.difficultyStars,
                        onIntent = onIntent,
                        modifier = Modifier.weight(1f),
                    )
                }
                ManualRecipeIngredientsSection(content, onIntent)
                ManualRecipeStepsSection(content, onIntent)
                ManualRecipeImageUploadSection(content, onPickImage)
            }
        }
        ManualRecipeFormLayout.Desktop -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ManualRecipeCard {
                        ManualRecipeBasicFields(content, onIntent)
                    }
                    ManualRecipeCard {
                        ManualRecipeMealTypeSection(content, onIntent, columns = 4)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        ManualRecipeDifficultyCard(
                            stars = content.difficultyStars,
                            onIntent = onIntent,
                            modifier = Modifier.weight(1f),
                        )
                        ManualRecipeDurationCard(
                            minutes = content.durationMinutes,
                            onIntent = onIntent,
                            modifier = Modifier.weight(1f),
                            useSlider = true,
                        )
                    }
                    ManualRecipeCard {
                        ManualRecipeIngredientsSection(content, onIntent, showDragHandle = true)
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ManualRecipeImageUploadSection(
                        content = content,
                        onPickImage = onPickImage,
                        modifier = Modifier.height(280.dp),
                        desktopPreview = true,
                    )
                    ManualRecipeCard {
                        ManualRecipeStepsSection(content, onIntent, numbered = true)
                    }
                }
            }
        }
    }
}

@Composable
private fun ManualRecipeCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ManualRecipeColors.CardWhite, CardShape)
            .border(1.dp, ManualRecipeColors.FieldBorder, CardShape)
            .padding(20.dp),
    ) {
        content()
    }
}

@Composable
private fun ManualRecipeBasicFields(
    content: ManualRecipeContentUi,
    onIntent: (ManualRecipeInputIntent) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "食谱名称",
            fontSize = ManualRecipeFonts.sectionTitle,
            fontWeight = FontWeight.SemiBold,
            color = ManualRecipeColors.TextPrimary,
        )
        ManualRecipeTextField(
            value = content.recipeName,
            onValueChange = { onIntent(ManualRecipeInputIntent.UpdateRecipeName(it)) },
            placeholder = if (content.recipeName.isEmpty()) "输入美味的名字..." else "",
            singleLine = true,
        )
        Text(
            text = "标签",
            fontSize = ManualRecipeFonts.sectionTitle,
            fontWeight = FontWeight.SemiBold,
            color = ManualRecipeColors.TextPrimary,
        )
        ManualRecipeTextField(
            value = content.tag,
            onValueChange = { onIntent(ManualRecipeInputIntent.UpdateTag(it)) },
            placeholder = "用逗号分隔，如：家常菜, 快手",
            singleLine = true,
        )
        if (content.tag.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                content.tag.split(',', '，')
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .forEach { tag ->
                        Text(
                            text = "# $tag",
                            fontSize = ManualRecipeFonts.helper,
                            color = ManualRecipeColors.BrandBrown,
                            modifier = Modifier
                                .background(ManualRecipeColors.ChipInactive, RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
            }
        }
    }
}

@Composable
private fun ManualRecipeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = ManualRecipeColors.TextSecondary, fontSize = ManualRecipeFonts.body)
        },
        shape = FieldShape,
        colors = manualRecipeFieldColors(),
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(imeAction = if (singleLine) ImeAction.Next else ImeAction.Default),
    )
}

@Composable
private fun manualRecipeFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ManualRecipeColors.BrandBrown.copy(alpha = 0.5f),
    unfocusedBorderColor = ManualRecipeColors.FieldBorder,
    focusedContainerColor = ManualRecipeColors.FieldBackground,
    unfocusedContainerColor = ManualRecipeColors.FieldBackground,
    focusedTextColor = ManualRecipeColors.TextPrimary,
    unfocusedTextColor = ManualRecipeColors.TextPrimary,
)

@Composable
private fun ManualRecipeMealTypeSection(
    content: ManualRecipeContentUi,
    onIntent: (ManualRecipeInputIntent) -> Unit,
    columns: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "用餐类型",
            fontSize = ManualRecipeFonts.sectionTitle,
            fontWeight = FontWeight.SemiBold,
            color = ManualRecipeColors.TextPrimary,
        )
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cols = if (maxWidth >= 520.dp) columns else 2
            val rows = MealType.entries.chunked(cols)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        row.forEach { mealType ->
                            ManualRecipeMealTypeChip(
                                mealType = mealType,
                                selected = content.selectedMealType == mealType.name,
                                onClick = { onIntent(ManualRecipeInputIntent.SelectMealType(mealType.name)) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        repeat(cols - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ManualRecipeMealTypeChip(
    mealType: MealType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon = when (mealType) {
        MealType.BREAKFAST -> Icons.Default.Restaurant
        MealType.LUNCH -> Icons.Default.Restaurant
        MealType.DINNER -> Icons.Default.Restaurant
        MealType.SNACK -> Icons.Default.Restaurant
    }
    val bg = if (selected) ManualRecipeColors.BrandOrange.copy(alpha = 0.15f) else ManualRecipeColors.ChipInactive
    val border = if (selected) ManualRecipeColors.BrandOrange else ManualRecipeColors.FieldBorder
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) ManualRecipeColors.BrandBrown else ManualRecipeColors.TextSecondary)
        Text(
            text = mealType.title,
            fontSize = ManualRecipeFonts.body,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) ManualRecipeColors.BrandBrown else ManualRecipeColors.TextSecondary,
        )
    }
}

@Composable
private fun ManualRecipeDurationCard(
    minutes: Int,
    onIntent: (ManualRecipeInputIntent) -> Unit,
    modifier: Modifier = Modifier,
    useSlider: Boolean = false,
) {
    Column(
        modifier = modifier
            .background(ManualRecipeColors.CardWhite, CardShape)
            .border(1.dp, ManualRecipeColors.FieldBorder, CardShape)
            .padding(16.dp),
    ) {
        Text(
            text = "烹饪时间",
            fontSize = ManualRecipeFonts.body,
            color = ManualRecipeColors.TextSecondary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$minutes 分钟",
            fontSize = ManualRecipeFonts.durationValue,
            fontWeight = FontWeight.Bold,
            color = ManualRecipeColors.TextPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (useSlider) {
            Slider(
                value = minutes.toFloat(),
                onValueChange = { onIntent(ManualRecipeInputIntent.UpdateDuration(it.toInt())) },
                valueRange = 5f..120f,
                steps = 22,
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("5min", fontSize = ManualRecipeFonts.helper, color = ManualRecipeColors.TextSecondary)
                Text("30min", fontSize = ManualRecipeFonts.helper, color = ManualRecipeColors.TextSecondary)
                Text("60min+", fontSize = ManualRecipeFonts.helper, color = ManualRecipeColors.TextSecondary)
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DurationButton("-5") {
                    onIntent(ManualRecipeInputIntent.UpdateDuration((minutes - 5).coerceIn(5, 180)))
                }
                DurationButton("+5") {
                    onIntent(ManualRecipeInputIntent.UpdateDuration((minutes + 5).coerceIn(5, 180)))
                }
            }
        }
    }
}

@Composable
private fun DurationButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = ManualRecipeColors.FieldBackground),
        shape = RoundedCornerShape(10.dp),
    ) {
        Text(label, color = ManualRecipeColors.TextPrimary, fontSize = ManualRecipeFonts.body)
    }
}

@Composable
private fun ManualRecipeDifficultyCard(
    stars: Int,
    onIntent: (ManualRecipeInputIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(ManualRecipeColors.CardWhite, CardShape)
            .border(1.dp, ManualRecipeColors.FieldBorder, CardShape)
            .padding(16.dp),
    ) {
        Text(
            text = "制作难度",
            fontSize = ManualRecipeFonts.body,
            color = ManualRecipeColors.TextSecondary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (1..5).forEach { star ->
                Text(
                    text = if (star <= stars) "★" else "☆",
                    color = if (star <= stars) ManualRecipeColors.StarActive else ManualRecipeColors.TextSecondary,
                    fontSize = ManualRecipeFonts.durationValue,
                    modifier = Modifier
                        .clickable {
                            val next = if (star == stars) 1 else star
                            onIntent(ManualRecipeInputIntent.UpdateDifficulty(next))
                        }
                        .padding(2.dp),
                )
            }
        }
    }
}

@Composable
private fun ManualRecipeIngredientsSection(
    content: ManualRecipeContentUi,
    onIntent: (ManualRecipeInputIntent) -> Unit,
    showDragHandle: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "食材清单",
                fontSize = ManualRecipeFonts.sectionTitle,
                fontWeight = FontWeight.SemiBold,
                color = ManualRecipeColors.TextPrimary,
            )
            Text(
                text = "+ 添加食材",
                fontSize = ManualRecipeFonts.body,
                color = ManualRecipeColors.BrandBrown,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    onIntent(ManualRecipeInputIntent.AddIngredient(Ingredient("1", "", "")))
                },
            )
        }
        content.ingredients.forEach { ingredient ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (showDragHandle) {
                    Text("⋮⋮", color = ManualRecipeColors.TextSecondary, modifier = Modifier.padding(end = 4.dp))
                }
                ManualRecipeTextField(
                    value = ingredient.name,
                    onValueChange = {
                        onIntent(ManualRecipeInputIntent.UpdateIngredient(ingredient.copy(name = it)))
                    },
                    placeholder = "食材名称",
                    modifier = Modifier.weight(1.2f),
                )
                ManualRecipeTextField(
                    value = ingredient.quantity,
                    onValueChange = {
                        onIntent(ManualRecipeInputIntent.UpdateIngredient(ingredient.copy(quantity = it)))
                    },
                    placeholder = "用量",
                    modifier = Modifier.weight(0.8f),
                )
                IconButton(onClick = { onIntent(ManualRecipeInputIntent.RemoveIngredient(ingredient)) }) {
                    Icon(Icons.Default.Delete, contentDescription = "删除", tint = ManualRecipeColors.TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun ManualRecipeStepsSection(
    content: ManualRecipeContentUi,
    onIntent: (ManualRecipeInputIntent) -> Unit,
    numbered: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "制作步骤",
                fontSize = ManualRecipeFonts.sectionTitle,
                fontWeight = FontWeight.SemiBold,
                color = ManualRecipeColors.TextPrimary,
            )
            Text(
                text = "+ 添加步骤",
                fontSize = ManualRecipeFonts.body,
                color = ManualRecipeColors.BrandBrown,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onIntent(ManualRecipeInputIntent.AddStep) },
            )
        }
        content.steps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (numbered) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(ManualRecipeColors.BrandBrown, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = Color.White,
                            fontSize = ManualRecipeFonts.helper,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                ManualRecipeTextField(
                    value = step,
                    onValueChange = { onIntent(ManualRecipeInputIntent.UpdateStep(index, it)) },
                    placeholder = "描述这一个步骤的具体操作...",
                    singleLine = false,
                    minLines = 2,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { onIntent(ManualRecipeInputIntent.RemoveStep(index)) }) {
                    Icon(Icons.Default.Delete, contentDescription = "删除", tint = ManualRecipeColors.TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun ManualRecipeImageUploadSection(
    content: ManualRecipeContentUi,
    onPickImage: () -> Unit,
    modifier: Modifier = Modifier,
    desktopPreview: Boolean = false,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (!desktopPreview) Modifier.height(200.dp) else Modifier),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = ManualRecipeColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, ManualRecipeColors.UploadBorder, CardShape)
                .clickable(enabled = !content.isUploading, onClick = onPickImage),
            contentAlignment = Alignment.Center,
        ) {
            if (content.uploadedImageUrl != null) {
                AsyncImage(
                    model = content.uploadedImageUrl,
                    contentDescription = content.recipeName,
                    modifier = Modifier.fillMaxSize(),
                )
                if (desktopPreview) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text("PREVIEW", color = Color.White, fontSize = ManualRecipeFonts.helper)
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.AutoMirrored.Filled.PlaylistAdd,
                        contentDescription = null,
                        tint = ManualRecipeColors.BrandOrange,
                        modifier = Modifier.size(36.dp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (desktopPreview) "点击上传主图 (可选)" else "点击上传图片",
                        fontWeight = FontWeight.SemiBold,
                        color = ManualRecipeColors.TextPrimary,
                        fontSize = ManualRecipeFonts.body,
                    )
                    Text(
                        text = "支持 JPG, PNG 格式",
                        color = ManualRecipeColors.TextSecondary,
                        fontSize = ManualRecipeFonts.helper,
                    )
                }
            }
            if (content.isUploading) {
                CircularProgressIndicator(color = ManualRecipeColors.BrandOrange)
            }
        }
    }
}
