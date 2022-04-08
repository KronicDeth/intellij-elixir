package org.elixir_lang.psi.stub.type

import com.intellij.openapi.util.text.StringUtil
import org.elixir_lang.psi.Variable.isDeclaration
import org.elixir_lang.psi.ElixirUnmatchedUnqualifiedNoArgumentsCall
import org.elixir_lang.psi.impl.ElixirUnmatchedUnqualifiedNoArgumentsCallImpl
import com.intellij.psi.stubs.StubElement
import java.io.IOException
import com.intellij.psi.stubs.StubInputStream
import org.elixir_lang.psi.Definition
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoArgumentsCall
import org.elixir_lang.psi.stub.call.Deserialized
import org.elixir_lang.psi.stub.type.call.Stub
import org.elixir_lang.semantic.enclosingModular

class UnmatchedUnqualifiedNoArgumentsCall(debugName: String) : Stub<UnmatchedUnqualifiedNoArgumentsCall?, ElixirUnmatchedUnqualifiedNoArgumentsCall?>(debugName) {
    override fun createPsi(stub: UnmatchedUnqualifiedNoArgumentsCall): ElixirUnmatchedUnqualifiedNoArgumentsCall =
        ElixirUnmatchedUnqualifiedNoArgumentsCallImpl(stub, this)

    override fun createStub(
        psi: ElixirUnmatchedUnqualifiedNoArgumentsCall,
        parentStub: StubElement<*>?
    ): UnmatchedUnqualifiedNoArgumentsCall =
        UnmatchedUnqualifiedNoArgumentsCall(
            parentStub,
            this,
            psi.resolvedModuleName(),
            psi.functionName(),
            psi.resolvedFinalArity(),
            psi.hasDoBlockOrKeyword(),
            StringUtil.notNullize(psi.name, "?"),
            psi.canonicalNameSet,
            definition(psi),
            psi.implementedProtocolName()
        )

    @Throws(IOException::class)
    override fun deserialize(
        dataStream: StubInputStream,
        parentStub: StubElement<*>?
    ): UnmatchedUnqualifiedNoArgumentsCall {
        val deserialized = Deserialized.deserialize(dataStream)
        return UnmatchedUnqualifiedNoArgumentsCall(parentStub, this, deserialized)
    }

    private fun definition(psi: ElixirUnmatchedUnqualifiedNoArgumentsCall): Definition? =
        if (isDeclaration(psi) && psi.enclosingModular is org.elixir_lang.semantic.quote.Call) {
            Definition.VARIABLE
        } else {
            null
        }
}
