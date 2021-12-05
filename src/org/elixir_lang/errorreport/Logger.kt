package org.elixir_lang.errorreport;

import com.intellij.diagnostic.AttachmentFactory;
import com.intellij.openapi.diagnostic.Attachment;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class Logger {
    /*
     * Public Static Methods
     */

    /**
     * Logs error to the {@code klass}'s {@link com.intellij.openapi.diagnostic.Logger} instance with the given
     * {@code userMessage} and the text of {@code element} as the details and containing file of {@code element} as an
     * attachment
     *
     * @param klass   Class whose logger to use
     * @param title   Title of error stored in {@link Throwable}.
     * @param element element responsible for the error
     */
    public static void error(@NotNull Class klass, @NotNull String title, PsiElement element) {
        error(com.intellij.openapi.diagnostic.Logger.getInstance(klass), title, element);
    }

    /**
     * Logs error {@link com.intellij.openapi.diagnostic.Logger} instance with the given {@code userMessage} and the
     * text of {@code element} as the details and containing file of {@code element} as an * attachment
     *
     * @param logger  logger to which to log an error.
     * @param title   Title of error stored in {@link Throwable}.
     * @param element element responsible for the error
     */
    public static void error(@NotNull com.intellij.openapi.diagnostic.Logger logger,
                             @NotNull String title,
                             @NotNull PsiElement element) {
        Throwable throwable = new Throwable(title);
        PsiFile containingFile = element.getContainingFile();
        String message = message(containingFile, element);

        VirtualFile virtualFile = containingFile.getVirtualFile();

        if (virtualFile != null) {
            Attachment attachment = AttachmentFactory.createAttachment(virtualFile);
            logger.error(message, throwable, attachment);
        } else {
            logger.error(message, throwable);
        }
    }

    /*
     * Private Static Methods
     */

    @NotNull
    private static String className(@NotNull PsiElement element) {
        return "\n" +
                "### Element Class Name\n" +
                '\n' +
                "```\n" +
                element.getClass().getName() +
                '\n' +
                "```";
    }

    @NotNull
    private static String excerpt(@NotNull PsiFile containingFile, @NotNull PsiElement element) {
        StringBuilder excerptBuilder = new StringBuilder();
        excerptBuilder
                .append('\n')
                .append("### Excerpt\n")
                .append('\n');

        excerptBuilder
                .append("```\n")
                .append(element.getText())
                .append('\n')
                .append("```\n");

        FileViewProvider fileViewProvider = containingFile.getViewProvider();
        Document document = fileViewProvider.getDocument();

        if (document != null) {
            VirtualFile virtualFile = containingFile.getVirtualFile();

            if (virtualFile != null) {
                String path = virtualFile.getPath();
                TextRange textRange = element.getTextRange();
                int startingLine = document.getLineNumber(textRange.getStartOffset());
                int endingLine = document.getLineNumber(textRange.getEndOffset());

                excerptBuilder
                        .append('\n')
                        .append("From: `").append(path).append(':').append(startingLine).append('`').append('\n')
                        .append("To: `").append(path).append(':').append(endingLine).append('`');
            }
        }

        return excerptBuilder.toString();
    }

    private static String message(@NotNull PsiFile containingFile,
                                  @NotNull PsiElement element) {
        return excerpt(containingFile, element) + "\n" +
                className(element);
    }
}
