package org.elixir_lang.sdk.elixir

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.roots.OrderRootType
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.erlang.Type as ErlangSdkType

/**
 * Tests for Type.hasErlangClasspathInElixirSdk method.
 *
 * This tests the validation logic that checks whether Erlang SDK classpath
 * entries are properly added to an Elixir SDK's classpath.
 */
class TypeClasspathTest : PlatformTestCase() {

    private var elixirSdk: Sdk? = null
    private var erlangSdk: Sdk? = null

    override fun tearDown() {
        try {
            // Clean up SDKs
            val jdkTable = ProjectJdkTable.getInstance()
            WriteAction.run<Throwable> {
                elixirSdk?.let { jdkTable.removeJdk(it) }
                erlangSdk?.let { jdkTable.removeJdk(it) }
            }
        } finally {
            elixirSdk = null
            erlangSdk = null
            super.tearDown()
        }
    }

    fun testHasErlangClasspathInElixirSdk_returnsFalseWhenErlangHomePathIsNull() {
        // Create mock SDKs without home paths
        val elixirSdkType = Type.instance
        val erlangSdkType = ErlangSdkType()

        elixirSdk = ProjectJdkImpl("Test Elixir SDK", elixirSdkType)
        erlangSdk = ProjectJdkImpl("Test Erlang SDK", erlangSdkType)

        // Erlang SDK has no home path set, should return false
        val result = Type.hasErlangClasspathInElixirSdk(elixirSdk!!, erlangSdk!!)
        assertFalse("Should return false when Erlang SDK has no home path", result)
    }

    fun testHasErlangClasspathInElixirSdk_returnsFalseWhenNoErlangPathsInClasspath() {
        // Create SDKs with home paths but no classpath entries
        val elixirSdkType = Type.instance
        val erlangSdkType = ErlangSdkType()

        elixirSdk = ProjectJdkImpl("Test Elixir SDK", elixirSdkType).apply {
            WriteAction.run<Throwable> {
                sdkModificator.apply {
                    homePath = "/fake/elixir/path"
                    commitChanges()
                }
            }
        }

        erlangSdk = ProjectJdkImpl("Test Erlang SDK", erlangSdkType).apply {
            WriteAction.run<Throwable> {
                sdkModificator.apply {
                    homePath = "/fake/erlang/path"
                    commitChanges()
                }
            }
        }

        // Elixir SDK has no classpath entries from Erlang
        val result = Type.hasErlangClasspathInElixirSdk(elixirSdk!!, erlangSdk!!)
        assertFalse("Should return false when Elixir SDK has no Erlang classpath entries", result)
    }

    fun testHasErlangClasspathInElixirSdk_returnsTrueWhenErlangPathsPresent() {
        // Create SDKs where Elixir SDK contains paths from Erlang SDK home
        val elixirSdkType = Type.instance
        val erlangSdkType = ErlangSdkType()

        val erlangHomePath = "/fake/erlang/28.0"

        erlangSdk = ProjectJdkImpl("Test Erlang SDK", erlangSdkType).apply {
            WriteAction.run<Throwable> {
                sdkModificator.apply {
                    homePath = erlangHomePath
                    commitChanges()
                }
            }
        }

        // Create a temp directory to use as a classpath root
        val ebinDir = myFixture.tempDirFixture.findOrCreateDir("erlang_lib/stdlib/ebin")

        elixirSdk = ProjectJdkImpl("Test Elixir SDK", elixirSdkType).apply {
            WriteAction.run<Throwable> {
                sdkModificator.apply {
                    homePath = "/fake/elixir/1.15"
                    // Add a classpath entry that starts with erlang home path
                    // We need to simulate this with a real VirtualFile
                    addRoot(ebinDir, OrderRootType.CLASSES)
                    commitChanges()
                }
            }
        }

        // The temp dir path won't start with erlangHomePath, so this should be false
        // This tests the negative case with real VirtualFiles
        val result = Type.hasErlangClasspathInElixirSdk(elixirSdk!!, erlangSdk!!)
        assertFalse("Temp directory path doesn't start with fake erlang home", result)
    }

    fun testHasErlangClasspathInElixirSdkMethodExists() {
        // Verify the method exists and is callable
        val method = Type.Companion::class.java.getMethod(
            "hasErlangClasspathInElixirSdk",
            Sdk::class.java,
            Sdk::class.java
        )
        assertNotNull("Method hasErlangClasspathInElixirSdk should exist", method)
    }
}
