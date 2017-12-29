package org.elixir_lang.mix.runner.exunit;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.ide.projectView.impl.ProjectRootsUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.sdk.elixir.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static org.elixir_lang.sdk.elixir.Type.mostSpecificSdk;

public class MixExUnitRunConfigurationProducer extends RunConfigurationProducer<MixExUnitRunConfiguration> {
  /*
   * CONSTANTS
   */

  private static int UNKNOWN_LINE = -1;

  /*
   * Constructors
   */

  public MixExUnitRunConfigurationProducer() {
    super(MixExUnitRunConfigurationType.getInstance());
  }

  /*
   * Instance Methods
   */

  @Override
  protected final boolean setupConfigurationFromContext(MixExUnitRunConfiguration runConfig, ConfigurationContext context, Ref<PsiElement> ref) {
    PsiElement location = ref.get();
    return location != null && location.isValid() &&
      setupConfigurationFromContextImpl(runConfig, location);
  }

  private boolean setupConfigurationFromContextImpl(@NotNull MixExUnitRunConfiguration configuration,
                                                    @NotNull PsiElement psiElement) {
    boolean contextApplicable = false;

    if (psiElement instanceof PsiDirectory) {
      PsiDirectory psiDirectory = (PsiDirectory) psiElement;

      Module module = ModuleUtilCore.findModuleForPsiElement(psiDirectory);
      Sdk sdk;

      if (module != null) {
        sdk = mostSpecificSdk(module);
      } else {
        ProjectRootManager projectRootManager = ProjectRootManager.getInstance(psiDirectory.getProject());
        sdk = projectRootManager.getProjectSdk();
      }

      SdkTypeId sdkTypeId = null;

      if (sdk != null) {
        sdkTypeId = sdk.getSdkType();
      }

      if ((sdkTypeId == null || sdkTypeId.equals(Type.getInstance())) &&
              ProjectRootsUtil.isInTestSource(psiDirectory.getVirtualFile(),  psiDirectory.getProject())) {
        String basePath = psiElement.getProject().getBasePath();
        String workingDirectory = workingDirectory(psiElement, basePath);

        configuration.setWorkingDirectory(workingDirectory);

        PsiDirectory dir = (PsiDirectory) psiElement;
        configuration.setName(configurationName(dir, workingDirectory, basePath));
        configuration.setProgramParameters(programParameters(dir, workingDirectory));

        contextApplicable = true;
      }
    } else {
      PsiFile containingFile = psiElement.getContainingFile();
      if (!(containingFile instanceof ElixirFile || containingFile instanceof PsiDirectory)) return false;

      if (ProjectRootsUtil.isInTestSource(containingFile)) {
        String basePath = psiElement.getProject().getBasePath();
        String workingDirectory = workingDirectory(psiElement, basePath);

        configuration.setWorkingDirectory(workingDirectory);

        int lineNumber = lineNumber(psiElement);
        configuration.setName(configurationName(containingFile, lineNumber, workingDirectory, basePath));
        configuration.setProgramParameters(programParameters(containingFile, lineNumber, workingDirectory));

        contextApplicable = true;
      }
    }

    return contextApplicable;
  }

  @Override
  public final boolean isConfigurationFromContext(MixExUnitRunConfiguration runConfig, ConfigurationContext context) {
    PsiElement location = context.getPsiLocation();
    return location != null && location.isValid() &&
      isConfigurationFromContextImpl(runConfig, location);
  }

  private boolean isConfigurationFromContextImpl(@NotNull MixExUnitRunConfiguration configuration,
                                                 @NotNull PsiElement psiElement) {
    PsiFile containingFile = psiElement.getContainingFile();
    VirtualFile vFile = containingFile != null ? containingFile.getVirtualFile() : null;
    if (vFile == null) return false;

    int lineNumber = lineNumber(psiElement);
    String workingDirectory = configuration.getWorkingDirectory();

    return StringUtil.equals(
            configuration.getName(),
            configurationName(
                    containingFile,
                    lineNumber,
                    workingDirectory,
                    psiElement.getProject().getBasePath()
            )
    ) &&
            StringUtil.equals(
                    configuration.getProgramParameters(),
                    programParameters(containingFile, lineNumber, workingDirectory)
            );
  }

