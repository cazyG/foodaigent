package org.xg.project.data.repository

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.xg.project.data.remote.httpClient
import org.xg.project.domain.model.DailyMenuRecord
import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.MenuItemData
import org.xg.project.domain.model.Recipe
import org.xg.project.domain.model.RecipeDraft
import org.xg.project.domain.Result
// 模拟网络请求数据层
class FoodRepository {
    
    private val baseUrl = "http://localhost:8090/api"

    @Serializable
    data class BaseResponse<T>(
        val success: Boolean,
        val data: T,
        val message: String
    )

    suspend fun fetchDailyRecords(): Result<List<DailyMenuRecord>> {
        return try {
            val response = httpClient.get("$baseUrl/daily-records").body<BaseResponse<List<DailyMenuRecord>>>()
            if (response.success) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            println("Network request failed for daily-records: ${e.message}")
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun fetchRecipes(): Result<List<Recipe>> {
        return try {
            val response = httpClient.get("$baseUrl/recipes").body<BaseResponse<List<Recipe>>>()
            if (response.success) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            println("Network request failed for recipes: ${e.message}")
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun fetchTasteRadar(): Result<Map<String, Float>> {
        return try {
            val response = httpClient.get("$baseUrl/taste-radar").body<BaseResponse<Map<String, Float>>>()
            if (response.success) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            println("Network request failed for taste-radar: ${e.message}")
            Result.Error(e.message ?: "Unknown error")
        }
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
        return try {
            val request = recipeDraft.toCreateRecipeRequest()
            val response = httpClient.post("$baseUrl/recipe") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<BaseResponse<CreateRecipeResponse>>()
            
            if (response.success) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            println("Network request failed for create recipe: ${e}")
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getAllRecipe(): Result<List<RecipeDraft>> {
        return try {
            val response = httpClient.get("$baseUrl/recipe").body<BaseResponse<List<RecipeDraft>>>()
            if (response.success) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            println("Network request failed for create recipe: ${e}")
            Result.Error(e.message ?: "Unknown error")
        }
    }
}