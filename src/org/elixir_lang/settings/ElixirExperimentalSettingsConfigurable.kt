package org.elixir_lang.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

/**
 * Configurable for Elixir experimental features.
 * Currently supports enabling HTML language injection for ~H sigils used in Phoenix LiveView templates.
 */
class ElixirExperimentalSettingsConfigurable : Configurable, Configurable.Beta {
    private var settingsPanel: DialogPanel? = null
    private val settings = ElixirExperimentalSettings.instance

    override fun createComponent(): JComponent {
        settingsPanel = panel {
            group("HTML Injection in ~H Sigils code blocks") {
                row {
                    checkBox("Enable ~H Sigil HTML language injection")
                        .comment("Provides HTML syntax highlighting and code completion within <a href='https://hexdocs.pm/phoenix_live_view/1.0.3/Phoenix.Component.html#sigil_H/2'>~H Sigils code blocks</a>, when working with Phoenix Live View HEEx templates, otherwise you'll see this as a string.<br/><br/><a href='https://github.com/joshuataylor/intellij-elixir/tree/sigil-injected-languages-poke?tab=readme-ov-file#h-sigil-html-injection-support'>Documentation for ~H Sigil HTML Injection Support.</a>")
                        .bindSelected(
                            getter = { settings.state.enableHtmlInjection },
                            setter = { settings.state.enableHtmlInjection = it }
                        )
                }
            }
            group("Literal Sigil Injection") {
                row {
                    checkBox("Enable literal sigil injection (~S, ~W, etc.)")
                        .comment("Enables literal sigil injection, which allows the Elixir sigil ~S to work. WARNING: THIS IS CURRENTLY BUGGY AND CAUSING ISSUES WITH ~s")
                        .bindSelected(
                            getter = { settings.state.enableLiteralSigilInjection },
                            setter = { settings.state.enableLiteralSigilInjection = it }
                        )
                }
            }
            group("Status Bar Widget") {
                row {
                    // @todo check enableDeleteSdkSmallIDE
                    checkBox("Shows a Status Bar Widget showing if the Elixir SDK is correctly configured") // TODO: localize
                        .comment("Adds a widget to the status bar that indicates whether the Elixir SDK is properly configured for the current project.")
                        .bindSelected(
                            getter = { settings.state.enableStatusBarWidget },
                            setter = { settings.state.enableStatusBarWidget = it }
                        )
                }
            }
//            group("Custom IDE Settings") {
//                row {
//                    // @todo check enableDeleteSdkSmallIDE
//                    checkBox("Enable Delete SDK action in Custom (small) IDEs (e.g. PyCharm, RubyMine)") // TODO: localize
//                        .comment("Adds a 'Delete SDK' action to the SDK selection settings window.")
//                        .bindSelected(
//                            getter = { settings.state.enableDeleteSdkSmallIDE },
//                            setter = { settings.state.enableDeleteSdkSmallIDE = it }
//                        )
//                }
//            }
            //
        }
        return settingsPanel!!
    }

    override fun isModified(): Boolean {
        return settingsPanel?.isModified() ?: false
    }

    override fun apply() {
        settingsPanel?.apply()
        
        // Trigger settings change notification so listeners can react to changes
        settings.updateState(settings.state.copy())
    }

    override fun getDisplayName(): String = "Elixir Experimental Settings"

    override fun reset() {
        settingsPanel?.reset()
    }

    override fun disposeUIResources() {
        settingsPanel = null
    }
}
