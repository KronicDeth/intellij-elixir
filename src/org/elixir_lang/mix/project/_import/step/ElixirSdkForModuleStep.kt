package org.elixir_lang.mix.project._import.step

import com.intellij.ide.JavaUiBundle
import com.intellij.ide.util.newProjectWizard.AddModuleWizard
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ex.ProjectRootManagerEx
import com.intellij.openapi.roots.ui.configuration.JdkComboBox
import com.intellij.openapi.roots.ui.configuration.ProjectStructureConfigurable
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel
import com.intellij.openapi.ui.MultiLineLabelUI
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.StartupUiUtil
import org.elixir_lang.Elixir
import org.elixir_lang.Elixir.elixirSdkHasErlangSdk
import org.elixir_lang.sdk.elixir.Type
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

// Ported from `ProjectJdkForModuleStep`
class ElixirSdkForModuleStep(private val wizardContext: WizardContext) : ModuleWizardStep() {
    private val project: Project = wizardContext.project ?: ProjectManager.getInstance().defaultProject
    private val projectSdksModel: ProjectSdksModel = ProjectStructureConfigurable.getInstance(project).projectJdksModel
    private val jdkComboBox: JdkComboBox = JdkComboBox(
        this.project,
        projectSdksModel,
        { it == Type.instance },
        { elixirSdkHasErlangSdk(it) },
        null,
        null
    ).apply {
        addActionListener {
            val defaultProject = ProjectManagerEx.getInstanceEx().defaultProject
            setAsDefaultButton.isEnabled = jdk !== ProjectRootManagerEx.getInstanceEx(defaultProject).projectSdk
        }
    }
    private val setAsDefaultButton: JButton = JButton(JavaUiBundle.message("button.set.default")).apply {
        setMnemonic('D')

        addActionListener {
            val jdk: Sdk? = this@ElixirSdkForModuleStep.jdk
            val defaultProject = ProjectManagerEx.getInstanceEx().defaultProject
            val runnable =
                Runnable { ProjectRootManagerEx.getInstanceEx(defaultProject).projectSdk = jdk }
            ApplicationManager.getApplication().runWriteAction(runnable)

            isEnabled = false
        }
    }
    private val panel: JPanel = JPanel(GridBagLayout()).apply {
        border = BorderFactory.createEtchedBorder()

        val label = JLabel(
            JavaUiBundle.message(
                "prompt.please.select.module.jdk",
                "Elixir SDK"
            )
        ).apply {
            setUI(MultiLineLabelUI())
        }
        add(
            label, GridBagConstraints(
                0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, JBInsets.create(8, 10), 0, 0
            )
        )

        val jdkLabel = JLabel(JavaUiBundle.message("label.project.jdk")).apply {
            font = StartupUiUtil.labelFont.deriveFont(Font.BOLD)
        }
        add(
            jdkLabel, GridBagConstraints(
                0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE, JBUI.insets(8, 10, 0, 10), 0, 0
            )
        )
        jdkLabel.labelFor = jdkComboBox

        add(
            jdkComboBox, GridBagConstraints(
                1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, JBUI.insets(2, 10, 10, 5), 0, 0
            )
        )

        add(
            setAsDefaultButton, GridBagConstraints(
                1, 2, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE, JBUI.insets(2, 10, 10, 5), 0, 0
            )
        )
    }

    override fun getPreferredFocusedComponent(): JComponent = jdkComboBox
    override fun getHelpId(): String? = null
    override fun getComponent(): JComponent = panel

    override fun updateDataModel() {
        wizardContext.projectJdk = jdk
    }

    override fun updateStep() {
        projectSdksModel.reset(project)
        jdkComboBox.reloadModel()
        val defaultJdk = defaultJdk

        if (defaultJdk != null) {
            jdkComboBox.selectedJdk = defaultJdk
        }

        setAsDefaultButton.isEnabled = defaultJdk != null
    }

    private val jdk: Sdk?
        get() = jdkComboBox.selectedJdk

    override fun getIcon(): Icon? = wizardContext.stepIcon

    private val defaultJdk: Sdk?
        get() =
            ProjectRootManagerEx.getInstanceEx(ProjectManagerEx.getInstanceEx().defaultProject).projectSdk
                ?: AddModuleWizard.getMostRecentSuitableSdk(wizardContext)

    override fun validate(): Boolean = jdkComboBox.selectedJdk.let { sdk ->
        if (sdk != null && Elixir.elixirSdkHasErlangSdk(sdk)) {
            try {
                projectSdksModel.apply(null, true)

                true
            } catch (e: ConfigurationException) {
                false
            }
        } else {
            false
        }
    }
}
