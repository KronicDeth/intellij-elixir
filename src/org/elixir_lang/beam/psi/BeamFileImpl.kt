package org.elixir_lang.beam.psi;

import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.intellij.lang.ASTNode;
import com.intellij.lang.FileASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.impl.source.PsiFileWithStubSupport;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.resolve.FileContextUtil;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.scope.ElementClassHint;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.reference.SoftReference;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.beam.Beam;
import org.elixir_lang.beam.MacroNameArity;
import org.elixir_lang.beam.chunk.Atoms;
import org.elixir_lang.beam.chunk.CallDefinitions;
import org.elixir_lang.beam.psi.impl.CallDefinitionStubImpl;
import org.elixir_lang.beam.psi.impl.ModuleElementImpl;
import org.elixir_lang.beam.psi.impl.ModuleImpl;
import org.elixir_lang.beam.psi.impl.ModuleStubImpl;
import org.elixir_lang.beam.psi.stubs.CallDefinitionStub;
import org.elixir_lang.beam.psi.stubs.ModuleStub;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.ModuleOwner;
import org.elixir_lang.psi.call.CanonicallyNamed;
import org.elixir_lang.psi.stub.impl.ElixirFileStubImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

import static com.intellij.reference.SoftReference.dereference;
import static org.elixir_lang.beam.Decompiler.defmoduleArgument;
import static org.elixir_lang.beam.chunk.CallDefinitions.macroNameAritySortedSet;
import static org.elixir_lang.beam.psi.stubs.ModuleStubElementTypes.MODULE;


// See com.intellij.psi.impl.compiled.ClsFileImpl
public class BeamFileImpl extends ModuleElementImpl implements ModuleOwner, PsiCompiledFile, PsiFileWithStubSupport {
    private static final Logger LOGGER = Logger.getInstance(BeamFileImpl.class);
    private static final String CAN_NOT_MODIFY_MESSAGE = "Cannot modify decompiled Beam files";
    private static final String BANNER = "# Source code recreated from a .beam file by IntelliJ Elixir\n" +
            "# Function clause bodies is not available\n";
    private static final Key<Document> MODULE_DOCUMENT_LINK_KEY = Key.create("module.document.link");

    @NotNull
    private final FileViewProvider fileViewProvider;
    private final boolean isForDecompiling;
    /**
     * NOTE: you absolutely MUST NOT hold PsiLock under the mirror lock
     */
    private final Object mirrorLock = new Object();
    private final Object stubLock = new Object();
    private volatile TreeElement mirrorFileElement;
    private SoftReference<StubTree> stub;

    public BeamFileImpl(@NotNull FileViewProvider fileViewProvider) {
        this(fileViewProvider, false);
    }

    private BeamFileImpl(@NotNull FileViewProvider fileViewProvider, boolean isForDecompiling) {
        this.fileViewProvider = fileViewProvider;
        this.isForDecompiling = isForDecompiling;
    }

    public static Optional<Stub> buildFileStub(@NotNull byte[] bytes, @NotNull String path) {
        Optional<Beam> beamOptional;

        try {
            beamOptional = Optional.ofNullable(Beam.Companion.from(bytes, path));
        } catch (IOException e) {
            LOGGER.error("IOException during BeamFileImpl.buildFileStub(bytes, " + path + ")",  e);
            beamOptional = Optional.empty();
        } catch (OtpErlangDecodeException e) {
            LOGGER.error("OtpErlangDecodeException during BeamFileImpl.buildFileStub(bytes, " + path + ")", e);
            beamOptional = Optional.empty();
        }

        return beamOptional.flatMap(BeamFileImpl::buildModuleStub).map(StubElement::getParentStub);
    }

    private static Optional<ModuleStub> buildModuleStub(@NotNull Beam beam) {
        Optional<ModuleStub> moduleStubOptional = Optional.empty();

        Atoms atoms = beam.atoms();

        if (atoms != null) {
            String moduleName = atoms.moduleName();

            if (moduleName != null) {
                String name = defmoduleArgument(moduleName);
                ElixirFileStubImpl parentStub = new ElixirFileStubImpl();
                ModuleStub moduleStub = new ModuleStubImpl(parentStub, name);

                buildCallDefinitions(moduleStub, beam, atoms);

                moduleStubOptional = Optional.of(moduleStub);
            }
        }

        return moduleStubOptional;
    }

    private static void buildCallDefinitions(@NotNull ModuleStub parentStub, @NotNull Beam beam, @NotNull Atoms atoms) {
        macroNameAritySortedSet(beam, atoms).forEach(macroNameArity -> buildCallDefinition(parentStub, macroNameArity));
    }

