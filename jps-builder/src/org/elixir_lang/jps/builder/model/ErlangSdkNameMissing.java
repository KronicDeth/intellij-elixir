package org.elixir_lang.jps.builder.model;

public class ErlangSdkNameMissing extends Exception {
    ErlangSdkNameMissing() {
        super("Erlang SDK Name is missing");
    }
}
