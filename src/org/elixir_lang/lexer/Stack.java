package org.elixir_lang.lexer;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.lexer.group.Quote;

/**
 * Created by luke.imhoff on 8/19/14.
 */
public class Stack {
    private final java.util.Stack<StackFrame> stack = new java.util.Stack<>();

    public void clear() {
        stack.clear();
    }

    public int size() {
        return stack.size();
    }

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

    private void push(StackFrame stackFrame) {
        if (stackFrame.getLastLexicalState() == 0 && !stack.empty()) {
            throw new NestedInitial(this);
        }

        stack.push(stackFrame);
    }

    public StackFrame pop() {
        return stack.pop();
    }

    public IElementType fragmentType() {
        return stack.peek().fragmentType();
    }

    public boolean isInterpolating() {
        return stack.peek().isInterpolating();
    }

    public boolean isSigil() {
        return stack.peek().isSigil();
    }

    public void nameSigil(char sigilName) {
        stack.peek().nameSigil(sigilName);
    }

    public IElementType promoterType() {
        return stack.peek().promoterType();
    }

    public void setPromoter(String promoter) {
        stack.peek().setPromoter(promoter);
    }

    public IElementType sigilNameType() {
        return stack.peek().sigilNameType();
    }

    public String terminator() {
        return stack.peek().getTerminator();
    }

    public IElementType terminatorType() {
        return stack.peek().terminatorType();
    }
}
