package org.xg.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.xg.project.data.remote.UploadService
import org.xg.project.data.remote.httpClient
import org.xg.project.domain.model.MealType
import io.github.ismoy.imagepickerkmp.domain.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.ImagePickerResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP


@Composable
fun ManualRecipeInputScreen(
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    var recipeName by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf(MealType.LUNCH) }
    var selectedImagePath by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadedImageUrl by remember { mutableStateOf<String?>(null) }
    
    val uploadService = remember { UploadService(httpClient) }
    val coroutineScope = rememberCoroutineScope()
    
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
            selectedImagePath = image?.uri
        }
        is ImagePickerResult.Error -> {
            println("图片选择错误: ${result.exception.message}")
        }
        else -> {}
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
                Text("返回", color = Color(0xFFF59E42))
            }
            Text("手动录入食谱", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Button(
                onClick = {
                    if (recipeName.isNotEmpty() && ingredients.isNotEmpty() && steps.isNotEmpty()) {
                        isUploading = true
                        coroutineScope.launch {
                            try {
                                // 上传图片（如果有）
                                if (selectedImagePath != null) {
                                    val filename = "recipe.jpg"
                                    val presignedUrlResponse = uploadService.getPresignedUrl(filename)
                                    // 这里需要将图片文件转换为ByteArray，实际项目中需要根据ImagePickerKMP的API来实现
                                    // 暂时使用空字节数组，实际项目中需要读取文件内容
                                    val imageBytes = ByteArray(0) // 暂时使用空字节数组
                                    val uploadResult = uploadService.uploadFile(
                                        presignedUrlResponse.data.putUrl,
                                        presignedUrlResponse.data.headers,
                                        imageBytes
                                    )
                                    if (uploadResult.success) {
                                        uploadedImageUrl = uploadResult.url
                                    }
                                }
                                
                                // 调用onSave回调
                                onSave()
                            } catch (e: Exception) {
                                println("保存失败: ${e.message}")
                            } finally {
                                isUploading = false
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E42))
            ) {
                Text("保存", color = Color.White)
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
                    if (selectedImagePath != null) {
                        AsyncImage(
                            model = selectedImagePath,
                            contentDescription = recipeName,
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
                value = recipeName,
                onValueChange = { recipeName = it },
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
                            onClick = { selectedMealType = mealType },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedMealType == mealType) Color(0xFFF59E42) else Color.White,
                                contentColor = if (selectedMealType == mealType) Color.White else Color(0xFF4B5563)
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
                value = duration,
                onValueChange = { duration = it },
                label = { Text("烹饪时间") },
                placeholder = { Text("例如：30分钟") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // 难度
            OutlinedTextField(
                value = difficulty,
                onValueChange = { difficulty = it },
                label = { Text("难度") },
                placeholder = { Text("例如：中等难度") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // 标签
            OutlinedTextField(
                value = tag,
                onValueChange = { tag = it },
                label = { Text("标签") },
                placeholder = { Text("例如：家常菜") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // 原材料
            OutlinedTextField(
                value = ingredients,
                onValueChange = { ingredients = it },
                label = { Text("原材料") },
                placeholder = { Text("请输入原材料，每行一种") },
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // 制作过程
            OutlinedTextField(
                value = steps,
                onValueChange = { steps = it },
                label = { Text("制作过程") },
                placeholder = { Text("请输入详细制作过程，每行一步") },
                maxLines = 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // 上传中加载框
        if (isUploading) {
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

