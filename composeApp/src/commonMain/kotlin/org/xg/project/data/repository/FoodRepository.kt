package org.xg.project.data.repository

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
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

// 模拟网络请求数据层
class FoodRepository {
    
    // 是否使用真实的 Ktor 网络请求，目前你可以将这个开关打开，并填写真实的 API 地址
    private val useRealNetwork = true
    private val baseUrl = "http://localhost:8080/api"

    suspend fun fetchDailyRecords(): List<DailyMenuRecord> {
        try {
            return httpClient.get("$baseUrl/daily-records").body()
        } catch (e: Exception) {
            println("Network request failed for daily-records: ${e.message}")
            throw e // Re-throw the exception as we are no longer falling back to local data
        }
    }

    suspend fun fetchRecipes(): List<Recipe> {
        try {
            return httpClient.get("$baseUrl/recipes").body()
        } catch (e: Exception) {
            println("Network request failed for recipes: ${e.message}")
            throw e
        }
    }

    suspend fun fetchTasteRadar(): Map<String, Float> {
        try {
            return httpClient.get("$baseUrl/taste-radar").body()
        } catch (e: Exception) {
            println("Network request failed for taste-radar: ${e.message}")
            throw e
        }
    }

    @Serializable
    data class CreateRecipeRequest(
        val name: String,
        val ingredients: String,
        val steps: String,
        val duration: String,
        val difficulty: String,
        val tag: String,
        val mealType: String,
        val imageUrl: String?
    )

    @Serializable
    data class CreateRecipeResponse(
        val success: Boolean,
        val recipeId: Int,
        val message: String
    )

    @Serializable
    private data class IngredientPayload(
        val name: String,
        val number: String
    )

    private fun RecipeDraft.toCreateRecipeRequest(): CreateRecipeRequest {
        return CreateRecipeRequest(
            name = name,
            ingredients = Json.encodeToString(
                ingredients.map {
                    IngredientPayload(
                        name = it.name,
                        number = it.quantity
                    )
                }
            ),
            steps = Json.encodeToString(steps),
            duration = duration,
            difficulty = difficulty,
            tag = tag,
            mealType = mealType.name,
            imageUrl = imageUrl
        )
    }

    suspend fun createRecipe(recipeDraft: RecipeDraft): CreateRecipeResponse {
        try {
            val request = recipeDraft.toCreateRecipeRequest()
            return httpClient.post("$baseUrl/recipe") {
                setBody(request)
            }.body()
        } catch (e: Exception) {
            println("Network request failed for create recipe: ${e.message}")
            throw e
        }
    }
}