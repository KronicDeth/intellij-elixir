package org.elixir_lang.beam.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.StubBasedPsiElement
import com.intellij.psi.impl.source.tree.TreeElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.util.IncorrectOperationException
import org.elixir_lang.NameArity
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.beam.psi.stubs.TypeDefinitionStub
import org.elixir_lang.semantic.type.Visibility

class TypeDefinitionImpl<T: TypeDefinitionStub<*>>(private val stub: T) : ModuleElementImpl(), TypeDefinition, StubBasedPsiElement<T> {
    override fun getProject(): Project = parent.project
    override fun getParent(): PsiElement = stub.parentStub.psi
    override fun getChildren(): Array<PsiElement> = emptyArray()

    override fun getNode(): ASTNode? = null


    override fun getName(): String = nameArity.name
    override fun setName(name: String): PsiElement = throw IncorrectOperationException()

    override fun getNameIdentifier(): PsiElement = this

    override val visibility: Visibility
        get() = TODO("Not yet implemented")

    override val nameArity: NameArity = stub.nameArity

    override fun appendMirrorText(buffer: StringBuilder, indentLevel: Int) {
        TODO("Not yet implemented")
    }

    override fun setMirror(element: TreeElement) {
        TODO("Not yet implemented")
    }

    override fun getElementType(): IStubElementType<out StubElement<*>, *> = stub.stubType
    override fun getStub(): T  = stub
}
