package org.elixir_lang.beam.psi

import com.ericsson.otp.erlang.OtpErlangDecodeException
import com.intellij.lang.FileASTNode
import org.elixir_lang.beam.Beam.Companion.from
import com.intellij.psi.impl.source.PsiFileWithStubSupport
import com.intellij.util.IncorrectOperationException
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.openapi.application.ApplicationManager
import org.elixir_lang.psi.stub.impl.ElixirFileStubImpl
import com.intellij.psi.scope.ElementClassHint
import com.intellij.psi.impl.source.resolve.FileContextUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.FileType
import org.elixir_lang.ElixirLanguage
import com.intellij.psi.impl.source.SourceTreeToPsiMap
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import java.lang.Runnable
import com.intellij.psi.impl.source.PsiFileImpl
import com.intellij.psi.impl.source.tree.TreeElement
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.stubs.*
import com.intellij.reference.SoftReference
import com.intellij.util.ArrayUtil
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.beam.Beam
import org.elixir_lang.beam.Decompiler
import org.elixir_lang.beam.MacroNameArity
import java.io.IOException
import org.elixir_lang.beam.psi.stubs.ModuleStub
import org.elixir_lang.beam.chunk.Atoms
import org.elixir_lang.beam.chunk.CallDefinitions
import org.elixir_lang.beam.psi.impl.*
import org.elixir_lang.beam.psi.stubs.CallDefinitionStub
import org.jetbrains.annotations.NonNls
import java.lang.StringBuilder
import java.util.*
import java.util.function.Consumer

// See com.intellij.psi.impl.compiled.ClsFileImpl
class BeamFileImpl private constructor(private val fileViewProvider: FileViewProvider, private val isForDecompiling: Boolean) : ModuleElementImpl(), PsiCompiledFile, PsiFileWithStubSupport {
    /**
     * NOTE: you absolutely MUST NOT hold PsiLock under the mirror lock
     */
    private val mirrorLock = Any()
    private val stubLock = Any()

    @Volatile
    private var mirrorFileElement: TreeElement? = null
    private var stub: SoftReference<StubTree>? = null

    constructor(fileViewProvider: FileViewProvider) : this(fileViewProvider, false) {}

    override fun getDecompiledPsiFile(): PsiFile = mirror as PsiFile

    /**
     * Returns the virtual file corresponding to the PSI file.
     *
     * @return the virtual file, or null if the file exists only in memory.
     */
    override fun getVirtualFile(): VirtualFile = fileViewProvider.virtualFile

    override fun getName(): String = virtualFile.name

    /**
     * Renames the element.
     *
     * @param name the new element name.
     * @return the element corresponding to this element after the rename (either `this`
     * or a different element if the rename caused the element to be replaced).
     * @throws IncorrectOperationException if the modification is not supported or not possible for some reason.
     */
    override fun setName(@NonNls name: String): PsiElement = throw IncorrectOperationException(CAN_NOT_MODIFY_MESSAGE)

    override fun processChildren(processor: PsiElementProcessor<PsiFileSystemItem>): Boolean = true

    /**
     * Returns the directory containing the file.
     *
     * @return the containing directory, or null if the file exists only in memory.
     */
    override fun getContainingDirectory(): PsiDirectory = virtualFile.parent!!.let { manager.findDirectory(it)!! }

    override fun isDirectory(): Boolean = false

    override fun getProject(): Project = manager.project

    /**
     * Returns the PSI manager for the project to which the PSI element belongs.
     *
     * @return the PSI manager instance.
     */
    override fun getManager(): PsiManager = fileViewProvider.manager

    /**
     * Returns the array of children for the PSI element.
     * Important: In some implementations children are only composite elements, i.e. not a leaf elements
     *
     * @return the array of child elements.
     */
    override fun getChildren(): Array<PsiElement> = arrayOf(module())

    private fun module(): Module = moduleStub().psi
    private fun moduleStub(): ModuleStub<Module> = getStub().childrenStubs.single() as ModuleStub<Module>

    private fun getStub(): PsiFileStub<*> = stubTree.root

