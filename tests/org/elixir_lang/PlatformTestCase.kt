package org.elixir_lang

import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.elixir_lang.injection.markdown.Injector
import java.nio.file.Path

abstract class PlatformTestCase : BasePlatformTestCase() {

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()

        // Allow access to testData directory in tests
        val testDataPath = Path.of(myFixture.testDataPath).toAbsolutePath().toString()
        VfsRootAccess.allowRootAccess(myFixture.testRootDisposable, testDataPath)
    }

}
