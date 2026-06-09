package org.xg.project.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int? = null,
    val name: String,
    val description: String? = null,
    val price: Double,
    val stock: Int,
    val userId: Int,
)

@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String? = null,
    val price: Double,
    val stock: Int,
)
