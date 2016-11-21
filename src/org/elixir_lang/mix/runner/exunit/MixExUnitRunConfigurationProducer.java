package org.elixir_lang.mix.runner.exunit;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.elixir_lang.psi.ElixirFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class MixExUnitRunConfigurationProducer extends RunConfigurationProducer<MixExUnitRunConfiguration> {
  public MixExUnitRunConfigurationProducer() {
    super(MixExUnitRunConfigurationType.getInstance());
  }

  @Override
  protected final boolean setupConfigurationFromContext(MixExUnitRunConfiguration runConfig, ConfigurationContext context, Ref<PsiElement> ref) {
    PsiElement location = ref.get();
    return location != null && location.isValid() &&
      setupConfigurationFromContextImpl(runConfig, location);
  }

  private boolean setupConfigurationFromContextImpl(@NotNull MixExUnitRunConfiguration configuration,
                                                    @NotNull PsiElement psiElement) {
    if (psiElement instanceof PsiDirectory) {
      String basePath = psiElement.getProject().getBasePath();
      String workingDirectory = workingDirectory(psiElement, basePath);

      configuration.setWorkingDirectory(workingDirectory);

      PsiDirectory dir = (PsiDirectory) psiElement;
      configuration.setName(configurationName(dir, workingDirectory, basePath));
      configuration.setProgramParameters(dir.getVirtualFile().getPath());
      return true;
    } else {
      PsiFile containingFile = psiElement.getContainingFile();
      if (!(containingFile instanceof ElixirFile || containingFile instanceof PsiDirectory)) return false;

      String basePath = psiElement.getProject().getBasePath();
      String workingDirectory = workingDirectory(psiElement, basePath);

      configuration.setWorkingDirectory(workingDirectory);

      int lineNumber = lineNumber(psiElement);
      configuration.setName(configurationName(containingFile, lineNumber, workingDirectory, basePath));
      configuration.setProgramParameters(programParameters(containingFile, lineNumber));

      return true;
    }
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
    return StringUtil.equals(
            configuration.getName(),
            configurationName(
                    containingFile,
                    lineNumber,
                    configuration.getWorkingDirectory(),
                    psiElement.getProject().getBasePath()
            )
    ) &&
            StringUtil.equals(configuration.getProgramParameters(), programParameters(containingFile, lineNumber));
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
      lineNumber = -1;
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
    if (lineNumber == -1) {
      return configurationName(file, workingDirectory, basePath);
    } else {
      return configurationName(file, workingDirectory, basePath) + ":" + lineNumber;
    }
  }

  private String programParameters(PsiFileSystemItem file, int lineNumber) {
    String path = file.getVirtualFile().getPath();
    if (lineNumber == -1) {
      return path;
    } else {
      return path + ":" + lineNumber;
    }
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
