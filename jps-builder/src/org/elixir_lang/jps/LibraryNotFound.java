package org.elixir_lang.jps;

public class LibraryNotFound extends Exception {
    public LibraryNotFound(String name) {
        super("library " + name + " not found");
    }
}
