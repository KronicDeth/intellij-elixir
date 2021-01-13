package org.elixir_lang.beam.psi.impl

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
import org.elixir_lang.psi.call.Call
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

        val callDefinitionByArityByName = callDefinitionByArityByName(element)

        for (callDefinitionStub in callDefinitions()) {
            callDefinitionByArityByName[callDefinitionStub.exportedName()]
                    ?.get(callDefinitionStub.exportedArity())
                    ?.let { (callDefinitionStub as ModuleElementImpl).setMirror(SourceTreeToPsiMap.psiToTreeNotNull(it)) }
        }
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

    override fun getProject(): Project = mirror.project

    companion object {
        @Contract(pure = true)
        private fun callDefinitionByArityByName(mirror: TreeElement): Map<String, Map<Int, MaybeExported>> {
            val mirrorPsi = mirror.psi

            return if (mirrorPsi is Call) {
                val initialResolveState = ResolveState.initial().putInitialVisitedElement(mirrorPsi)
                val callDefinitionByArityByName = mutableMapOf<String, MutableMap<Int, MaybeExported>>()

                callDefinitionClauseCallWhile(mirrorPsi, initialResolveState) { call: Call?, accResolvedState: ResolveState? ->
                    if (call is MaybeExported) {
                        val maybeExportedCall = call as MaybeExported

                        maybeExportedCall
                                .exportedName()
                                ?.let { exportedName ->
                                    callDefinitionByArityByName
                                            .getOrPut(exportedName) { mutableMapOf() }[maybeExportedCall.exportedArity()] = maybeExportedCall
                                }
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
