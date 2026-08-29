package org.elixir_lang.sdk.erlang_dependent

import com.intellij.openapi.roots.JavadocOrderRootType
import com.intellij.openapi.roots.OrderRootType
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.ProcessOutput
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType
import org.elixir_lang.sdk.erlang.Type as ErlangSdkType

/**
 * Regression tests for https://github.com/KronicDeth/intellij-elixir/issues/976.
 *
 * On a small IDE - PhpStorm, WebStorm, RubyMine and friends - `JavadocOrderRootType` is not
 * registered, so `JavadocOrderRootType.getInstance()` throws
 * `AssertionError: Root type class com.intellij.openapi.roots.JavadocOrderRootType not found`.
 * `org.elixir_lang.sdk.erlang.Type` has routed around that through
 * [org.elixir_lang.sdk.Type.documentationRootType] since the SDK code was converted to Kotlin, but
 * [org.elixir_lang.sdk.erlang_dependent.Type] - which [ElixirSdkType] inherits from without
 * overriding the method - kept calling `getInstance()` directly, and
 * `org.elixir_lang.facet.sdk.Editor.showTabForType` calls it once per registered `OrderRootType`
 * whenever the SDK editor panel is built.
 *
 * These tests cannot reproduce the `AssertionError` itself: tests run on the full platform (`IC`/`IU`),
 * where `getInstance()` resolves fine. What they pin is that the *guarded* path is taken - on a small
 * IDE the documentation root type is not applicable, which is only true if `getInstance()` was never
 * consulted. Small-IDE-ness is forced through [ProcessOutput.isSmallIdeOverride], the test seam added
 * for exactly this purpose.
 */
class RootTypeApplicableTest : PlatformTestCase() {
    @Throws(Exception::class)
    override fun tearDown() {
        try {
            ProcessOutput.isSmallIdeOverride = null
        } finally {
            super.tearDown()
        }
    }

    /**
     * The regression itself. Before the fix this returned `true`, because the inherited
     * `isRootTypeApplicable` compared against `JavadocOrderRootType.getInstance()` unconditionally.
     */
    fun testJavadocRootTypeIsNotApplicableToElixirSdkOnSmallIde() {
        ProcessOutput.isSmallIdeOverride = true

        assertFalse(
            "the Javadoc root type must not be applicable on a small IDE - reaching it means " +
                    "JavadocOrderRootType.getInstance() was called, which throws there",
            ElixirSdkType.instance.isRootTypeApplicable(JavadocOrderRootType.getInstance())
        )
    }

    /**
     * The Elixir SDK inherits `isRootTypeApplicable` from `erlang_dependent.Type`, so the two must
     * agree. This is the invariant the issue's title asks for: the dependent type gets the same
     * protected access the Erlang type already had.
     */
    fun testElixirAndErlangSdkTypesAgreeOnSmallIde() {
        ProcessOutput.isSmallIdeOverride = true

        val javadocRootType = JavadocOrderRootType.getInstance()

        assertEquals(
            "erlang_dependent.Type and erlang.Type must guard the documentation root type the same way",
            ErlangSdkType.instance.isRootTypeApplicable(javadocRootType),
            ElixirSdkType.instance.isRootTypeApplicable(javadocRootType)
        )
    }

    /**
     * The guard must not cost the documentation tab on a full IDE, where the root type does exist.
     */
    fun testJavadocRootTypeStaysApplicableOnFullIde() {
        ProcessOutput.isSmallIdeOverride = false

        assertTrue(
            "the Javadoc root type is registered on a full IDE and must stay applicable there",
            ElixirSdkType.instance.isRootTypeApplicable(JavadocOrderRootType.getInstance())
        )
    }

    /**
     * `CLASSES` and `SOURCES` are platform-wide and unaffected by IDE size; the guard must leave them
     * alone.
     */
    fun testClassesAndSourcesStayApplicableRegardlessOfIdeSize() {
        for (smallIde in listOf(true, false)) {
            ProcessOutput.isSmallIdeOverride = smallIde

            assertTrue(
                "CLASSES must be applicable with isSmallIde=$smallIde",
                ElixirSdkType.instance.isRootTypeApplicable(OrderRootType.CLASSES)
            )
            assertTrue(
                "SOURCES must be applicable with isSmallIde=$smallIde",
                ElixirSdkType.instance.isRootTypeApplicable(OrderRootType.SOURCES)
            )
        }
    }
}