    override fun getStubTree(): StubTree {
        ApplicationManager.getApplication().assertReadAccessAllowed()
        val unsynchronizedStubTree = SoftReference.dereference(stub)

        return if (unsynchronizedStubTree != null) {
            unsynchronizedStubTree
        } else {
            // build newStub out of lock to avoid deadlock
            val indexStubTree = StubTreeLoader
                    .getInstance()
                    .readOrBuild(project, virtualFile, this) as StubTree?

            val newStubTree = if (indexStubTree == null) {
                if (LOGGER.isDebugEnabled) {
                    LOGGER.debug("No stub for BEAM file in index: " + virtualFile.presentableUrl)
                }

                StubTree(ElixirFileStubImpl())
            } else {
                indexStubTree
            }

            synchronized(stubLock) {
                // recheck with lock held, in case another thread built and set while we were building
                val synchronizedStubTree = SoftReference.dereference(stub)

                if (synchronizedStubTree != null ) {
                    synchronizedStubTree
                } else {
                    newStubTree
                            .root
                            .let { it as PsiFileStubImpl<PsiFile> }
                            .setPsi(this)

                    stub = SoftReference(newStubTree)

                    newStubTree
                }
            }
        }
    }

    override fun getParent(): PsiDirectory = containingDirectory

    /**
     * Returns the first child of the PSI element.
     *
     * @return the first child, or null if the element has no children.
     */
    override fun getFirstChild(): PsiElement? = getStub().childrenStubs.firstOrNull()?.psi

    /**
     * Returns the last child of the PSI element.
     *
     * @return the last child, or null if the element has no children.
     */
    override fun getLastChild(): PsiElement? = getStub().childrenStubs.lastOrNull()?.psi

    /**
     * Returns the next sibling of the PSI element.
     *
     * @return the next sibling, or null if the node is the last in the list of siblings.
     */
    override fun getNextSibling(): PsiElement? {
        val siblings = parent.children
        val i = ArrayUtil.indexOf(siblings, this)

        return if (i < 0 || i >= siblings.size - 1) {
            null
        } else {
            siblings[i + 1]
        }
    }

    /**
     * Returns the previous sibling of the PSI element.
     *
     * @return the previous sibling, or null if the node is the first in the list of siblings.
     */
    override fun getPrevSibling(): PsiElement? {
        val siblings = parent.children
        val i = ArrayUtil.indexOf(siblings, this)

        return if (i < 1) {
            null
        } else {
            siblings[i - 1]
        }
    }

    /**
     * Returns the file containing the PSI element.
     *
     * @return the file instance, or null if the PSI element is not contained in a file (for example,
     * the element represents a package or directory).
     * @throws PsiInvalidElementAccessException if this element is invalid
     */
    override fun getContainingFile(): PsiFile {
        if (!isValid) {
            throw PsiInvalidElementAccessException(this)
        }
        return this
    }

    override fun appendMirrorText(buffer: StringBuilder, indentLevel: Int) {
        buffer.append(BANNER)
        getStub().childrenStubs.single().psi.let { it as ModuleElementImpl }.appendMirrorText(buffer, 0)
    }

    /**
     * Creates a copy of the file containing the PSI element and returns the corresponding
     * element in the created copy. Resolve operations performed on elements in the copy
     * of the file will resolve to elements in the copy, not in the original file.
     *
     * @return the element in the file copy corresponding to this element.
     */
    override fun copy(): PsiElement = this

    /**
     * Checks if this PSI element is valid. Valid elements and their hierarchy members
     * can be accessed for reading and writing. Valid elements can still correspond to
     * underlying documents whose text is different, when those documents have been changed
     * and not yet committed ([PsiDocumentManager.commitDocument]).
     * (In this case an attempt to change PSI will result in an exception).
     *
     *
     * Any access to invalid elements results in [PsiInvalidElementAccessException].
     *
     *
     * Once invalid, elements can't become valid again.
     *
     *
     * Elements become invalid in following cases:
     *
     *  * They have been deleted via PSI operation ([.delete])
     *  * They have been deleted as a result of an incremental reparse (document commit)
     *  * Their containing file has been changed externally, or renamed so that its PSI had to be rebuilt from scratch
     *
     *
     * @return true if the element is valid, false otherwise.
     * @see PsiUtilCore.ensureValid
     */
    override fun isValid(): Boolean = isForDecompiling || virtualFile.isValid

