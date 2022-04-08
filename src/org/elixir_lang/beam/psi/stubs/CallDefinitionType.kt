package org.elixir_lang.beam.psi.stubs

import com.intellij.lang.ASTNode
import com.intellij.lang.LighterAST
import com.intellij.lang.LighterASTNode
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.elixir_lang.NameArity
import org.elixir_lang.beam.psi.CallDefinition
import org.elixir_lang.beam.psi.CallDefinitionElement
import org.elixir_lang.beam.psi.ModuleDefinition
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl
import org.elixir_lang.beam.psi.impl.CallDefinitionStubImpl
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.call.definition.clause.Visibility
import java.io.IOException

object CallDefinitionType : ModuleElementType<CallDefinitionStub<CallDefinition>, CallDefinition>("CallDefinition") {
    override fun createCompositeNode(): ASTNode = CallDefinitionElement(this)
    override fun createPsi(stub: CallDefinitionStub<CallDefinition>): CallDefinition = CallDefinitionImpl(stub)

    @Throws(IOException::class)
    override fun serialize(stub: CallDefinitionStub<CallDefinition>,
                           stubOutputStream: StubOutputStream) {
        stubOutputStream.writeName(stub.visibility.toString())
        stubOutputStream.writeName(stub.time.toString())

        val nameArity = stub.nameArity
        stubOutputStream.writeName(nameArity.name)
        stubOutputStream.writeVarInt(nameArity.arity)
    }

    @Throws(IOException::class)
    override fun deserialize(stubInputStream: StubInputStream,
                             parentStub: StubElement<*>): CallDefinitionStub<CallDefinition> {
        val visibility = Visibility.valueOf(stubInputStream.readNameString()!!)
        val time = Time.valueOf(stubInputStream.readNameString()!!)

        val name = stubInputStream.readNameString()!!
        val arity = stubInputStream.readVarInt()
        val nameArity = NameArity(name, arity)

        return CallDefinitionStubImpl((parentStub as ModuleDefinitionStub<ModuleDefinition, TypeDefinition, CallDefinition>), visibility, time, nameArity)
    }

    override fun createStub(tree: LighterAST, node: LighterASTNode, parentStub: StubElement<*>): CallDefinitionStub<CallDefinition> {
        TODO("Not yet implemented")
    }

    override fun indexStub(stub: CallDefinitionStub<CallDefinition>, sink: IndexSink) {
        val name = stub.nameArity.name
        sink.occurrence(AllName.KEY, name)
    }
}
