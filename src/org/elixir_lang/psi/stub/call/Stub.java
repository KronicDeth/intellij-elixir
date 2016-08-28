package org.elixir_lang.psi.stub.call;

import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

// I normally wouldn't add the redundant StubBased prefix, but it makes generating from Elixir.bnf work
public class Stub<T extends org.elixir_lang.psi.call.StubBased> extends NamedStubBase<T> {
    private static Collection<String> collectionStringRefToCollectionString(
            Collection<StringRef> canonicalNameCollection
    ) {
        Collection<String> stringCollection = new ArrayList<String>(canonicalNameCollection.size());

        for (StringRef canonicalName : canonicalNameCollection) {
            stringCollection.add(StringRef.toString(canonicalName));
        }

        return stringCollection;
    }

    private static Collection<StringRef> collectionStringToCollectionStringRef(
            Collection<String> canonicalNameCollection
    ) {
        Collection<StringRef> stringRefCollection = new ArrayList<StringRef>(canonicalNameCollection.size());

        for (String canonicalName : canonicalNameCollection) {
            stringRefCollection.add(StringRef.fromString(canonicalName));
        }

        return stringRefCollection;
    }

    /*
     * Fields
     */

    private final Collection<StringRef> canonicalNameCollection;
    private final boolean hasDoBlockOrKeyword;
    private final int resolvedFinalArity;
    private final StringRef resolvedFunctionName;
    @Nullable
    private final StringRef resolvedModuleName;

    /*
     * Constructors
     */

    public Stub(StubElement parent,
                @NotNull IStubElementType elementType,
                @Nullable String resolvedModuleName,
                @Nullable String resolvedFunctionName,
                int resolvedFinalArity,
                boolean hasDoBlockOrKeyword,
                @NotNull String name,
                @NotNull Collection<String> canonicalNameCollection) {
        this(
                parent,
                elementType,
                StringRef.fromString(resolvedModuleName),
                StringRef.fromString(resolvedFunctionName),
                resolvedFinalArity,
                hasDoBlockOrKeyword,
                StringRef.fromString(name),
                collectionStringToCollectionStringRef(canonicalNameCollection)
        );
    }

    public Stub(StubElement parent,
                @NotNull IStubElementType elementType,
                @Nullable StringRef resolvedModuleName,
                @Nullable StringRef resolvedFunctionName,
                int resolvedFinalArity,
                boolean hasDoBlockOrKeyword,
                @NotNull StringRef name,
                @NotNull Collection<StringRef> canonicalNameCollection) {
        super(parent, elementType, name);
        this.canonicalNameCollection = canonicalNameCollection;
        this.hasDoBlockOrKeyword = hasDoBlockOrKeyword;
        this.resolvedFinalArity = resolvedFinalArity;
        this.resolvedFunctionName = resolvedFunctionName;
        this.resolvedModuleName = resolvedModuleName;
    }

    /*
     * Instance Methods
     */

    /**
     * These names do not depend on aliases or nested modules.
     *
     * @return the canonical texts of the reference
     * @see PsiReference#getCanonicalText()
     */
    public Collection<String> canonicalNameCollection() {
        return collectionStringRefToCollectionString(canonicalNameCollection);
    }

    /**
     * Whether this call has a {@code do} block or a {@code :do} keyword, so it is a macro
     *
     * @return {@code true} if {@link Call#getDoBlock()} is NOT {@code null} or there is a {@code "do"} keyword argument
     * @see org.elixir_lang.psi.impl.ElixirPsiImplUtil#keywordArgument(Call, String)
     */
    public boolean hasDoBlockOrKeyword() {
        return hasDoBlockOrKeyword;
    }

    /**
     * The final arity that is non-{@code null}.
     *
     * @return {@link Call#resolvedSecondaryArity()} if it is not {@code null}; {@link Call#resolvedPrimaryArity()} if
     *   it is not {@code null}; otherwise, {@code 0}.
     */
    public Integer resolvedFinalArity() {
        return resolvedFinalArity;
    }

    /**
     * @return name of the function/macro after taking into account any imports
     */
    @Nullable
    public String resolvedFunctionName() {
        return StringRef.toString(resolvedFunctionName);
    }

    /**
     * @return name of the qualifying module after taking into account any aliases
     */
    @Nullable
    public String resolvedModuleName() {
        return StringRef.toString(resolvedModuleName);
    }

}
