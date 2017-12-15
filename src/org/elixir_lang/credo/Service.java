package org.elixir_lang.credo;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@com.intellij.openapi.components.State(
    name = "Credo",
    storages = {
        @Storage(file = StoragePathMacros.PROJECT_FILE),
        @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/" + "credo.xml", scheme = StorageScheme.DIRECTORY_BASED)
    }
)
public class Service implements PersistentStateComponent<State> {
    @NotNull
    private State state = new State();

    @NotNull
    public static Service getInstance(@NotNull Project project) {
        Service persisted = ServiceManager.getService(project, Service.class);
        Service service;

        if (persisted != null) {
            service = persisted;
        } else {
            service = new Service();
        }

        return service;
    }

    @Nullable
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(State state) {
        this.state = state;
    }

    public boolean includeExplanation() {
        return state.includeExplanation;
    }

    public void includeExplanation(boolean includeExplanation) {
        state.includeExplanation = includeExplanation;
    }
}
