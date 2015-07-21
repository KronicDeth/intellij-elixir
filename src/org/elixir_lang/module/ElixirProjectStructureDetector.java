package org.elixir_lang.module;

import com.intellij.ide.util.importProject.ModuleDescriptor;
import com.intellij.ide.util.importProject.ProjectDescriptor;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectJdkForModuleStep;
import com.intellij.ide.util.projectWizard.importSources.DetectedProjectRoot;
import com.intellij.ide.util.projectWizard.importSources.DetectedSourceRoot;
import com.intellij.ide.util.projectWizard.importSources.ProjectFromSourcesBuilder;
import com.intellij.ide.util.projectWizard.importSources.ProjectStructureDetector;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.sdk.ElixirSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by zyuyou on 2015/5/27.
 *
 */
public class ElixirProjectStructureDetector extends ProjectStructureDetector {
  @NotNull
  @Override
  public DirectoryProcessingResult detectRoots(@NotNull File dir, @NotNull File[] children, @NotNull File base, @NotNull List<DetectedProjectRoot> result) {
    Pattern pattern = Pattern.compile(".*\\.ex[s]*");
    List<File> filesByMask = FileUtil.findFilesByMask(pattern, base);
    if(!filesByMask.isEmpty()){
      result.add(new DetectedProjectRoot(dir) {
        @NotNull
        @Override
        public String getRootTypeName() {
          return "Elixir";
        }
      });
    }
    return DirectoryProcessingResult.SKIP_CHILDREN;
  }

  /**
   * This detector just for import non-organization project which not like mix-project
   * So that it's no need to detect directories such as libs, deps, tests etc
   *
   * And mix-project will imported by the MixProjectImportBuilder.java
   * */
  @Override
  public void setupProjectStructure(@NotNull Collection<DetectedProjectRoot> roots, @NotNull ProjectDescriptor projectDescriptor, @NotNull ProjectFromSourcesBuilder builder) {
    if(!roots.isEmpty() && !builder.hasRootsFromOtherDetectors(this)){
      List<ModuleDescriptor> modules = projectDescriptor.getModules();
      if(modules.isEmpty()){
        modules = new ArrayList<ModuleDescriptor>();
        for(DetectedProjectRoot root : roots){
          modules.add(new ModuleDescriptor(root.getDirectory(), ElixirModuleType.getInstance(), ContainerUtil.<DetectedSourceRoot>emptyList()));
        }
        projectDescriptor.setModules(modules);
      }
    }
  }

  @Override
  public List<ModuleWizardStep> createWizardSteps(ProjectFromSourcesBuilder builder, ProjectDescriptor projectDescriptor, Icon stepIcon) {
    ProjectJdkForModuleStep projectJdkForModuleStep = new ProjectJdkForModuleStep(builder.getContext(), ElixirSdkType.getInstance());
    return Collections.<ModuleWizardStep>singletonList(projectJdkForModuleStep);
  }


}
