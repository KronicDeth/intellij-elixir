package org.elixir_lang.credo;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Configurable implements SearchableConfigurable {
    private static final String ID = "Credo";
    private JPanel panel;
    private JCheckBox includeExplanationCheckBox;
    private Service service;

    public Configurable(@NotNull Project project) {
        service = Service.getInstance(project);
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return ID;
    }

    // MUST be defined in 2016.3.7
    @Nullable
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return panel;
    }

    @Override
    public boolean isModified() {
        return service.includeExplanation() != includeExplanationCheckBox.getModel().isSelected();
    }

    @Override
    public void apply() {
        service.includeExplanation(includeExplanationCheckBox.getModel().isSelected());
    }

    @Override
    public void reset() {
        includeExplanationCheckBox.getModel().setSelected(service.includeExplanation());
    }

    @NotNull
    @Override
    public String getId() {
        return ID;
    }
}
