package org.elixir_lang.icons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.LayeredIcon;
import com.intellij.util.PlatformIcons;

import javax.swing.*;

/**
 * Created by zyuyou on 15/7/6.
 */
public interface ElixirIcons {
  Icon FILE = IconLoader.getIcon("/icons/elixir-16.png");
  Icon MIX_MODULE_CONFLICT = AllIcons.Actions.Cancel;

  Icon ELIXIR_APPLICATION = IconLoader.getIcon("/icons/elixir-Application-16.png");
  Icon ELIXIR_SUPERVISOR = IconLoader.getIcon("/icons/elixir-Supervisor-16.png");
  Icon ELIXIR_GEN_EVENT = IconLoader.getIcon("/icons/elixir-GenEvent-16.png");
  Icon ELIXIR_GEN_SERVER = IconLoader.getIcon("/icons/elixir-GenServer-16.png");

  Icon ELIXIR_MARK = IconLoader.getIcon("/icons/elixir-mark.png");
  Icon ELIXIR_MODULE_NODE = new LayeredIcon(PlatformIcons.FOLDER_ICON, ELIXIR_MARK);

  Icon MIX = IconLoader.getIcon("/icons/mix-16.png");
  Icon MIX_EX_UNIT = new LayeredIcon(MIX, AllIcons.Nodes.JunitTestMark);
}
