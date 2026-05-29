package org.elixir_lang.mix

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.execution.ParametersListUtil
import org.elixir_lang.PlatformTestCase
import java.util.concurrent.Callable

class InstallMixDependenciesRunConfigurationTest : PlatformTestCase() {
    fun testCreatesInstallRunConfiguration() {
        val projectDir = myFixture.tempDirFixture.findOrCreateDir("mix_install")
        myFixture.tempDirFixture.createFile("mix_install/mix.exs", "defmodule MixInstall.MixProject do\nend\n")

        val settings = ApplicationManager.getApplication()
            .executeOnPooledThread(Callable {
                ReadAction.nonBlocking(Callable {
                    createInstallMixDependenciesRunConfiguration(project, projectDir)
                }).executeSynchronously()
            })
            .get()
        assertNotNull("Expected run configuration to be created", settings)

        val configuration = settings!!.configuration as Configuration
        assertEquals(INSTALL_MIX_DEPS_NAME, settings.name)
        assertNotNull(configuration.workingDirectory)
        assertTrue(FileUtil.pathsEqual(projectDir.path, configuration.workingDirectory))
        assertEquals(ParametersListUtil.join(INSTALL_MIX_DEPS_ARGS), configuration.programParameters)
        assertNotNull(configuration.configurationModule.module)
    }

    fun testCreatesMixDepsStatusRunConfiguration() {
        val projectDir = myFixture.tempDirFixture.findOrCreateDir("mix_status")
        myFixture.tempDirFixture.createFile("mix_status/mix.exs", "defmodule MixStatus.MixProject do\nend\n")

        val settings = ApplicationManager.getApplication()
            .executeOnPooledThread(Callable {
                ReadAction.nonBlocking(Callable {
                    createMixDepsStatusRunConfiguration(project, projectDir)
                }).executeSynchronously()
            })
            .get()
        assertNotNull("Expected run configuration to be created", settings)

        val configuration = settings!!.configuration as Configuration
        assertEquals(MIX_DEPS_STATUS_NAME, settings.name)
        assertNotNull(configuration.workingDirectory)
        assertTrue(FileUtil.pathsEqual(projectDir.path, configuration.workingDirectory))
        assertEquals(MIX_DEPS_STATUS_ARGS, configuration.programParameters)
        assertNotNull(configuration.configurationModule.module)
    }
}
