package org.elixir_lang.beam.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiElementBase;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.beam.psi.BeamFileImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

// See com.intellij.psi.impl.compiled.ClsElementImpl
public abstract class ModuleElementImpl extends PsiElementBase implements PsiCompiledElement {
    public static final Key<PsiCompiledElement> COMPILED_ELEMENT = Key.create("COMPILED_ELEMENT");
    private static final Logger LOGGER = Logger.getInstance(ModuleElementImpl.class);

    private volatile TreeElement mirror = null;

    protected static void appendText(@NotNull PsiElement stub, @NotNull StringBuilder buffer, int indentLevel) {
        ((ModuleElementImpl) stub).appendMirrorText(buffer, indentLevel);
    }

    protected static <T extends PsiElement> void setMirrors(@NotNull T[] stubs,
                                                            @NotNull T[] mirrors) throws InvalidMirrorException {
        setMirrors(Arrays.asList(stubs), Arrays.asList(mirrors));
    }

    private static <T extends PsiElement> void setMirrors(@NotNull List<T> stubs,
                                                          @NotNull List<T> mirrors) throws InvalidMirrorException {
        if (stubs.size() != mirrors.size()) {
            throw new InvalidMirrorException(stubs, mirrors);
        }
        for (int i = 0; i < stubs.size(); i++) {
            setMirror(stubs.get(i), mirrors.get(i));
        }
    }

    private static <T extends PsiElement> void setMirror(@Nullable T stub,
                                                         @Nullable T mirror) throws InvalidMirrorException {
        if (stub == null || mirror == null) {
            throw new InvalidMirrorException(stub, mirror);
        }
        ((ModuleElementImpl) stub).setMirror(SourceTreeToPsiMap.psiToTreeNotNull(mirror));
    }


    /**
     * Returns the text of the PSI element.
     *
     * @return the element text.
     */
    @Override
    public String getText() {
        PsiElement mirror = getMirror();
        String text;

        if (mirror != null) {
            text = mirror.getText();
        } else {
            StringBuilder buffer = new StringBuilder();
            appendMirrorText(buffer, 0);
            LOGGER.warn(
                    "Mirror wasn't set for " + this + " in " + getContainingFile() +
                    ", expected text '" + buffer + "'"
            );
            text = buffer.toString();
        }

        return text;
    }

    public abstract void appendMirrorText(@NotNull StringBuilder buffer, int indentLevel);

    /**
     * Returns the text of the PSI element as a character array.
     *
     * @return the element text as a character array.
     */
    @NotNull
    @Override
    public char[] textToCharArray() {
        PsiElement mirror = getMirror();
        char[] charArray = ArrayUtil.EMPTY_CHAR_ARRAY;

        if (mirror != null) {
            charArray = mirror.textToCharArray();
        }

        return charArray;
    }

    /**
     * Returns the corresponding PSI element in a decompiled file created by IDEA from
     * the library element.
     *
     * @return the counterpart of the element in decompiled file.
     */
    @Override
    public PsiElement getMirror() {
        TreeElement nonVolatileMirror = mirror;

        if (nonVolatileMirror == null) {
            ((BeamFileImpl) getContainingFile()).getMirror();
            nonVolatileMirror = mirror;
        }

        return SourceTreeToPsiMap.treeElementToPsi(nonVolatileMirror);
    }

    public abstract void setMirror(@NotNull TreeElement element) throws InvalidMirrorException;

    /**
     * Returns the language of the PSI element.
     *
     * @return the language instance.
     */
    @NotNull
    @Override
    public Language getLanguage() {
        return ElixirLanguage.INSTANCE;
    }

    /**
     * Returns the text range in the document occupied by the PSI element.
     *
     * @return the text range.
     */
    @Override
    public TextRange getTextRange() {
        PsiElement mirror = getMirror();
        TextRange textRange = TextRange.EMPTY_RANGE;

        if (mirror != null) {
            textRange = mirror.getTextRange();
        }

        return textRange;
    }

