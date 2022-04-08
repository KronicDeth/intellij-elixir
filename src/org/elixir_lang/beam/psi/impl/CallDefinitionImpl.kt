package org.elixir_lang.beam.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.StubBasedPsiElement
import com.intellij.psi.impl.source.tree.TreeElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.util.IncorrectOperationException
import org.elixir_lang.NameArity
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.beam.psi.CallDefinition
import org.elixir_lang.beam.psi.stubs.CallDefinitionStub
import org.elixir_lang.semantic.call.definition.clause.Time
import org.jetbrains.annotations.NonNls

class CallDefinitionImpl<T : CallDefinitionStub<*>>(private val stub: T) : ModuleElementImpl(), CallDefinition, StubBasedPsiElement<T> {
    override fun getProject(): Project = parent.project
    override fun getParent(): PsiElement = stub.parentStub.psi
    override fun getChildren(): Array<PsiElement> = emptyArray()

    override fun getElementType(): IStubElementType<*, *> = stub.stubType
    override fun getStub(): T = stub

    override fun appendMirrorText(buffer: StringBuilder, indentLevel: Int) {}
    override fun setMirror(element: TreeElement) = setMirrorCheckingType(element, null)

    override val visibility: Visibility = stub.visibility
    override val time: Time = stub.time
    override fun getName(): String = nameArity.name
    override val nameArity: NameArity = stub.nameArity

    override fun getNameIdentifier(): PsiElement = this

    /**
     * Renames the element.
     *
     * @param name the new element name.
     * @return the element corresponding to this element after the rename (either `this`
     * or a different element if the rename caused the element to be replaced).
     * @throws IncorrectOperationException if the modification is not supported or not possible for some reason.
     */
    override fun setName(@NonNls name: String): PsiElement =
            throw IncorrectOperationException("Cannot modify module name in Beam files")

    override fun getNavigationElement(): PsiElement = mirror

    override fun getNode(): ASTNode? = null
}
