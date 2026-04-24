package org.xg.project.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@Serializable
data class RecipeMenu(
    val id: Int,
    val name: String,
    val duration: String,
    val difficulty: String,
    val tag: String,
    val mealType: MealType,
    @Serializable(with = RecipeMenuIngredientsSerializer::class)
    val ingredients: List<RecipeMenuIngredients> = emptyList(),
    @Serializable(with = RecipeMenuStepsSerializer::class)
    val steps: List<String> = emptyList(),
    val img: String? = null,
    val imageUrl: String? = null,
    val submitter: String? = null,
    val submitTime: Long? = null
){
    object RecipeMenuIngredientsSerializer : KSerializer<List<RecipeDraftIngredient>> {
        override val descriptor: SerialDescriptor = Json
            .serializersModule
            .serializer<List<RecipeDraftIngredient>>()
            .descriptor

        override fun deserialize(decoder: Decoder): List<RecipeDraftIngredient> {
            val stringValue = decoder.decodeString()
            return if (stringValue.isBlank()) {
                emptyList()
            } else {
                Json.decodeFromString(stringValue)
            }
        }

        override fun serialize(encoder: Encoder, value: List<RecipeDraftIngredient>) {
            val jsonString = Json.encodeToString(value)
            encoder.encodeString(jsonString)
        }
    }

    object RecipeMenuStepsSerializer : KSerializer<List<String>> {
        override val descriptor: SerialDescriptor = Json
            .serializersModule
            .serializer<List<String>>()
            .descriptor

        override fun deserialize(decoder: Decoder): List<String> {
            val stringValue = decoder.decodeString()
            return if (stringValue.isBlank()) {
                emptyList()
            } else {
                Json.decodeFromString(stringValue)
            }
        }

        override fun serialize(encoder: Encoder, value: List<String>) {
            val jsonString = Json.encodeToString(value)
            encoder.encodeString(jsonString)
        }
    }
}
@Serializable
data class RecipeMenuIngredients(
    val name: String,
    val number: String
)
