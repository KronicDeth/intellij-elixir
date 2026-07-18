package org.elixir_lang.sdk

import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.io.FileUtil
import org.elixir_lang.PlatformTestCase
import java.nio.file.Path

class SdkDetectionContextTest : PlatformTestCase() {

    override fun tearDown() {
        try {
            SdkDetectionContext.clear()
        } finally {
            super.tearDown()
        }
    }

    fun testResolveIsNullWhenNothingPublished() {
        SdkDetectionContext.clear()

        assertNull(SdkDetectionContext.resolve())
        assertNull(SdkDetectionContext.resolve(null))
    }

    fun testPublishedDirectoryIsResolved() {
        val directory = FileUtil.createTempDirectory("sdkContext", null).path

        SdkDetectionContext.set(directory)

        assertEquals(Path.of(directory), SdkDetectionContext.resolve())
    }

    fun testDefaultProjectFallsBackToPublishedDirectory() {
        val directory = FileUtil.createTempDirectory("sdkContext", null).path

        SdkDetectionContext.set(directory)

        assertEquals(
            "The default project carries no location, so the published directory should win",
            Path.of(directory),
            SdkDetectionContext.resolve(ProjectManager.getInstance().defaultProject)
        )
    }

    fun testUnparseableDirectoryResolvesToNull() {
        // A NUL character is rejected by Path.of() on every platform
        SdkDetectionContext.set(Char(0) + "invalid")

        assertNull(SdkDetectionContext.resolve())
    }

    fun testNullDirectoryResolvesToNull() {
        SdkDetectionContext.set(FileUtil.createTempDirectory("sdkContext", null).path)

        SdkDetectionContext.set(null)

        assertNull(SdkDetectionContext.resolve())
    }

    fun testClearRemovesPublishedDirectory() {
        SdkDetectionContext.set(FileUtil.createTempDirectory("sdkContext", null).path)

        SdkDetectionContext.clear()

        assertNull(SdkDetectionContext.resolve())
    }
}
