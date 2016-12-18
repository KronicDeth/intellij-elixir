package org.elixir_lang.beam.project_view_node;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

import static org.elixir_lang.psi.call.name.Module.ELIXIR_PREFIX;
import static org.elixir_lang.psi.call.name.Module.stripElixirPrefix;

public class Module extends ProjectViewNode<String> {
    /*
     * Fields
     */

    @Nullable
    String locationString;
    @NotNull
    String presentableText;

    /*
     * Constructors
     */

    public Module(@NotNull Project project, @NotNull String name, @Nullable ViewSettings viewSettings) {
        super(project, name, viewSettings);

        if (name.startsWith(ELIXIR_PREFIX)) {
            String stripped = name.substring(ELIXIR_PREFIX.length());

            String[] strings = stripped.split("\\.");

            if (strings.length == 1) {
                locationString = null;
                presentableText = stripped;
            } else {
                locationString = StringUtil.join(strings, 0, strings.length - 1, ".");
                presentableText = strings[strings.length - 1];
            }
        } else {
            locationString = null;
            presentableText = ":" + name;
        }
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
        if (locationString != null) {
            presentation.setLocationString(locationString);
        }

        presentation.setPresentableText(presentableText);
    }
}
