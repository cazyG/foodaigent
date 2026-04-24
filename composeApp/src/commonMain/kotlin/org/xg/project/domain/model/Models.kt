package org.xg.project.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MenuItemData(
    val name: String,
    val desc: String,
    val chef: String
)

@Serializable
data class DailyMenuRecord(
    val date: String,
    val breakfast: List<MenuItemData> = emptyList(),
    val lunch: List<MenuItemData> = emptyList(),
    val dinner: List<MenuItemData> = emptyList(),
    val snack: List<MenuItemData> = emptyList(),
    val stars: Float = 0f,
    val comment: String = "",
    val imgUrl: String? = null
)

@Serializable
enum class MealType(val title: String,name: String) {
    BREAKFAST("早餐","BREAKFAST"),
    LUNCH("午餐","LUNCH"),
    DINNER("晚餐","DINNER"),
    SNACK("宵夜","SNACK")
}

@Serializable
data class Recipe(
    val id: Int,
    val name: String,
    val duration: String,
    val difficulty: String,
    val tag: String,
    val mealType: MealType,
    val ingredients: List<RecipeDraftIngredient> = emptyList(),
    val steps: List<String> = emptyList(),
    val img: String? = null,
    val imageUrl: String? = null,
    val submitter: String? = null,
    val submitTime: Long? = null
)
