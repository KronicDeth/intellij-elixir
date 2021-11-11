package org.elixir_lang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.lang.Exception

@org.junit.Ignore("abstract")
open class PlatformTestCase : BasePlatformTestCase() {
    @Throws(Exception::class)
    override fun tearDown() {
        try {
            super.tearDown()
        } catch (e: Exception) {

        }
    }

}
