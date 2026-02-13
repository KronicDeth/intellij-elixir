package org.elixir_lang.jps.builder.sdk_type;

import org.elixir_lang.jps.builder.model.SdkProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

public class Elixir extends JpsSdkType<SdkProperties> implements JpsElementTypeWithDefaultProperties<SdkProperties> {
    public static final Elixir INSTANCE = new Elixir();

    @NotNull
    @Override
    public SdkProperties createDefaultProperties() {
        return new SdkProperties(null);
    }
}
