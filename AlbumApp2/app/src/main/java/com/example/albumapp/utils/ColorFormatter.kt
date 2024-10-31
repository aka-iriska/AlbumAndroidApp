package com.example.albumapp.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

fun colorToHex(color: Color): String {
    return "#%08X".format(color.toArgb()) // сохраняем в формате #AARRGGBB
}
fun hexToColor(hex: String): Color {
    return Color(android.graphics.Color.parseColor(hex))
}