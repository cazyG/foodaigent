package org.xg.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.ismoy.imagepickerkmp.domain.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.domain.extensions.loadBytes
import io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.ImagePickerResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP
import kotlinx.coroutines.flow.collect
import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.domain.model.Ingredient
import org.xg.project.domain.model.MealType
import org.xg.project.presentation.manualrecipeinput.ManualRecipeInputIntent
import org.xg.project.presentation.manualrecipeinput.ManualRecipeInputUiEvent
import org.xg.project.presentation.manualrecipeinput.ManualRecipeInputViewModel

private val GlassBlue = Color(0xFF95B6FF)
private val GlassBlueDark = Color(0xFF89A7FF)
private val GlassBgTop = Color(0xFFF2F7FF)
private val GlassBgBottom = Color(0xFFFAF4FF)
private val GlassText = Color(0xFF475569)
private val GlassDelete = Color(0xFFFFA0B4)
private val GlassSurface = Color.White.copy(alpha = 0.14f)
private val GlassSurfaceStrong = Color.White.copy(alpha = 0.2f)
private val GlassStroke = Color.White.copy(alpha = 0.58f)
private val FieldShape = RoundedCornerShape(14.dp)
private val AppBarTextSize = 15.sp
private val SectionTitleSize = 16.sp
private val BodyTextSize = 14.sp
private val HelperTextSize = 12.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualRecipeInputScreen(
    viewModel: ManualRecipeInputViewModel = koinViewModel<ManualRecipeInputViewModel>(),
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // 焦点管理
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val (nameFocusRequester, durationFocusRequester, tagFocusRequester, stepsFocusRequester) = FocusRequester.createRefs()
    val resetAndBack = {
        focusManager.clearFocus()
        keyboardController?.hide()
        viewModel.handleIntent(ManualRecipeInputIntent.ResetForm)
        onBack()
    }

    // 使用ImagePickerKMP 1.0.38版本的API
    val pickerConfig = remember {
        ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                allowMultiple = false,
                selectionLimit = 1
            )
        )
    }
    val picker = rememberImagePickerKMP(
        config = pickerConfig
    )
    val result = picker.result

    // 处理图片选择结果
    LaunchedEffect(result) {
        when (result) {
            is ImagePickerResult.Success -> {
                val file = result.photos.firstOrNull()
                val imageBytes = result.photos.firstOrNull()?.loadBytes()
                if (imageBytes != null) {
                    viewModel.handleIntent(
                        ManualRecipeInputIntent.UploadImage(
                            file?.fileName ?: "",
                            imageBytes
                        )
                    )
                }
            }

            is ImagePickerResult.Error -> {
                println("图片选择错误: ${result.exception.message}")
            }

            else -> {}
        }
    }

    // 监听一次性UI事件
    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                ManualRecipeInputUiEvent.SaveSuccess -> onSave()
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .background(GlassStyle.BgGradient),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "手动录入食谱",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = GlassText
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = resetAndBack,
                        modifier = Modifier.sizeIn(minHeight = 36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = GlassText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            "返回",
                            color = GlassText,
                            fontWeight = FontWeight.Medium,
                            fontSize = AppBarTextSize
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            viewModel.handleIntent(ManualRecipeInputIntent.SaveRecipe)
                        },
                        modifier = Modifier.sizeIn(minHeight = 36.dp)
                    ) {
                        Text(
                            "保存",
                            color = GlassText,
                            fontWeight = FontWeight.Medium,
                            fontSize = AppBarTextSize
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlassSurfaceStrong,
                    titleContentColor = GlassText,
                    navigationIconContentColor = GlassText,
                    actionIconContentColor = GlassText
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
            ) {
                // 错误提示
                if (state.error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .background(
                                color = Color(0xFFFF5A6F).copy(alpha = 0.16f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(14.dp)
                    ) {
                        Text(
                            text = state.error!!,
                            color = Color(0xFFB00020),
                            fontSize = BodyTextSize
                        )
                    }
                }

                // 主体内容
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .shadow(
                            6.dp,
                            RoundedCornerShape(20.dp),
                            ambientColor = Color.White.copy(alpha = 0.24f),
                            spotColor = Color.Black.copy(alpha = 0.07f)
                        )
                        .background(
                            color = GlassSurface,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = GlassStroke,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    // 食谱名称
                    OutlinedTextField(
                        value = state.recipeName,
                        onValueChange = {
                            viewModel.handleIntent(ManualRecipeInputIntent.UpdateRecipeName(it))
                        },
                        label = { Text("食谱名称") },
                        shape = FieldShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White.copy(alpha = 0.66f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            focusedLabelColor = GlassText.copy(alpha = 0.75f),
                            unfocusedLabelColor = GlassText.copy(alpha = 0.55f),
                            focusedContainerColor = Color.White.copy(alpha = 0.06f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .focusRequester(nameFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { durationFocusRequester.requestFocus() },
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        )
                    )

                    // 标签
                    OutlinedTextField(
                        value = state.tag,
                        onValueChange = {
                            viewModel.handleIntent(
                                ManualRecipeInputIntent.UpdateTag(
                                    it
                                )
                            )
                        },
                        label = { Text("标签") },
                        placeholder = { Text("例如：家常菜") },
                        shape = FieldShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White.copy(alpha = 0.66f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                            focusedLabelColor = GlassText.copy(alpha = 0.75f),
                            unfocusedLabelColor = GlassText.copy(alpha = 0.55f),
                            focusedContainerColor = Color.White.copy(alpha = 0.06f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .focusRequester(tagFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { stepsFocusRequester.requestFocus() },
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        )
                    )

                    // 用餐类型（宽屏一行四个，窄屏自动两行）
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            "用餐类型",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = SectionTitleSize,
                            color = GlassText,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val isWideScreen = maxWidth >= 520.dp
                            val columns = if (isWideScreen) 4 else 2
                            val mealTypeRows = MealType.entries.chunked(columns)

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                mealTypeRows.forEach { mealTypeRow ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        mealTypeRow.forEach { mealType ->
                                            val isSelected = state.selectedMealType == mealType.name
                                            Button(
                                                onClick = {
                                                    viewModel.handleIntent(
                                                        ManualRecipeInputIntent.SelectMealType(
                                                            mealType.name
                                                        )
                                                    )
                                                    durationFocusRequester.requestFocus()
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isSelected) Color.White.copy(
                                                        alpha = 0.34f
                                                    ) else Color.White.copy(alpha = 0.16f),
                                                    contentColor = GlassText.copy(alpha = if (isSelected) 0.92f else 0.78f)
                                                ),
                                                shape = RoundedCornerShape(16.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = mealType.title,
                                                    fontSize = BodyTextSize,
                                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                                )
                                            }
                                        }
                                        repeat(columns - mealTypeRow.size) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 烹饪时间
                    val selectedDurationMinutes = state.durationMinutes

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .focusRequester(durationFocusRequester)
                            .focusable()
                    ) {
                        Text(
                            text = "烹饪时间",
                            color = GlassText.copy(alpha = 0.75f),
                            fontSize = BodyTextSize,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color.White.copy(alpha = 0.06f),
                                    shape = FieldShape
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.5f),
                                    shape = FieldShape
                                )
                                .padding(vertical = 14.dp, horizontal = 16.dp)
                        ) {
                            val updateDurationMinutes: (Int) -> Unit = { delta ->
                                val next = (selectedDurationMinutes + delta).coerceIn(5, 180)
                                if (next != selectedDurationMinutes) {
                                    viewModel.handleIntent(
                                        ManualRecipeInputIntent.UpdateDuration(
                                            next
                                        )
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "${selectedDurationMinutes}分钟",
                                    color = GlassText.copy(alpha = 0.92f),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { updateDurationMinutes(10) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White.copy(
                                                alpha = 0.18f
                                            )
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            "+10分钟",
                                            color = GlassText.copy(alpha = 0.9f),
                                            fontSize = BodyTextSize
                                        )
                                    }
                                    Button(
                                        onClick = { updateDurationMinutes(5) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White.copy(
                                                alpha = 0.18f
                                            )
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            "+5分钟",
                                            color = GlassText.copy(alpha = 0.9f),
                                            fontSize = BodyTextSize
                                        )
                                    }
                                    Button(
                                        onClick = { updateDurationMinutes(-5) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White.copy(
                                                alpha = 0.18f
                                            )
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            "-5分钟",
                                            color = GlassText.copy(alpha = 0.9f),
                                            fontSize = BodyTextSize
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 难度
                    val selectedDifficultyStars = state.difficultyStars

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "难度",
                            color = GlassText.copy(alpha = 0.75f),
                            fontSize = BodyTextSize,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            (1..5).forEach { star ->
                                Text(
                                    text = if (star <= selectedDifficultyStars) "★" else "☆",
                                    color = if (star <= selectedDifficultyStars) Color(0xFFFFC107) else GlassText.copy(
                                        alpha = 0.5f
                                    ),
                                    fontSize = 28.sp,
                                    modifier = Modifier
                                        .clickable {
                                            val nextDifficulty =
                                                if (star == selectedDifficultyStars) 1 else star
                                            viewModel.handleIntent(
                                                ManualRecipeInputIntent.UpdateDifficulty(
                                                    nextDifficulty
                                                )
                                            )
                                        }
                                        .padding(horizontal = 2.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // 原材料
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "原材料",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = SectionTitleSize,
                                color = GlassText,
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    viewModel.handleIntent(
                                        ManualRecipeInputIntent.AddIngredient(
                                            Ingredient("1", "", "")
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(
                                        alpha = 0.18f
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "添加",
                                    tint = GlassText.copy(alpha = 0.86f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "添加",
                                    color = GlassText.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = BodyTextSize
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        val ingredientNameFocusRequesters =
                            remember { mutableMapOf<String, FocusRequester>() }
                        val ingredientQuantityFocusRequesters =
                            remember { mutableMapOf<String, FocusRequester>() }

                        state.ingredients.forEachIndexed { index, ingredient ->
                            val nameFocusRequester =
                                ingredientNameFocusRequesters.getOrPut(ingredient.id) { FocusRequester() }
                            val quantityFocusRequester =
                                ingredientQuantityFocusRequesters.getOrPut(ingredient.id) { FocusRequester() }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = ingredient.name,
                                    label = { Text("材料名称") },
                                    shape = FieldShape,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.White.copy(alpha = 0.66f),
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                        focusedLabelColor = GlassText.copy(alpha = 0.75f),
                                        unfocusedLabelColor = GlassText.copy(alpha = 0.55f),
                                        focusedContainerColor = Color.White.copy(alpha = 0.06f),
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
                                    ),
                                    onValueChange = {
                                        viewModel.handleIntent(
                                            ManualRecipeInputIntent.UpdateIngredient(
                                                ingredient.copy(name = it)
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(2f)
                                        .focusRequester(nameFocusRequester),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    keyboardActions = KeyboardActions(
                                        onNext = {
                                            quantityFocusRequester.requestFocus()
                                        },
                                        onDone = {
                                            focusManager.clearFocus()
                                            keyboardController?.hide()
                                        }
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    label = { Text("数量") },
                                    value = ingredient.quantity,
                                    shape = FieldShape,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.White.copy(alpha = 0.66f),
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                        focusedLabelColor = GlassText.copy(alpha = 0.75f),
                                        unfocusedLabelColor = GlassText.copy(alpha = 0.55f),
                                        focusedContainerColor = Color.White.copy(alpha = 0.06f),
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
                                    ),
                                    onValueChange = {
                                        viewModel.handleIntent(
                                            ManualRecipeInputIntent.UpdateIngredient(
                                                ingredient.copy(quantity = it)
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .focusRequester(quantityFocusRequester),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    keyboardActions = KeyboardActions(
                                        onNext = {
                                            if (index < state.ingredients.size - 1) {
                                                ingredientNameFocusRequesters[state.ingredients[index + 1].id]?.requestFocus()
                                            } else {
                                                stepsFocusRequester.requestFocus()
                                            }
                                        },
                                        onDone = {
                                            focusManager.clearFocus()
                                            keyboardController?.hide()
                                        }
                                    )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                IconButton(
                                    onClick = {
                                        viewModel.handleIntent(
                                            ManualRecipeInputIntent.RemoveIngredient(
                                                ingredient
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(0.3f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "删除",
                                        tint = GlassDelete,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // 制作过程
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "制作过程",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = SectionTitleSize,
                                color = GlassText,
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = { viewModel.handleIntent(ManualRecipeInputIntent.AddStep) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(
                                        alpha = 0.18f
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "添加步骤",
                                    tint = GlassText.copy(alpha = 0.86f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "添加",
                                    color = GlassText.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = BodyTextSize
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val stepFocusRequesters = remember { mutableMapOf<Int, FocusRequester>() }

                        state.steps.forEachIndexed { index, step ->
                            val currentStepFocusRequester =
                                stepFocusRequesters.getOrPut(index) { FocusRequester() }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = step,
                                    onValueChange = {
                                        viewModel.handleIntent(
                                            ManualRecipeInputIntent.UpdateStep(
                                                index,
                                                it
                                            )
                                        )
                                    },
                                    label = { Text("步骤 ${index + 1}") },
                                    placeholder = { Text("请输入步骤内容") },
                                    shape = FieldShape,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.White.copy(alpha = 0.66f),
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                        focusedLabelColor = GlassText.copy(alpha = 0.75f),
                                        unfocusedLabelColor = GlassText.copy(alpha = 0.55f),
                                        focusedContainerColor = Color.White.copy(alpha = 0.06f),
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
                                    ),
                                    modifier = Modifier
                                        .weight(2f)
                                        .focusRequester(
                                            if (index == 0) stepsFocusRequester else currentStepFocusRequester
                                        ),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = if (index < state.steps.size - 1) ImeAction.Next else ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = {
                                            if (index < state.steps.size - 1) {
                                                stepFocusRequesters[index + 1]?.requestFocus()
                                            }
                                        },
                                        onDone = {
                                            focusManager.clearFocus()
                                            keyboardController?.hide()
                                        }
                                    )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                IconButton(
                                    onClick = {
                                        viewModel.handleIntent(
                                            ManualRecipeInputIntent.RemoveStep(
                                                index
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(0.3f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "删除步骤",
                                        tint = GlassDelete,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // 图片上传
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.20f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = GlassStroke,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    if (state.isUploading) return@clickable
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    picker.launchGallery()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.uploadedImageUrl != null) {
                                AsyncImage(
                                    model = state.uploadedImageUrl,
                                    contentDescription = state.recipeName,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                                        contentDescription = "上传图片",
                                        tint = GlassBlue.copy(alpha = 0.7f),
                                        modifier = Modifier.size(32.dp).padding(bottom = 8.dp)
                                    )
                                    Text(
                                        "点击上传图片",
                                        color = GlassText.copy(alpha = 0.9f),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = BodyTextSize
                                    )
                                    Text(
                                        "(可选)",
                                        color = GlassText.copy(alpha = 0.6f),
                                        fontSize = HelperTextSize
                                    )
                                }
                            }

                            if (state.isUploading) {
                                CircularProgressIndicator(color = GlassBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}
