package org.elixir_lang.tool_manager.mise

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import org.elixir_lang.PlatformTestCase
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * Unit tests for [MiseRefreshTrigger.shouldTrigger] (internal).
 *
 * All three branching cases are covered:
 * - [VFileContentChangeEvent] (exact-path match)
 * - [VFileDeleteEvent]        (exact-path match)
 * - [VFileCreateEvent]        (parent-in-contentRoots + child-name pattern match)
 * - Unknown event subtype     (always false)
 *
 * The event subclasses are all `final`, so they are mocked via Mockito 5 + inline agent.
 * Only the fields actually read by the function are stubbed; everything else is left at
 * the Mockito default (null / false / 0).
 */
class MiseRefreshTriggerTest : PlatformTestCase() {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private val exactPaths = setOf("/project/mise.toml", "/home/user/.config/mise/config.toml")
    private val contentRoots = setOf("/project", "/another/project")

    private fun contentChange(path: String): VFileContentChangeEvent =
        mock(VFileContentChangeEvent::class.java).also { `when`(it.path).thenReturn(path) }

    private fun delete(path: String): VFileDeleteEvent =
        mock(VFileDeleteEvent::class.java).also { `when`(it.path).thenReturn(path) }

    private fun create(parentPath: String, childName: String): VFileCreateEvent {
        val parent = mock(VirtualFile::class.java)
        `when`(parent.path).thenReturn(parentPath)
        return mock(VFileCreateEvent::class.java).also {
            `when`(it.parent).thenReturn(parent)
            `when`(it.childName).thenReturn(childName)
        }
    }

    /** A plain unknown subtype - not one of the three handled branches. */
    private fun unknownEvent(): VFileEvent = mock(VFileEvent::class.java)

    // -------------------------------------------------------------------------
    // VFileContentChangeEvent
    // -------------------------------------------------------------------------

    fun testContentChange_pathInExactPaths_returnsTrue() {
        assertTrue(
            MiseRefreshTrigger.shouldTrigger(contentChange("/project/mise.toml"), exactPaths, contentRoots, "")
        )
    }

    fun testContentChange_pathNotInExactPaths_returnsFalse() {
        assertFalse(
            MiseRefreshTrigger.shouldTrigger(contentChange("/project/README.md"), exactPaths, contentRoots, "")
        )
    }

    // -------------------------------------------------------------------------
    // VFileDeleteEvent
    // -------------------------------------------------------------------------

    fun testDelete_pathInExactPaths_returnsTrue() {
        assertTrue(
            MiseRefreshTrigger.shouldTrigger(delete("/project/mise.toml"), exactPaths, contentRoots, "")
        )
    }

    fun testDelete_pathNotInExactPaths_returnsFalse() {
        assertFalse(
            MiseRefreshTrigger.shouldTrigger(delete("/project/mix.exs"), exactPaths, contentRoots, "")
        )
    }

    // -------------------------------------------------------------------------
    // VFileCreateEvent - pattern matching (^mise\.toml$)
    // -------------------------------------------------------------------------

    fun testCreate_miseToml_inContentRoot_returnsTrue() {
        assertTrue(
            MiseRefreshTrigger.shouldTrigger(create("/project", "mise.toml"), exactPaths, contentRoots, "")
        )
    }

    fun testCreate_miseLocalToml_inContentRoot_returnsTrue() {
        // Matches ^mise\..+\.toml$ (the "mise.<env>.toml" pattern)
        assertTrue(
            MiseRefreshTrigger.shouldTrigger(create("/project", "mise.local.toml"), exactPaths, contentRoots, "")
        )
    }

    fun testCreate_miseCustomEnvToml_inContentRoot_returnsTrue() {
        // Another variant of the ^mise\..+\.toml$ pattern
        assertTrue(
            MiseRefreshTrigger.shouldTrigger(create("/project", "mise.test.toml"), exactPaths, contentRoots, "")
        )
    }

    fun testCreate_toolVersions_inContentRoot_returnsTrue() {
        // Matches ^\.tool-versions$
        assertTrue(
            MiseRefreshTrigger.shouldTrigger(create("/project", ".tool-versions"), exactPaths, contentRoots, "")
        )
    }

    fun testCreate_unrelatedFile_inContentRoot_returnsFalse() {
        assertFalse(
            MiseRefreshTrigger.shouldTrigger(create("/project", "README.md"), exactPaths, contentRoots, "")
        )
    }

