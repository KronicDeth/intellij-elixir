package org.elixir_lang.mix.importWizard;

import com.intellij.compiler.CompilerWorkspaceConfiguration;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.ex.ProjectRootManagerEx;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.packaging.artifacts.ModifiableArtifactModel;
import com.intellij.projectImport.ProjectImportBuilder;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.configuration.ElixirCompilerSettings;
import org.elixir_lang.icons.ElixirIcons;
import org.elixir_lang.mix.settings.MixSettings;
import org.elixir_lang.module.ElixirModuleType;
import org.elixir_lang.sdk.ElixirSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by zyuyou on 15/7/1.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/rebar/importWizard/RebarProjectImportBuilder.java
 */
public class MixProjectImportBuilder extends ProjectImportBuilder<ImportedOtpApp> {
  private static final Logger LOG = Logger.getInstance(MixProjectImportBuilder.class);

  private boolean myOpenProjectSettingsAfter = false;
  @Nullable
  private VirtualFile myProjectRoot = null;
  @NotNull
  private List<ImportedOtpApp> myFoundOtpApps = Collections.emptyList();
  @NotNull private List<ImportedOtpApp> mySelectedOtpApps = Collections.emptyList();
  @NotNull private String myMixPath = "";
  private boolean myIsImportingProject;


  @NotNull
  @Override
  public String getName() {
    return "Mix";
  }

  @Override
  public Icon getIcon() {
    return ElixirIcons.MIX;
  }

  @Override
  public boolean isSuitableSdkType(SdkTypeId sdkType) {
    return sdkType == ElixirSdkType.getInstance();
  }

  @Override
  public List<ImportedOtpApp> getList() {
    return new ArrayList<ImportedOtpApp>(myFoundOtpApps);
  }

  @Override
  public void setList(List<ImportedOtpApp> selectedOtpApps) throws ConfigurationException {
    if(selectedOtpApps != null){
      mySelectedOtpApps = selectedOtpApps;
    }
  }

  @Override
  public boolean isMarked(ImportedOtpApp importedOtpApp) {
    return importedOtpApp != null && mySelectedOtpApps.contains(importedOtpApp);
  }

  @Override
  public void setOpenProjectSettingsAfter(boolean openProjectSettingsAfter) {
    myOpenProjectSettingsAfter = openProjectSettingsAfter;
  }

  @Override
  public void cleanup() {
    myOpenProjectSettingsAfter = false;
    myProjectRoot = null;
    myFoundOtpApps = Collections.emptyList();
    mySelectedOtpApps = Collections.emptyList();
  }

  /**
   * check for reusing *.iml or *.eml
   *
   * */
  @SuppressWarnings("DialogTitleCapitalization")
  @Override
  public boolean validate(Project current, Project dest) {
    if(!findIdeaModuleFiles(mySelectedOtpApps)){
      return true;
    }

    int resultCode = Messages.showYesNoCancelDialog(ApplicationInfoEx.getInstanceEx().getFullApplicationName() + " module files found:\n\n" +
        StringUtil.join(myFoundOtpApps, new Function<ImportedOtpApp, String>() {
          @Override
          public String fun(ImportedOtpApp importedOtpApp) {
            VirtualFile ideaModuleFile = importedOtpApp.getIdeaModuleFile();
            return ideaModuleFile != null ? "    " + ideaModuleFile.getPath() + "\n" : "";
          }
        }, "") + "\nWould you like to reuse them?", "Module files found", Messages.getQuestionIcon());

    if(resultCode == Messages.YES){
      return true;
    }else if(resultCode == Messages.NO){
      try{
        deleteIdeaModuleFiles(mySelectedOtpApps);
        return true;
      }catch (IOException e){
        LOG.error(e);
        return false;
      }
    }else {
      return false;
    }
  }

