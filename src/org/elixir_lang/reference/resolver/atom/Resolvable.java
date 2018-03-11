package org.elixir_lang.reference.resolver.atom;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.tree.IElementType;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.psi.*;
import org.elixir_lang.reference.resolver.atom.resolvable.Exact;
import org.elixir_lang.reference.resolver.atom.resolvable.Pattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import static org.elixir_lang.psi.impl.ParentImpl.addChildTextCodePoints;
import static org.elixir_lang.psi.impl.QuotableImpl.childNodes;

/**
 * How to resolve an {@link ElixirAtom}.
 * <p>
 * If the {ElixirAtom} is a normal, unquoted atom, it can be resolved exactly, but if it's quoted and contains
 * interpolation, then it cannot be resolved exactly.
 */
public abstract class Resolvable {
    @NotNull
    public static Resolvable resolvable(@NotNull ElixirAtom atom) {
        ElixirCharListLine charListLine = atom.getCharListLine();
        Resolvable resolvable;

        if (charListLine != null) {
            resolvable = resolvable(charListLine);
        } else {
            ElixirStringLine stringLine = atom.getStringLine();

            if (stringLine != null) {
                resolvable = resolvable(stringLine);
            } else {
                ASTNode atomNode = atom.getNode();
                ASTNode atomFragmentNode = atomNode.getLastChildNode();

                assert atomFragmentNode.getElementType() == ElixirTypes.ATOM_FRAGMENT;

                resolvable = new Exact(":" + atomFragmentNode.getText());
            }
        }

        return resolvable;
    }

    @NotNull
    private static <I extends Bodied & Parent> Resolvable resolvable(@NotNull I parentBodied) {
        Body body = parentBodied.getBody();

        return resolvable(parentBodied, childNodes(body));
    }

    @NotNull
    private static Resolvable resolvable(@NotNull Parent parent, @NotNull ASTNode[] children) {
        Resolvable resolvable;

        if (children.length == 0) {
            resolvable = new Exact(":\"\"");
        } else {
            List<String> regexList = new LinkedList<>();
            List<Integer> codePointList = null;

            for (ASTNode child : children) {
                IElementType elementType = child.getElementType();

                if (elementType == parent.getFragmentType()) {
                    codePointList = parent.addFragmentCodePoints(codePointList, child);
                } else if (elementType == ElixirTypes.ESCAPED_CHARACTER) {
                    codePointList = parent.addEscapedCharacterCodePoints(codePointList, child);
                } else if (elementType == ElixirTypes.ESCAPED_EOL) {
                    codePointList = parent.addEscapedEOL(codePointList, child);
                } else if (elementType == ElixirTypes.HEXADECIMAL_ESCAPE_PREFIX) {
                    codePointList = addChildTextCodePoints(codePointList, child);
                } else if (elementType == ElixirTypes.INTERPOLATION) {
                    if (codePointList != null) {
                        regexList.add(codePointListToString(codePointList));
                        codePointList = null;
                    }

                    regexList.add(interpolation());
                } else if (elementType == ElixirTypes.QUOTE_HEXADECIMAL_ESCAPE_SEQUENCE ||
                        elementType == ElixirTypes.SIGIL_HEXADECIMAL_ESCAPE_SEQUENCE) {
                    codePointList = parent.addHexadecimalEscapeSequenceCodePoints(codePointList, child);
                } else {
                    throw new NotImplementedException("Can't convert to Resolvable " + child);
                }
            }

            if (codePointList != null && regexList.isEmpty()) {
                resolvable = resolvableLiteral(codePointList);
            } else {
                if (codePointList != null) {
                    regexList.add(codePointListToRegex(codePointList));
                }

                resolvable = new Pattern(join(regexList));
            }
        }

        return resolvable;
    }

    @NotNull
    private static String join(List<String> regexList) {
        return String.join("", regexList);
    }

    @Contract(pure = true)
    @NotNull
    private static String interpolation() {
        return ".*";
    }

    @NotNull
    private static String codePointListToRegex(@NotNull List<Integer> codePointList) {
        String string = codePointListToString(codePointList);
        return java.util.regex.Pattern.quote(string);
    }

    @NotNull
    private static String codePointListToString(@NotNull List<Integer> codePointList) {
        StringBuilder stringAccumulator = new StringBuilder();

        for (int codePoint : codePointList) {
            stringAccumulator.appendCodePoint(codePoint);
        }

        return stringAccumulator.toString();
    }

    @NotNull
    private static Resolvable resolvableLiteral(List<Integer> codePointList) {
        StringBuilder stringAccumulator = new StringBuilder();

        for (int codePoint : codePointList) {
            stringAccumulator.appendCodePoint(codePoint);
        }

        return new Exact(":" + stringAccumulator.toString());
    }

    public abstract ResolveResult[] resolve(@NotNull Project project);
}
