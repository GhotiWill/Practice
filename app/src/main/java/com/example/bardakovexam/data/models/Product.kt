package com.example.bardakovexam.data.models

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String,
    val title: String,
    @SerializedName("category_id") val categoryId: String? = null,
    @SerializedName("cost") val cost: Double,
    val description: String,
    @SerializedName("is_best_seller") val isBestSeller: Boolean = false
) {
    fun photoUrl(): String = "https://dqltsitdzuqzcylqiukm.supabase.co/storage/v1/object/public/ProductPhotos/$id.png"
}
