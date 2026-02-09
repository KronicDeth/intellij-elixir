package org.elixir_lang.jps.builder.sdk_type;

public class MissingHomePath extends Exception {
    public MissingHomePath() {
        super("SDK home path is missing");
    }
}
