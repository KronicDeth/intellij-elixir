package org.elixir_lang.local_quick_fix;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.ElixirAdjacentExpression;

/**
 * Created by luke.imhoff on 12/7/14.
 */
public class AddSemicolon extends AddEndOfExpression {
    /*
     * Constructors
     */

    public AddSemicolon(PsiElement startElement, ElixirAdjacentExpression adjacentExpression) {
        super(startElement, adjacentExpression);
    }

    /*
     * Methods
     */

    public String displayEndOfExpression() {
        return "`;`";
    }

    public String endOfExpression() {
        return "; ";
    }
}
