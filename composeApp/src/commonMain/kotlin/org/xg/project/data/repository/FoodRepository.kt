package org.xg.project.data.repository

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.xg.project.data.remote.httpClient
import org.xg.project.domain.model.DailyMenuRecord
import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.Recipe
import org.xg.project.domain.model.RecipeDraft
import org.xg.project.domain.model.RecipeDraftIngredient
import org.xg.project.domain.Result
// 模拟网络请求数据层
class FoodRepository {
    
    private val baseUrl = "http://localhost:8090/api"

    /**
     * 说明：
     * - 实际运行时后端可能没有启动/接口可能返回非 JSON（例如 404 HTML），这会导致 Ktor 在调用 body<T>() 时抛出 NoTransformationFoundException。
     * - 同时在部分环境下，泛型的 BaseResponse<T> 反序列化可能出现 “Serializer not found” 的问题。
     *
     * 因此这里统一用 JsonElement 先接住，再把 data 转成目标类型，避免因为响应头/序列化导致运行时报错。
     */
    @Serializable
    private data class BaseResponseJson(
        val success: Boolean,
        val data: JsonElement? = null,
        val message: String = ""
    )

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    private inline fun <reified T> BaseResponseJson.decodeData(): T {
        val element = data ?: JsonNull
        return json.decodeFromJsonElement<T>(element)
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

    suspend fun fetchDailyRecords(): Result<List<DailyMenuRecord>> {
        @Suppress("UNCHECKED_CAST")
        return safeApiCall<List<DailyMenuRecord>>(
            apiCall = { httpClient.get("$baseUrl/daily-records") }
        ) as Result<List<DailyMenuRecord>>
    }

    suspend fun fetchRecipes(): Result<List<Recipe>> {
        @Suppress("UNCHECKED_CAST")
        return safeApiCall<List<RecipeApiDto>>(
            apiCall = { httpClient.get("$baseUrl/recipe") },
            mapData = { list -> list.map { it.toRecipe() } }
        ) as Result<List<Recipe>>
    }

    suspend fun fetchTasteRadar(): Result<Map<String, Float>> {
        @Suppress("UNCHECKED_CAST")
        return safeApiCall<Map<String, Float>>(
            apiCall = { httpClient.get("$baseUrl/taste-radar") }
        ) as Result<Map<String, Float>>
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

    suspend fun createRecipe(recipeDraft: RecipeDraft): Result<CreateRecipeResponse> {
        @Suppress("UNCHECKED_CAST")
        return safeApiCall<CreateRecipeResponse>(
            apiCall = {
                httpClient.post("$baseUrl/recipe") {
                    contentType(ContentType.Application.Json)
                    setBody(recipeDraft.toCreateRecipeRequest())
                }
            }
        ) as Result<CreateRecipeResponse>
    }

    private fun RecipeApiDto.toRecipe(): Recipe {
        val parsedMealType = runCatching { MealType.valueOf(mealType) }
            .getOrDefault(MealType.BREAKFAST)

        val parsedIngredients = runCatching {
            json.decodeFromString<List<RecipeDraftIngredient>>(ingredients)
        }.getOrDefault(emptyList())

        val parsedSteps = runCatching {
            json.decodeFromString<List<String>>(steps)
        }.getOrDefault(emptyList())

        return Recipe(
            id = id,
            name = name,
            ingredients = parsedIngredients,
            steps = parsedSteps,
            duration = duration,
            difficulty = difficulty,
            tag = tag,
            mealType = parsedMealType,
            imageUrl = sanitizeUrl(imageUrl),
            submitter = submitter,
            submitTime = submitTime
        )
    }

    private suspend inline fun <reified T> safeApiCall(
        apiCall: () -> io.ktor.client.statement.HttpResponse,
        crossinline mapData: (T) -> Any = { it as Any }
    ): Result<Any> {
        return try {
            val httpResponse = apiCall()
            if (!httpResponse.status.isSuccess()) {
                val text = httpResponse.bodyAsText()
                Result.Error("接口请求失败：HTTP ${httpResponse.status.value} ${httpResponse.status.description}. 响应：$text")
            } else {
                val wrapper = httpResponse.body<BaseResponseJson>()
                if (wrapper.success) {
                    Result.Success(mapData(wrapper.decodeData<T>()))
                } else {
                    Result.Error(wrapper.message.ifBlank { "请求失败" })
                }
            }
        } catch (e: Exception) {
            println("Network request failed: ${e.message}")
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getAllRecipe(): Result<List<Recipe>> {
        @Suppress("UNCHECKED_CAST")
        return safeApiCall<List<RecipeApiDto>>(
            apiCall = { httpClient.get("$baseUrl/recipe") },
            mapData = { list -> list.map { it.toRecipe() } }
        ) as Result<List<Recipe>>
    }
}
