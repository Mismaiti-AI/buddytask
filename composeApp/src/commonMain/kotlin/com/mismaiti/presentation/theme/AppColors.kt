package com.mismaiti.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Seed-based Color Palette using MaterialKolor
 *
 * MaterialKolor generates a full Material 3 color scheme from a single seed color.
 * This replaces the need to manually define 30+ color values.
 *
 * ## How to change the theme
 *
 * **Option A: Single seed color (recommended)**
 * Change [SeedColor] below — MaterialKolor derives the entire palette automatically.
 *
 * **Option B: From project-context.json (code generation)**
 * The backend replaces [SeedColor] with the color from `ui_design.primary_color`.
 *
 * **Option C: From hex string at runtime**
 * Use [seedColorFromHex] to parse a hex string from remote config.
 */

// ══════════════════════════════════════════════════════════════
// SEED COLOR — Change this single value to re-theme the entire app
// ══════════════════════════════════════════════════════════════

val SeedColor = Color(0xFF4C662B)

// ══════════════════════════════════════════════════════════════
// Utility — Parse hex string to seed color
// ══════════════════════════════════════════════════════════════

fun seedColorFromHex(hex: String): Color {
    val cleaned = hex.removePrefix("#")
    val argb = when (cleaned.length) {
        6 -> "FF$cleaned"
        8 -> cleaned
        else -> "FF4C662B" // fallback to default seed
    }
    return Color(argb.toLong(16))
}