    @NotNull
    private static CallDefinitionStub buildCallDefinition(@NotNull ModuleStub parentStub,
                                                          @NotNull MacroNameArity macroNameArity) {
        //noinspection ConstantConditions
        return buildCallDefinition(parentStub, macroNameArity.macro, macroNameArity.name, macroNameArity.arity);
    }

    @NotNull
    private static CallDefinitionStub buildCallDefinition(@NotNull ModuleStub parentStub,
                                                          @NotNull String macro,
                                                          @NotNull String name,
                                                          @NotNull Integer arity) {
        return new CallDefinitionStubImpl(parentStub, macro, name, arity);
    }

    @Override
    public PsiFile getDecompiledPsiFile() {
        return (PsiFile) getMirror();
    }

    /**
     * Returns the virtual file corresponding to the PSI file.
     *
     * @return the virtual file, or null if the file exists only in memory.
     */
    @Override
    public VirtualFile getVirtualFile() {
        return fileViewProvider.getVirtualFile();
    }

    @NotNull
    @Override
    public String getName() {
        return getVirtualFile().getName();
    }

    /**
     * Renames the element.
     *
     * @param name the new element name.
     * @return the element corresponding to this element after the rename (either <code>this</code>
     * or a different element if the rename caused the element to be replaced).
     * @throws IncorrectOperationException if the modification is not supported or not possible for some reason.
     */
    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException(CAN_NOT_MODIFY_MESSAGE);
    }

    @Override
    public boolean processChildren(PsiElementProcessor<PsiFileSystemItem> processor) {
        return true;
    }

    /**
     * Returns the directory containing the file.
     *
     * @return the containing directory, or null if the file exists only in memory.
     */
    @Override
    public PsiDirectory getContainingDirectory() {
        VirtualFile parentFile = getVirtualFile().getParent();
        PsiDirectory containingDirectory = null;

        if (parentFile != null) {
            containingDirectory = getManager().findDirectory(parentFile);
        }

        return containingDirectory;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    /**
     * Returns the PSI manager for the project to which the PSI element belongs.
     *
     * @return the PSI manager instance.
     */
    @Override
    public PsiManager getManager() {
        return fileViewProvider.getManager();
    }

    /**
     * Returns the array of children for the PSI element.
     * Important: In some implementations children are only composite elements, i.e. not a leaf elements
     *
     * @return the array of child elements.
     */
    @NotNull
    @Override
    public PsiElement[] getChildren() {
        return modulars();
    }

    @NotNull
    public CanonicallyNamed[] modulars() {
        return (CanonicallyNamed[]) getStub().getChildrenByType(MODULE, new ModuleImpl[1]);
    }

    public PsiFileStub getStub() {
        return getStubTree().getRoot();
    }

    @NotNull
    @Override
    public StubTree getStubTree() {
        ApplicationManager.getApplication().assertReadAccessAllowed();

        StubTree stubTree = dereference(stub);
        if (stubTree != null) return stubTree;

        // build newStub out of lock to avoid deadlock
        StubTree newStubTree = (StubTree) StubTreeLoader
                .getInstance()
                .readOrBuild(getProject(), getVirtualFile(), this);

        if (newStubTree == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No stub for BEAM file in index: " + getVirtualFile().getPresentableUrl());
            }


            newStubTree = new StubTree(new ElixirFileStubImpl());
        }

        synchronized (stubLock) {
            stubTree = dereference(stub);
            if (stubTree != null) return stubTree;

            stubTree = newStubTree;

            @SuppressWarnings("unchecked") PsiFileStubImpl<PsiFile> fileStub = (PsiFileStubImpl) stubTree.getRoot();
            fileStub.setPsi(this);

            stub = new SoftReference<StubTree>(stubTree);
        }

        return stubTree;
    }

    @Nullable
    public ASTNode findTreeForStub(StubTree stubTree, StubElement<?> stubElement) {
        return null;
    }

    @Override
    public PsiDirectory getParent() {
        return getContainingDirectory();
    }

    /**
     * Returns the first child of the PSI element.
     *
     * @return the first child, or null if the element has no children.
     */
    @Override
    public PsiElement getFirstChild() {
        @SuppressWarnings("unchecked") final List<StubElement> children = getStub().getChildrenStubs();
        PsiElement firstChild = null;

        if (!children.isEmpty()) {
            firstChild = children.get(0).getPsi();
        }

        return firstChild;
    }

    /**
     * Returns the last child of the PSI element.
     *
     * @return the last child, or null if the element has no children.
     */
    @Override
    public PsiElement getLastChild() {
        @SuppressWarnings("unchecked") final List<StubElement> children = getStub().getChildrenStubs();
        PsiElement lastChild = null;

        if (!children.isEmpty()) {
            lastChild = children.get(children.size() - 1).getPsi();
        }

        return lastChild;
    }

    /**
     * Returns the next sibling of the PSI element.
     *
     * @return the next sibling, or null if the node is the last in the list of siblings.
     */
    @Override
    public PsiElement getNextSibling() {
        @SuppressWarnings("ConstantConditions") final PsiElement[] siblings = getParent().getChildren();
        final int i = ArrayUtil.indexOf(siblings, this);
        PsiElement nextSibling;

        if (i < 0 || i >= siblings.length - 1) {
            nextSibling = null;
        } else {
            nextSibling = siblings[i + 1];
        }

        return nextSibling;
    }

    /**
     * Returns the previous sibling of the PSI element.
     *
     * @return the previous sibling, or null if the node is the first in the list of siblings.
     */
    @Override
    public PsiElement getPrevSibling() {
        @SuppressWarnings("ConstantConditions") final PsiElement[] siblings = getParent().getChildren();
        final int i = ArrayUtil.indexOf(siblings, this);
        PsiElement prevSibling;

        if (i < 1) {
            prevSibling = null;
        } else {
            prevSibling = siblings[i - 1];
        }

        return prevSibling;
    }

    /**
     * Returns the file containing the PSI element.
     *
     * @return the file instance, or null if the PSI element is not contained in a file (for example,
     * the element represents a package or directory).
     * @throws PsiInvalidElementAccessException if this element is invalid
     */
    @Override
    public PsiFile getContainingFile() throws PsiInvalidElementAccessException {
        if (!isValid()) {
            throw new PsiInvalidElementAccessException(this);
        }

        return this;
    }

    @Override
    public void appendMirrorText(@NotNull StringBuilder buffer, int indentLevel) {
        buffer.append(BANNER);

        CanonicallyNamed[] modulars = modulars();

        if (modulars.length > 0) {
            appendText(modulars[0], buffer, 0);
        }
    }

    /**
     * Creates a copy of the file containing the PSI element and returns the corresponding
     * element in the created copy. Resolve operations performed on elements in the copy
     * of the file will resolve to elements in the copy, not in the original file.
     *
     * @return the element in the file copy corresponding to this element.
     */
    @Override
    public PsiElement copy() {
        return this;
    }

    /**
     * Checks if this PSI element is valid. Valid elements and their hierarchy members
     * can be accessed for reading and writing. Valid elements can still correspond to
     * underlying documents whose text is different, when those documents have been changed
     * and not yet committed ({@link PsiDocumentManager#commitDocument(Document)}).
     * (In this case an attempt to change PSI will result in an exception).
     * <p>
     * Any access to invalid elements results in {@link PsiInvalidElementAccessException}.
     * <p>
     * Once invalid, elements can't become valid again.
     * <p>
     * Elements become invalid in following cases:
     * <ul>
     * <li>They have been deleted via PSI operation ({@link #delete()})</li>
     * <li>They have been deleted as a result of an incremental reparse (document commit)</li>
     * <li>Their containing file has been changed externally, or renamed so that its PSI had to be rebuilt from scratch</li>
     * </ul>
     *
     * @return true if the element is valid, false otherwise.
     * @see PsiUtilCore#ensureValid(PsiElement)
     */
    @Override
    public boolean isValid() {
        return isForDecompiling || getVirtualFile().isValid();
    }

    /**
     * Checks if the contents of the element can be modified (if it belongs to a
     * non-read-only source file.)
     *
     * @return true if the element can be modified, false otherwise.
     */
    @Override
    public boolean isWritable() {
        return false;
    }

    /**
     * Passes the declarations contained in this PSI element and its children
     * for processing to the specified scope processor.
     *
     * @param processor  the processor receiving the declarations.
     * @param lastParent the child of this element has been processed during the previous
     *                   step of the tree up walk (declarations under this element do not need
     *                   to be processed again)
     * @param place      the original element from which the tree up walk was initiated.   @return true if the declaration processing should continue or false if it should be stopped.
     */
    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       @Nullable PsiElement lastParent,
                                       @NotNull PsiElement place) {
        processor.handleEvent(PsiScopeProcessor.Event.SET_DECLARATION_HOLDER, this);
        final ElementClassHint classHint = processor.getHint(ElementClassHint.KEY);
        boolean keepProcessing = true;

        if (classHint == null || classHint.shouldProcess(ElementClassHint.DeclarationKind.CLASS)) {
            for (CanonicallyNamed modular : modulars()) {
                if (!processor.execute(modular, state)) {
                    keepProcessing = false;

                    break;
                }
            }
        }

        return keepProcessing;
    }

    /**
     * Returns the element which should be used as the parent of this element in a tree up
     * walk during a resolve operation. For most elements, this returns <code>getParent()</code>,
     * but the context can be overridden for some elements like code fragments (see
     * {@link PsiElementFactory#createCodeBlockCodeFragment(String, PsiElement, boolean)}).
     *
     * @return the resolve context element.
     */
    @Nullable
    @Override
    public PsiElement getContext() {
        return FileContextUtil.getFileContext(this);
    }

    /**
     * Checks if an actual source or class file corresponds to the element. Non-physical elements include,
     * for example, PSI elements created for the watch expressions in the debugger.
     * Non-physical elements do not generate tree change event.
     * Also, {@link PsiDocumentManager#getDocument(PsiFile)} returns null for non-physical elements.
     * Not to be confused with {@link FileViewProvider#isPhysical()}.
     *
     * @return true if the element is physical, false otherwise.
     */
    @Override
    public boolean isPhysical() {
        return true;
    }

    /**
     * Gets the modification stamp value. Modification stamp is a value changed by any modification
     * of the content of the file. Note that it is not related to the file modification time.
     *
     * @return the modification stamp value
     * @see VirtualFile#getModificationStamp()
     */
    @Override
    public long getModificationStamp() {
        return getVirtualFile().getModificationStamp();
    }

    /**
     * Returns the same file.
     *
     * @return the same file
     */
    @NotNull
    @Override
    public PsiFile getOriginalFile() {
        return this;
    }

    /**
     * Returns the file type for the file.
     *
     * @return {@link org.elixir_lang.beam.FileType#INSTANCE}
     */
    @NotNull
    @Override
    public FileType getFileType() {
        return org.elixir_lang.beam.FileType.INSTANCE;
    }

    /**
     * This file.
     *
     * @return a single-element array containing <code>this</code>
     * @deprecated Use {@link FileViewProvider#getAllFiles()} instead.
     */
    @Deprecated
    @NotNull
    @Override
    public PsiFile[] getPsiRoots() {
        return new PsiFile[]{this};
    }

    @NotNull
    @Override
    public FileViewProvider getViewProvider() {
        return fileViewProvider;
    }

    @Override
    public FileASTNode getNode() {
        return null;
    }

    /**
     * Called by the PSI framework when the contents of the file changes. Can be used to invalidate
     * file-level caches. If you override this method, you <b>must</b> call the base class implementation.
     */
    @Override
    public void subtreeChanged() {
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
    @Override
    public void checkSetName(String name) throws IncorrectOperationException {
        throw new IncorrectOperationException(CAN_NOT_MODIFY_MESSAGE);
    }

    /**
     * Returns the corresponding PSI element in a decompiled file created by IDEA from
     * the library element.
     *
     * @return the counterpart of the element in decompiled file.
     */
    @Override
    public PsiElement getMirror() {
        TreeElement mirrorTreeElement = mirrorFileElement;

        if (mirrorTreeElement == null) {
            synchronized (mirrorLock) {
                mirrorTreeElement = mirrorFileElement;
                if (mirrorTreeElement == null) {
                    VirtualFile file = getVirtualFile();
                    String fileName = file.getName() + ".decompiled.ex";

                    final Document document = FileDocumentManager.getInstance().getDocument(file);
                    assert document != null : file.getUrl();

                    CharSequence mirrorText = document.getImmutableCharSequence();
                    PsiFileFactory factory = PsiFileFactory.getInstance(getManager().getProject());
                    PsiFile mirror = factory.createFileFromText(
                            fileName,
                            ElixirLanguage.INSTANCE,
                            mirrorText,
                            false,
                            false
                    );

                    mirrorTreeElement = SourceTreeToPsiMap.psiToTreeNotNull(mirror);
                    try {
                        final TreeElement finalMirrorTreeElement = mirrorTreeElement;
                        ProgressManager.getInstance().executeNonCancelableSection(new Runnable() {
                            public void run() {
                                setMirror(finalMirrorTreeElement);
                                putUserData(MODULE_DOCUMENT_LINK_KEY, document);
                            }
                        });
                    } catch (InvalidMirrorException e) {
                        //noinspection ThrowableResultOfMethodCallIgnored
                        LOGGER.error(file.getUrl(), e);
                    }

                    ((PsiFileImpl) mirror).setOriginalFile(this);
                    mirrorFileElement = mirrorTreeElement;
                }
            }
        }

        return mirrorTreeElement.getPsi();
    }

    @Override
    public void setMirror(@NotNull TreeElement element) throws InvalidMirrorException {
        PsiElement mirrorElement = SourceTreeToPsiMap.treeToPsiNotNull(element);

        if (!(mirrorElement instanceof ElixirFile)) {
            throw new InvalidMirrorException("Unexpected mirror file: " + mirrorElement);
        }

        ElixirFile mirrorFile = (ElixirFile) mirrorElement;
        setMirrors(modulars(), mirrorFile.modulars());
    }
}
