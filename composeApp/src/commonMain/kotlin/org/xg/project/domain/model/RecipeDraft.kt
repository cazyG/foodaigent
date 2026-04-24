package org.xg.project.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@Serializable
data class RecipeDraftIngredient(
    val name: String,
    val number: String
)

@Serializable
data class RecipeDraft(
    val name: String,
    @Serializable(with = IngredientsSerializer::class)
    val ingredients: List<RecipeDraftIngredient>,
    @Serializable(with = StepsSerializer::class)
    val steps: List<String>,
    val duration: String,
    val difficulty: String,
    val tag: String,
    val mealType: String,
    val imageUrl: String?,
    val submitter: String? = null
) {
    object IngredientsSerializer : KSerializer<List<RecipeDraftIngredient>> {
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

    object StepsSerializer : KSerializer<List<String>> {
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
