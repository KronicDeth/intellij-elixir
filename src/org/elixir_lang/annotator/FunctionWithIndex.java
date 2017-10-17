package org.elixir_lang.annotator;

public interface FunctionWithIndex<T, R> {
    R apply(T element, int index);
}
