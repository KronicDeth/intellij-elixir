package org.elixir_lang.beam.psi.stubs

import com.intellij.lang.ASTNode
import org.elixir_lang.psi.stub.type.Named.Companion.indexStubbic
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.stub.call.Stubbic
import java.lang.IllegalArgumentException

/**
 *
 * @param <S> The stub element that [.serialize] and
 * [.indexStub] should accept.  These stubs should be created by
 * [org.elixir_lang.beam.psi.BeamFileImpl.buildFileStub].
 * @param <P> The PSI element that should be returned by [.createPsi] in subclasses </P></S>
 * */
abstract class ModuleElementType<S : StubElement<*>?, P : PsiElement?>(id: String) : ModuleStubElementType<S, P>(id) {
    /**
     * @throws IllegalArgumentException [PsiElement] should never be created from [ASTNode] because
     * [S] is for binary [org.elixir_lang.beam.psi.BeamFileImpl].  Only
     * [createStub] should be used to create [PsiElement] from [S].
     */
    override fun createPsi(node: ASTNode): P {
        throw IllegalArgumentException(
                "ModuleElementType stubs should never have psi created from ASTNodes as they are only created from " +
                        "BeamFileImpl#buildFileStub and ModuleElementType#deserialize"
        )
    }
}
