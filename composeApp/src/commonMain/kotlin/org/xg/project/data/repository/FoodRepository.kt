package org.xg.project.data.repository

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.xg.project.data.remote.httpClient
import org.xg.project.domain.model.DailyMenuRecord
import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.MenuItemData
import org.xg.project.domain.model.Recipe

// 模拟网络请求数据层
class FoodRepository {
    
    // 是否使用真实的 Ktor 网络请求，目前你可以将这个开关打开，并填写真实的 API 地址
    private val useRealNetwork = false
    private val baseUrl = "https://your-api-domain.com/api"

    suspend fun fetchDailyRecords(): List<DailyMenuRecord> {
        if (useRealNetwork) {
            try {
                return httpClient.get("$baseUrl/daily-records").body()
            } catch (e: Exception) {
                // 网络请求失败时降级走本地模拟数据
                println("Network request failed for daily-records: ${e.message}, falling back to local data.")
            }
        }
        
        delay(800) // 模拟网络延迟
        return listOf(
            DailyMenuRecord(
                date = "2026.04.08 (今天)",
                breakfast = listOf(
                    MenuItemData("全麦欧包 & 煎蛋", "配料：黑咖啡、蓝莓、无糖酸奶", "丈夫掌勺"),
                    MenuItemData("全麦欧包 & 煎蛋1", "配料：黑咖啡、蓝莓、无糖酸奶", "丈夫掌勺"),
                    MenuItemData("全麦欧包 & 煎蛋2", "配料：黑咖啡、蓝莓、无糖酸奶", "丈夫掌勺")
                ),
                lunch = listOf(
                    MenuItemData("清蒸鲈鱼 & 蚝油生菜", "配料：糙米饭、排骨海带汤", "妻子掌勺")
                ),
                dinner = emptyList(),
                snack = emptyList(),
                stars = 0f,
                comment = ""
            ),
            DailyMenuRecord(
                date = "2026.04.07 (昨天)",
                breakfast = listOf(MenuItemData("包子", "肉包", "妻子掌勺")),
                lunch = listOf(MenuItemData("酸汤肥牛", "配料：金针菇", "老公掌勺")),
                dinner = listOf(MenuItemData("麻辣香锅", "配料：藕片", "老公掌勺")),
                snack = emptyList(),
                stars = 5f,
                comment = "“老公大展身手的一次，辣度刚刚好，藕片很清脆！”",
                imgUrl = "https://modao.cc/agent-py/media/generated_images/2026-03-30/8801c842ff4c4601b0eaef5a8bb46f63.jpg"
            ),
            DailyMenuRecord(
                date = "2026.04.06",
                breakfast = emptyList(),
                lunch = listOf(MenuItemData("外卖打卡（披萨）", "榴莲披萨", "无")),
                dinner = emptyList(),
                snack = emptyList(),
                stars = 4f,
                comment = "“加班太累了，今天不做饭。这家榴莲披萨料好足。”"
            ),
            DailyMenuRecord(
                date = "2026.04.05",
                breakfast = emptyList(),
                lunch = listOf(MenuItemData("外卖打卡（披萨）", "榴莲披萨", "无")),
                dinner = emptyList(),
                snack = emptyList(),
                stars = 4f,
                comment = "“加班太累了，今天不做饭。这家榴莲披萨料好足。”"
            ),
            DailyMenuRecord(
                date = "2026.04.04",
                breakfast = emptyList(),
                lunch = listOf(MenuItemData("外卖打卡（披萨）", "榴莲披萨", "无")),
                dinner = emptyList(),
                snack = emptyList(),
                stars = 4f,
                comment = "“加班太累了，今天不做饭。这家榴莲披萨料好足。”"
            )
        )
    }

