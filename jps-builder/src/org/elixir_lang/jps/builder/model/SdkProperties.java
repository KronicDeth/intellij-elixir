package org.elixir_lang.jps.builder.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.ex.JpsElementBase;

public class SdkProperties extends JpsElementBase<SdkProperties> {
    @Nullable
    private String erlangSdkName;

    public SdkProperties(@Nullable String erlangSdkName) {
        this.erlangSdkName = erlangSdkName;
    }

    @NotNull
    @Override
    // JPS element API still calls createCopy despite removal deprecation.
    @SuppressWarnings("removal")
    public SdkProperties createCopy() {
        return new SdkProperties(erlangSdkName);
    }

    @Override
    // JPS element API still calls applyChanges despite removal deprecation.
    @SuppressWarnings("removal")
    public void applyChanges(@NotNull SdkProperties modified) {
        // not supported
    }

    @NotNull
    public String ensureErlangSdkName() throws ErlangSdkNameMissing {
        String erlangSdkName = this.erlangSdkName;

        if (erlangSdkName == null) {
            throw new ErlangSdkNameMissing();
        }

        return erlangSdkName;
    }

    public void setErlangSdkName(@Nullable String erlangSdkName) {
        this.erlangSdkName = erlangSdkName;
    }
}
