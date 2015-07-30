package org.elixir_lang.inspection;

import com.intellij.ProjectTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootAdapter;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import org.elixir_lang.ElixirFileType;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.sdk.ElixirSdkRelease;
import org.elixir_lang.sdk.ElixirSdkType;
import org.elixir_lang.sdk.ElixirSystemUtil;
import org.elixir_lang.settings.ElixirExternalToolsConfigurable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by zyuyou on 15/7/10.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/inspection/SetupSDKNotificationProvider.java
 * todo: extract the common one
 */
public class SetupSDKNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel>{
  private static final Key<EditorNotificationPanel> KEY = Key.create("Setup Elixir SDK");

  private final Project myProject;

  public SetupSDKNotificationProvider(Project project, final EditorNotifications notifications){
    myProject = project;
    myProject.getMessageBus().connect(project).subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
      @Override
      public void rootsChanged(ModuleRootEvent event) {
        notifications.updateAllNotifications();
      }
    });
  }

  @NotNull
  @Override
  public Key<EditorNotificationPanel> getKey() {
    return KEY;
  }

  @Nullable
  @Override
  public EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull FileEditor fileEditor) {
    if(!(file.getFileType() instanceof ElixirFileType)) return null;

    PsiFile psiFile = PsiManager.getInstance(myProject).findFile(file);
    if(psiFile == null || psiFile.getLanguage() != ElixirLanguage.INSTANCE) return null;

    ElixirSdkRelease sdkRelease = ElixirSdkType.getRelease(psiFile);
    if(sdkRelease != null) return null;

    return createPanel(myProject, psiFile);
  }

  @NotNull
  private static EditorNotificationPanel createPanel(@NotNull final Project project, @NotNull final PsiFile file) {
    EditorNotificationPanel panel = new EditorNotificationPanel();

    // project.sdk.not.defined -> "Project SDK is not defined"
    // project.sdk.setup -> "Setup SDK"
    panel.setText(ProjectBundle.message("project.sdk.not.defined"));
    panel.createActionLabel(ProjectBundle.message("project.sdk.setup"), new Runnable() {
      @Override
      public void run() {
        if(ElixirSystemUtil.isSmallIde()){
          ShowSettingsUtil.getInstance().showSettingsDialog(project, ElixirExternalToolsConfigurable.ELIXIR_RELATED_TOOLS);
          return;
        }

        Sdk projectSdk = ProjectSettingsService.getInstance(project).chooseAndSetSdk();
        if(projectSdk == null) return;
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
          @Override
          public void run() {
            Module module = ModuleUtilCore.findModuleForPsiElement(file);
            if(module != null){
              ModuleRootModificationUtil.setSdkInherited(module);
            }
          }
        });
      }
    });
    return panel;
  }

}
