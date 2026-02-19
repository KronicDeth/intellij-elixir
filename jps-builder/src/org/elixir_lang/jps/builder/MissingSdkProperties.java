package org.elixir_lang.jps.builder;

public class MissingSdkProperties extends Throwable {
    public MissingSdkProperties() {
        super("SdkProperties are required to get Erlang SDK Name");
    }
}