    /**
     * Finds a leaf PSI element at the specified offset from the start of the text range of this node.
     *
     * @param offset the relative offset for which the PSI element is requested.
     * @return the element at the offset, or null if none is found.
     */
    @Nullable
    @Override
    public PsiElement findElementAt(int offset) {
        // Return the fine-grained decompiled *mirror* leaf (a real source-like PsiElement with a non-null
        // getNode()), exactly as the platform's own com.intellij.psi.impl.compiled.ClsFileImpl does. The
        // previous implementation mapped the mirror leaf BACK to the coarse stub element
        // (Module/CallDefinition/TypeDefinition), which is a PsiCompiledElement whose getNode() returns null.
        // Global GotoDeclarationHandlers from other installed plugins (JavaScript's JSGotoDeclarationHandler,
        // LESS's LessDeclarationSearcher, ...) call sourceElement.getNode().getElementType() with no null
        // check, so the coarse element made them throw NPE inside GotoDeclarationOrUsageHandler2 /
        // fromGTDProviders, aborting the WHOLE Ctrl-Click gesture - navigation from anywhere inside a
        // decompiled .beam editor did nothing. Returning the mirror leaf gives every reference/navigation
        // provider (ours and third parties') a normal element, and Ctrl-Click / Find Usages flow through the
        // mirror's ordinary Elixir symbol machinery, matching source files.
        PsiElement mirror = getMirror();
        return mirror != null ? mirror.findElementAt(offset) : null;
    }

    /**
     * Finds a reference at the specified offset from the start of the text range of this node.
     *
     * @param offset the relative offset for which the reference is requested.
     * @return the reference at the offset, or null if none is found.
     */
    @Nullable
    @Override
    public PsiReference findReferenceAt(int offset) {
        // Delegate straight to the decompiled mirror (see findElementAt): the mirror's leaves carry the
        // ordinary Elixir references, so Ctrl-Click resolves through the same path source files use, and
        // no coarse getNode()==null element is ever handed to reference/navigation providers.
        PsiElement mirror = getMirror();
        return mirror != null ? mirror.findReferenceAt(offset) : null;
    }

    /**
     * Returns the offset in the file to which the caret should be placed
     * when performing the navigation to the element. (For classes implementing
     * {@link PsiNamedElement}, this should return the offset in the file of the
     * name identifier.)
     *
     * @return the offset of the PSI element.
     */
    @Override
    public int getTextOffset() {
        PsiElement mirror = getMirror();
        int textOffset = -1;

        if (mirror != null) {
            textOffset = mirror.getTextOffset();
        }

        return textOffset;
    }

    /**
     * Returns the length of text of the PSI element.
     *
     * @return the text length.
     */
    @Override
    public int getTextLength() {
        String text = getText();
        int textLength = 0;

        if (text != null) {
            textLength = text.length();
        }

        return textLength;
    }

    @Override
    public int getStartOffsetInParent() {
        PsiElement mirror = getMirror();
        int startOffsetInParent = -1;

        if (mirror != null) {
            startOffsetInParent = mirror.getStartOffsetInParent();
        }

        return startOffsetInParent;
    }

    @SuppressWarnings("PMD.DefaultPackage")
    void setMirrorCheckingType(@NotNull TreeElement element,
                               @Nullable IElementType type) throws InvalidMirrorException {
        if (type != null && element.getElementType() != type) {
            throw new InvalidMirrorException(element.getElementType() + " != " + type);
        }

        element.getPsi().putUserData(COMPILED_ELEMENT, this);
        mirror = element;
    }


    protected static class InvalidMirrorException extends RuntimeException {
        public InvalidMirrorException(@NotNull @NonNls String message) {
            super(message);
        }

        @SuppressWarnings("PMD.DefaultPackage")
        InvalidMirrorException(@Nullable PsiElement stubElement, @Nullable PsiElement mirrorElement) {
            this("stub:" + stubElement + "; mirror:" + mirrorElement);
        }

        @SuppressWarnings("PMD.DefaultPackage")
        InvalidMirrorException(@NotNull List<? extends PsiElement> stubElements,
                               @NotNull List<? extends PsiElement> mirrorElements) {
            this("stub:" + stubElements + "; mirror:" + mirrorElements);
        }
    }
}
