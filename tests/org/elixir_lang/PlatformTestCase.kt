package org.elixir_lang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Ignore
import java.lang.Exception

@Ignore("abstract")
abstract class PlatformTestCase : BasePlatformTestCase() {
    @Throws(Exception::class)
    override fun tearDown() {
        try {
            super.tearDown()
        } catch (e: Exception) {

        }
    }

}