    suspend fun fetchRecipes(): List<Recipe> {
        if (useRealNetwork) {
            try {
                return httpClient.get("$baseUrl/recipes").body()
            } catch (e: Exception) {
                // 网络请求失败时降级走本地模拟数据
                println("Network request failed for recipes: ${e.message}, falling back to local data.")
            }
        }

        delay(800) // 模拟网络延迟
        return listOf(
            Recipe(1, "经典红烧肉", "45分钟", "中等难度", "老公爱吃", MealType.LUNCH, "https://modao.cc/agent-py/media/generated_images/2026-03-30/8801c842ff4c4601b0eaef5a8bb46f63.jpg"),
            Recipe(2, "牛油果大虾沙拉", "15分钟", "新手入门", "老婆最爱", MealType.LUNCH),
            Recipe(3, "西红柿炒鸡蛋", "10分钟", "必点基础", "", MealType.DINNER),
            Recipe(4, "秘制宫保鸡丁", "25分钟", "挑战厨艺", "", MealType.DINNER),
            Recipe(5, "孔雀开屏清蒸鱼", "20分钟", "颜值极高", "低脂健康", MealType.DINNER),
            Recipe(6, "正宗麻婆豆腐", "15分钟", "下饭神器", "", MealType.LUNCH),
            Recipe(7, "冬瓜薏米排骨汤", "90分钟", "滋补养生", "", MealType.DINNER),
            Recipe(8, "蒜蓉粉丝蒸大虾", "20分钟", "宴客之选", "", MealType.DINNER),
            Recipe(9, "全麦三明治", "10分钟", "新手入门", "减脂", MealType.BREAKFAST),
            Recipe(10, "燕麦水果酸奶碗", "5分钟", "简单", "快手", MealType.BREAKFAST),
            Recipe(11, "煎饺", "15分钟", "中等难度", "中式", MealType.BREAKFAST),
            Recipe(12, "烤鸡翅", "25分钟", "简单", "宵夜最爱", MealType.SNACK),
            Recipe(13, "芝士焗红薯", "20分钟", "简单", "甜品", MealType.SNACK)
        )
    }

    suspend fun fetchTasteRadar(): Map<String, Float> {
        if (useRealNetwork) {
            try {
                return httpClient.get("$baseUrl/taste-radar").body()
            } catch (e: Exception) {
                // 网络请求失败时降级走本地模拟数据
                println("Network request failed for taste-radar: ${e.message}, falling back to local data.")
            }
        }

        delay(300)
        return mapOf(
            "辣" to 0.8f,
            "甜" to 0.4f,
            "咸" to 0.6f,
            "酸" to 0.3f,
            "鲜" to 0.7f,
            "苦" to 0.1f
        )
    }

    @Serializable
    data class CreateRecipeRequest(
        val name: String,
        val ingredients: String,
        val steps: String,
        val duration: String,
        val difficulty: String,
        val tag: String,
        val mealType: MealType,
        val imageUrl: String?
    )

    @Serializable
    data class CreateRecipeResponse(
        val success: Boolean,
        val recipeId: Int,
        val message: String
    )

    suspend fun createRecipe(
        name: String,
        ingredients: String,
        steps: String,
        duration: String,
        difficulty: String,
        tag: String,
        mealType: MealType,
        imageUrl: String?
    ): CreateRecipeResponse {
        if (useRealNetwork) {
            try {
                val request = CreateRecipeRequest(
                    name = name,
                    ingredients = ingredients,
                    steps = steps,
                    duration = duration,
                    difficulty = difficulty,
                    tag = tag,
                    mealType = mealType,
                    imageUrl = imageUrl
                )
                
                return httpClient.post("$baseUrl/recipes") {
                    setBody(request)
                }.body()
            } catch (e: Exception) {
                println("Network request failed for create recipe: ${e.message}")
                throw e
            }
        }

        // 模拟网络延迟和成功响应
        delay(500)
        return CreateRecipeResponse(
            success = true,
            recipeId = (System.currentTimeMillis() % 1000).toInt(),
            message = "食谱创建成功"
        )
    }
}