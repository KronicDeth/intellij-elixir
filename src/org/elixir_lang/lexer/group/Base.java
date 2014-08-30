package org.elixir_lang.lexer.group;

import com.intellij.psi.tree.IElementType;

/**
 * Created by luke.imhoff on 8/21/14.
 */
public abstract class Base {
    public final IElementType promoterType;
    public final IElementType heredocPromoterType;
    public final IElementType fragmentType;
    public final IElementType terminatorType;
    public final IElementType heredocTerminatorType;

    public Base(IElementType promoterType, IElementType heredocPromoterType, IElementType fragmentType, IElementType terminatorType, IElementType heredocTerminatorType) {
        this.promoterType = promoterType;
        this.heredocPromoterType = heredocPromoterType;
        this.fragmentType = fragmentType;
        this.terminatorType = terminatorType;
        this.heredocTerminatorType = heredocTerminatorType;
    }

    public IElementType promoterType(String promoter) {
        IElementType promoterType;

        if (isHeredocPromoter(promoter)) {
            promoterType = this.heredocPromoterType;
        } else {
            promoterType = this.promoterType;
        }

        return promoterType;
    }

    public IElementType terminatorType(String terminator) {
        IElementType terminatorType;

        if (isHeredocTerminator(terminator)) {
            terminatorType = this.heredocTerminatorType;
        } else {
            terminatorType = this.terminatorType;
        }

        return terminatorType;
    }

    public static boolean isHeredocPromoter(String promoter) {
        return promoter.equals("\"\"\"") || promoter.equals("'''");
    }
    public static boolean isHeredocTerminator(String terminator) {
        return terminator.equals("\"\"\"") || terminator.equals("'''");
    }
}
