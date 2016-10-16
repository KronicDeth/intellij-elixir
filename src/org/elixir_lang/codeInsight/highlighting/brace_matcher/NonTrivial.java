package org.elixir_lang.codeInsight.highlighting.brace_matcher;

import com.intellij.codeInsight.highlighting.PairedBraceMatcherAdapter;
import com.intellij.lang.BracePair;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.codeInsight.highlighting.brace_matcher.Paired.DO_END;
import static org.elixir_lang.codeInsight.highlighting.brace_matcher.Paired.FN_END;

public class NonTrivial extends PairedBraceMatcherAdapter {
  public NonTrivial() {
    super(new Paired(), ElixirLanguage.INSTANCE);
  }

  @Nullable
  @Override
  public BracePair findPair(boolean left, HighlighterIterator iterator, CharSequence fileText, FileType fileType) {
    BracePair pair = super.findPair(left, iterator, fileText, fileType);

    if (pair == DO_END || pair == FN_END) {
      iterator.advance();
      IElementType tokenType = iterator.getTokenType();

      if (tokenType == ElixirTypes.KEYWORD_PAIR_COLON) {
        pair = null;
      }

      iterator.retreat();
    }

    return pair;
  }
}
