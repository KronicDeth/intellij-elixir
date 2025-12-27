package org.elixir_lang

import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.testFramework.TestLoggerFactory
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Rule
import java.nio.file.Path

abstract class PlatformTestCase : BasePlatformTestCase() {

    @Rule
    @JvmField
    val testWatcher = TestLoggerFactory.createTestWatcher()

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()

        // Allow access to testData directory in tests
        val testDataPath = Path.of(myFixture.testDataPath).toAbsolutePath().toString()
        VfsRootAccess.allowRootAccess(myFixture.testRootDisposable, testDataPath)
    }

    @Throws(Exception::class)
    override fun tearDown() {
            super.tearDown()
    }

}
