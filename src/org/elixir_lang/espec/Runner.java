package org.elixir_lang.espec;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import org.jetbrains.annotations.NotNull;

public final class Runner extends DefaultProgramRunner {
    private static final String MIX_RUNNER_ID = "MixESpecRunner";

    @NotNull
    @Override
    public String getRunnerId() {
        return MIX_RUNNER_ID;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return DefaultRunExecutor.EXECUTOR_ID.equals(executorId) && profile instanceof Configuration;
    }
}
