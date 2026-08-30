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

    /**
     * One error passed to [LoggedErrorProcessor.processError], with the `#` that `TestLoggerFactory`
     * prefixes onto logger names already stripped from [category].
     *
     * [org.elixir_lang.errorreport.Logger] puts its own title in the [Throwable] and a PSI excerpt in
     * the log message, so a test asserting on what *the plugin* reported wants [title], while one
     * asserting on what a platform logger reported wants [message].
     */
    protected data class LoggedError(val category: String, val message: String, val title: String?)

    /**
     * Executes code that may log errors, capturing every one of them.
     *
     * Everything is captured and the caller filters, because the three things worth keying on differ
     * per test - an exact category, a partial one, or the [LoggedError.title] of an error raised
     * through [org.elixir_lang.errorreport.Logger]. Baking any one of those into the helper would
     * leave the other two writing their own [LoggedErrorProcessor].
     *
     * @param suppress whether to swallow what is captured. [LoggedErrorProcessor]'s default action
     *   set includes [LoggedErrorProcessor.Action.RETHROW], so a test that deliberately trips a
     *   logged error fails on the error itself rather than on its own assertion unless this is true.
     *   Pass `false` to keep the default, where any logged error should fail the test outright and
     *   the captured list only sharpens the message.
     * @param block The code to execute
     * @return Pair of (result from block, errors in the order they were logged)
     */
    // `List` is qualified because `org.elixir_lang.List` is a PSI class in this package and shadows it.
    protected fun <T> captureLoggedErrors(
        suppress: Boolean = true,
        block: () -> T
    ): Pair<T, kotlin.collections.List<LoggedError>> {
        val captured = mutableListOf<LoggedError>()
        var result: T? = null

        val processor = object : LoggedErrorProcessor() {
            override fun processError(
                category: String,
                message: String,
                details: Array<out String>,
                t: Throwable?
            ): Set<Action> {
                captured.add(LoggedError(category.removePrefix("#"), message, t?.message))

                return if (suppress) Action.NONE else Action.ALL
            }
        }

        LoggedErrorProcessor.executeWith<RuntimeException>(processor) {
            result = block()
        }

        @Suppress("UNCHECKED_CAST")
        return Pair(result as T, captured.toList())
    }

}