    /**
     * Checks if the contents of the element can be modified (if it belongs to a
     * non-read-only source file.)
     *
     * @return true if the element can be modified, false otherwise.
     */
    override fun isWritable(): Boolean = false

    /**
     * Passes the declarations contained in this PSI element and its children
     * for processing to the specified scope processor.
     *
     * @param processor  the processor receiving the declarations.
     * @param lastParent the child of this element has been processed during the previous
     * step of the tree up walk (declarations under this element do not need
     * to be processed again)
     * @param place      the original element from which the tree up walk was initiated.   @return true if the declaration processing should continue or false if it should be stopped.
     */
    override fun processDeclarations(processor: PsiScopeProcessor,
                                     state: ResolveState,
                                     lastParent: PsiElement?,
                                     place: PsiElement): Boolean {
        processor.handleEvent(PsiScopeProcessor.Event.SET_DECLARATION_HOLDER, this)
        val classHint = processor.getHint(ElementClassHint.KEY)
        return if (classHint == null || classHint.shouldProcess(ElementClassHint.DeclarationKind.CLASS)) {
            processor.execute(module(), state)
        } else {
            true
        }
    }

    /**
     * Returns the element which should be used as the parent of this element in a tree up
     * walk during a resolve operation. For most elements, this returns `getParent()`,
     * but the context can be overridden for some elements like code fragments (see
     * [PsiElementFactory.createCodeBlockCodeFragment]).
     *
     * @return the resolve context element.
     */
    override fun getContext(): PsiElement? = FileContextUtil.getFileContext(this)

    /**
     * Checks if an actual source or class file corresponds to the element. Non-physical elements include,
     * for example, PSI elements created for the watch expressions in the debugger.
     * Non-physical elements do not generate tree change event.
     * Also, [PsiDocumentManager.getDocument] returns null for non-physical elements.
     * Not to be confused with [FileViewProvider.isPhysical].
     *
     * @return true if the element is physical, false otherwise.
     */
    override fun isPhysical(): Boolean = true

    override fun getNode(): FileASTNode? = null

    /**
     * Gets the modification stamp value. Modification stamp is a value changed by any modification
     * of the content of the file. Note that it is not related to the file modification time.
     *
     * @return the modification stamp value
     * @see VirtualFile.getModificationStamp
     */
    override fun getModificationStamp(): Long = virtualFile.modificationStamp

    /**
     * Returns the same file.
     *
     * @return the same file
     */
    override fun getOriginalFile(): PsiFile = this

    /**
     * Returns the file type for the file.
     *
     * @return [org.elixir_lang.beam.FileType.INSTANCE]
     */
    override fun getFileType(): FileType = org.elixir_lang.beam.FileType.INSTANCE

    /**
     * This file.
     *
     * @return a single-element array containing `this`
     */
    @Deprecated("Use {@link FileViewProvider#getAllFiles()} instead.")
    override fun getPsiRoots(): Array<PsiFile> = arrayOf(this)

    override fun getViewProvider(): FileViewProvider = fileViewProvider

    /**
     * Called by the PSI framework when the contents of the file changes. Can be used to invalidate
     * file-level caches. If you override this method, you **must** call the base class implementation.
     */
    override fun subtreeChanged() {
        /* .beam files are assumed not to change internally without the file itself changing, which the file event
           system will pickup */
    }

    /**
     * Checks if it is possible to rename the element to the specified name,
     * and throws an exception if the rename is not possible. Does not actually modify anything.
     *
     * @param name the new name to check the renaming possibility for.
     * @throws IncorrectOperationException if the rename is not supported or not possible for some reason.
     */
    override fun checkSetName(name: String): Unit = throw IncorrectOperationException(CAN_NOT_MODIFY_MESSAGE)

