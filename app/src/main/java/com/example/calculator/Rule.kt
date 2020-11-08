package com.example.calculator

import android.widget.Button

data class Rule(
    val condition: (Button) -> Boolean,
    val action: ((Button) -> Unit)?
)