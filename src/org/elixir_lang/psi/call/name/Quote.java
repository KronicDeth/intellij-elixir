package org.elixir_lang.psi.call.name;

import static org.elixir_lang.psi.call.name.Module.KERNEL;

public class Quote {
    public static final String FUNCTION = "quote";
    public static final String MODULE = KERNEL;
    public static class KeywordArgument {
        public static final String LOCATION = "location";
        public static final String UNQUOTE = "unquote";
    }
}
