package org.xg.project.data.repository

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.xg.project.data.remote.httpClient
import org.xg.project.data.model.BaseResponse
import org.xg.project.data.model.ResponseResult
import org.xg.project.domain.model.DailyMenuRecord
import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.RecipeDraft
import org.xg.project.domain.model.RecipeDraftIngredient
import org.xg.project.domain.Result
import org.xg.project.domain.model.RecipeMenu

// 模拟网络请求数据层
class FoodRepository {
    
    private val baseUrl = "http://43.167.217.211:8090/api"


    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    /**
     * /api/recipe 返回的实际结构里：
     * - ingredients / steps 是“JSON 字符串”（例如 "[{\"name\":\"xx\",\"number\":\"1\"}]")
     * - mealType 是字符串（DINNER/SNACK/...）
     * - imageUrl 里被包了一层 `...`（反引号）
     *
     * 因此不能直接反序列化成 RecipeDraft，需要先用 DTO 接住再转换。
     */
    @Serializable
    private data class RecipeApiDto(
        val id: Int,
        val name: String,
        val ingredients: String,
        val steps: String,
        val duration: String,
        val difficulty: String,
        val tag: String,
        val mealType: String,
        val imageUrl: String? = null,
        val submitter: String? = null,
        val submitTime: Long? = null
    )

    private fun sanitizeUrl(url: String?): String? =
        url?.trim()?.trim('`')?.takeIf { it.isNotBlank() }

    private fun RecipeApiDto.toRecipeDraft(): RecipeDraft {
        val parsedMealType = runCatching { MealType.valueOf(mealType) }
            .getOrDefault(MealType.BREAKFAST)

        val parsedIngredients = runCatching {
            json.decodeFromString<List<RecipeDraftIngredient>>(ingredients)
        }.getOrDefault(emptyList())

        val parsedSteps = runCatching {
            json.decodeFromString<List<String>>(steps)
        }.getOrDefault(emptyList())

        return RecipeDraft(
            name = name,
            ingredients = parsedIngredients,
            steps = parsedSteps,
            duration = duration,
            difficulty = difficulty,
            tag = tag,
            mealType = parsedMealType,
            imageUrl = sanitizeUrl(imageUrl)
        )
    }

    suspend fun fetchDailyRecords(): Result<List<DailyMenuRecord>> = runCatching {
        val res = httpClient.get("$baseUrl/daily-records").body<BaseResponse<List<DailyMenuRecord>>>()
        if (res.success) Result.Success(res.data) else Result.Error(res.message.ifBlank { "请求失败" })
    }.getOrElse { e ->
        println("Network request failed: ${e.message}")
        Result.Error(e.message ?: "Unknown error")
    }

    suspend fun fetchTasteRadar(): Result<Map<String, Float>> = runCatching {
        val res = httpClient.get("$baseUrl/taste-radar").body<BaseResponse<Map<String, Float>>>()
        if (res.success) Result.Success(res.data) else Result.Error(res.message.ifBlank { "请求失败" })
    }.getOrElse { e ->
        println("Network request failed: ${e.message}")
        Result.Error(e.message ?: "Unknown error")
    }


    @Serializable
    data class CreateRecipeResponse(
        val recipeId: Int
    )

    @Serializable
    data class CreateRecipeRequest(
        val name: String,
        val ingredients: String,
        val steps: String,
        val duration: String,
        val difficulty: String,
        val tag: String,
        val mealType: String,
        val imageUrl: String?,
        val submitter: String?,
    )

    @Serializable
    private data class IngredientPayload(
        val name: String,
        val number: String
    )

    private fun RecipeDraft.toCreateRecipeRequest(): CreateRecipeRequest {
        return CreateRecipeRequest(
            name = name,
            ingredients = Json.encodeToString(ingredients),
            steps = Json.encodeToString(steps),
            duration = duration,
            difficulty = difficulty,
            tag = tag,
            mealType = mealType.name,
            imageUrl = imageUrl,
            submitter = "admin"
        )
    }

    suspend fun createRecipe(recipeDraft: RecipeDraft): Result<CreateRecipeResponse> = runCatching {
        val request = recipeDraft.toCreateRecipeRequest()
        val res = httpClient.post("$baseUrl/recipe") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<BaseResponse<CreateRecipeResponse>>()
        if (res.success) Result.Success(res.data) else Result.Error(res.message.ifBlank { "请求失败" })
    }.getOrElse { e ->
        println("Network request failed: ${e.message}")
        Result.Error(e.message ?: "Unknown error")
    }


    suspend fun getAllRecipe(): ResponseResult<List<RecipeMenu>> = runCatching {
        val res = httpClient.get("$baseUrl/recipe").body<BaseResponse<List<RecipeMenu>>>()
        print("****->getAllRecipe   ${res.data}")
        if (res.success) ResponseResult.Success(res.data)
        else ResponseResult.Error(res.message.ifBlank { "请求失败" })
    }.getOrElse { e ->
        println("Network request failed: ${e.message}")
        ResponseResult.Error(e.message ?: "Unknown error")
    }
}
