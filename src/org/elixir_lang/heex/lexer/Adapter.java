package org.elixir_lang.heex.lexer;

import com.intellij.lexer.FlexAdapter;
import org.elixir_lang.heex.lexer.Flex;

import java.io.Reader;

public class Adapter extends FlexAdapter {
    public Adapter() {
        super(new Flex((Reader) null));
    }
}
