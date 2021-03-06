package org.elixir_lang.jps.model;

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
    public SdkProperties createCopy() {
        return new SdkProperties(erlangSdkName);
    }

    @Override
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
