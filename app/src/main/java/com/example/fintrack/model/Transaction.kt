package com.example.fintrack.model

data class Transaction(
    val amount: Double,
    val type: String,
    val category: String,
    val title: String,
    val date: String
)
