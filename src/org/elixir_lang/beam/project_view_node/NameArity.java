package org.elixir_lang.beam.project_view_node;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.elixir_lang.beam.chunk.exports.Export;
import org.elixir_lang.structure_view.element.Timed;
import org.elixir_lang.structure_view.element.Visible;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;

public class NameArity extends ProjectViewNode<Export> {
    /*
     * CONSTANTS
     */

    private static final String MACRO_PREFIX = "MACRO-";

    /*
     * Static Methods
     */

    @NotNull
    private static String stripMacroPrefix(@NotNull String maybePrefixed) {
        String stripped = maybePrefixed;

        if (maybePrefixed.startsWith(MACRO_PREFIX)) {
            stripped = maybePrefixed.substring(MACRO_PREFIX.length());
        }

        return stripped;
    }

    @Contract(pure = true)
    @NotNull
    private static Timed.Time time(@NotNull String name) {
        Timed.Time time = Timed.Time.RUN;

        if (name.startsWith(MACRO_PREFIX)) {
            time = Timed.Time.COMPILE;
        }

        return time;
    }

    /*
     * Fields
     */

    @NotNull
    private final Icon icon;
    @NotNull
    private final String location;
    @NotNull
    private final String presentableText;

    /**
     * Creates an instance of the project view node.
     *
     * @param project      the project containing the node.
     * @param export       the object (for example, a PSI element) represented by the project view node
     * @param viewSettings the settings of the project view.
     */
    NameArity(Project project,
              Export export,
              @NotNull String location,
              @NotNull String name,
              @Nullable Integer arity,
              ViewSettings viewSettings) {
        super(project, export, viewSettings);
        this.icon = org.elixir_lang.name_arity.PresentationData.icon(false, false, false, time(name), Visible.Visibility.PUBLIC);
        this.location = location;
        this.presentableText = org.elixir_lang.name_arity.PresentationData.presentableText(
                stripMacroPrefix(name),
                arity
        );
    }

    /**
     * Checks if this node or one of its children represents the specified virtual file.
     *
     * @param file the file to check for.
     * @return true if the file is found in the subtree, false otherwise.
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
        presentation.setIcon(icon);
        presentation.setLocationString(location);
        presentation.setPresentableText(presentableText);
    }
}
