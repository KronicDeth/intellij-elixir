package org.elixir_lang.jps.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.ex.JpsElementBase;

public class SdkProperties extends JpsElementBase<SdkProperties> {
    @Nullable
    private String erlangSdkName;
    @Nullable
    private final String mixHome;
    @Nullable
    private final String mixHomeReplacePrefix;
    private final boolean wslUncPath;

    public SdkProperties(@Nullable String erlangSdkName,
                         @Nullable String mixHome,
                         @Nullable String mixHomeReplacePrefix,
                         boolean wslUncPath) {
        this.erlangSdkName = erlangSdkName;
        this.mixHome = mixHome;
        this.mixHomeReplacePrefix = mixHomeReplacePrefix;
        this.wslUncPath = wslUncPath;
    }

    @NotNull
    @Override
    public SdkProperties createCopy() {
        return new SdkProperties(erlangSdkName, mixHome, mixHomeReplacePrefix, wslUncPath);
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

    @Nullable
    public String getMixHome() {
        return mixHome;
    }

    @Nullable
    public String getMixHomeReplacePrefix() {
        return mixHomeReplacePrefix;
    }

    public boolean isWslUncPath() {
        return wslUncPath;
    }

}
