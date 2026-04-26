package org.elixir_lang.beam.decompiler

private const val BODY_NOT_DECOMPILED_COMMENT = "# body not decompiled"

fun appendNotDecompiledBody(decompiled: StringBuilder) {
    decompiled
        .append(" do\n")
        .append("    ")
        .append(BODY_NOT_DECOMPILED_COMMENT)
        .append("\n  end\n")
}
