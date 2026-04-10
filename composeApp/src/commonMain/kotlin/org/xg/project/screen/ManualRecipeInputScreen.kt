package org.xg.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.ismoy.imagepickerkmp.domain.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.ImagePickerResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP
import org.koin.compose.viewmodel.koinViewModel
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
    when (result) {
        is ImagePickerResult.Success -> {
            // 获取选中的图片
            val image = result.photos.firstOrNull()
            viewModel.handleIntent(ManualRecipeInputIntent.SelectImage(image?.uri))
        }
        is ImagePickerResult.Error -> {
            println("图片选择错误: ${result.exception.message}")
        }
        else -> {}
    }

    // 监听保存成功状态
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            onSave()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCFAF2))
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEDD5))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = Color(0xFFF59E42),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("返回", color = Color(0xFFF59E42))
            }
            Text("手动录入食谱", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Button(
                onClick = {
                    viewModel.handleIntent(ManualRecipeInputIntent.SaveRecipe)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E42))
            ) {
                Text("保存", color = Color.White)
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
        ) {
            // 图片上传
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray)
                        .clickable {
                            // 使用ImagePickerKMP 1.0.38版本的API启动图片选择器
                            picker.launchGallery()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.selectedImagePath != null) {
                        AsyncImage(
                            model = state.selectedImagePath,
                            contentDescription = state.recipeName,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("点击上传图片", color = Color.Gray)
                            Text("(可选)", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }

            // 食谱名称
            OutlinedTextField(
                value = state.recipeName,
                onValueChange = { viewModel.handleIntent(ManualRecipeInputIntent.UpdateRecipeName(it)) },
                label = { Text("食谱名称") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // 用餐类型
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    "用餐类型", 
                    fontWeight = FontWeight.Medium, 
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MealType.values().forEach { mealType ->
                        Button(
                            onClick = { viewModel.handleIntent(ManualRecipeInputIntent.SelectMealType(mealType)) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.selectedMealType == mealType) Color(0xFFF59E42) else Color.White,
                                contentColor = if (state.selectedMealType == mealType) Color.White else Color(0xFF4B5563)
                            ),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(mealType.title)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // 难度
            OutlinedTextField(
                value = state.difficulty,
                onValueChange = { viewModel.handleIntent(ManualRecipeInputIntent.UpdateDifficulty(it)) },
                label = { Text("难度") },
                placeholder = { Text("例如：中等难度") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // 标签
            OutlinedTextField(
                value = state.tag,
                onValueChange = { viewModel.handleIntent(ManualRecipeInputIntent.UpdateTag(it)) },
                label = { Text("标签") },
                placeholder = { Text("例如：家常菜") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // 原材料
            OutlinedTextField(
                value = state.ingredients,
                onValueChange = { viewModel.handleIntent(ManualRecipeInputIntent.UpdateIngredients(it)) },
                label = { Text("原材料") },
                placeholder = { Text("请输入原材料，每行一种") },
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // 制作过程
            OutlinedTextField(
                value = state.steps,
                onValueChange = { viewModel.handleIntent(ManualRecipeInputIntent.UpdateSteps(it)) },
                label = { Text("制作过程") },
                placeholder = { Text("请输入详细制作过程，每行一步") },
                maxLines = 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // 加载框
        if (state.isSaving || state.isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFF59E42))
            }
        }
    }
}

