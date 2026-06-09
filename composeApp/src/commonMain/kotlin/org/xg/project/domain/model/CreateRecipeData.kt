package org.xg.project.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateRecipeData(
    val recipeId: Int,
)
