package org.elixir_lang.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.ElixirFileType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ElementFactory {
    @Contract(pure = true)
    @NotNull
    public static ElixirFile createFile(@NotNull final Project project, @NotNull final String text) {
        String filename = "dummy." + ElixirFileType.INSTANCE.getDefaultExtension();
        return (ElixirFile) PsiFileFactory.getInstance(project).createFileFromText(
                filename,
                ElixirFileType.INSTANCE,
                text
        );
    }

    @Contract(pure = true)
    @NotNull
    public static AtUnqualifiedNoParenthesesCall createModuleAttributeDeclaration(
            Project project, String name, String value
    ) {
        String text = "defmodule Dummy do\n" +
                "  " + name + " " + value + "\n" +
                "end";
        final ElixirFile file = createFile(project, text);

        //noinspection ConstantConditions
        Collection<AtUnqualifiedNoParenthesesCall> atUnqualifiedNoParenthesesCallCollection = PsiTreeUtil.collectElementsOfType(
                file, AtUnqualifiedNoParenthesesCall.class
        );

        assert atUnqualifiedNoParenthesesCallCollection.size() == 1;

        AtUnqualifiedNoParenthesesCall[] atUnqualifiedNoParenthesesCalls = atUnqualifiedNoParenthesesCallCollection.toArray(
                new AtUnqualifiedNoParenthesesCall[1]
        );

        return atUnqualifiedNoParenthesesCalls[0];
    }

    @Contract(pure = true)
    @NotNull
    public static AtNonNumericOperation createModuleAttributeUsage(Project project, String name) {
        String text = "defmodule Dummy do\n" +
                      "  " + name + "\n" +
                      "end";
        final ElixirFile file = createFile(project, text);
        Collection<AtNonNumericOperation> elementCollection = PsiTreeUtil.collectElementsOfType(
                file, AtNonNumericOperation.class
        );

        assert elementCollection.size() == 1;

        AtNonNumericOperation[] atNonNumericOperations = elementCollection.toArray(new AtNonNumericOperation[1]);

        return atNonNumericOperations[0];
    }
}
