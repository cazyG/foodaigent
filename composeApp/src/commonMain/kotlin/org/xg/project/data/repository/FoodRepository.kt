package org.xg.project.data.repository

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import org.xg.project.data.remote.ApiConfig
import org.xg.project.data.remote.decodeBaseResponse
import org.xg.project.data.remote.httpClient
import org.xg.project.data.remote.toUserFriendlyNetworkMessage
import org.xg.project.data.model.ResponseResult
import org.xg.project.domain.model.DailyMenuRecord
import org.xg.project.domain.model.MealType
import org.xg.project.domain.model.MenuItemData
import org.xg.project.domain.model.CreateRecipeData
import org.xg.project.domain.model.RecipeDraft
import org.xg.project.domain.Result
import org.xg.project.domain.model.RecipeMenu

class FoodRepository {

    private val baseUrl = ApiConfig.API_BASE_URL


    suspend fun fetchDailyRecords(): Result<List<DailyMenuRecord>> {
        val remoteResult = fetchDailyRecordsFromRemote()
        if (remoteResult is Result.Success) {
            return remoteResult
        }
        val fallbackResult = buildDailyRecordsFromRecipes()
        if (fallbackResult is Result.Success) {
            return fallbackResult
        }
        return Result.Success(listOf(emptyTodayRecord()))
    }

    private suspend fun fetchDailyRecordsFromRemote(): Result<List<DailyMenuRecord>> {
        return try {
            val response = httpClient.get("$baseUrl/daily-records")
            when (response.status) {
                HttpStatusCode.NotFound -> Result.Error("daily-records not found", canRetry = false)
                else -> response.decodeBaseResponse()
            }
        } catch (e: Exception) {
            Result.Error(toUserFriendlyNetworkMessage(e), canRetry = true)
        }
    }

    private suspend fun buildDailyRecordsFromRecipes(): Result<List<DailyMenuRecord>> {
        return when (val recipesResult = fetchRecipes()) {
            is Result.Success -> {
                Result.Success(listOf(recipesResult.data.toDailyMenuRecord(todayLabel())))
            }
            is Result.Error -> recipesResult
        }
    }

    private suspend fun fetchRecipes(): Result<List<RecipeMenu>> {
        return try {
            val response = httpClient.get("$baseUrl/recipe")
            response.decodeBaseResponse()
        } catch (e: Exception) {
            Result.Error(toUserFriendlyNetworkMessage(e))
        }
    }

    suspend fun fetchTasteRadar(): Result<Map<String, Float>> {
        return try {
            val response = httpClient.get("$baseUrl/taste-radar")
            when (response.status) {
                HttpStatusCode.NotFound -> Result.Success(emptyMap())
                else -> response.decodeBaseResponse()
            }
        } catch (e: Exception) {
            Result.Error(toUserFriendlyNetworkMessage(e))
        }
    }

    suspend fun createRecipe(recipeDraft: RecipeDraft): Result<CreateRecipeData> {
        return try {
            val response = httpClient.post("$baseUrl/recipe") {
                contentType(ContentType.Application.Json)
                setBody(recipeDraft)
            }
            response.decodeBaseResponse()
        } catch (e: Exception) {
            Result.Error(toUserFriendlyNetworkMessage(e))
        }
    }

    suspend fun getAllRecipe(): ResponseResult<List<RecipeMenu>> =
        when (val result = fetchRecipes()) {
            is Result.Success -> ResponseResult.Success(result.data)
            is Result.Error -> ResponseResult.Error(result.message)
        }

    private fun todayLabel(): String {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return "${today.year}年${today.month.number}月${today.day}日"
    }

    private fun emptyTodayRecord(): DailyMenuRecord = DailyMenuRecord(
        date = todayLabel(),
        breakfast = emptyList(),
        lunch = emptyList(),
        dinner = emptyList(),
        snack = emptyList(),
    )

}

private fun List<RecipeMenu>.toDailyMenuRecord(date: String): DailyMenuRecord {
    val grouped = groupBy { it.mealType }
    val heroImage = firstOrNull { !it.imageUrl.isNullOrBlank() }?.imageUrl
        ?: firstOrNull { !it.img.isNullOrBlank() }?.img
    return DailyMenuRecord(
        date = date,
        breakfast = grouped.menuItemsFor(MealType.BREAKFAST),
        lunch = grouped.menuItemsFor(MealType.LUNCH),
        dinner = grouped.menuItemsFor(MealType.DINNER),
        snack = grouped.menuItemsFor(MealType.SNACK),
        imgUrl = heroImage,
    )
}

private fun Map<MealType, List<RecipeMenu>>.menuItemsFor(mealType: MealType): List<MenuItemData> =
    this[mealType].orEmpty().map { it.toMenuItemData() }

private fun RecipeMenu.toMenuItemData(): MenuItemData {
    val description = listOf(duration, tag, difficulty)
        .filter { it.isNotBlank() }
        .joinToString(" · ")
    return MenuItemData(
        name = name,
        desc = description,
        chef = submitter?.takeIf { it.isNotBlank() } ?: "小厨",
    )
}
