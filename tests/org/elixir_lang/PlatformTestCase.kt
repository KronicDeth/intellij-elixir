package org.elixir_lang

import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.testFramework.LoggedErrorProcessor
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

    /**
     * Executes code that is expected to log a warning, capturing and returning the warning message.
     *
     * @param category The logger category to monitor (e.g., "org.elixir_lang.sdk.erlang.Type")
     * @param block The code to execute that will log the warning
     * @return Pair of (result from block, captured warning message or null)
     */
    protected fun <T> captureLoggedWarning(category: String, block: () -> T): Pair<T, String?> {
        var capturedMessage: String? = null
        var result: T? = null

        val processor = object : LoggedErrorProcessor() {
            override fun processWarn(logCategory: String, message: String, t: Throwable?): Boolean {
                // TestLoggerFactory prefixes categories with '#'
                val normalizedCategory = logCategory.removePrefix("#")
                if (normalizedCategory == category) {
                    capturedMessage = message
                }
                return false
            }
        }

        LoggedErrorProcessor.executeWith<RuntimeException>(processor) {
            result = block()
        }

        @Suppress("UNCHECKED_CAST")
        return Pair(result as T, capturedMessage)
    }

}
