package org.elixir_lang.beam.psi.impl

import com.intellij.lang.ASTNode
import org.elixir_lang.beam.psi.stubs.CallDefinitionStub
import com.intellij.psi.StubBasedPsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.TreeElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.util.IncorrectOperationException
import org.elixir_lang.beam.psi.CallDefinition
import org.jetbrains.annotations.Contract
import org.jetbrains.annotations.NonNls
import java.lang.StringBuilder

class CallDefinitionImpl<T : CallDefinitionStub<*>>(private val stub: T) : ModuleElementImpl(), CallDefinition, StubBasedPsiElement<T> {
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
    override fun getParent(): PsiElement = stub.parentStub.psi

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

    /**
     * @return `true`
     */
    @Contract(pure = true)
    override fun isExported(): Boolean = true

    /**
     * The arity of the function or macro that was exported into the compiled .beam file
     */
    override fun exportedArity(): Int = stub.callDefinitionClauseHeadArity()

    /**
     * The name that was exported into the compiled .beam file
     */
    override fun exportedName(): String = stub.name
}
