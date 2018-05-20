package org.elixir_lang.debugger.line_breakpoint.availability_processor

import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.debugger.line_breakpoint.AvailabilityProcessor
import org.elixir_lang.psi.ElixirTypes

class EEx : AvailabilityProcessor() {
    override fun process(psiElement: PsiElement): Boolean =
            when (psiElement.language) {
                ElixirLanguage ->
                    if (psiElement is LeafPsiElement) {
                        when (psiElement.elementType) {
                            ElixirTypes.COMMENT,
                            ElixirTypes.EEX_CLOSING,
                            ElixirTypes.EEX_COMMENT,
                            ElixirTypes.EEX_COMMENT_MARKER,
                            ElixirTypes.EEX_DATA,
                            ElixirTypes.EEX_EMPTY_MARKER,
                            ElixirTypes.EEX_ESCAPED_OPENING,
                            ElixirTypes.EEX_FORWARD_SLASH_MARKER,
                            ElixirTypes.EEX_OPENING,
                            ElixirTypes.EEX_PIPE_MARKER,
                            TokenType.WHITE_SPACE -> true
                            else -> {
                                isAvailable = true

                                false
                            }
                        }
                    } else {
                        true
                    }
                else -> true
            }

}
