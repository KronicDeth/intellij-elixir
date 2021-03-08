package org.elixir_lang.beam.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import org.elixir_lang.psi.scope.putInitialVisitedElement
import org.elixir_lang.psi.Modular.callDefinitionClauseCallWhile
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.StubBasedPsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import org.elixir_lang.psi.call.MaybeExported
import org.elixir_lang.beam.psi.stubs.ModuleStubElementTypes
import com.intellij.util.IncorrectOperationException
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.SourceTreeToPsiMap
import com.intellij.psi.impl.source.tree.TreeElement
import org.elixir_lang.beam.psi.Module
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.getModuleName
import org.jetbrains.annotations.Contract
import org.jetbrains.annotations.NonNls
import java.lang.StringBuilder
import java.util.ArrayList

// See com.intellij.psi.impl.compiled.ClsClassImpl
class ModuleImpl<T : StubElement<*>?>(private val stub: T) : ModuleElementImpl(), Module, StubBasedPsiElement<T> {
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
    override fun getParent(): PsiElement = stub!!.getParentStub().psi

    override fun getElementType(): IStubElementType<*, *> = stub!!.getStubType()

    override fun getStub(): T = stub

    override fun appendMirrorText(buffer: StringBuilder, indentLevel: Int) {
    }

    override fun setMirror(element: TreeElement) {
        setMirrorCheckingType(element, null)

        val callDefinitionClauseByArityByName = callDefinitionClauseByArityByName(element)

        for (callDefinitionStub in callDefinitions()) {
            val name = callDefinitionStub.exportedName()
            val callDefinitionClauseByArity = callDefinitionClauseByArityByName[name]

            if (callDefinitionClauseByArity != null) {
                val arity = callDefinitionStub.exportedArity()
                val callDefinitionClause = callDefinitionClauseByArity[arity]

                if (callDefinitionClause != null) {
                    (callDefinitionStub as ModuleElementImpl).setMirror(SourceTreeToPsiMap.psiToTreeNotNull(callDefinitionClause))
                } else {
                    LOGGER.error("No decompiled source function with name/arity (${moduleName(element)}.${name}/${arity})")
                }
            } else {
                LOGGER.error("No decompiled source function with name (${moduleName(element)}.$name)")
            }
        }
    }

    private fun moduleName(element: TreeElement): String {
        return element.psi.let { it as? Call}?.getModuleName() ?: "<unknown module>"
    }

    private fun callDefinitions(): Array<MaybeExported> =
            getStub()!!.getChildrenByType(ModuleStubElementTypes.CALL_DEFINITION, emptyArray())

    /**
     * @return `null` if it does not have a canonical name OR if it has more than one canonical name
     */
    override fun canonicalName(): String? {
        assert(stub != null)
        return null
    }

    /**
     * @return empty set if no canonical names
     */
    override fun canonicalNameSet(): Set<String> {
        assert(stub != null)
        return emptySet()
    }

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

    override fun getProject(): Project {
        return parent.project
    }

    companion object {
        private val LOGGER = Logger.getInstance(ModuleImpl::class.java)

        @Contract(pure = true)
        private fun callDefinitionClauseByArityByName(mirror: TreeElement): Map<String, Map<Int, Call>> {
            val mirrorPsi = mirror.psi

            return if (mirrorPsi is Call) {
                val initialResolveState = ResolveState.initial().putInitialVisitedElement(mirrorPsi)
                val callDefinitionByArityByName = mutableMapOf<String, MutableMap<Int, Call>>()

                callDefinitionClauseCallWhile(mirrorPsi, initialResolveState) { call: Call, accResolvedState: ResolveState? ->
                    CallDefinitionClause.nameArityRange(call)?.let { nameArityRange ->
                        val callDefinitionByArity = callDefinitionByArityByName.getOrPut(nameArityRange.name) {
                            mutableMapOf()
                        }

                        nameArityRange.arityRange.forEach { arity -> callDefinitionByArity[arity] = call }
                    }

                    true
                }

                callDefinitionByArityByName
            } else {
                emptyMap()
            }
        }
    }
}
