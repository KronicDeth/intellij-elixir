package org.elixir_lang.mix;

import com.intellij.ide.util.importProject.ModuleDescriptor;
import com.intellij.ide.util.importProject.ProjectDescriptor;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectJdkForModuleStep;
import com.intellij.ide.util.projectWizard.importSources.ProjectFromSourcesBuilder;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.SdkType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.util.*;

public class ProjectStructureDetector extends com.intellij.ide.util.projectWizard.importSources.ProjectStructureDetector {
    @NotNull
    @Override
    public DirectoryProcessingResult detectRoots(@NotNull File dir, @NotNull File[] children, @NotNull File base, @NotNull List<com.intellij.ide.util.projectWizard.importSources.DetectedProjectRoot> result) {
        DirectoryProcessingResult directoryProcessingResult = DirectoryProcessingResult.PROCESS_CHILDREN;

        for (File child : children) {
            String childName = child.getName();

            if (childName.equals("mix.exs") && child.isFile()) {
                result.add(new DetectedProjectRoot(dir));
                directoryProcessingResult = DirectoryProcessingResult.SKIP_CHILDREN;

                break;
            }
        }

        return directoryProcessingResult;
    }

    /**
     * @link package org.intellij.erlang.editor.ErlangProjectStructureDetector#createWizardSteps
     */
    @Override
    public List<ModuleWizardStep> createWizardSteps(ProjectFromSourcesBuilder builder, ProjectDescriptor projectDescriptor, Icon stepIcon) {
        /* ProjectJdkForModuleStep should actually be called ProjectSdkForModuleStep since it uses the Sdk type, and
           doesn't assume a Jdk. */
        ProjectJdkForModuleStep projectJdkForModuleStep = new ProjectJdkForModuleStep(builder.getContext(), SdkType.getInstance());
        return Collections.<ModuleWizardStep>singletonList(projectJdkForModuleStep);
    }

    /**
     *
     * @param roots
     * @param projectDescriptor
     * @param builder
     * @see  org.intellij.erlang.editor.ErlangProjectStructureDetector.setupProjectStructure
     */
    @Override
    public void setupProjectStructure(@NotNull Collection<com.intellij.ide.util.projectWizard.importSources.DetectedProjectRoot> roots, @NotNull ProjectDescriptor projectDescriptor, @NotNull ProjectFromSourcesBuilder builder) {
        if (!roots.isEmpty() && !builder.hasRootsFromOtherDetectors(this)) {
            List<ModuleDescriptor> modules = projectDescriptor.getModules();
            if (modules.isEmpty()) {
                modules = new ArrayList<ModuleDescriptor>(roots.size());

                for (com.intellij.ide.util.projectWizard.importSources.DetectedProjectRoot root : roots) {
                    File rootDirectory = root.getDirectory();
                    Collection<DetectedSourceRoot> sourceRoots = new ArrayList<DetectedSourceRoot>(2);

                    File lib = new File(rootDirectory, "lib");

                    if (lib.isDirectory()) {
                        sourceRoots.add(new DetectedSourceRoot(lib));
                    }

                    File test = new File(rootDirectory, "test");

                    if (test.isDirectory()) {
                        sourceRoots.add(new DetectedSourceRoot(test));
                    }

                    modules.add(
                            new ModuleDescriptor(
                                    rootDirectory,
                                    ModuleType.getInstance(),
                                    sourceRoots
                            )
                    );
                }

                projectDescriptor.setModules(modules);
            }
        }
    }
}
