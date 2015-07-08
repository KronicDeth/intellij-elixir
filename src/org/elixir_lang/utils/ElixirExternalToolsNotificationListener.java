package org.elixir_lang.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import org.elixir_lang.settings.ElixirExternalToolsConfigurable;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;

/**
 * Created by zyuyou on 15/7/8.
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/utils/ErlangExternalToolsNotificationListener.java
 */
public class ElixirExternalToolsNotificationListener implements NotificationListener {
  @NotNull
  private final Project myProject;

  public ElixirExternalToolsNotificationListener(@NotNull Project project) {
    myProject = project;
  }

  @Override
  public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
    if(event.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
      if(event.getDescription().equals("configure") && !myProject.isDisposed()){
        ShowSettingsUtil.getInstance().showSettingsDialog(myProject, ElixirExternalToolsConfigurable.ELIXIR_RELATED_TOOLS);
      }
    }
  }
}
