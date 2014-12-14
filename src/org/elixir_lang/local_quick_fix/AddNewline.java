package org.elixir_lang.local_quick_fix;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.ElixirAdjacentExpression;

/**
 * Created by luke.imhoff on 12/7/14.
 */
public class AddNewline extends AddEndOfExpression {
    /*
     * Constructors
     */

    public AddNewline(PsiElement startElement, ElixirAdjacentExpression adjacentExpression) {
        super(startElement, adjacentExpression);
    }

    /*
     * Methods
     */

    public String displayEndOfExpression() {
        return "newline";
    }

    public String endOfExpression() {
        return "\n";
    }
}
