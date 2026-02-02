package org.elixir_lang.mix.project._import.step

import com.intellij.ide.JavaUiBundle
import com.intellij.ide.util.newProjectWizard.AddModuleWizard
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.client.ClientKind
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ex.ProjectRootManagerEx
import com.intellij.openapi.roots.ui.configuration.JdkComboBox
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel
import com.intellij.openapi.ui.MultiLineLabelUI
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.StartupUiUtil
import org.elixir_lang.Elixir
import org.elixir_lang.sdk.elixir.Type
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.SystemDependent
import org.jetbrains.annotations.Unmodifiable
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

// Ported from `ProjectJdkForModuleStep`

class ElixirSdkForModuleStep(private val wizardContext: WizardContext) : ModuleWizardStep() {
    private val project: Project = wizardContext.project ?: ProjectManager.getInstance().defaultProject

    /**
     * Wrapper that makes a project report isDefault() = false and provides the actual project path.
     * This allows ProjectSdksModel.reset() to properly instantiate the EelProvider
     * based on the project's path, even when the underlying project is the default project.
     *
     * Without this wrapper, the default project returns null for projectFilePath, which causes
     * getEelDescriptor() to return LocalEelDescriptor, filtering out WSL/remote SDKs.
     *
     * Note: Implementing Project interface directly is generally discouraged by the platform,
     * but necessary here for delegation pattern to work with the specific methods we need to override.
     *
     * Also note, default methods are not delegated, which is why there are all the straight pass-through
     * overrides.
     */
    @Suppress("UnstableApiUsage", "NonExtendableApiUsage")
    private class NonDefaultProjectWrapper(
        private val delegate: Project,
        private val actualProjectPath: String?
    ) : Project by delegate {
        // To allow us to even attempt to use the Eel methods, we cannot be the default project
        override fun isDefault(): Boolean = false
        // and we need to have an actual project path
        override fun getProjectFilePath(): String? = actualProjectPath

        // Delegate to default method
        override fun getPresentableUrl(): @SystemDependent @NonNls String? {
            return delegate.presentableUrl
        }

        // Delegate to default method
        override fun scheduleSave() {
            delegate.scheduleSave()
        }

        // Delegate to default method
        @Deprecated("Deprecated in Java")
        override fun getComponent(name: String): com.intellij.openapi.components.BaseComponent? {
            return delegate.getComponent(name)
        }

        // Delegate to default method
        override fun <T> getServiceForClient(serviceClass: Class<T?>): T? {
            return delegate.getServiceForClient(serviceClass)
        }

        // Delegate to default method
        override fun <T> getServices(
            serviceClass: Class<T?>,
            client: ClientKind?
        ): @Unmodifiable List<T?> {
            return delegate.getServices(serviceClass, client)
        }

        // Delegate to default method
        override fun <T> getServiceIfCreated(serviceClass: Class<T?>): T? {
            return delegate.getServiceIfCreated(serviceClass)
        }

        // Delegate to default method
        override fun logError(error: Throwable, pluginId: PluginId) {
            delegate.logError(error, pluginId)
        }
    }

    /**
     * The project to use for SDK model operations.
     * If the wizard context's project is the default project, we wrap it to make isDefault() return false
     * and provide the actual import path, which enables proper EelProvider instantiation for environment-aware SDK filtering.
     */
    private val projectForSdkModel: Project = if (project.isDefault) {
        // Construct a project file path from the import directory.
        // The .idea/misc.xml path is used because getEelDescriptor() calls Path.of(filePath).getEelDescriptor(),
        // which only needs the path prefix to determine the environment (e.g., //wsl$/ItronUbuntu/... for WSL).
        val projectPath = wizardContext.projectFileDirectory.let { "$it/.idea/misc.xml" }
        NonDefaultProjectWrapper(project, projectPath)
    } else {
        project
    }

    /**
     * Create a new ProjectSdksModel instance rather than using ProjectStructureConfigurable's shared instance.
     * This allows us to control the project reference passed to reset() via projectForSdkModel,
     * ensuring proper EEL-based SDK filtering for import wizards.
     */
    private val projectSdksModel: ProjectSdksModel = ProjectSdksModel()
    private val jdkComboBox: JdkComboBox = JdkComboBox(
        this.project,
        projectSdksModel,
        { it == Type.instance },
        { Elixir.elixirSdkHasErlangSdk(it) },
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
        projectSdksModel.reset(projectForSdkModel)
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
            } catch (_: ConfigurationException) {
                false
            }
        } else {
            false
        }
    }
}
