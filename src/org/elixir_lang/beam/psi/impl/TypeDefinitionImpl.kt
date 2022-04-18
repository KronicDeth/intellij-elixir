package org.elixir_lang.beam.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.StubBasedPsiElement
import com.intellij.psi.impl.source.tree.TreeElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.util.IncorrectOperationException
import org.elixir_lang.Arity
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.beam.psi.stubs.TypeDefinitionStub
import org.elixir_lang.type.Visibility
import org.jetbrains.annotations.NonNls

class TypeDefinitionImpl<T : TypeDefinitionStub<*>>(private val stub: T) : ModuleElementImpl(), TypeDefinition,
                                                                           StubBasedPsiElement<T> {
    override fun getProject(): Project = parent.project

    /**
     * Returns the array of children for the PSI element.
     * Important: In some implementations children are only composite elements, i.e. not a leaf elements
     *
     * @return the array of child elements.
     */
    override fun getChildren(): Array<PsiElement> = emptyArray()

    /**
     * Returns the parent of the PSI element.
     *
     * @return the parent of the element, or null if the element has no parent.
     */
    override fun getParent(): ModuleImpl<*> = stub.parentStub.psi as ModuleImpl<*>

    override fun getElementType(): IStubElementType<*, *> = stub.stubType

    override fun getStub(): T = stub

    override fun appendMirrorText(buffer: StringBuilder, indentLevel: Int) {}

    override fun setMirror(element: TreeElement) = setMirrorCheckingType(element, null)

    /**
     * @return `null` if it does not have a canonical name OR if it has more than one canonical name
     */
    override fun canonicalName(): String? = null

    /**
     * @return empty set if no canonical names
     */
    override fun canonicalNameSet(): Set<String> = emptySet()

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

    override val visibility: Visibility
        get() = stub.visibility

    override fun getName(): String = stub.name

    override val arity: Arity
        get() = stub.arity
}
