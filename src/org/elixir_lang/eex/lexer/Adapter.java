package org.elixir_lang.eex.lexer;

import com.intellij.lexer.FlexAdapter;
import org.elixir_lang.eex.lexer.Flex;

import java.io.Reader;

public class Adapter extends FlexAdapter {
    public Adapter() {
        super(new Flex((Reader) null));
    }
}
