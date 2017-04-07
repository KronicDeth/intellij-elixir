package org.elixir_lang.psi.scope;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.Function;
import org.elixir_lang.errorreport.Logger;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.Import;
import org.elixir_lang.psi.Modular;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.modular.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.psi.call.name.Module.KERNEL_SPECIAL_FORMS;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.macroChildCalls;

public abstract class CallDefinitionClause implements PsiScopeProcessor {
    /*
     * CONSTANTS
     */

    protected static final Key<Call> IMPORT_CALL = new Key<Call>("IMPORT_CALL");
    public static final Key<String> MODULAR_CANONICAL_NAME = new Key<String>("MODULAR_CANONICAL_NAME");

    /*
     * Public Instance Methods
     */

    /**
     * @param element candidate element.
     * @param state   current state of resolver.
     * @return false to stop processing.
     */
    @Override
    public boolean execute(@NotNull PsiElement element, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (element instanceof Call) {
            keepProcessing = execute((Call) element, state);
        } else if (element instanceof ElixirFile) {
            keepProcessing = implicitImports(element, state);
        }

        return keepProcessing;
    }

    @Nullable
    @Override
    public <T> T getHint(@NotNull Key<T> hintKey) {
        return null;
    }

    @Override
    public void handleEvent(@NotNull Event event, @Nullable Object associated) {
    }

    /*
     * Protected Instance Methods
     */

    /**
     * Called on every {@link Call} where {@link org.elixir_lang.structure_view.element.CallDefinitionClause#is} is
     * {@code true} when checking tree with {@link #execute(Call, ResolveState)}
     *
     * @return {@code true} to keep searching up tree; {@code false} to stop searching.
     */
    protected abstract boolean executeOnCallDefinitionClause(Call element, ResolveState state);

    /**
     * Whether to continue searching after each Module's children have been searched.
     *
     * @return {@code true} to keep searching up the PSI tree; {@code false} to stop searching.
     */
    protected abstract boolean keepProcessing();

    /*
     * Private Instance Methods
     */

    private boolean execute(@NotNull Call element, @NotNull final ResolveState state) {
        boolean keepProcessing = true;

        if (org.elixir_lang.structure_view.element.CallDefinitionClause.is(element)) {
            keepProcessing = executeOnCallDefinitionClause(element, state);
        } else if (Import.is(element)) {
            final ResolveState importState = state.put(IMPORT_CALL, element);

            try {
                Import.callDefinitionClauseCallWhile(
                        element,
                        new Function<Call, Boolean>() {
                            @Override
                            public Boolean fun(Call callDefinitionClause) {
                                return executeOnCallDefinitionClause(callDefinitionClause, importState);
                            }
                        }
                );
            } catch (StackOverflowError stackOverflowError) {
                Logger.error(CallDefinitionClause.class, "StackOverflowError while processing import", element);
            }
        } else if (Module.is(element)) {
            Call[] childCalls = macroChildCalls(element);

            if (childCalls != null) {
                for (Call childCall : childCalls) {
                    if (!execute(childCall, state)) {
                        break;
                    }
                }
            }

            // Only check MultiResolve.keepProcessing at the end of a Module to all multiple arities
            keepProcessing = keepProcessing();

            if (keepProcessing) {
                // the implicit `import Kernel` and `import Kernel.SpecialForms`
                keepProcessing = implicitImports(element, state);
            }
        }

        return keepProcessing;
    }

    private boolean implicitImports(@NotNull PsiElement element, @NotNull ResolveState state) {
        Project project = element.getProject();

        boolean keepProcessing = org.elixir_lang.reference.Module.forEachNavigationElement(
                project,
                KERNEL,
                new Function<PsiElement, Boolean>() {
                    @Override
                    public Boolean fun(PsiElement navigationElement) {
                        boolean keepProcessingNavigationElements = true;

                        if (navigationElement instanceof Call) {
                            Call modular = (Call) navigationElement;

                            keepProcessingNavigationElements = Modular.callDefinitionClauseCallWhile(
                                    modular,
                                    new Function<Call, Boolean>() {
                                        @Override
                                        public Boolean fun(Call callDefinitionClause) {
                                            return executeOnCallDefinitionClause(callDefinitionClause, state);
                                        }
                                    }
                            );
                        }

                        return keepProcessingNavigationElements;
                    }
                }
        );

        // the implicit `import Kernel.SpecialForms`
        if (keepProcessing) {
            ResolveState modularCanonicalNameState = state.put(MODULAR_CANONICAL_NAME, KERNEL_SPECIAL_FORMS);
            keepProcessing = org.elixir_lang.reference.Module.forEachNavigationElement(
                    project,
                    KERNEL_SPECIAL_FORMS,
                    new Function<PsiElement, Boolean>() {
                        @Override
                        public Boolean fun(PsiElement navigationElement) {
                            boolean keepProcessingNavigationElements = true;

                            if (navigationElement instanceof Call) {
                                Call modular = (Call) navigationElement;

                                keepProcessingNavigationElements = Modular.callDefinitionClauseCallWhile(
                                        modular,
                                        new Function<Call, Boolean>() {
                                            @Override
                                            public Boolean fun(Call callDefinitionClause) {
                                                return executeOnCallDefinitionClause(
                                                        callDefinitionClause,
                                                        modularCanonicalNameState
                                                );
                                            }
                                        }
                                );
                            }

                            return keepProcessingNavigationElements;
                        }
                    }
            );
        }

        return keepProcessing;
    }
}
