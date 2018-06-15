package org.elixir_lang.sdk.erlang_dependent;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.projectRoots.ValidatableSdkAdditionalData;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.elixir_lang.sdk.elixir.Type;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SdkAdditionalData implements ValidatableSdkAdditionalData {
    @NotNull
    private final Sdk elixirSdk;
    @Nullable
    private Sdk erlangSdk;
    @Nullable
    private String erlangSdkName;
    private static final String ERLANG_SDK_NAME = "erlang-sdk-name";

    public SdkAdditionalData(@Nullable Sdk erlangSdk, @NotNull Sdk elixirSdk) {
        this.erlangSdk = erlangSdk;
        this.elixirSdk = elixirSdk;
    }

    // readExternal
    public SdkAdditionalData(@NotNull Sdk elixirSdk) {
        this.elixirSdk = elixirSdk;
    }


    /**
     * Checks if the ERLANG_SDK_NAME properties are configured correctly, and throws an exception
     * if they are not.
     *
     * @param sdkModel the model containing all configured SDKs.
     * @throws ConfigurationException if the ERLANG_SDK_NAME is not configured correctly.
     * @since 5.0.1
     */
    @Override
    public void checkValid(SdkModel sdkModel) throws ConfigurationException {
        if (getErlangSdk() == null) {
            throw new ConfigurationException("Please configure the Erlang ERLANG_SDK_NAME");
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new SdkAdditionalData(erlangSdk, elixirSdk);
    }

    public void readExternal(Element element) throws InvalidDataException {
        DefaultJDOMExternalizer.readExternal(this, element);
        erlangSdkName = element.getAttributeValue(ERLANG_SDK_NAME);
    }

    public void writeExternal(Element element) throws WriteExternalException {
        DefaultJDOMExternalizer.writeExternal(this, element);
        final Sdk sdk = getErlangSdk();

        if (sdk != null) {
            element.setAttribute(ERLANG_SDK_NAME, sdk.getName());
        }
    }

    @Nullable
    public Sdk getErlangSdk() {
        final ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();

        if (erlangSdk == null) {
            if (erlangSdkName != null) {
                erlangSdk = jdkTable.findJdk(erlangSdkName);
                erlangSdkName = null;
            } else {
                for (Sdk jdk : jdkTable.getAllJdks()) {
                    if (Type.staticIsValidDependency(jdk)) {
                        erlangSdk = jdk;
                        break;
                    }
                }
            }
        }

        return erlangSdk;
    }

    public void setErlangSdk(@Nullable Sdk erlangSdk) {
        this.erlangSdk = erlangSdk;
    }

    @NotNull
    public Sdk ensureErlangSdk() throws MissingErlangSdk {
        Sdk erlangSdk = getErlangSdk();

        if (erlangSdk == null) {
            throw new MissingErlangSdk(elixirSdk);
        }

        return erlangSdk;
    }
}
