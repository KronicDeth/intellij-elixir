package org.elixir_lang.psi.walk

import org.elixir_lang.psi.Body
import org.elixir_lang.psi.HeredocLineable
import org.elixir_lang.psi.HeredocLiteral
import org.elixir_lang.psi.Line

/**
 * The parts of a string, char list, heredoc or sigil. An expression in them sits inside an `ElixirInterpolation`, so a
 * walk that carries through an interpolation has to carry through these too. The literal variants (`~S`, `~W`, ...)
 * hold no interpolation and are met by no walk.
 */
object StringParts {
    val SHAPES: List<Class<*>> = listOf(
        Body::class.java,
        Line::class.java,
        HeredocLineable::class.java,
        HeredocLiteral::class.java
    )
}
