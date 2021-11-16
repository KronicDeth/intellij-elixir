package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.childExpressions

data class Branches(val primary: ElixirStabBody?, val alternative: ElixirStabBody?) {
    val primaryChildExpressions: Sequence<PsiElement> = primary?.childExpressions().orEmpty()
    val alternativeChildExpressions: Sequence<PsiElement> = alternative?.childExpressions().orEmpty()

    constructor(call: Call) : this(call.doBlock)

    constructor(doBlock: ElixirDoBlock?) : this(doBlock?.stab?.stabBody, doBlock
            ?.blockList
            ?.blockItemList
            ?.firstOrNull { blockItem ->
                blockItem.blockIdentifier.textMatches("else")
            }
            ?.stab
            ?.stabBody)
}
