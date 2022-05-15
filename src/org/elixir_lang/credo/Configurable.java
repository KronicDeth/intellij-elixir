package org.elixir_lang.credo;

import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Configurable implements SearchableConfigurable {
    private static final String ID = "Credo";
    private JPanel panel;
    private JTextField elixirArguments;
    private JTextField erlArguments;
    private EnvironmentVariablesComponent environmentVariablesComponent;
    private Service service;

    public Configurable(@NotNull Project project) {
        service = Service.getInstance(project);
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return ID;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return panel;
    }

    @Override
    public boolean isModified() {
        return !service.getElixirArguments().equals(elixirArguments.getText()) ||
                !service.getErlArguments().equals(erlArguments.getText()) ||
                !service.getEnvironmentVariableData().equals(environmentVariablesComponent.getEnvData());
    }

    @Override
    public void apply() {
        service.setElixirArguments(elixirArguments.getText());
        service.setErlArguments(erlArguments.getText());
        service.setEnvironmentVariableData(environmentVariablesComponent.getEnvData());
    }

    @Override
    public void reset() {
        elixirArguments.setText(service.getElixirArguments());
        erlArguments.setText(service.getErlArguments());
        environmentVariablesComponent.setEnvData(service.getEnvironmentVariableData());
    }

    @NotNull
    @Override
    public String getId() {
        return ID;
    }
}
