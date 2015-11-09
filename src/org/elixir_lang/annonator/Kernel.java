package org.elixir_lang.annonator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import org.elixir_lang.ElixirSyntaxHighlighter;
import org.elixir_lang.psi.ElixirUnmatchedUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Annotates functions and macros from `Kernel` and `Kernel.SpecialForms` modules.
 */
public class Kernel implements Annotator, DumbAware {
    /*
     * CONSTANTS
     */

    private static final Set<String> RESOLVED_FUNCTION_NAME_SET = new HashSet<String>(
            Arrays.asList(
                    new String[] {
                            "abs",
                            "apply",
                            "apply",
                            "binary_part",
                            "bit_size",
                            "byte_size",
                            "div",
                            "elem",
                            "exit",
                            "function_exported?",
                            "get_and_update_in",
                            "get_in",
                            "hd",
                            "inspect",
                            "is_atom",
                            "is_binary",
                            "is_bitstring",
                            "is_boolean",
                            "is_float",
                            "is_function",
                            "is_function",
                            "is_integer",
                            "is_list",
                            "is_map",
                            "is_number",
                            "is_pid",
                            "is_port",
                            "is_reference",
                            "is_tuple",
                            "length",
                            "macro_exported?",
                            "make_ref",
                            "map_size",
                            "max",
                            "min",
                            "node",
                            "node",
                            "not",
                            "put_elem",
                            "put_in",
                            "rem",
                            "round",
                            "self",
                            "send",
                            "spawn",
                            "spawn",
                            "spawn_link",
                            "spawn_link",
                            "spawn_monitor",
                            "spawn_monitor",
                            "struct",
                            "throw",
                            "tl",
                            "trunc",
                            "tuple_size",
                            "update_in"
                    }
            )
    );

    private static final Set<String> RESOLVED_MACRO_NAME_SET = new HashSet<String>(
            Arrays.asList(
                    new String[] {
                            "@",
                            "alias!",
                            "and",
                            "binding",
                            "def",
                            "defdelegate",
                            "defexception",
                            "defimpl",
                            "defmacro",
                            "defmacrop",
                            "defmodule",
                            "defoverridable",
                            "defp",
                            "defprotocol",
                            "defstruct",
                            "destructure",
                            "get_and_update_in",
                            "if",
                            "in",
                            "is_nil",
                            "match?",
                            "or",
                            "put_in",
                            "raise",
                            "raise",
                            "reraise",
                            "reraise",
                            "sigil_C",
                            "sigil_R",
                            "sigil_S",
                            "sigil_W",
                            "sigil_c",
                            "sigil_r",
                            "sigil_s",
                            "sigil_w",
                            "to_char_list",
                            "to_string",
                            "unless",
                            "update_in",
                            "use",
                            "var!"
                    }
            )
    );

    private static final Set<String> RESOLVED_SPECIAL_FORMS_MACRO_NAME_SET = new HashSet<String>(
            Arrays.asList(
                    new String[]{
                            "__CALLER__",
                            "__DIR__",
                            "__ENV__",
                            "__MODULE__",
                            "__aliases__",
                            "__block__",
                            "alias",
                            "case",
                            "cond",
                            "fn",
                            "for",
                            "import",
                            "quote",
                            "receive",
                            "require",
                            "super",
                            "try",
                            "unquote",
                            "unquote_splicing"
                    }
            )
    );

    /*
     * Public Instance Methods
     */

    /**
     * Annotates the specified PSI element.
     * It is guaranteed to be executed in non-reentrant fashion.
     * I.e there will be no call of this method for this instance before previous call get completed.
     * Multiple instances of the annotator might exist simultaneously, though.
     *
     * @param element to annotate.
     * @param holder  the container which receives annotations created by the plugin.
     */
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull final AnnotationHolder holder) {
        element.accept(
                new ElixirVisitor() {
                    @Override
                    public void visitUnmatchedUnqualifiedNoParenthesesCall(@NotNull ElixirUnmatchedUnqualifiedNoParenthesesCall unmatchedUnqualifiedNoParenthesesCall) {
                        // the Kernel annotator only cares about Elixir.Kernel
                        if (unmatchedUnqualifiedNoParenthesesCall.resolvedModuleName().equals("Elixir.Kernel")) {
                            String resolvedFunctionName = unmatchedUnqualifiedNoParenthesesCall.resolvedFunctionName();

                            // a function can't take a do block
                            if (unmatchedUnqualifiedNoParenthesesCall.getDoBlock() == null) {
                                if (RESOLVED_FUNCTION_NAME_SET.contains(resolvedFunctionName)) {
                                    ASTNode node = unmatchedUnqualifiedNoParenthesesCall.getNode();
                                    ASTNode identifier = node.getFirstChildNode();
                                    highlight(identifier, holder, ElixirSyntaxHighlighter.KERNEL_FUNCTION);
                                }
                            }

                            if (RESOLVED_MACRO_NAME_SET.contains(resolvedFunctionName)) {
                                ASTNode node = unmatchedUnqualifiedNoParenthesesCall.getNode();
                                ASTNode identifier = node.getFirstChildNode();
                                highlight(identifier, holder, ElixirSyntaxHighlighter.KERNEL_MACRO);
                            }

                            if (RESOLVED_SPECIAL_FORMS_MACRO_NAME_SET.contains(resolvedFunctionName)) {
                                ASTNode node = unmatchedUnqualifiedNoParenthesesCall.getNode();
                                ASTNode identifier = node.getFirstChildNode();
                                highlight(identifier, holder, ElixirSyntaxHighlighter.KERNEL_SPECIAL_FORMS_MACRO);
                            }
                        }
                    }
                }
        );
    }

    /*
     * Private Instance Methods
     */

    /**
     * Highlights `node` with the given `textAttributesKey`.
     *
     * @param node node to highlight
     * @param annotationHolder the container which receives annotations created by the plugin.
     * @param textAttributesKey text attributes to apply to the `textRange`.
     */
    private void highlight(@NotNull ASTNode node, @NotNull AnnotationHolder annotationHolder, @NotNull TextAttributesKey textAttributesKey) {
        annotationHolder.createInfoAnnotation(node, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
        annotationHolder.createInfoAnnotation(node, null).setEnforcedTextAttributes(EditorColorsManager.getInstance().getGlobalScheme().getAttributes(textAttributesKey));
    }
}
