package com.khoros.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

private val interFamily = FontFamily.SansSerif

val Typography = Typography(
    headlineSmall = TextStyle(fontFamily = interFamily, fontSize = 30.sp),
    titleLarge = TextStyle(fontFamily = interFamily, fontSize = 22.sp),
    bodyLarge = TextStyle(fontFamily = interFamily, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = interFamily, fontSize = 14.sp)
)
