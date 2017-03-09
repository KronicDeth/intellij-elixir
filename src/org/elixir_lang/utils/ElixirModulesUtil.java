package org.elixir_lang.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleFileIndex;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.Processor;
import com.intellij.util.containers.Convertor;
import org.elixir_lang.ElixirFileType;
import org.elixir_lang.psi.ElixirFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

public class ElixirModulesUtil {
  @NotNull
  public static Collection<ElixirFile> getElixirFiles(@NotNull Project project) {
    HashSet<ElixirFile> elixirModules = new HashSet<>();
    for (Module module : ModuleManager.getInstance(project).getModules()) {
      addElixirFiles(module, false, elixirModules, ElixirFileType.INSTANCE);
    }
    return elixirModules;
  }

  private static Collection<ElixirFile> addElixirFiles(@NotNull Module module,
                                                       boolean onlyTestModules,
                                                       @NotNull Collection<ElixirFile> elixirModules,
                                                       @NotNull ElixirFileType type) {
    Processor<VirtualFile> filesCollector = getElixirModulesCollector(PsiManager.getInstance(module.getProject()), elixirModules, type);
    collectFiles(module, onlyTestModules, filesCollector);
    return elixirModules;
  }

  @Nullable
  private static Processor<VirtualFile> getElixirModulesCollector(@NotNull final PsiManager psiManager,
                                                                  @NotNull final Collection<ElixirFile> elixirFiles,
                                                                  @NotNull final ElixirFileType type) {
    return new Processor<VirtualFile>() {
      @Override
      public boolean process(@NotNull VirtualFile virtualFile) {
        if (virtualFile.getFileType() == type) {
          PsiFile psiFile = psiManager.findFile(virtualFile);
          if (psiFile instanceof ElixirFile) {
            elixirFiles.add((ElixirFile) psiFile);
          }
        }
        return true;
      }
    };
  }

  private static void collectFiles(@NotNull Module module,
                                   boolean onlyTestModules,
                                   @NotNull Processor<VirtualFile> filesCollector) {
    ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
    ModuleFileIndex moduleFileIndex = rootManager.getFileIndex();
    Convertor<VirtualFile, Boolean> sourceDirectoriesFilter = onlyTestModules ? getTestDirectoriesFilter(moduleFileIndex)
        : getSourceDirectoriesFilter(moduleFileIndex);

    for (VirtualFile sourceRoot : rootManager.getSourceRoots(onlyTestModules)) {
      VfsUtilCore.processFilesRecursively(sourceRoot, filesCollector, sourceDirectoriesFilter);
    }
  }

  private static Convertor<VirtualFile, Boolean> getSourceDirectoriesFilter(@NotNull final ModuleFileIndex moduleFileIndex) {
    return new Convertor<VirtualFile, Boolean>() {
      @Override
      public Boolean convert(@NotNull VirtualFile dir) {
        return moduleFileIndex.isInSourceContent(dir);
      }
    };
  }

  @NotNull
  private static Convertor<VirtualFile, Boolean> getTestDirectoriesFilter(@NotNull final ModuleFileIndex moduleFileIndex) {
    return new Convertor<VirtualFile, Boolean>() {
      @Override
      public Boolean convert(@NotNull VirtualFile dir) {
        return moduleFileIndex.isInTestSourceContent(dir);
      }
    };
  }

  @NotNull
  public static String elixirModuleNameToErlang(@NotNull String moduleName) {
    if (moduleName.equals("true") || moduleName.equals("false") || moduleName.equals("nil")) {
      return moduleName;
    } else if (moduleName.charAt(0) == ':') {
      return moduleName.substring(1);
    } else {
      return "Elixir." + moduleName;
    }
  }

  @NotNull
  public static String erlangModuleNameToElixir(@NotNull String moduleName) {
    if (moduleName.equals("true") || moduleName.equals("false") || moduleName.equals("nil")) {
      return moduleName;
    } else if (moduleName.startsWith("Elixir.")) {
      return moduleName.substring("Elixir.".length());
    } else {
      return ":" + moduleName;
    }
  }
}
