package org.elixir_lang.sdk.erlang_dependent;

import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.impl.DependentSdkType;
import com.intellij.openapi.roots.JavadocOrderRootType;
import com.intellij.openapi.roots.OrderRootType;
import org.elixir_lang.sdk.ProcessOutput;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.sdk.elixir.Type.erlangSdkType;

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
        boolean isValidDependency;

        if (ProcessOutput.isSmallIde()) {
            isValidDependency = sdkTypeCanonicalName.equals(ERLANG_SDK_FOR_ELIXIR_SDK_TYPE_CANONICAL_NAME);
        } else {
            isValidDependency = sdkTypeCanonicalName.equals(ERLANG_SDK_TYPE_CANONICAL_NAME) ||
                    sdkTypeCanonicalName.equals(ERLANG_SDK_FOR_ELIXIR_SDK_TYPE_CANONICAL_NAME);
        }

        return isValidDependency;
    }

    @Override
    protected boolean isValidDependency(Sdk sdk) {
        return staticIsValidDependency(sdk);
    }

    @Override
    public String getUnsatisfiedDependencyMessage() {
        return "You need to configure an " + getDependencyType().getName();
    }

    @Override
    protected SdkType getDependencyType() {
        return erlangSdkType(ProjectJdkTable.getInstance());
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
