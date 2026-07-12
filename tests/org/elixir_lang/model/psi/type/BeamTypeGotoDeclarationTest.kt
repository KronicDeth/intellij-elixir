package org.elixir_lang.model.psi.type

import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertGotoDeclarationChosenAtCaret
import org.elixir_lang.code_insight.assertShowUsagesChosenAtCaret
import org.elixir_lang.code_insight.gotoDeclarationDestinationAtCaret
import org.elixir_lang.code_insight.nonDeclarationUsageCountAtCaret
import org.elixir_lang.code_insight.psiUsagesAtCaret
import java.io.File

/**
 * Behavioural Go To Declaration coverage for types defined in decompiled BEAM modules: remote Erlang
 * library types (`:queue.queue/0`) and the built-in types faked onto `:erlang` (`integer/0`). Unlike
 * source `@type`s these resolve to `TypeDefinitionImpl` decompiled PSI, so they exercise the branch of
 * the type reference that navigates into the `.beam` mirror rather than a source attribute.
 */
class BeamTypeGotoDeclarationTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/type"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
        addBeamLibrary()
    }

    fun testCtrlClickOnRemoteBeamTypeChoosesGotoDeclaration() {
        myFixture.configureByFiles("beam_qualified_type_goto.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationNavigatesToRemoteBeamType() {
        myFixture.configureByFiles("beam_qualified_type_goto.ex")
        val target = myFixture.gotoDeclarationDestinationAtCaret()
        assertNotNull("Go To Declaration should navigate into the decompiled :queue module", target)
        assertTrue(
            "Should land in the decompiled queue.beam, not the source file (was ${target!!.containingFile.name})",
            target.containingFile.name.startsWith("queue")
        )
        assertEquals("Should land on the `queue` type name identifier", "queue", target.text)
        assertTrue(
            "The `queue` identifier should sit inside the decompiled `@type queue` definition",
            generateSequence(target) { it.parent }.any { it.text.startsWith("@type queue") }
        )
    }

    fun testCtrlClickOnBuiltinTypeChoosesGotoDeclaration() {
        myFixture.configureByFiles("builtin_type_goto.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationNavigatesToBuiltinType() {
        myFixture.configureByFiles("builtin_type_goto.ex")
        val target = myFixture.gotoDeclarationDestinationAtCaret()
        assertNotNull("Go To Declaration should navigate into the decompiled :erlang module", target)
        assertTrue(
            "Should land in the decompiled erlang.beam, not the source file (was ${target!!.containingFile.name})",
            target.containingFile.name.startsWith("erlang")
        )
        assertEquals("Should land on the `integer` type name identifier", "integer", target.text)
        assertTrue(
            "The `integer` identifier should sit inside the decompiled `@type integer` definition",
            generateSequence(target) { it.parent }.any { it.text.startsWith("@type integer") }
        )
    }

    fun testFindUsagesFromRemoteBeamTypeUsageFindsSourceUsages() {
        myFixture.configureByFiles("beam_qualified_type_find_usages.ex")
        assertEquals(
            "Find Usages from a `:queue.queue()` usage should find the two source spec sites plus the " +
                "queue/0 usages inside the decompiled queue.beam (specs returning `queue()`)",
            5,
            myFixture.nonDeclarationUsageCountAtCaret(project)
        )
    }

    fun testFindUsagesFromBuiltinTypeUsageFindsSourceUsages() {
        myFixture.configureByFiles("builtin_type_find_usages.ex")
        assertTrue(
            "Find Usages from an `integer()` usage should find both source spec sites",
            myFixture.nonDeclarationUsageCountAtCaret(project) >= 2
        )
    }

    fun testFindUsagesFromWithinBeamTypeFindsSourceUsages() {
        myFixture.configureByFiles("beam_qualified_type_find_usages.ex")
        openBeamAndMoveCaretTo("queue.beam", "@type queue")
        assertEquals(
            "Find Usages on the `@type queue` (queue/0) definition inside the decompiled queue.beam should find " +
                "the two source spec sites plus the queue/0 usages within the same decompiled file - the same " +
                "total as from a source `:queue.queue()` usage of the same symbol",
            5,
            myFixture.nonDeclarationUsageCountAtCaret(project)
        )
    }

    /**
     * Regression guard for the decompiled-`.beam` semantic highlighting wiring.
     *
     * The highlighting daemon ([com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl.queuePassesCreation])
     * decides whether to schedule semantic passes for a `PsiCompiledFile` by calling
     * [com.intellij.codeInsight.daemon.impl.TextEditorBackgroundHighlighter.getCachedFileToHighlight], which swaps
     * the compiled file for `PsiCompiledElement.getCachedMirror()`. If that returns `null`, the daemon bails out on
     * every attempt and no annotator (semantic) highlighting ever runs over the decompiled text. [BeamFileImpl]
     * overrides `getCachedMirror()` to expose the mirror AST built by `getMirror()`; this asserts that gate is open
     * once the mirror has been materialised.
     */
    fun testDecompiledBeamIsSubmittedToHighlightingDaemon() {
        myFixture.configureByFiles("beam_qualified_type_find_usages.ex")

        val beamIo = File(File(testDataPath, "ebin").absoluteFile, "queue.beam")
        val beamVf = com.intellij.openapi.vfs.LocalFileSystem.getInstance().refreshAndFindFileByIoFile(beamIo)!!
        val beamPsi = myFixture.psiManager.findFile(beamVf) as org.elixir_lang.beam.psi.BeamFileImpl

        // Before the mirror is materialised, getCachedMirror() must not trigger decompilation (returns null).
        assertNull(
            "getCachedMirror() must not build the mirror on its own - the daemon relies on it being cheap",
            beamPsi.cachedMirror
        )

        // Materialise the mirror exactly as the daemon's background renew (renewFile -> getDecompiledPsiFile) does.
        val mirror = beamPsi.decompiledPsiFile

        assertSame(
            "Once built, getCachedMirror() must expose the same mirror the daemon will highlight",
            mirror,
            beamPsi.cachedMirror
        )

        val context = com.intellij.codeInsight.multiverse.anyContext()
        val submitted = com.intellij.codeInsight.daemon.impl.TextEditorBackgroundHighlighter
            .getCachedFileToHighlight(project, beamVf, context)
        assertSame(
            "The highlighting daemon must submit the decompiled mirror (not null) for the compiled .beam, " +
                "otherwise semantic highlighting is never scheduled",
            mirror,
            submitted
        )
    }

    fun testWithinBeamUsagesSurvivePointerRoundTripToDistinctLines() {
        myFixture.configureByFiles("beam_qualified_type_find_usages.ex")
        openBeamAndMoveCaretTo("queue.beam", "@opaque queue")
        val usages = myFixture.psiUsagesAtCaret(project).filterNot { it.declaration }
        assertTrue("Expected within-beam usages of queue/1", usages.size >= 10)

        // The Find Usages tool window does NOT render PsiUsage objects directly. It converts each one via
        // `PsiUsage2UsageInfo`, which builds `UsageInfo(usage.file, usage.range, false)`, then wraps it in a
        // `UsageInfo2UsageAdapter`. That adapter computes the displayed line (and dedups by it) from the
        // UsageInfo's file/document. Reproduce that exact conversion here with the public `UsageInfo` +
        // `UsageInfo2UsageAdapter`: this is the step the count/range assertions (and even a raw pointer
        // round-trip) skip, which is why they stay green while the IDE collapses every within-beam usage onto
        // line 1. The adapter short-circuits to line -1 for any file whose `getFileType().isBinary()` is true,
        // so the fix is that the within-beam usages must resolve to a file the usage view treats as text.
        val rows = com.intellij.openapi.application.ReadAction.compute<List<Pair<Int, String?>>, RuntimeException> {
            usages.map { u ->
                val adapter = com.intellij.usages.UsageInfo2UsageAdapter(
                    com.intellij.usageView.UsageInfo(u.file, u.range, false)
                )
                Pair(adapter.line, adapter.usageInfo.virtualFile?.name)
            }
        }
        val distinctLines = rows.map { it.first }.distinct().sorted()
        val vfiles = rows.map { it.second }.distinct()
        assertEquals(
            "Within-beam usages must navigate within the decompiled queue.beam, not a phantom in-memory file; " +
                "got vfiles=$vfiles",
            listOf("queue.beam"),
            vfiles
        )
        assertTrue(
            "Within-beam usages must render on several distinct lines in the usage view; instead they " +
                "collapsed to lines $distinctLines",
            distinctLines.size >= 3
        )
    }

    fun testWithinBeamUsagesAreAnchoredToNavigableCompiledFile() {
        myFixture.configureByFiles("beam_qualified_type_find_usages.ex")
        openBeamAndMoveCaretTo("queue.beam", "@opaque queue")
        val usages = myFixture.psiUsagesAtCaret(project).filterNot { it.declaration }
        assertTrue("Expected within-beam usages of queue/1", usages.isNotEmpty())
        // Every within-beam usage must be anchored to the navigable decompiled (compiled) file, not the
        // in-memory mirror `ElixirFile`. The mirror has no editor document/VirtualFile, so the usage view
        // cannot map mirror-anchored usages to their real decompiled lines - they all collapse onto one line.
        val mirrorAnchored = usages.filterNot { it.file is com.intellij.psi.PsiCompiledFile }
        assertTrue(
            "Within-beam Find Usages results must be anchored to the compiled BEAM file so the usage view can " +
                "map them to distinct lines; ${mirrorAnchored.size}/${usages.size} were anchored to the in-memory " +
                "mirror instead (${mirrorAnchored.map { it.file.javaClass.simpleName }.distinct()})",
            mirrorAnchored.isEmpty()
        )
    }

    fun testFindUsagesFromWithinBeamParametrisedTypeFindsWithinBeamUsages() {
        myFixture.configureByFiles("beam_qualified_type_find_usages.ex")
        // Caret on the `queue` name of the parametrised `@opaque queue(item)` (queue/1) definition inside
        // the decompiled queue.beam. Its usages live in the SAME decompiled file - most obviously the
        // right-hand side `queue(any())` of `@type queue :: queue(any())`, plus many @spec references.
        // These are missed because Find Usages is driven by SearchService.searchWord, which relies on the
        // word index; a .beam file's indexed content is the binary bytes, so no occurrence inside the
        // decompiled text is ever visited.
        openBeamAndMoveCaretTo("queue.beam", "@opaque queue")
        assertTrue(
            "Find Usages on the parametrised `@opaque queue(item)` (queue/1) definition inside the decompiled " +
                "queue.beam should find its many within-beam usages - the RHS `queue(any())` plus the numerous " +
                "`@spec` references throughout the module (was ${myFixture.nonDeclarationUsageCountAtCaret(project)})",
            myFixture.nonDeclarationUsageCountAtCaret(project) >= 10
        )
    }

    fun testCtrlClickWithinBeamTypeChoosesShowUsages() {
        myFixture.configureByFiles("beam_qualified_type_find_usages.ex")
        openBeamAndMoveCaretTo("queue.beam", "@type queue")
        myFixture.assertShowUsagesChosenAtCaret(
            "Ctrl+Click on the `@type queue` definition inside the decompiled queue.beam should choose show-usages"
        )
    }

    fun testGoToDeclarationFromWithinBeamTypeUsageNavigatesToDefinition() {
        myFixture.configureByFiles("beam_qualified_type_find_usages.ex")
        // Caret on the `queue` in the right-hand side `queue(any())` of `@type queue :: queue(any())`;
        // that is a usage of the parametrised `queue/1` type (`@opaque queue(item)`), defined in the same beam.
        openBeamAndMoveCaretTo("queue.beam", ":: queue")
        val target = myFixture.gotoDeclarationDestinationAtCaret()
        assertNotNull("Go To Declaration from a type usage inside the decompiled queue.beam should navigate", target)
        assertTrue(
            "Should land in the decompiled queue.beam (was ${target!!.containingFile.name})",
            target.containingFile.name.startsWith("queue")
        )
        assertEquals("Should land on the `queue` type name identifier", "queue", target.text)
        assertTrue(
            "The `queue` identifier should sit inside the decompiled `@opaque queue(item)` definition",
            generateSequence(target) { it.parent }.any { it.text.startsWith("@opaque queue") }
        )
    }

    private fun openBeamAndMoveCaretTo(beamName: String, anchor: String) {
        val beamIo = File(File(testDataPath, "ebin").absoluteFile, beamName)
        val beamVf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(beamIo)
        assertNotNull("Could not find decompiled $beamName in VFS", beamVf)
        myFixture.configureFromExistingVirtualFile(beamVf!!)
        val text = myFixture.editor.document.text
        val anchorIndex = text.indexOf(anchor)
        assertTrue("Anchor '$anchor' not found in decompiled $beamName", anchorIndex >= 0)
        myFixture.editor.caretModel.moveToOffset(anchorIndex + anchor.length - 1)
    }

    @Throws(Exception::class)
    override fun tearDown() {
        try {
            removeBeamLibrary()
        } finally {
            super.tearDown()
        }
    }

    private fun addBeamLibrary() {
        val ebinDir = File(testDataPath, "ebin").absoluteFile
        assertTrue("ebin/ fixture directory not found at ${ebinDir.absolutePath}", ebinDir.isDirectory)

        VfsRootAccess.allowRootAccess(myFixture.testRootDisposable, ebinDir.path)

        val ebinVf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(ebinDir)
        assertNotNull("Could not find ebin/ fixture directory in VFS", ebinVf)

        WriteAction.run<Throwable> {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            val model = libraryTable.modifiableModel
            val library = model.createLibrary(BEAM_LIBRARY_NAME)
            val libModel = library.modifiableModel
            libModel.addRoot(ebinVf!!, OrderRootType.CLASSES)
            libModel.commit()
            model.commit()

            ModuleRootModificationUtil.addDependency(myFixture.module, library)
        }
    }

    private fun removeBeamLibrary() {
        ModuleRootModificationUtil.updateModel(myFixture.module) { model ->
            model.orderEntries
                .filter { it.presentableName == BEAM_LIBRARY_NAME }
                .forEach { model.removeOrderEntry(it) }
        }

        WriteAction.run<Throwable> {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            libraryTable.getLibraryByName(BEAM_LIBRARY_NAME)?.let { library ->
                libraryTable.modifiableModel.let { model ->
                    model.removeLibrary(library)
                    model.commit()
                }
            }
        }
    }

    companion object {
        private const val BEAM_LIBRARY_NAME = "type-goto-beam-lib"
    }
}
