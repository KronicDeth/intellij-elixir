package org.elixir_lang.code

import com.ericsson.otp.erlang.OtpErlangAtom

fun sanitizeErlangVariableName(name: OtpErlangAtom): String = sanitizeErlangVariableName(name.atomValue())

fun sanitizeErlangVariableName(name: String): String {
    val lowered = name.replaceFirstChar { it.lowercase() }
    val trailingPunctuation = lowered.lastOrNull()?.takeIf { it == '?' || it == '!' }?.toString() ?: ""
    val stem = if (trailingPunctuation.isEmpty()) lowered else lowered.dropLast(1)

    val sanitizedStem = buildString(stem.length.coerceAtLeast(1)) {
        for ((index, character) in stem.withIndex()) {
            val isAllowed = if (index == 0) {
                character.isLowerCase() || character == '_'
            } else {
                character.isLetterOrDigit() || character == '_'
            }

            append(if (isAllowed) character else '_')
        }
    }.ifEmpty { "_" }

    return escapeElixirVariableKeyword("$sanitizedStem$trailingPunctuation")
}