  private int lineNumber(@NotNull PsiElement psiElement) {
    PsiFile containingFile = psiElement.getContainingFile();
    Project project = containingFile.getProject();
    PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
    Document document = psiDocumentManager.getDocument(containingFile);
    int documentLineNumber = 0;

    if (document != null) {
      int textOffset = psiElement.getTextOffset();
      documentLineNumber = document.getLineNumber(textOffset);
    }

    int lineNumber;

    if (documentLineNumber == 0) {
      lineNumber = UNKNOWN_LINE;
    } else {
      lineNumber = documentLineNumber + 1;
    }

    return lineNumber;
  }

  private String configurationName(PsiFileSystemItem file,
                                   @Nullable String workingDirectory,
                                   @Nullable String basePath) {
    String filePath = file.getVirtualFile().getPath();
    String suffix = null;

    if (workingDirectory != null) {
      String prefix = workingDirectory + File.separator;

      if (filePath.startsWith(prefix)) {
        suffix = filePath.substring(prefix.length());
      }

      if (basePath != null && !workingDirectory.equals(basePath) && workingDirectory.startsWith(basePath)) {
        String otpAppName = new File(workingDirectory).getName();

        suffix = otpAppName + " " + suffix;
      }
    }

    if (suffix == null) {
      suffix = file.getName();
    }

    return "Mix ExUnit " + suffix;
  }

  private String configurationName(PsiFileSystemItem file,
                                   int lineNumber,
                                   @Nullable String workingDirectory,
                                   @Nullable String basePath) {
    if (lineNumber == UNKNOWN_LINE) {
      return configurationName(file, workingDirectory, basePath);
    } else {
      return configurationName(file, workingDirectory, basePath) + ":" + lineNumber;
    }
  }

  @NotNull
  private String programParameters(@NotNull PsiFileSystemItem item, @Nullable String workingDirectory) {
    return programParameters(item, UNKNOWN_LINE, workingDirectory);
  }

  @NotNull
  private String programParameters(@NotNull PsiFileSystemItem item,
                                   int lineNumber,
                                   @Nullable String workingDirectory) {
    String path = item.getVirtualFile().getPath();
    String relativePath = path;

    if (workingDirectory != null) {
      String prefix = workingDirectory + File.separator;

      if (path.startsWith(prefix)) {
        relativePath = path.substring(prefix.length());
      }
    }

    String programParameters = relativePath;

    if (lineNumber != UNKNOWN_LINE) {
      programParameters += ":" + lineNumber;
    }

    return programParameters;
  }

  @Nullable
  private static String workingDirectory(@NotNull PsiDirectory directory, @Nullable String basePath) {
    String workingDirectory;

    if (directory.findFile("mix.exs") != null) {
      workingDirectory = directory.getVirtualFile().getPath();
    } else {
      PsiDirectory parent = directory.getParent();

      if (parent != null) {
        workingDirectory = workingDirectory(parent, basePath);
      } else {
        workingDirectory = basePath;
      }
    }

    return workingDirectory;
  }

  @Nullable
  private static String workingDirectory(@NotNull PsiElement element, @Nullable String basePath) {
    String workingDirectory;

    if (element instanceof PsiDirectory) {
      workingDirectory = workingDirectory((PsiDirectory) element, basePath);
    } else if (element instanceof PsiFile) {
      workingDirectory = workingDirectory((PsiFile) element, basePath);
    } else {
      workingDirectory = workingDirectory(element.getContainingFile(), basePath);
    }

    return workingDirectory;
  }

  @Nullable
  private static String workingDirectory(@NotNull PsiFile file, @Nullable String basePath) {
    return workingDirectory(file.getContainingDirectory(), basePath);
  }
}
