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
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ex.ProjectRootManagerEx
import com.intellij.openapi.roots.ui.configuration.JdkComboBox
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel
import com.intellij.openapi.ui.MultiLineLabelUI
import com.intellij.platform.eel.EelMachine
import com.intellij.platform.eel.provider.getEelDescriptor
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.StartupUiUtil
import org.elixir_lang.Elixir
import org.elixir_lang.mix.project._import.Builder
import org.elixir_lang.sdk.elixir.Type
import org.jetbrains.annotations.VisibleForTesting
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.nio.file.InvalidPathException
import java.nio.file.Path
import javax.swing.*

// Ported from `ProjectJdkForModuleStep`

class ElixirSdkForModuleStep(private val wizardContext: WizardContext) : ModuleWizardStep() {
    private val project: Project = wizardContext.project ?: ProjectManager.getInstance().defaultProject

    /**
     * The [EelMachine] of the environment the project is being imported from (e.g. a WSL distro
     * when importing from a `\\wsl$\...` path), or null when the import directory cannot be parsed.
     *
     * ProjectSdksModel.reset() filters SDKs to the project's environment, but during import the
     * wizard supplies the default project, which always resolves to the local environment, so
     * WSL/remote SDKs are filtered out. The APIs that would let us pass the real target
     * environment (syncSdks(EelMachine), sdkMatchesEel()) are @ApiStatus.Internal, and
     * implementing Project to fake the path violates @NonExtendable on Project -- see
     * https://youtrack.jetbrains.com/issue/IJPL-243914. Instead, the target environment is
     * resolved from the wizard's project directory with the public (experimental)
     * Path.getEelDescriptor(), and matching SDKs are re-added via the public
     * ProjectSdksModel.addSdk() in [addImportTargetSdks].
     */
    @VisibleForTesting
    internal fun importTargetMachine(): EelMachine? =
        try {
            Path.of(wizardContext.projectFileDirectory).getEelDescriptor().machine
        } catch (_: InvalidPathException) {
            null
        }

    /**
     * The [EelMachine] owning the SDK's home path, or null when the home path is absent or invalid.
     */
    @VisibleForTesting
    internal fun sdkMachine(sdk: Sdk): EelMachine? =
        sdk.homePath?.let { home ->
            try {
                Path.of(home).getEelDescriptor().machine
            } catch (_: InvalidPathException) {
                null
            }
        }

    /**
     * Whether the SDK should be visible for the current import target: hides SDKs from other
     * environments (e.g. local SDKs when importing from WSL), mirroring the environment filtering
     * that ProjectSdksModel.reset() applies for opened projects. Permissive when either
     * environment cannot be determined, so SDK validation stays the deciding factor.
     */
    @VisibleForTesting
    internal fun sdkVisibleForImportTarget(sdk: Sdk): Boolean {
        val targetMachine = importTargetMachine() ?: return true
        val machine = sdkMachine(sdk) ?: return true

        return machine == targetMachine
    }

    /**
     * Re-adds registered SDKs that reset() filtered out because the default project resolves to
     * the local environment. Replicates the internal ProjectSdksModel.syncSdks(EelMachine) using
     * public API: addSdk() clones the SDK into the model and fires sdkAdded, as syncSdks() does,
     * and apply() treats SDKs already present in ProjectJdkTable as updates, not additions.
     */
    private fun addImportTargetSdks() {
        val targetMachine = importTargetMachine() ?: return
        val projectSdks = projectSdksModel.projectSdks

        for (sdk in ProjectJdkTable.getInstance().allJdks) {
            if (projectSdks.containsKey(sdk) || projectSdks.containsValue(sdk)) continue

            if (sdkMachine(sdk) == targetMachine) {
                projectSdksModel.addSdk(sdk)
            }
        }
    }

    /**
     * Create a new ProjectSdksModel instance rather than using ProjectStructureConfigurable's
     * shared instance, so that import-target SDKs added by [addImportTargetSdks] stay local to
     * this wizard step.
     */
    @get:VisibleForTesting
    internal val projectSdksModel: ProjectSdksModel = ProjectSdksModel()
    private val jdkComboBox: JdkComboBox = JdkComboBox(
        this.project,
        projectSdksModel,
        { it == Type.instance },
        { sdk -> Elixir.elixirSdkHasErlangSdk(sdk) && sdkVisibleForImportTarget(sdk) },
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
        (wizardContext.projectBuilder as? Builder)?.elixirSdk = jdk
    }

    override fun updateStep() {
        projectSdksModel.reset(project)
        addImportTargetSdks()
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
