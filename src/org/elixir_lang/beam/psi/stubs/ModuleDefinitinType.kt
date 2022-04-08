package org.elixir_lang.beam.psi.stubs

import com.intellij.lang.ASTNode
import com.intellij.lang.LighterAST
import com.intellij.lang.LighterASTNode
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.elixir_lang.beam.psi.CallDefinition
import org.elixir_lang.beam.psi.ModuleDefinition
import org.elixir_lang.beam.psi.ModuleElement
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.beam.psi.impl.ModuleDefinitionImpl
import org.elixir_lang.beam.psi.impl.ModuleDefinitionStubImpl
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.stub.call.Deserialized
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.psi.stub.index.ModularName
import org.elixir_lang.psi.stub.type.Named.Companion.indexStubbic
import java.io.IOException

object ModuleDefinitionType : ModuleElementType<ModuleDefinitionStub<ModuleDefinition, TypeDefinition, CallDefinition>, ModuleDefinition>("Module") {
    override fun createCompositeNode(): ASTNode = ModuleElement(this)
    override fun createPsi(stub: ModuleDefinitionStub<ModuleDefinition, TypeDefinition, CallDefinition>): ModuleDefinition = ModuleDefinitionImpl(stub)

    @Throws(IOException::class)
    override fun serialize(stub: ModuleDefinitionStub<ModuleDefinition, TypeDefinition, CallDefinition>,
                           stubOutputStream: StubOutputStream) {
        stubOutputStream.writeName(stub.name)
    }

    @Throws(IOException::class)
    override fun deserialize(stubInputStream: StubInputStream,
                             parentStub: StubElement<*>): ModuleDefinitionStub<ModuleDefinition, TypeDefinition, CallDefinition> {
        val name = stubInputStream.readNameString()!!
        return ModuleDefinitionStubImpl(parentStub, name)
    }

    override fun createStub(tree: LighterAST, node: LighterASTNode, parentStub: StubElement<*>): ModuleDefinitionStub<ModuleDefinition, TypeDefinition, CallDefinition> {
        TODO("Not yet implemented")
    }

    override fun indexStub(stub: ModuleDefinitionStub<ModuleDefinition, TypeDefinition, CallDefinition>, sink: IndexSink) {
        val name = stub.name
        sink.occurrence(AllName.KEY, name)
        sink.occurrence(ModularName.KEY, name)
    }
}
