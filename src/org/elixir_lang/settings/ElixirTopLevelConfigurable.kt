package org.elixir_lang.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

private val LOG = logger<ElixirTopLevelConfigurable>()

/**
 * Top-level Elixir settings page for full IDEs.
 * Hosts the plugin's general settings directly, with child configurables
 * (Credo, Dialyzer, SDKs) providing additional editable settings.
 */
class ElixirTopLevelConfigurable : Configurable, Configurable.NoScroll {
    private var settingsPanel: DialogPanel? = null
    private val settings = ElixirExperimentalSettings.instance

    override fun getDisplayName(): String = "Elixir"

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
                    checkBox("Enable Status Bar Widget showing if the Elixir SDK is correctly configured")
                        .comment("Adds a widget to the status bar that indicates whether the Elixir SDK is properly configured for the current project.")
                        .bindSelected(
                            getter = { settings.state.enableStatusBarWidget },
                            setter = { settings.state.enableStatusBarWidget = it }
                        )
                }
            }
            group("Mix Deps Checker") {
                row {
                    checkBox("Enable automatic Mix deps checking")
                        .comment("Checks dependency status on project open and when mix.exs/mix.lock changes. Requires an Elixir SDK to be configured.")
                        .bindSelected(
                            getter = { settings.state.enableMixDepsCheck },
                            setter = { settings.state.enableMixDepsCheck = it }
                        )
                }
            }
        }
        return settingsPanel!!
    }

    override fun isModified(): Boolean {
        return settingsPanel?.isModified() ?: false
    }

    override fun apply() {
        if (!isModified()) {
            LOG.debug("No modifications detected, skipping apply")
            return
        }

        val oldState = settings.state.copy()
        LOG.debug("ElixirTopLevelConfigurable.apply() - oldState: $oldState")

        settingsPanel?.apply()

        val newState = settings.state.copy()
        LOG.debug("ElixirTopLevelConfigurable.apply() - newState: $newState")

        val messageBus = ApplicationManager.getApplication().messageBus
        messageBus.syncPublisher(ElixirExperimentalSettings.SETTINGS_CHANGED_TOPIC).settingsChanged(oldState, newState)
    }

    override fun reset() {
        settingsPanel?.reset()
    }

    override fun disposeUIResources() {
        settingsPanel = null
    }
}
