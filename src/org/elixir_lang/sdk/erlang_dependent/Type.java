package org.elixir_lang.sdk.erlang_dependent;

import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.impl.DependentSdkType;
import com.intellij.openapi.roots.JavadocOrderRootType;
import com.intellij.openapi.roots.OrderRootType;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An SDK that depends on an Erlang SDK, either
 * * org.intellij.erlang.sdk.ErlangSdkType when intellij-erlang IS installed
 * * org.elixir_lang.sdk.erlang.Type when intellij-erlang IS NOT installed
 */
public abstract class Type extends DependentSdkType {
    private static final String ERLANG_SDK_TYPE_CANONICAL_NAME = "org.intellij.erlang.sdk.ErlangSdkType";
    private static final String ERLANG_SDK_FOR_ELIXIR_SDK_TYPE_CANONICAL_NAME = org.elixir_lang.sdk.erlang.Type.class.getCanonicalName();

    protected Type(@NotNull String name) {
        super(name);
    }

    public static boolean staticIsValidDependency(Sdk sdk) {
        String sdkTypeCanonicalName = sdk.getSdkType().getClass().getCanonicalName();

        return sdkTypeCanonicalName.equals(ERLANG_SDK_TYPE_CANONICAL_NAME) ||
                sdkTypeCanonicalName.equals(ERLANG_SDK_FOR_ELIXIR_SDK_TYPE_CANONICAL_NAME);
    }

    @Override
    protected boolean isValidDependency(Sdk sdk) {
        return staticIsValidDependency(sdk);
    }

    @Override
    public String getUnsatisfiedDependencyMessage() {
        return "You need to configure an Erlang SDK (from intellij-erlang) or " +
                "Erlang SDK for Elixir SDK (from intellij-elixir) first";
    }

    @Override
    protected SdkType getDependencyType() {
        Class<? extends SdkType> sdkTypeClass;

        try {
            sdkTypeClass = (Class<? extends SdkType>) Class.forName(ERLANG_SDK_TYPE_CANONICAL_NAME);
        } catch (ClassNotFoundException e) {
            sdkTypeClass = org.elixir_lang.sdk.erlang.Type.class;
        }

        return SdkType.findInstance(sdkTypeClass);
    }

    @Nullable
    @Override
    public com.intellij.openapi.projectRoots.AdditionalDataConfigurable createAdditionalDataConfigurable(
            @NotNull SdkModel sdkModel,
            @NotNull SdkModificator sdkModificator
    ) {
        return null;
    }

    @Override
    public boolean isRootTypeApplicable(@NotNull OrderRootType type) {
        return type == OrderRootType.CLASSES ||
                type == OrderRootType.SOURCES ||
                type == JavadocOrderRootType.getInstance();
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {
        // intentionally left blank
    }
}
