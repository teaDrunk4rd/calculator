package com.example.calculator

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

fun Double.toStringWithSpaces(): String {
    val parts = this.toString().split(".").toMutableList()

    val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH);
    formatSymbols.decimalSeparator = '.';
    formatSymbols.groupingSeparator = ' ';
    val formatter = DecimalFormat("###,###.#", formatSymbols)
    parts[0] = formatter.format(parts[0].toInt())

    return parts.joinToString(".")
}

fun Int.toStringWithSpaces(): String {
    val parts = this.toString().split(".").toMutableList()

    val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH);
    formatSymbols.decimalSeparator = '.';
    formatSymbols.groupingSeparator = ' ';
    val formatter = DecimalFormat("###,###.#", formatSymbols)
    parts[0] = formatter.format(parts[0].toInt())

    return parts.joinToString(".")
}