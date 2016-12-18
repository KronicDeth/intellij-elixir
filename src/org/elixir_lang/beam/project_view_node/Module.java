package org.elixir_lang.beam.project_view_node;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

import static org.elixir_lang.psi.call.name.Module.stripElixirPrefix;

public class Module extends ProjectViewNode<String> {
    /*
     * Static Methods
     */

    @NotNull
    private static String presentableText(@NotNull String name) {
        final String presentableText;

        if (name.startsWith("Elixir.")) {
            presentableText = stripElixirPrefix(name);
        } else {
            /* assume it is an Erlang module name and should be treated as an atom instead
               an alias */
            presentableText = ":" + name;
        }

        return presentableText;
    }

    /*
     * Fields
     */

    String presentableText = null;

    /*
     * Constructors
     */

    public Module(@NotNull Project project, @NotNull String name, @Nullable ViewSettings viewSettings) {
        super(project, name, viewSettings);
    }

    /*
     * Instance Methods
     */

    @Override
    public boolean contains(@NotNull VirtualFile file) {
        return false;
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    protected void update(PresentationData presentation) {
        if (presentableText == null) {
            presentableText = presentableText(getValue());
        }

        presentation.setPresentableText(presentableText);
    }
}
