package org.elixir_lang.icons

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.LayeredIcon
import com.intellij.ui.RowIcon
import com.intellij.util.IconUtil
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
object ElixirIcons {
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
                icon = ElixirIcons.UNKNOWN
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
    val FILE = IconLoader.getIcon("/icons/elixir-16.png")
    @JvmField
    val IMPLEMENTATION: Icon = RowIconFactory.create(PlatformIcons.ANONYMOUS_CLASS_ICON, AllIcons.General.OverridingMethod)
    @JvmField
    val MIX_MODULE_CONFLICT = AllIcons.Actions.Cancel
    @JvmField
    val MODULE = PlatformIcons.PACKAGE_ICON
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

    @JvmField
    val ELIXIR_APPLICATION = IconLoader.getIcon("/icons/elixir-Application-16.png")
    @JvmField
    val ELIXIR_SUPERVISOR = IconLoader.getIcon("/icons/elixir-Supervisor-16.png")
    @JvmField
    val ELIXIR_GEN_EVENT = IconLoader.getIcon("/icons/elixir-GenEvent-16.png")
    @JvmField
    val ELIXIR_GEN_SERVER = IconLoader.getIcon("/icons/elixir-GenServer-16.png")

    private val ELIXIR_MARK = IconUtil.toSize(IconLoader.getIcon("/icons/elixir-16.png"), 8, 8)
    @JvmField
    val ELIXIR_MODULE_NODE: Icon = LayeredIcon(PlatformIcons.FOLDER_ICON, ELIXIR_MARK)

    private val ELIXIR = IconLoader.getIcon("/icons/elixir-16.png")
    @JvmField
    val IEX = IconUtil.addText(ELIXIR, ">")

    @JvmField
    val MIX = IconLoader.getIcon("/icons/mix-16.png")
    @JvmField
    val MIX_EX_UNIT: Icon = LayeredIcon(MIX, AllIcons.Nodes.JunitTestMark)
    @JvmField
    val IEX_MIX: Icon = IconUtil.addText(MIX, ">")

    @JvmField
    val DISTILLERY = IconLoader.getIcon("/icons/distillery-16.png")
}

