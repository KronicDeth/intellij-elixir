package org.elixir_lang.mix;

import com.intellij.ide.util.importProject.ModuleDescriptor;
import com.intellij.ide.util.importProject.ProjectDescriptor;
import com.intellij.ide.util.projectWizard.importSources.DetectedProjectRoot;
import com.intellij.ide.util.projectWizard.importSources.DetectedSourceRoot;
import com.intellij.ide.util.projectWizard.importSources.ProjectFromSourcesBuilder;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class ProjectStructureDetector extends com.intellij.ide.util.projectWizard.importSources.ProjectStructureDetector {
    @NotNull
    @Override
    public DirectoryProcessingResult detectRoots(@NotNull File dir, @NotNull File[] children, @NotNull File base, @NotNull List<DetectedProjectRoot> result) {
        Map<String, File> childByName = new HashMap<String, File>(children.length);

        for (File child : children) {
            childByName.put(child.getName(), child);
        }

        File mixFile = childByName.get("mix.exs");

        DirectoryProcessingResult directoryProcessingResults = DirectoryProcessingResult.PROCESS_CHILDREN;

        if (mixFile != null && mixFile.isFile()) {
            result.add(
                    new DetectedProjectRoot(dir) {
                        @NotNull
                        @Override
                        public String getRootTypeName() {
                            return "Elixir";
                        }
                    }
            );

            directoryProcessingResults = DirectoryProcessingResult.SKIP_CHILDREN;
        }

        return directoryProcessingResults;
    }

    /**
     *
     * @param roots
     * @param projectDescriptor
     * @param builder
     * @see  org.intellij.erlang.editor.ErlangProjectStructureDetector.setupProjectStructure
     */
    @Override
    public void setupProjectStructure(@NotNull Collection<DetectedProjectRoot> roots, @NotNull ProjectDescriptor projectDescriptor, @NotNull ProjectFromSourcesBuilder builder) {
        if (!roots.isEmpty() && !builder.hasRootsFromOtherDetectors(this)) {
            List<ModuleDescriptor> modules = projectDescriptor.getModules();
            if (modules.isEmpty()) {
                modules = new ArrayList<ModuleDescriptor>();
                for (DetectedProjectRoot root : roots) {
                    modules.add(new ModuleDescriptor(root.getDirectory(), ModuleType.getInstance(), ContainerUtil.<DetectedSourceRoot>emptyList()));
                }
                projectDescriptor.setModules(modules);
            }
        }
    }
}
