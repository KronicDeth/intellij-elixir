package org.elixir_lang.beam;

import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.elixir_lang.beam.project_view_node.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class TreeStructureProvider implements com.intellij.ide.projectView.TreeStructureProvider {
    /**
     * Allows a plugin to modify the list of children displayed for the specified node in the
     * project view.
     *
     * @param parent   the parent node.
     * @param children the list of child nodes according to the default project structure.
     *                 Elements of the collection are of type {@link ProjectViewNode}.
     * @param settings the current project view settings.
     * @return the modified collection of child nodes, or <code>children</code> if no modifications
     * are required.
     */
    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent,
                                               @NotNull Collection<AbstractTreeNode> children,
                                               ViewSettings settings) {

        if (parent instanceof ProjectViewNode) {
            ProjectViewNode parentProjectViewNode = (ProjectViewNode) parent;
            Object value = parentProjectViewNode.getValue();

            if (value instanceof PsiFile) {
                PsiFile psiFile = (PsiFile) value;

                VirtualFile virtualFile = psiFile.getVirtualFile();

                if (virtualFile != null && Beam.is(virtualFile)) {
                    Project project = parent.getProject();

                    if (project != null) {
                        Beam beam = Beam.from(virtualFile);

                        if (beam != null) {
                            children.add(new Module(project, beam, settings));
                        }
                    }
                }
            }
        }

        return children;
    }

    /**
     * Returns a user data object of the specified type for the specified selection in the
     * project view.
     *
     * @param selected the list of nodes currently selected in the project view.
     * @param dataName the identifier of the requested data object (for example, as defined in
     *                 {@link PlatformDataKeys})
     * @return the data object, or null if no data object can be returned by this provider.
     * @see DataProvider
     */
    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> selected, String dataName) {
        return null;
    }
}
