package org.elixir_lang.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
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
    public static AtUnqualifiedNoParenthesesCall<?> createModuleAttributeDeclaration(
            Project project, String name, String value
    ) {
        String text = "defmodule Dummy do\n" +
                "  " + name + " " + value + "\n" +
                "end";
        final ElixirFile file = createFile(project, text);

        //noinspection ConstantConditions
        @SuppressWarnings("rawtypes") Collection<AtUnqualifiedNoParenthesesCall> atUnqualifiedNoParenthesesCallCollection = PsiTreeUtil.collectElementsOfType(
                file, AtUnqualifiedNoParenthesesCall.class
        );

        assert atUnqualifiedNoParenthesesCallCollection.size() == 1;

        AtUnqualifiedNoParenthesesCall<?>[] atUnqualifiedNoParenthesesCalls = atUnqualifiedNoParenthesesCallCollection.toArray(
                new AtUnqualifiedNoParenthesesCall[1]
        );

        return atUnqualifiedNoParenthesesCalls[0];
    }

    public static QualifiedNoArgumentsCall<?> createQualifiedNoArgumentsCall(Project project, String qualifier, String name) {
        return onlyElementOfType(project, qualifier + "." + name, QualifiedNoArgumentsCall.class);
    }

    public static UnqualifiedNoArgumentsCall<?> createUnqualifiedNoArgumentsCall(Project project, String name) {
        return onlyElementOfType(project, name, UnqualifiedNoArgumentsCall.class);
    }

    /*
     * Private Static Methods
     */

    @NotNull
    private static <T extends PsiElement> T onlyElementOfType(@NotNull PsiFile file, @NotNull Class<T> clazz) {
        Collection<T> elementCollection = PsiTreeUtil.collectElementsOfType(file, clazz);
        assert elementCollection.size() == 1;

        return elementCollection.iterator().next();
    }

    @NotNull
    private static <T extends PsiElement> T onlyElementOfType(@NotNull Project project,
                                                              @NotNull String text,
                                                              @NotNull Class<T> clazz) {
        ElixirFile file = createFile(project, text);
        return onlyElementOfType(file, clazz);
    }
}
