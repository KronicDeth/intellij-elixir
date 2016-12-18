package org.elixir_lang.beam.project_view_node;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.elixir_lang.beam.Beam;
import org.elixir_lang.beam.chunk.Atoms;
import org.elixir_lang.beam.chunk.Exports;
import org.elixir_lang.beam.chunk.exports.Export;
import org.elixir_lang.icons.ElixirIcons;
import org.intellij.erlang.icons.ErlangIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

import static org.elixir_lang.psi.call.name.Module.ELIXIR_PREFIX;

public class Module extends ProjectViewNode<Beam> {
    /*
     * Fields
     */

    @Nullable
    final Atoms atoms;
    @NotNull
    final String childrenLocationString;
    @Nullable
    final Icon icon;
    @Nullable
    final String locationString;
    @NotNull
    final String presentableText;

    /*
     * Constructors
     */

    public Module(@NotNull Project project, @NotNull Beam beam, @Nullable ViewSettings viewSettings) {
        super(project, beam, viewSettings);

        atoms = beam.atoms();

        if (atoms != null) {
            String name = atoms.moduleName();

            if (name != null) {
                if (name.startsWith(ELIXIR_PREFIX)) {
                    icon = ElixirIcons.MODULE;

                    String stripped = name.substring(ELIXIR_PREFIX.length());

                    String[] strings = stripped.split("\\.");

                    childrenLocationString = stripped;

                    if (strings.length == 1) {
                        locationString = null;
                        presentableText = stripped;
                    } else {
                        locationString = StringUtil.join(strings, 0, strings.length - 1, ".");
                        presentableText = strings[strings.length - 1];
                    }
                } else {
                    icon = ErlangIcons.MODULE;

                    locationString = null;
                    presentableText = ":" + name;
                    childrenLocationString = presentableText;
                }
            } else {
                icon = null;
                locationString = null;
                presentableText = "?";
                childrenLocationString = presentableText;
            }
        } else {
            icon = null;
            locationString = null;
            presentableText = "?";
            childrenLocationString = presentableText;
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
        Collection<AbstractTreeNode> children = new ArrayList<AbstractTreeNode>();

        if (atoms != null) {
            Beam beam = getValue();
            Exports exports = beam.exports();

            if (exports != null) {
                Pair<SortedMap<String, SortedMap<Integer, Export>>, SortedSet<Export>>
                        exportByArityByNameNamelessExports = exports.exportByArityByName(atoms);

                SortedMap<String, SortedMap<Integer, Export>> exportByArityByName =
                        exportByArityByNameNamelessExports.first;

                for (SortedMap.Entry<String, SortedMap<Integer, Export>> nameExportByArity :
                        exportByArityByName.entrySet()) {
                    String name = nameExportByArity.getKey();
                    SortedMap<Integer, Export> exportByArity = nameExportByArity.getValue();

                    for (SortedMap.Entry<Integer, Export> arityExport : exportByArity.entrySet()) {
                        Integer arity = arityExport.getKey();
                        Export export = arityExport.getValue();

                        children.add(
                                new NameArity(
                                        getProject(),
                                        export,
                                        childrenLocationString,
                                        name,
                                        arity,
                                        getSettings()
                                )
                        );
                    }
                }
            }
        }

        return children;
    }

    @Override
    protected void update(PresentationData presentation) {
        if (icon != null) {
            presentation.setIcon(icon);
        }

        if (locationString != null) {
            presentation.setLocationString(locationString);
        }

        presentation.setPresentableText(presentableText);
    }
}
