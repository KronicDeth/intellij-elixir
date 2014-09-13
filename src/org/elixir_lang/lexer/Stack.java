package org.elixir_lang.lexer;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.lexer.group.Quote;

/**
 * Created by luke.imhoff on 8/19/14.
 */
public class Stack extends java.util.Stack<StackFrame> {
    /*
     * Instance
     */

    public void push(int currentLexicalState) {
        StackFrame stackFrame = new StackFrame(currentLexicalState);
        push(stackFrame);
    }

    public void push(String quotePromoter, int currentLexicalState) {
        org.elixir_lang.lexer.group.Quote quote = org.elixir_lang.lexer.group.Quote.fetch(quotePromoter);
        push(quote, quotePromoter, currentLexicalState);
    }

    public void push(Quote quote, String quotePromoter, int currentLexicalState) {
        StackFrame stackFrame = new StackFrame(quote, quotePromoter, currentLexicalState);
        push(stackFrame);
    }

    public IElementType fragmentType() {
        return peek().fragmentType();
    }

    public boolean isInterpolating() {
        return peek().isInterpolating();
    }

    public boolean isSigil() {
        return peek().isSigil();
    }

    public void nameSigil(char sigilName) {
        peek().nameSigil(sigilName);
    }

    public IElementType promoterType() {
        return peek().promoterType();
    }

    public void setPromoter(String promoter) {
        peek().setPromoter(promoter);
    }

    public IElementType sigilNameType() {
        return peek().sigilNameType();
    }

    public String terminator() {
        return peek().getTerminator();
    }

    public IElementType terminatorType() {
        return peek().terminatorType();
    }
}
