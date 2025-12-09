package org.elixir_lang.beam.psi.stubs

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.beam.psi.TypeDefinitionElement
import org.elixir_lang.beam.psi.impl.TypeDefinitionImpl
import org.elixir_lang.beam.psi.impl.TypeDefinitionStubImpl
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.type.Visibility
import java.io.IOException

class TypeDefinitionType(debugName: String) : ModuleElementType<TypeDefinitionStub<*>, TypeDefinition>(debugName) {
    override fun createCompositeNode(): ASTNode = TypeDefinitionElement(this)

    override fun createPsi(stub: TypeDefinitionStub<*>): TypeDefinition = TypeDefinitionImpl(stub)

    override fun createStub(psi: TypeDefinition, parentStub: StubElement<out PsiElement>): TypeDefinitionStub<*> =
        super.createStub(psi, parentStub)

    @Throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>): TypeDefinitionStub<*> {
        val visibility = Visibility.valueOf(dataStream.readNameString()!!)
        val name = dataStream.readNameString()!!
        val arity = dataStream.readVarInt()

        // FIX: Explicit generic type <TypeDefinition> and cast parentStub to ModuleStub<*>
        @Suppress("UNCHECKED_CAST")
        return TypeDefinitionStubImpl<TypeDefinition>(
            parentStub as ModuleStub<*>,
            visibility,
            name,
            arity
        )
    }

    @Throws(IOException::class)
    override fun serialize(stub: TypeDefinitionStub<*>, stubOutputStream: StubOutputStream) {
        stubOutputStream.writeName(stub.visibility.toString())
        stubOutputStream.writeName(stub.name)
        stubOutputStream.writeVarInt(stub.arity)
    }

    override fun indexStub(stub: TypeDefinitionStub<*>, sink: IndexSink) {
        sink.occurrence(AllName.KEY, stub.name)
    }
}