    fun testCreate_miseToml_parentNotContentRoot_returnsFalse() {
        // The file name matches, but the parent directory is not a watched content root.
        assertFalse(
            MiseRefreshTrigger.shouldTrigger(create("/some/other/dir", "mise.toml"), exactPaths, contentRoots, "")
        )
    }

    fun testCreate_miseTomlBak_trailingChars_returnsFalse() {
        // Trailing ".bak" must not match due to the $ anchor in ^mise\.toml$
        assertFalse(
            MiseRefreshTrigger.shouldTrigger(create("/project", "mise.toml.bak"), exactPaths, contentRoots, "")
        )
    }

    fun testCreate_notMiseToml_leadingChars_returnsFalse() {
        // Leading chars must not match due to the ^ anchor in ^mise\.toml$
        assertFalse(
            MiseRefreshTrigger.shouldTrigger(create("/project", "notmise.toml"), exactPaths, contentRoots, "")
        )
    }

    // -------------------------------------------------------------------------
    // Unknown event type
    // -------------------------------------------------------------------------

    fun testUnknownEventType_returnsFalse() {
        assertFalse(
            MiseRefreshTrigger.shouldTrigger(unknownEvent(), exactPaths, contentRoots, "")
        )
    }

    // -------------------------------------------------------------------------
    // VFileCreateEvent - trusted-configs directory
    // -------------------------------------------------------------------------

    fun testCreate_anyFile_inTrustedConfigsDir_returnsTrue() {
        // `mise trust` creates a file named with an opaque hash in the trusted-configs dir.
        assertTrue(
            MiseRefreshTrigger.shouldTrigger(
                create("/home/user/.local/state/mise/trusted-configs", "IdeaProjects-myproject-abc123"),
                exactPaths, contentRoots,
                "/home/user/.local/state/mise/trusted-configs"
            )
        )
    }

    fun testCreate_anyFile_trustedConfigsDirEmpty_doesNotTrigger() {
        // When trustedConfigsDirString is empty (stateDir unavailable), do not trigger.
        assertFalse(
            MiseRefreshTrigger.shouldTrigger(
                create("/home/user/.local/state/mise/trusted-configs", "IdeaProjects-myproject-abc123"),
                exactPaths, contentRoots,
                ""
            )
        )
    }

    // -------------------------------------------------------------------------
    // VFileContentChangeEvent - trusted-configs directory
    // -------------------------------------------------------------------------

    fun testContentChange_pathInTrustedConfigsDir_returnsTrue() {
        // User re-ran `mise trust` on an already-trusted config - file is updated, not created.
        assertTrue(
            MiseRefreshTrigger.shouldTrigger(
                contentChange("/home/user/.local/state/mise/trusted-configs/IdeaProjects-myproject-abc123"),
                exactPaths, contentRoots,
                "/home/user/.local/state/mise/trusted-configs"
            )
        )
    }

    fun testContentChange_pathInTrustedConfigsDir_trustedConfigsDirEmpty_returnsFalse() {
        assertFalse(
            MiseRefreshTrigger.shouldTrigger(
                contentChange("/home/user/.local/state/mise/trusted-configs/IdeaProjects-myproject-abc123"),
                exactPaths, contentRoots,
                ""
            )
        )
    }

    // -------------------------------------------------------------------------
    // VFileDeleteEvent - trusted-configs directory
    // -------------------------------------------------------------------------

    fun testDelete_pathInTrustedConfigsDir_returnsTrue() {
        // User untrusted the config - the trust file is removed; re-scan to surface the error.
        assertTrue(
            MiseRefreshTrigger.shouldTrigger(
                delete("/home/user/.local/state/mise/trusted-configs/IdeaProjects-myproject-abc123"),
                exactPaths, contentRoots,
                "/home/user/.local/state/mise/trusted-configs"
            )
        )
    }

    fun testDelete_pathInTrustedConfigsDir_trustedConfigsDirEmpty_returnsFalse() {
        assertFalse(
            MiseRefreshTrigger.shouldTrigger(
                delete("/home/user/.local/state/mise/trusted-configs/IdeaProjects-myproject-abc123"),
                exactPaths, contentRoots,
                ""
            )
        )
    }
}
