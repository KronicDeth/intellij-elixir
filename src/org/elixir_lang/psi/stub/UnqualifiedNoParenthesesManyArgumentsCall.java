package org.elixir_lang.psi.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.elixir_lang.psi.ElixirUnqualifiedNoParenthesesManyArgumentsCall;
import org.elixir_lang.psi.stub.call.Stub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public class UnqualifiedNoParenthesesManyArgumentsCall extends Stub<ElixirUnqualifiedNoParenthesesManyArgumentsCall> {
    public UnqualifiedNoParenthesesManyArgumentsCall(
            StubElement parent,
            @NotNull IStubElementType elementType,
            @Nullable StringRef resolvedModuleName,
            @Nullable StringRef resolvedFunctionName,
            int resolvedFinalArity,
            boolean hasDoBlockOrKeyword,
            @NotNull StringRef name,
            @NotNull Set<StringRef> canonicalNameSet
    ) {
        super(
                parent,
                elementType,
                resolvedModuleName,
                resolvedFunctionName,
                resolvedFinalArity,
                hasDoBlockOrKeyword,
                name,
                canonicalNameSet
        );
    }

    public UnqualifiedNoParenthesesManyArgumentsCall(
            StubElement parent,
            @NotNull IStubElementType elementType,
            @Nullable String resolvedModuleName,
            @Nullable String resolvedFunctionName,
            int resolvedFinalArity,
            boolean hasDoBlockOrKeyword,
            @NotNull String name,
            @NotNull Set<String> canonicalNameSet
    ) {
        super(
                parent,
                elementType,
                resolvedModuleName,
                resolvedFunctionName,
                resolvedFinalArity,
                hasDoBlockOrKeyword,
                name,
                canonicalNameSet
        );
    }
}