    /**
     * Returns the corresponding PSI element in a decompiled file created by IDEA from
     * the library element.
     *
     * @return the counterpart of the element in decompiled file.
     */
    override fun getMirror(): PsiElement {
        var mirrorTreeElement = mirrorFileElement
        if (mirrorTreeElement == null) {
            synchronized(mirrorLock) {
                mirrorTreeElement = mirrorFileElement
                if (mirrorTreeElement == null) {
                    val file = virtualFile
                    val fileName = file.name + ".decompiled.ex"
                    val document = FileDocumentManager.getInstance().getDocument(file)
                    assert(document != null) { file.url }
                    val mirrorText = document!!.immutableCharSequence
                    val factory = PsiFileFactory.getInstance(manager.project)
                    val mirror = factory.createFileFromText(
                            fileName,
                            ElixirLanguage,
                            mirrorText,
                            false,
                            false
                    )
                    mirrorTreeElement = SourceTreeToPsiMap.psiToTreeNotNull(mirror)
                    try {
                        val finalMirrorTreeElement = mirrorTreeElement!!
                        ProgressManager.getInstance().executeNonCancelableSection(Runnable {
                            setMirror(finalMirrorTreeElement)
                            putUserData(MODULE_DOCUMENT_LINK_KEY, document)
                        })
                    } catch (e: InvalidMirrorException) {
                        LOGGER.error(file.url, e)
                    }
                    (mirror as PsiFileImpl).originalFile = this
                    mirrorFileElement = mirrorTreeElement
                }
            }
        }
        return mirrorTreeElement!!.psi
    }

    override fun setMirror(element: TreeElement) {
        val mirrorElement = SourceTreeToPsiMap.treeToPsiNotNull<PsiElement>(element)
        if (mirrorElement !is ElixirFile) {
            throw InvalidMirrorException("Unexpected mirror file: $mirrorElement")
        }
        setMirrors(arrayOf(module()), mirrorElement.modulars())
    }

    companion object {
        private val LOGGER = Logger.getInstance(BeamFileImpl::class.java)
        private const val CAN_NOT_MODIFY_MESSAGE = "Cannot modify decompiled Beam files"
        private const val BANNER = "# Source code recreated from a .beam file by IntelliJ Elixir\n" +
                "# Function clause bodies is not available\n"
        private val MODULE_DOCUMENT_LINK_KEY = Key.create<Document>("module.document.link")

        @JvmStatic
        fun buildFileStub(bytes: ByteArray?, path: String): Stub? =
                safeFrom(bytes, path)?.let { buildModuleStub(it) }?.parentStub

        private fun safeFrom(bytes: ByteArray?, path: String): Beam? = try {
            from(bytes!!, path)
        } catch (e: IOException) {
            LOGGER.error("IOException during BeamFileImpl.buildFileStub(bytes, $path)", e)
            null
        } catch (e: OtpErlangDecodeException) {
            LOGGER.error("OtpErlangDecodeException during BeamFileImpl.buildFileStub(bytes, $path)", e)
            null
        }

        private fun buildModuleStub(beam: Beam): ModuleStub<*>? = beam
                .atoms()
                ?.let { atoms ->
                    atoms
                            .moduleName()
                            ?.let { moduleName ->
                                val name = Decompiler.defmoduleArgument(moduleName)
                                val parentStub = ElixirFileStubImpl()
                                val moduleStub: ModuleStub<*> = ModuleStubImpl<ModuleImpl<*>>(parentStub, name)
                                buildCallDefinitions(moduleStub, beam, atoms)

                                moduleStub
                            }
                }

        private fun buildCallDefinitions(parentStub: ModuleStub<*>, beam: Beam, atoms: Atoms) {
            CallDefinitions.macroNameAritySortedSet(beam, atoms).forEach(Consumer { macroNameArity: MacroNameArity -> buildCallDefinition(parentStub, macroNameArity) })
        }

        private fun buildCallDefinition(parentStub: ModuleStub<*>,
                                        macroNameArity: MacroNameArity): CallDefinitionStub<*> =
                buildCallDefinition(parentStub, macroNameArity.macro, macroNameArity.name, macroNameArity.arity)

        private fun buildCallDefinition(parentStub: ModuleStub<*>,
                                        macro: String,
                                        name: String,
                                        arity: Int): CallDefinitionStub<*> =
                CallDefinitionStubImpl<CallDefinitionImpl<*>>(parentStub, macro, name, arity)
    }
}
