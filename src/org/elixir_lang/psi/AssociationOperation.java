package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;

/**
 * A binary operator with a left operand, association operator, and right operand, but the association operator is
 * ignored for quoting, so not an {@link InfixOperation}.
 */
public interface AssociationOperation extends Quotable {
}
