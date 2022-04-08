package org.elixir_lang.beam.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.StubBasedPsiElement
import com.intellij.psi.impl.source.SourceTreeToPsiMap
import com.intellij.psi.impl.source.tree.TreeElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.util.IncorrectOperationException
import org.elixir_lang.beam.psi.CallDefinition
import org.elixir_lang.beam.psi.ModuleDefinition
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.beam.psi.stubs.CallDefinitionStub
import org.elixir_lang.beam.psi.stubs.ModuleDefinitionStub
import org.elixir_lang.beam.psi.stubs.TypeDefinitionStub
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.getModuleName
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.semantic.semantic
import org.jetbrains.annotations.Contract
import org.jetbrains.annotations.NonNls

// See com.intellij.psi.impl.compiled.ClsClassImpl
class ModuleDefinitionImpl(private val stub: ModuleDefinitionStub<ModuleDefinition, TypeDefinition, CallDefinition>) :
    ModuleElementImpl(), ModuleDefinition,
    StubBasedPsiElement<ModuleDefinitionStub<ModuleDefinition, TypeDefinition, CallDefinition>> {
    override fun getProject(): Project = parent.project
    override fun getParent(): PsiElement = stub.parentStub.psi
    override fun getChildren(): Array<PsiElement> = stub.childrenStubs.map(StubElement<*>::getPsi).toTypedArray()

    override fun getElementType(): IStubElementType<*, *> = stub.stubType
    override fun getStub(): ModuleDefinitionStub<ModuleDefinition, TypeDefinition, CallDefinition> = stub

    override fun appendMirrorText(buffer: StringBuilder, indentLevel: Int) {}
    override fun setMirror(element: TreeElement) {
        setMirrorCheckingType(element, null)

        val callDefinitionClauseByArityByName = callDefinitionClauseByArityByName(element)

        for (callDefinition in callDefinitions) {
            val nameArity = callDefinition.nameArity
            val name = nameArity.name
            val callDefinitionClauseByArity = callDefinitionClauseByArityByName[name]

            if (callDefinitionClauseByArity != null) {
                val arity = nameArity.arity
                val callDefinitionClause = callDefinitionClauseByArity[arity]

                if (callDefinitionClause != null) {
                    (callDefinition as ModuleElementImpl).setMirror(
                        SourceTreeToPsiMap.psiToTreeNotNull(
                            callDefinitionClause.psiElement
                        )
                    )
                } else if (callDefinition.visibility == Visibility.PUBLIC) {
                    LOGGER.error("No decompiled source function with name/arity (${moduleName(element)}.${name}/${arity})")
                }
            } else if (callDefinition.visibility == Visibility.PUBLIC) {
                LOGGER.error("No decompiled source function with name (${moduleName(element)}.$name)")
            }
        }
    }

    private fun moduleName(element: TreeElement): String =
        element.psi.let { it as? Call }?.getModuleName() ?: "<unknown module>"

    override fun setName(@NonNls name: String): PsiElement =
        throw IncorrectOperationException("Cannot modify module name in Beam files")

    override fun getName(): String = stub.name
    override fun getNameIdentifier(): PsiElement = this

    override fun getNavigationElement(): PsiElement = mirror
    override fun getNode(): ASTNode? = null

    override val typeDefinitions: List<TypeDefinition> =
        stub.childrenStubs.filterIsInstance<TypeDefinitionStub<TypeDefinition>>()
            .map(TypeDefinitionStub<TypeDefinition>::getPsi)
    override val callDefinitions: List<CallDefinition> =
        stub.childrenStubs.filterIsInstance<CallDefinitionStub<CallDefinition>>()
            .map(CallDefinitionStub<CallDefinition>::getPsi)

    companion object {
        private val LOGGER = Logger.getInstance(ModuleDefinitionImpl::class.java)

        @Contract(pure = true)
        private fun callDefinitionClauseByArityByName(mirror: TreeElement): Map<String, Map<Int, Clause>> {
            val mirrorPsi = mirror.psi

            return if (mirrorPsi is Call) {
                val callDefinitionClauseByArityByName = mutableMapOf<String, MutableMap<Int, Clause>>()

                mirrorPsi.semantic?.let { it as? org.elixir_lang.semantic.Modular }?.let { modular ->
                    modular.callDefinitions.forEach { callDefinition ->
                        callDefinition.nameArityInterval?.let { nameArityInterval ->
                            val callDefinitionClauseByArity =
                                callDefinitionClauseByArityByName.getOrPut(nameArityInterval.name) {
                                    mutableMapOf()
                                }

                            nameArityInterval.arityInterval.closed().forEach { arity ->
                                callDefinitionClauseByArity.computeIfAbsent(arity) {
                                    callDefinition.clauses.first()
                                }
                            }
                        }
                    }
                }

                callDefinitionClauseByArityByName
            } else {
                emptyMap()
            }
        }
    }
}
