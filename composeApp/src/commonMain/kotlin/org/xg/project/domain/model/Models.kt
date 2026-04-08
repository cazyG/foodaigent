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
enum class MealType(val title: String) {
    BREAKFAST("早餐"),
    LUNCH("午餐"),
    DINNER("晚餐"),
    SNACK("宵夜")
}

@Serializable
data class Recipe(
    val id: Int,
    val name: String,
    val duration: String,
    val difficulty: String,
    val tag: String,
    val mealType: MealType,
    val img: String? = null
)
