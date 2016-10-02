package org.elixir_lang.icons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.LayeredIcon;
import com.intellij.ui.RowIcon;
import com.intellij.util.PlatformIcons;
import org.elixir_lang.structure_view.element.Timed;
import org.elixir_lang.structure_view.element.Visible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    class Time {
        public static final Icon COMPILE = AllIcons.Actions.Compile;
        public static final Icon RUN = AllIcons.General.Run;

        @NotNull
        public static Icon from(@NotNull Timed.Time time) {
            Icon icon = null;

            switch (time) {
                case COMPILE:
                    icon = COMPILE;
                    break;
                case RUN:
                    icon = RUN;
                    break;
            }

            assert icon != null;

            return icon;
        }
    }

    class Visibility {
        public static final Icon PRIVATE = PlatformIcons.PRIVATE_ICON;
        public static final Icon PUBLIC = PlatformIcons.PUBLIC_ICON;

        @NotNull
        public static Icon from(@Nullable Visible.Visibility visibility) {
            Icon icon = null;


            if (visibility != null) {
                switch (visibility) {
                    case PRIVATE:
                        icon = PRIVATE;
                        break;
                    case PUBLIC:
                        icon = PUBLIC;
                        break;
                }

                assert icon != null;
            } else {
                icon = ElixirIcons.UNKNOWN;
            }

            return icon;
        }
    }

    Icon CALLBACK = AllIcons.Gutter.ImplementedMethod;
    Icon CALL_DEFINITION = PlatformIcons.FUNCTION_ICON;
    Icon CALL_DEFINITION_CLAUSE = RowIconFactory.create(CALL_DEFINITION, PlatformIcons.PACKAGE_LOCAL_ICON);
    Icon DELEGATION = RowIconFactory.create(AllIcons.General.Run, PlatformIcons.PACKAGE_LOCAL_ICON);
    Icon EXCEPTION = PlatformIcons.EXCEPTION_CLASS_ICON;
    Icon FIELD = AllIcons.Nodes.Field;
    Icon FILE = IconLoader.getIcon("/icons/elixir-16.png");
    Icon IMPLEMENTATION = RowIconFactory.create(PlatformIcons.ANONYMOUS_CLASS_ICON, AllIcons.General.OverridingMethod);
    Icon MIX_MODULE_CONFLICT = AllIcons.Actions.Cancel;
    Icon MODULE = PlatformIcons.PACKAGE_ICON;
    Icon OVERRIDABLE = AllIcons.General.OverridenMethod;
    Icon OVERRIDE = AllIcons.General.OverridingMethod;
    Icon PARAMETER = AllIcons.Nodes.Parameter;
    Icon PROTOCOL = RowIconFactory.create(PlatformIcons.ANONYMOUS_CLASS_ICON, AllIcons.General.OverridenMethod);
    Icon STRUCTURE = AllIcons.Toolwindows.ToolWindowStructure;
    // same icon as intellij-erlang to match their look and feel
    Icon TYPE = IconLoader.getIcon("/icons/type.png");
    // MUST be after TYPE
    Icon SPECIFICATION = RowIconFactory.create(CALL_DEFINITION, TYPE);


    // it is the unknown that is only a question mark
    Icon UNKNOWN = AllIcons.RunConfigurations.Unknown;

    Icon VARIABLE = AllIcons.Nodes.Variable;

    Icon ELIXIR_APPLICATION = IconLoader.getIcon("/icons/elixir-Application-16.png");
    Icon ELIXIR_SUPERVISOR = IconLoader.getIcon("/icons/elixir-Supervisor-16.png");
    Icon ELIXIR_GEN_EVENT = IconLoader.getIcon("/icons/elixir-GenEvent-16.png");
    Icon ELIXIR_GEN_SERVER = IconLoader.getIcon("/icons/elixir-GenServer-16.png");

    Icon ELIXIR_MARK = IconLoader.getIcon("/icons/elixir-mark.png");
    Icon ELIXIR_MODULE_NODE = new LayeredIcon(PlatformIcons.FOLDER_ICON, ELIXIR_MARK);

    Icon MIX = IconLoader.getIcon("/icons/mix-16.png");
    Icon MIX_EX_UNIT = new LayeredIcon(MIX, AllIcons.Nodes.JunitTestMark);
}

