package org.elixir_lang.beam.psi.impl;

import com.intellij.util.io.StringRef;
import org.elixir_lang.beam.psi.CallDefinition;
import org.elixir_lang.beam.psi.stubs.CallDefinitionStub;
import org.elixir_lang.beam.psi.stubs.ModuleStub;
import org.elixir_lang.beam.psi.stubs.ModuleStubElementTypes;
import org.elixir_lang.psi.stub.call.Deserialized;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static org.elixir_lang.psi.call.name.Module.KERNEL;

/**
 * A call definition stub fakes a {@code def} or {@code defmacro}.
 */
public class CallDefinitionStubImpl<T extends CallDefinition> extends StubbicBase<T> implements CallDefinitionStub<T> {
    /*
     * CONSTANTS
     */

    /**
     * Arity of {@code def} or {@code defmacro} call, not the arity of the defined function or macro
     */
    public static final int RESOLVED_FINAL_ARITY = 2;

    public static final String RESOLVED_MODULE_NAME = KERNEL;

    /*
     * Fields
     */

    @NotNull
    private final String resolvedFunctionName;
    /**
     * Arity of the call definition being defined.  Needed to find source element.
     */
    private final int callDefinitionClauseHeadArity;

    public CallDefinitionStubImpl(@NotNull ModuleStub parentStub,
                                  @NotNull Deserialized deserialized,
                                  int callDefinitionClauseHeadArity) {
        super(parentStub, ModuleStubElementTypes.CALL_DEFINITION, deserialized.name.toString());

        StringRef resolvedModuleName = deserialized.resolvedModuleName;
        assert resolvedModuleName != null;
        assert resolvedModuleName.toString().equals(RESOLVED_MODULE_NAME);

        StringRef resolvedFunctionName = deserialized.resolvedFunctionName;
        assert resolvedFunctionName != null;

        this.resolvedFunctionName = resolvedFunctionName.toString();

        assert deserialized.resolvedFinalArity == RESOLVED_FINAL_ARITY;

        assert deserialized.hasDoBlockOrKeyword == HAS_DO_BLOCK_OR_KEYWORD;

        Set<StringRef> canonicalNameSet = deserialized.canonicalNameSet;
        assert canonicalNameSet.size() == 1;
        assert canonicalNameSet.iterator().next().toString().equals(this.getName());

        this.callDefinitionClauseHeadArity = callDefinitionClauseHeadArity;
    }

    public CallDefinitionStubImpl(@NotNull ModuleStub parentStub,
                                  @NotNull String macro,
                                  @NotNull String name,
                                  int callDefinitionClauseHeadArity) {
        super(parentStub, ModuleStubElementTypes.CALL_DEFINITION, name);
        this.resolvedFunctionName = macro;
        this.callDefinitionClauseHeadArity = callDefinitionClauseHeadArity;
    }

    @Override
    public int callDefinitionClauseHeadArity() {
        return callDefinitionClauseHeadArity;
    }

    /**
     * Arity of {@code def ... do} or {@code defmacro ... do}.
     *
     * @return 2 (1 for the call definition clause head and 1 for the do block)
     */
    @Override
    public Integer resolvedFinalArity() {
        return RESOLVED_FINAL_ARITY;
    }

    /**
     * @return name of the function/macro after taking into account any imports
     */
    @Nullable
    @Override
    public String resolvedFunctionName() {
        return resolvedFunctionName;
    }

    /**
     * @return {@link org.elixir_lang.psi.call.name.Module#KERNEL}
     */
    @Nullable
    @Override
    public String resolvedModuleName() {
        return RESOLVED_MODULE_NAME;
    }
}
