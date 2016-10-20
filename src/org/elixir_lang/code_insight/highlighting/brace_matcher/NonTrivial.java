package org.elixir_lang.code_insight.highlighting.brace_matcher;

import com.intellij.codeInsight.highlighting.PairedBraceMatcherAdapter;
import com.intellij.lang.BracePair;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.Nullable;

public class NonTrivial extends PairedBraceMatcherAdapter {
  public NonTrivial() {
    super(new Paired(), ElixirLanguage.INSTANCE);
  }

  @Nullable
  @Override
  public BracePair findPair(boolean left, HighlighterIterator iterator, CharSequence fileText, FileType fileType) {
    BracePair pair = super.findPair(left, iterator, fileText, fileType);

    if (pair == Paired.DO_END || pair == Paired.FN_END) {
      iterator.advance();

      if (!iterator.atEnd()) {
        IElementType tokenType = iterator.getTokenType();

        if (tokenType == ElixirTypes.KEYWORD_PAIR_COLON) {
          pair = null;
        }
      }

      iterator.retreat();
    }

    return pair;
  }
}