  @Override
  public List<Module> commit(@NotNull Project project,
                             @Nullable ModifiableModuleModel moduleModel,
                             @NotNull ModulesProvider modulesProvider,
                             @Nullable ModifiableArtifactModel artifactModel) {

    Set<String> selectedAppNames = ContainerUtil.newHashSet();

    for (ImportedOtpApp importedOtpApp:mySelectedOtpApps){
      selectedAppNames.add(importedOtpApp.getName());
    }

    Sdk projectSdk = fixProjectSdk(project);
    List<Module> createModules = new ArrayList<Module>();
    final List<ModifiableRootModel> createdRootModels = new ArrayList<ModifiableRootModel>();
    final ModifiableModuleModel obtainedModuleModel =
        moduleModel != null ? moduleModel : ModuleManager.getInstance(project).getModifiableModel();

    VirtualFile _buildDir = null;
    for (ImportedOtpApp importedOtpApp:mySelectedOtpApps){
      // add Module
      VirtualFile ideaModuleDir = importedOtpApp.getRoot();
      String ideaModuleFile = ideaModuleDir.getCanonicalPath() + File.separator + importedOtpApp.getName() + ".iml";
      Module module = obtainedModuleModel.newModule(ideaModuleFile, ElixirModuleType.getInstance().getId());
      createModules.add(module);

      // add rootModule
      importedOtpApp.setModule(module);
      if(importedOtpApp.getIdeaModuleFile() == null){
        ModifiableRootModel rootModel = ModuleRootManager.getInstance(module).getModifiableModel();

        // Make it inherit SDK from the project.
        rootModel.inheritSdk();

        // Initialize source and test paths.
        ContentEntry content = rootModel.addContentEntry(importedOtpApp.getRoot());
        addSourceDirToContent(content, ideaModuleDir, "lib", false);
        addSourceDirToContent(content, ideaModuleDir, "test", true);

        // Exclude standard folders
        // excludeDirFromContent(content, ideaModuleDir, "_build");

        // Initialize output paths according to mix conventions.
        CompilerModuleExtension compilerModuleExt = rootModel.getModuleExtension(CompilerModuleExtension.class);
        compilerModuleExt.inheritCompilerOutputPath(false);

        _buildDir = myProjectRoot != null && myProjectRoot.equals(ideaModuleDir) ? ideaModuleDir : ideaModuleDir.getParent().getParent();
        compilerModuleExt.setCompilerOutputPath(_buildDir + StringUtil.replace("/_build/dev/lib/" + importedOtpApp.getName() + "/ebin", "/", File.separator));
        compilerModuleExt.setCompilerOutputPathForTests(_buildDir + StringUtil.replace("/_build/test/lib/" + importedOtpApp.getName() + "/ebin", "/", File.separator));

        createdRootModels.add(rootModel);

        // Set inter-module dependencies
        resolveModuleDeps(rootModel, importedOtpApp, projectSdk, selectedAppNames);
      }
    }

    // Commit project structure.
    LOG.info("Commit project structure");
    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      @Override
      public void run() {
        for (ModifiableRootModel rootModel : createdRootModels) {
          rootModel.commit();
        }
        obtainedModuleModel.commit();
      }
    });

    MixSettings.getInstance(project).setMixPath(myMixPath);
    if(myIsImportingProject){
      ElixirCompilerSettings.getInstance(project).setUseMixCompilerEnabled(true);
    }
    CompilerWorkspaceConfiguration.getInstance(project).CLEAR_OUTPUT_DIRECTORY = false;

    return createModules;
  }

  public boolean setProjectRoot(@NotNull final VirtualFile projectRoot){
    if(projectRoot.equals(myProjectRoot)){
      return true;
    }

    boolean unitTestMode = ApplicationManager.getApplication().isUnitTestMode();

    myProjectRoot = projectRoot;
    if(!unitTestMode && projectRoot instanceof VirtualDirectoryImpl){
      ((VirtualDirectoryImpl)projectRoot).refreshAndFindChild("deps");
    }

    ProgressManager.getInstance().run(new Task.Modal(getCurrentProject(), "Scanning Mix Projects", true){
      @Override
      public void run(@NotNull final ProgressIndicator indicator) {
        List<VirtualFile> mixExsFiles = findMixExs(myProjectRoot, indicator);
        final LinkedHashSet<ImportedOtpApp> importedOtpApps = new LinkedHashSet<ImportedOtpApp>(mixExsFiles.size());

        VfsUtilCore.visitChildrenRecursively(projectRoot, new VirtualFileVisitor() {
          @Override
          public boolean visitFile(@NotNull VirtualFile file) {
            indicator.checkCanceled();
            if(file.isDirectory()){
              indicator.setText2(file.getPath());
              if(isBuildOrConfigOrTestsDirectory(projectRoot.getPath(), file.getPath())) return false;
            }

            ContainerUtil.addAllNotNull(importedOtpApps, createImportedOtpApp(file));

            return true;
          }
        });

        myFoundOtpApps = ContainerUtil.newArrayList(importedOtpApps);
      }
    });

    Collections.sort(myFoundOtpApps, new Comparator<ImportedOtpApp>() {
      @Override
      public int compare(ImportedOtpApp o1, ImportedOtpApp o2) {
        int nameCompareResult = String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
        if (nameCompareResult == 0) {
          return String.CASE_INSENSITIVE_ORDER.compare(o1.getRoot().getPath(), o1.getRoot().getPath());
        }
        return nameCompareResult;
      }
    });

    mySelectedOtpApps = myFoundOtpApps;

    return !myFoundOtpApps.isEmpty();
  }

  public void setMixPath(@NotNull String mixPath){
    myMixPath = mixPath;
  }

  public void setIsImportingProject(boolean isImportingProject){
    myIsImportingProject = isImportingProject;
  }

  /**
   * private methos
   * */
  private static boolean isBuildOrConfigOrTestsDirectory(String projectRootPath, String path){
    return (projectRootPath + "/_build").equals(path)
        || (projectRootPath + "/config").equals(path)
        || (projectRootPath + "/tests").equals(path);
  }

  @Nullable
  private static Sdk fixProjectSdk(@NotNull Project project){
    final ProjectRootManagerEx projectRootMgr = ProjectRootManagerEx.getInstanceEx(project);
    Sdk selectedSdk = projectRootMgr.getProjectSdk();
    if(selectedSdk == null || selectedSdk.getSdkType() != ElixirSdkType.getInstance()){
      final Sdk moreSuitableSdk = ProjectJdkTable.getInstance().findMostRecentSdkOfType(ElixirSdkType.getInstance());
      ApplicationManager.getApplication().runWriteAction(new Runnable() {
        @Override
        public void run() {
          projectRootMgr.setProjectSdk(moreSuitableSdk);
        }
      });
      return moreSuitableSdk;
    }
    return selectedSdk;
  }

  private static void addSourceDirToContent(@NotNull ContentEntry content,
                                            @NotNull VirtualFile root,
                                            @NotNull String sourceDir,
                                            boolean test){
    VirtualFile sourceDirFile = root.findChild(sourceDir);
    if(sourceDirFile != null){
      content.addSourceFolder(sourceDirFile, test);
    }
  }

  private static void excludeDirFromContent(ContentEntry content, VirtualFile root, String excludeDir){
    VirtualFile excludeDirFile = root.findChild(excludeDir);
    if(excludeDirFile != null){
      content.addExcludeFolder(excludeDirFile);
    }
  }

  @NotNull
  private List<VirtualFile> findMixExs(@NotNull final VirtualFile root, @NotNull final ProgressIndicator indicator){
    // synchronous and recursive
    root.refresh(false, true);

    final List<VirtualFile> foundMixExs = new ArrayList<VirtualFile>();
    VfsUtilCore.visitChildrenRecursively(root, new VirtualFileVisitor() {
      @Override
      public boolean visitFile(@NotNull VirtualFile file) {
        indicator.checkCanceled();
        if(file.isDirectory()){
          if(isBuildOrConfigOrTestsDirectory(root.getPath(), file.getPath())) return false;
          indicator.setText2(file.getPath());
        }else if(file.getName().equalsIgnoreCase("mix.exs")){
          foundMixExs.add(file);
        }
        return true;
      }
    });

    return foundMixExs;
  }

  @Nullable
  private static ImportedOtpApp createImportedOtpApp(@NotNull VirtualFile appRoot){
    VirtualFile appMixFile = appRoot.findChild("mix.exs");
    if(appMixFile == null){
      return null;
    }
    return new ImportedOtpApp(appRoot, appMixFile);
  }

  @Nullable
  private static VirtualFile findFileByExtension(@NotNull VirtualFile dir, @NotNull String extension){
    for(VirtualFile file : dir.getChildren()){
      String fileName = file.getName();
      if(!file.isDirectory() && fileName.endsWith(extension)){
        return file;
      }
    }
    return null; //To change body of created methods use File | settings | File Templates.
  }

  private static void deleteIdeaModuleFiles(@NotNull final List<ImportedOtpApp> importedOtpApps) throws IOException{
    final IOException[] ex = new IOException[1];

    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      @Override
      public void run() {
        for(ImportedOtpApp importedOtpApp : importedOtpApps){
          VirtualFile ideaModuleFile = importedOtpApp.getIdeaModuleFile();
          if(ideaModuleFile != null){
            try{
              ideaModuleFile.delete(this);
              importedOtpApp.setIdeaModuleFile(null);
            }catch (IOException e){
              ex[0] = e;
            }
          }
        }
      }
    });

    if(ex[0] != null){
      throw ex[0];
    }
  }

  private static boolean findIdeaModuleFiles(@NotNull List<ImportedOtpApp> importedOtpApps){
    boolean ideaModuleFileExists = false;
    for (ImportedOtpApp importedOtpApp : importedOtpApps){
      VirtualFile applicationRoot = importedOtpApp.getRoot();
      String ideaModuleName = importedOtpApp.getName();
      VirtualFile imlFile = applicationRoot.findChild(ideaModuleName + ".iml");
      if(imlFile != null){
        ideaModuleFileExists = true;
        importedOtpApp.setIdeaModuleFile(imlFile);
      }else{
        VirtualFile emlFile = applicationRoot.findChild(ideaModuleName + ".eml");
        if(emlFile != null){
          ideaModuleFileExists = true;
          importedOtpApp.setIdeaModuleFile(emlFile);
        }
      }
    }

    return ideaModuleFileExists;
  }

  @NotNull
  private static Set<String> resolveModuleDeps(@NotNull ModifiableRootModel rootModel,
                                               @NotNull ImportedOtpApp importedOtpApp,
                                               @Nullable Sdk projectSdk,
                                               @NotNull Set<String> allImportedAppNames){
    HashSet<String> unresolvedAppNames = ContainerUtil.newHashSet();
    for (String depAppName : importedOtpApp.getDeps()){
      if(allImportedAppNames.contains(depAppName)){
        rootModel.addInvalidModuleEntry(depAppName);
      }else if(projectSdk != null && isSdkOtpApp(depAppName, projectSdk)){
        // Sdk is already a dependency
        LOG.info("Sdk otp-app:[" + depAppName + "] is already a dependy.");
      }else{
        rootModel.addInvalidModuleEntry(depAppName);
        unresolvedAppNames.add(depAppName);
      }
    }
    return unresolvedAppNames;
  }

  private static boolean isSdkOtpApp(@NotNull String otpAppName, @NotNull Sdk sdk){
    for (VirtualFile sdkLibDir : sdk.getRootProvider().getFiles(OrderRootType.SOURCES)){
      for(VirtualFile child: sdkLibDir.getChildren()){
        if(child.isDirectory() && child.getName().equals(otpAppName)){
          return true;
        }
      }
    }
    return false;
  }
}
