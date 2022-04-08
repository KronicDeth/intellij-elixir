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
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.beam.psi.TypeDefinitionElement
import org.elixir_lang.beam.psi.impl.TypeDefinitionImpl
import org.elixir_lang.beam.psi.impl.TypeDefinitionStubImpl
import org.elixir_lang.psi.stub.index.AllName
import java.io.IOException

object TypeDefinitionType : ModuleElementType<TypeDefinitionStub<TypeDefinition>, TypeDefinition>("TypeDefinition") {
    override fun createCompositeNode(): ASTNode = TypeDefinitionElement(this)
    override fun createPsi(stub: TypeDefinitionStub<TypeDefinition>): TypeDefinition = TypeDefinitionImpl(stub)

    @Throws(IOException::class)
    override fun deserialize(stubInputStream: StubInputStream,
                             parentStub: StubElement<*>): TypeDefinitionStub<TypeDefinition> {
        val visibility = stubInputStream.readName()
        val name = stubInputStream.readName()
        val arity = stubInputStream.readVarInt()

        return TypeDefinitionStubImpl((parentStub as ModuleDefinitionStub<ModuleDefinition, TypeDefinition, CallDefinition>), visibility!!, name!!, arity)
    }

    @Throws(IOException::class)
    override fun serialize(stub: TypeDefinitionStub<TypeDefinition>,
                           stubOutputStream: StubOutputStream) {
        stubOutputStream.writeName(stub.visibility.toString())
        val (name, arity) = stub.nameArity
        stubOutputStream.writeName(name)
        stubOutputStream.writeVarInt(arity)
    }

    override fun createStub(tree: LighterAST, node: LighterASTNode, parentStub: StubElement<*>): TypeDefinitionStub<TypeDefinition> {
        TODO("Not yet implemented")
    }

    override fun indexStub(stub: TypeDefinitionStub<TypeDefinition>, sink: IndexSink) {
        val name = stub.nameArity.name
        sink.occurrence(AllName.KEY, name)
    }
}
