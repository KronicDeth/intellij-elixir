package org.elixir_lang.configuration;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.elixir_lang.jps.shared.CompilerOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by zyuyou on 15/7/6.
 */
@State(
        name = CompilerOptions.COMPONENT_NAME,
        storages = {
                @Storage(value = "compiler.xml")
        }
)
public class ElixirCompilerSettings implements PersistentStateComponent<CompilerOptions> {
    private CompilerOptions myCompilerOptions = new CompilerOptions();

    @Nullable
    @Override
    public CompilerOptions getState() {
        return myCompilerOptions;
    }

    @Override
    public void loadState(@NotNull CompilerOptions state) {
        myCompilerOptions = state;
    }

    @NotNull
    public static ElixirCompilerSettings getInstance(@NotNull Project project) {
        ElixirCompilerSettings persisted = project.getService(ElixirCompilerSettings.class);
        return persisted != null ? persisted : new ElixirCompilerSettings();
    }

    /* use mix-compiler */
    public boolean isUseMixCompilerEnabled() {
        return myCompilerOptions.useMixCompiler;
    }

    public void setUseMixCompilerEnabled(boolean useMixCompiler) {
        myCompilerOptions.useMixCompiler = useMixCompiler;
    }

    /* attach docs */
    public boolean isAttachDocsEnabled() {
        return myCompilerOptions.attachDocsEnabled;
    }

    public void setAttachDocsEnabled(boolean useDocs) {
        myCompilerOptions.attachDocsEnabled = useDocs;
    }

    /* attach debug-info */
    public boolean isAttachDebugInfoEnabled() {
        return myCompilerOptions.attachDebugInfoEnabled;
    }

    public void setAttachDebugInfoEnabled(boolean useDebugInfo) {
        myCompilerOptions.attachDebugInfoEnabled = useDebugInfo;
    }

    /* warnings-as-errors */
    public boolean isWarningsAsErrorsEnabled() {
        return myCompilerOptions.warningsAsErrorsEnabled;
    }

    public void setWarningsAsErrorsEnabled(boolean useWarningsAsErrors) {
        myCompilerOptions.warningsAsErrorsEnabled = useWarningsAsErrors;
    }

    /* ignore-module-conflict */
    public boolean isIgnoreModuleConflictEnabled() {
        return myCompilerOptions.ignoreModuleConflictEnabled;
    }

    public void setIgnoreModuleConflictEnabled(boolean useIgnoreModuleConflict) {
        myCompilerOptions.ignoreModuleConflictEnabled = useIgnoreModuleConflict;
    }

}
