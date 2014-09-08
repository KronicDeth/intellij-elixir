package org.elixir_lang.elixir_flex_lexer.group;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.junit.Before;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by luke.imhoff on 9/6/14.
 */
public abstract class GroupTest extends Test {
    protected abstract IElementType fragmentType();
}
