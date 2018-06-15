package org.elixir_lang.jps;

public class MissingSdkProperties extends Throwable {
    public MissingSdkProperties() {
        super("SdkProperties are required to get Erlang SDK Name");
    }
}
