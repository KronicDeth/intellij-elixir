package org.elixir_lang.mix;

import com.intellij.ide.util.importProject.LibraryDescriptor;
import com.intellij.ide.util.importProject.ModuleDescriptor;
import com.intellij.ide.util.importProject.ProjectDescriptor;
import com.intellij.ide.util.projectWizard.importSources.DetectedProjectRoot;
import com.intellij.ide.util.projectWizard.importSources.DetectedSourceRoot;
import com.intellij.ide.util.projectWizard.importSources.ProjectFromSourcesBuilder;
import com.intellij.openapi.util.io.FileUtil;
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
        }

        // Process children so that deps will also be identified so they can be registered as libraries.
        return DirectoryProcessingResult.PROCESS_CHILDREN;
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

                DetectedProjectRoot commonRoot = null;

                for (DetectedProjectRoot root : roots) {
                    if (commonRoot == null) {
                        commonRoot = root;
                    } else {
                        if (FileUtil.isAncestor(root.getDirectory(), commonRoot.getDirectory(), false)) {
                            commonRoot = root;
                        }
                    }
                }

                List<DetectedProjectRoot> libraryRoots = new ArrayList<DetectedProjectRoot>();

                for (DetectedProjectRoot root : roots) {
                    if (root != commonRoot) {
                        libraryRoots.add(root);
                    }
                }

                modules.add(
                        new ModuleDescriptor(
                                commonRoot.getDirectory(),
                                ModuleType.getInstance(),
                                ContainerUtil.<DetectedSourceRoot>emptyList()
                        )
                );
                projectDescriptor.setModules(modules);

                List<LibraryDescriptor> libraries = new ArrayList<LibraryDescriptor>();

                for (DetectedProjectRoot libraryRoot : libraryRoots) {
                    File libraryRootDirectory = libraryRoot.getDirectory();

                    libraries.add(
                            new LibraryDescriptor(
                                    libraryRootDirectory.getName(),
                                    Arrays.asList(libraryRootDirectory)
                            )
                    );
                }

                projectDescriptor.setLibraries(libraries);
            }
        }
    }
}
