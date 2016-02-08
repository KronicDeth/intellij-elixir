package org.elixir_lang.icons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.LayeredIcon;
import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;

import javax.swing.*;

// RowIcon on travis-ci does not have RowIcon(Icon...) constructor, so fake it
class RowIconFactory {
  public static RowIcon create(Icon... icons) {
    RowIcon rowIcon = new RowIcon(icons.length);

    for (int i = 0; i < icons.length; i++) {
      rowIcon.setIcon(icons[i], i);
    }

    return rowIcon;
  }
}

/**
 * Created by zyuyou on 15/7/6.
 */
public interface ElixirIcons {

  interface Time {
    Icon COMPILE = AllIcons.Actions.Compile;
    Icon RUN = AllIcons.General.Run;
  }

  interface Visibility {
    Icon PRIVATE = PlatformIcons.PRIVATE_ICON;
    Icon PUBLIC = PlatformIcons.PUBLIC_ICON;
  }

  Icon CALL_DEFINITION = PlatformIcons.FUNCTION_ICON;
  Icon CALL_DEFINITION_CLAUSE = RowIconFactory.create(CALL_DEFINITION, PlatformIcons.PACKAGE_LOCAL_ICON);
  Icon DELEGATION = RowIconFactory.create(AllIcons.General.Run, PlatformIcons.PACKAGE_LOCAL_ICON);
  Icon EXCEPTION = PlatformIcons.EXCEPTION_CLASS_ICON;
  Icon FILE = IconLoader.getIcon("/icons/elixir-16.png");
  Icon IMPLEMENTATION = PlatformIcons.ANONYMOUS_CLASS_ICON;
  Icon MIX_MODULE_CONFLICT = AllIcons.Actions.Cancel;
  Icon MODULE = PlatformIcons.PACKAGE_ICON;
  Icon OVERRIDABLE = AllIcons.General.OverridenMethod;

  // it is the unknown that is only a question mark
  Icon UNKNOWN = AllIcons.RunConfigurations.Unknown;

  Icon ELIXIR_APPLICATION = IconLoader.getIcon("/icons/elixir-Application-16.png");
  Icon ELIXIR_SUPERVISOR = IconLoader.getIcon("/icons/elixir-Supervisor-16.png");
  Icon ELIXIR_GEN_EVENT = IconLoader.getIcon("/icons/elixir-GenEvent-16.png");
  Icon ELIXIR_GEN_SERVER = IconLoader.getIcon("/icons/elixir-GenServer-16.png");

  Icon ELIXIR_MARK = IconLoader.getIcon("/icons/elixir-mark.png");
  Icon ELIXIR_MODULE_NODE = new LayeredIcon(PlatformIcons.FOLDER_ICON, ELIXIR_MARK);

  Icon MIX = IconLoader.getIcon("/icons/mix-16.png");
  Icon MIX_EX_UNIT = new LayeredIcon(MIX, AllIcons.Nodes.JunitTestMark);
}

