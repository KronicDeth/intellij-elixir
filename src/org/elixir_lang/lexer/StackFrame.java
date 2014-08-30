package org.elixir_lang.lexer;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.lexer.group.Base;
import org.elixir_lang.lexer.group.Quote;
import org.elixir_lang.lexer.group.Sigil;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luke.imhoff on 8/19/14.
 */
public class StackFrame {
    /*
     * Static
     */

    public static final Map<String, String> terminatorByPromoter = new HashMap<String, String>();

    static {
        terminatorByPromoter.put("'", "'");
        terminatorByPromoter.put("'''", "'''");
        terminatorByPromoter.put("(", ")");
        terminatorByPromoter.put("<", ">");
        terminatorByPromoter.put("[", "]");
        terminatorByPromoter.put("\"", "\"");
        terminatorByPromoter.put("\"\"\"", "\"\"\"");
        terminatorByPromoter.put("{", "}");
    }

    /*
     * Instance
     */

    private Base group = null;
    private Boolean interpolation = null;
    private Integer lastLexicalState = null;
    private String promoter = null;
    private Character sigilName = null;

    public StackFrame(int lastLexicalState) {
        this.lastLexicalState = lastLexicalState;
    }

    public StackFrame(Quote group, String promoter, int lastLexicalState) {
        this.group = group;
        this.interpolation = true;
        this.lastLexicalState = lastLexicalState;
        this.promoter = promoter;
    }

    private Base getGroup() {
        if (this.group == null) {
            throw new IllegalStateException("Group not set.");
        }

        return this.group;
    }

    // private because Quote groups should be set from constructors and Sigil groups should be set from nameSigil
    private void setGroup(Base group) {
        if (this.group != null) {
            throw new IllegalStateException(
                    "Group already set to " + this.group + ".  " +
                            "It is illegal to set group more than once in any StackFrame."
            );
        }

        this.group = group;
    }

    public void nameSigil(char sigilName) {
        Sigil group = Sigil.fetch(sigilName);
        setGroup(group);

        setSigilName(sigilName);
        setInterpolation(SigilName.isInterpolating(sigilName));
    }

    public void setQuotePromoter(String quotePromoter) {
        setInterpolation(true);
        this.group = Quote.fetch(quotePromoter);
    }

    private char getSigilName() {
        if (sigilName == null) {
            throw new IllegalStateException("SigilName is not set.");
        }

        return sigilName;
    }

    // setSigilName is private because public API is nameSigil, which in addition to setting interpolation and sigilName
    // with setSigilName, also sets the group with setGroup.
    private void setSigilName(char sigilName) {
        if (this.sigilName != null) {
            throw new IllegalStateException(
                    "SigilName already set to " + this.sigilName + ".  " +
                            "It is illegal to set sigilName more than once in any StackFrame."
            );
        }

        this.sigilName = sigilName;
    }

    private void setInterpolation(boolean interpolation) {
        if (this.interpolation != null) {
            throw new IllegalStateException(
                    "Interpolation already set to " + this.interpolation + ".  " +
                            "It is illegal to set interpolation more than once in any StackFrame."
            );
        }

        this.interpolation = interpolation;
    }

    public boolean isInterpolating() {
        if (this.interpolation == null) {
            throw new IllegalStateException("Interpolation not set.");
        }

        return this.interpolation.booleanValue();
    }

    public int getLastLexicalState() {
        if (lastLexicalState == null) {
            throw new IllegalStateException("LastLexicalState not set");
        }

        return lastLexicalState.intValue();
    }

    public void setLastLexicalState(int lastLexicalState) {
        if (this.lastLexicalState != null) {
            throw new IllegalStateException("LastLexicalState already set");
        }

        this.lastLexicalState = lastLexicalState;
    }

    public String getPromoter() {
        if (promoter == null) {
            throw new IllegalStateException("Promoter not set.");
        }

        return promoter;
    }

    public void setPromoter(@NotNull String promoter) {
        if (this.promoter != null) {
            throw new IllegalStateException(
                    "Promoter already set to " + this.promoter + ". " +
                            "It is illegal to set promoter more than once in any StackFrame."

            );
        }

        this.promoter = promoter;
    }

    public IElementType promoterType() {
        String promoter = getPromoter();
        Base group = getGroup();
        IElementType promoterType;

        if (Base.isHeredocPromoter(promoter)) {
            promoterType = group.heredocPromoterType;
        } else {
            promoterType = group.promoterType;
        }

        return promoterType;
    }

    public IElementType fragmentType() {
        return getGroup().fragmentType;
    }

    public IElementType sigilNameType() {
        return SigilName.elementType(getSigilName());
    }

    public IElementType terminatorType() {
        String promoter = getPromoter();
        Base group = getGroup();
        IElementType terminatorType;

        if (Base.isHeredocPromoter(promoter)) {
            terminatorType = group.heredocTerminatorType;
        } else {
            terminatorType = group.terminatorType;
        }

        return terminatorType;
    }

    public String getTerminator() {
        String promoter = getPromoter();
        String terminator = terminatorByPromoter.get(promoter);

        // unregistered promoters are their own terminators
        if (terminator == null) {
            terminatorByPromoter.put(promoter, promoter);
            terminator = promoter;
        }

        return terminator;
    }
}
