package org.elixir_lang

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.RowIcon
import com.intellij.util.PlatformIcons
import org.elixir_lang.structure_view.element.Timed
import javax.swing.Icon

// RowIcon on travis-ci does not have RowIcon(Icon...) constructor, so fake it
internal object RowIconFactory {
    fun create(vararg icons: Icon): RowIcon {
        val rowIcon = RowIcon(icons.size)

        for (i in icons.indices) {
            rowIcon.setIcon(icons[i], i)
        }

        return rowIcon
    }
}

/**
 * Created by zyuyou on 15/7/6.
 */
object Icons {
    object Time {
        @JvmField
        val COMPILE = AllIcons.Actions.Compile
        @JvmField
        val RUN = AllIcons.General.Run

        @JvmStatic
        fun from(time: Timed.Time): Icon {
            var icon: Icon? = null

            when (time) {
                Timed.Time.COMPILE -> icon = COMPILE
                Timed.Time.RUN -> icon = RUN
            }

            assert(icon != null)

            return icon
        }
    }

    object Visibility {
        @JvmField
        val PRIVATE = PlatformIcons.PRIVATE_ICON
        @JvmField
        val PUBLIC = PlatformIcons.PUBLIC_ICON

        @JvmStatic
        fun from(visibility: org.elixir_lang.Visibility?): Icon {
            var icon: Icon? = null

            if (visibility != null) {
                when (visibility) {
                    org.elixir_lang.Visibility.PRIVATE -> icon = PRIVATE
                    org.elixir_lang.Visibility.PUBLIC -> icon = PUBLIC
                }

                assert(icon != null)
            } else {
                icon = UNKNOWN
            }

            return icon!!
        }
    }

    @JvmField
    val CALLBACK = AllIcons.Gutter.ImplementedMethod
    @JvmField
    val CALL_DEFINITION = PlatformIcons.FUNCTION_ICON
    @JvmField
    val CALL_DEFINITION_CLAUSE: Icon = RowIconFactory.create(CALL_DEFINITION, PlatformIcons.PACKAGE_LOCAL_ICON)
    @JvmField
    val DELEGATION: Icon = RowIconFactory.create(AllIcons.General.Run, PlatformIcons.PACKAGE_LOCAL_ICON)
    @JvmField
    val EXCEPTION = PlatformIcons.EXCEPTION_CLASS_ICON
    @JvmField
    val FIELD = AllIcons.Nodes.Field
    @JvmField
    val FILE = IconLoader.getIcon("/icons/file/elixir.svg")
    @JvmField
    val IMPLEMENTATION: Icon = RowIconFactory.create(PlatformIcons.ANONYMOUS_CLASS_ICON, AllIcons.General.OverridingMethod)
    @JvmField
    val MIX_MODULE_CONFLICT = AllIcons.Actions.Cancel
    @JvmField
    val OVERRIDABLE = AllIcons.General.OverridenMethod
    @JvmField
    val OVERRIDE = AllIcons.General.OverridingMethod
    @JvmField
    val PARAMETER = AllIcons.Nodes.Parameter
    @JvmField
    val PROTOCOL: Icon = RowIconFactory.create(PlatformIcons.ANONYMOUS_CLASS_ICON, AllIcons.General.OverridenMethod)
    @JvmField
    val STRUCTURE = AllIcons.Toolwindows.ToolWindowStructure
    // same icon as intellij-erlang to match their look and feel
    @JvmField
    val TYPE = IconLoader.getIcon("/icons/type.png")
    // MUST be after TYPE
    @JvmField
    val SPECIFICATION: Icon = RowIconFactory.create(CALL_DEFINITION, TYPE)


    // it is the unknown that is only a question mark
    @JvmField
    val UNKNOWN = AllIcons.RunConfigurations.Unknown

    @JvmField
    val VARIABLE = AllIcons.Nodes.Variable

    object File {
        @JvmField
        val APPLICATION = IconLoader.getIcon("/icons/file/elixir/application.svg")

        @JvmField
        val GEN_EVENT = IconLoader.getIcon("/icons/file/elixir/gen-event.svg")

        @JvmField
        val GEN_SERVER = IconLoader.getIcon("/icons/file/elixir/gen-server.svg")

        @JvmField
        val SUPERVISOR = IconLoader.getIcon("/icons/file/elixir/supervisor.svg")
    }

    @JvmField
    val LANGUAGE = IconLoader.getIcon("/icons/language/elixir.svg")

    @JvmField
    val MODULE = IconLoader.getIcon("/icons/module/elixir.svg")
}

