package com.example.calculator

data class Operation(
    val sign: Char,
    val func: (Double, Double) -> Double,
    val priority: Priority
)
