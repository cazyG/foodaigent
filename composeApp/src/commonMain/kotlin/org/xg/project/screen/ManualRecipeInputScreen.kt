package org.xg.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.domain.model.Ingredient
import org.xg.project.domain.model.MealType
import org.xg.project.presentation.manualrecipeinput.ManualRecipeInputIntent
import org.xg.project.presentation.manualrecipeinput.ManualRecipeInputViewModel

@Composable
fun ManualRecipeInputScreen(
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val viewModel = koinViewModel<ManualRecipeInputViewModel>()
    val state by viewModel.state.collectAsState()

    // 焦点管理
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val (nameFocusRequester, durationFocusRequester, difficultyFocusRequester, tagFocusRequester, stepsFocusRequester) = FocusRequester.createRefs()

    // 使用ImagePickerKMP 1.0.38版本的API
    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(
                allowMultiple = false,
                selectionLimit = 1
            )
        )
    )
    val result = picker.result

    // 处理图片选择结果
    LaunchedEffect(result) {
        when (result) {
            is ImagePickerResult.Success -> {
                val image = result.photos.firstOrNull()
                val imageBytes = image?.loadBytes()
                viewModel.handleIntent(ManualRecipeInputIntent.SelectImage(image?.uri))
                viewModel.handleIntent(ManualRecipeInputIntent.UploadImage(imageBytes))
            }
            is ImagePickerResult.Error -> {
                println("图片选择错误: ${result.exception.message}")
            }
            else -> {}
        }
    }

    // 监听保存成功状态
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            onSave()
        }
    }

    // --- 科技风青春活力主题色 ---
    val TechBlue = Color(0xFF2979FF)      // 活力电光蓝 (主要按钮/强调色)
    val TechLightBlue = Color(0xFFE0F2FE) // 浅蓝背景 (次要按钮)
    val TechBg = Color(0xFFF0F4F8)        // 科技感浅灰蓝背景
    val TechText = Color(0xFF1E293B)      // 深色文字
    val TechDelete = Color(0xFFFF4D4F)    // 警示红/活力粉红 (删除操作)
    val TechCardBg = Color(0xFFE2E8F0)    // 卡片/占位符背景
    val FieldShape = RoundedCornerShape(12.dp) // 统一输入框圆角

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TechBg)
    ) {
        // 顶部栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = TechLightBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = TechBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("返回", color = TechBlue, fontWeight = FontWeight.Bold)
            }
            Text("手动录入食谱", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TechText)
            Button(
                onClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    viewModel.handleIntent(ManualRecipeInputIntent.SaveRecipe)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TechBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("保存", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // 错误提示
        if (state.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red.copy(alpha = 0.1f))
                    .padding(16.dp)
            ) {
                Text(state.error!!, color = Color.Red)
            }
        }

        // 主体内容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            // 食谱名称
            OutlinedTextField(
                value = state.recipeName,
                onValueChange = { viewModel.handleIntent(ManualRecipeInputIntent.UpdateRecipeName(it)) },
                label = { Text("食谱名称") },
                shape = FieldShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechBlue,
                    focusedLabelColor = TechBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(nameFocusRequester),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { durationFocusRequester.requestFocus() })
            )

            // 用餐类型
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "用餐类型",
                    fontWeight = FontWeight.Bold,
                    color = TechText,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MealType.values().forEach { mealType ->
                        val isSelected = state.selectedMealType == mealType.name
                        Button(
                            onClick = {
                                viewModel.handleIntent(ManualRecipeInputIntent.SelectMealType(mealType.name))
                                durationFocusRequester.requestFocus()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) TechBlue else Color.White,
                                contentColor = if (isSelected) Color.White else Color(0xFF64748B)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(mealType.title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }

            // 烹饪时间
            OutlinedTextField(
                value = state.duration,
                onValueChange = { viewModel.handleIntent(ManualRecipeInputIntent.UpdateDuration(it)) },
                label = { Text("烹饪时间") },
                placeholder = { Text("例如：30分钟") },
                shape = FieldShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechBlue,
                    focusedLabelColor = TechBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(durationFocusRequester),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { difficultyFocusRequester.requestFocus() })
            )

            // 难度
            OutlinedTextField(
                value = state.difficulty,
                onValueChange = { viewModel.handleIntent(ManualRecipeInputIntent.UpdateDifficulty(it)) },
                label = { Text("难度") },
                placeholder = { Text("例如：中等难度") },
                shape = FieldShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechBlue,
                    focusedLabelColor = TechBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(difficultyFocusRequester),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { tagFocusRequester.requestFocus() })
            )

            // 标签
            OutlinedTextField(
                value = state.tag,
                onValueChange = { viewModel.handleIntent(ManualRecipeInputIntent.UpdateTag(it)) },
                label = { Text("标签") },
                placeholder = { Text("例如：家常菜") },
                shape = FieldShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechBlue,
                    focusedLabelColor = TechBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(tagFocusRequester),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { stepsFocusRequester.requestFocus() })
            )

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
                    Text("原材料", fontWeight = FontWeight.Bold, color = TechText, modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            viewModel.handleIntent(ManualRecipeInputIntent.AddIngredient(Ingredient("1", "", "")))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TechLightBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "添加",
                            tint = TechBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("添加", color = TechBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                val ingredientNameFocusRequesters = remember(state.ingredients.size) {
                    List(state.ingredients.size) { FocusRequester() }
                }
                val ingredientQuantityFocusRequesters = remember(state.ingredients.size) {
                    List(state.ingredients.size) { FocusRequester() }
                }

                state.ingredients.forEachIndexed { index, ingredient ->
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
                                focusedBorderColor = TechBlue,
                                focusedLabelColor = TechBlue
                            ),
                            onValueChange = {
                                viewModel.handleIntent(ManualRecipeInputIntent.UpdateIngredient(ingredient.copy(name = it)))
                            },
                            modifier = Modifier
                                .weight(2f)
                                .focusRequester(ingredientNameFocusRequesters[index]),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = {
                                if (index < ingredientQuantityFocusRequesters.size) {
                                    ingredientQuantityFocusRequesters[index].requestFocus()
                                }
                            })
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            label = { Text("数量") },
                            value = ingredient.quantity,
                            shape = FieldShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TechBlue,
                                focusedLabelColor = TechBlue
                            ),
                            onValueChange = {
                                viewModel.handleIntent(ManualRecipeInputIntent.UpdateIngredient(ingredient.copy(quantity = it)))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(ingredientQuantityFocusRequesters[index]),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = {
                                if (index < state.ingredients.size - 1) {
                                    ingredientNameFocusRequesters[index + 1].requestFocus()
                                } else {
                                    stepsFocusRequester.requestFocus()
                                }
                            })
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "删除",
                            tint = TechDelete,
                            modifier = Modifier.size(28.dp).weight(0.3f).clickable{
                                viewModel.handleIntent(ManualRecipeInputIntent.RemoveIngredient(ingredient))
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // 制作过程
            OutlinedTextField(
                value = state.steps,
                onValueChange = { viewModel.handleIntent(ManualRecipeInputIntent.UpdateSteps(it)) },
                label = { Text("制作过程") },
                placeholder = { Text("请输入详细制作过程，每行一步") },
                shape = FieldShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechBlue,
                    focusedLabelColor = TechBlue
                ),
                maxLines = 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(stepsFocusRequester),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            )

            // 图片上传
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TechCardBg)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
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
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(32.dp).padding(bottom = 8.dp)
                            )
                            Text("点击上传图片", color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                            Text("(可选)", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
