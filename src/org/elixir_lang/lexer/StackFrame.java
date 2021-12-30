package org.elixir_lang.lexer;

import com.intellij.psi.tree.IElementType;
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

    public static final Map<String, String> TERMINATOR_BY_PROMOTER = new HashMap<>();

    static {
        TERMINATOR_BY_PROMOTER.put("'", "'");
        TERMINATOR_BY_PROMOTER.put("'''", "'''");
        TERMINATOR_BY_PROMOTER.put("(", ")");
        TERMINATOR_BY_PROMOTER.put("/", "/");
        TERMINATOR_BY_PROMOTER.put("<", ">");
        TERMINATOR_BY_PROMOTER.put("[", "]");
        TERMINATOR_BY_PROMOTER.put("\"", "\"");
        TERMINATOR_BY_PROMOTER.put("\"\"\"", "\"\"\"");
        TERMINATOR_BY_PROMOTER.put("{", "}");
        TERMINATOR_BY_PROMOTER.put("|", "|");
    }

    /*
     * Instance
     */

    private Boolean interpolation = null;
    private Integer lastLexicalState;
    private String promoter = null;
    private Character sigilName = null;

    public StackFrame(int lastLexicalState) {
        this.lastLexicalState = lastLexicalState;
    }

    public StackFrame(String promoter, int lastLexicalState) {
        this.interpolation = true;
        this.lastLexicalState = lastLexicalState;
        this.promoter = promoter;
    }

    public boolean isGroup() {
        return this.promoter != null;
    }

    public void nameSigil(char sigilName) {
        setSigilName(sigilName);
        setInterpolation(SigilName.isInterpolating(sigilName));
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
                    "Parent already set to " + this.interpolation + ".  " +
                            "It is illegal to set interpolation more than once in any StackFrame."
            );
        }

        this.interpolation = interpolation;
    }

    public boolean isInterpolating() {
        if (this.interpolation == null) {
            throw new IllegalStateException("Parent not set.");
        }

        return this.interpolation;
    }

    public int getLastLexicalState() {
        if (lastLexicalState == null) {
            throw new IllegalStateException("LastLexicalState not set");
        }

        return lastLexicalState;
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

    public boolean isSigil() {
        return sigilName != null;
    }

    public IElementType sigilNameType() {
        return SigilName.elementType(getSigilName());
    }

    public String getTerminator() {
        String promoter = getPromoter();
        String terminator = TERMINATOR_BY_PROMOTER.get(promoter);

        // unregistered promoters are their own terminators
        if (terminator == null) {
            TERMINATOR_BY_PROMOTER.put(promoter, promoter);
            terminator = promoter;
        }

        return terminator;
    }
}
