package com.example.data.model

data class Dhikr(
    val id: Int,
    val categoryId: String,
    val title: String,
    val text: String,
    val count: Int = 1,
    val source: String = "",
    val benefit: String = "",
    val audioUrl: String? = null
)

data class DhikrCategory(
    val id: String,
    val title: String,
    val description: String,
    val iconResName: String,
    val order: Int
)
