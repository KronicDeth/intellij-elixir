package org.elixir_lang.github.issues.create;

import com.intellij.errorreport.bean.ErrorBean;
import com.intellij.openapi.diagnostic.Attachment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Request {
    /*
     * CONSTANTS
     */

    private static final String TITLE = "[auto-generated]";

    /*
     * Static Methods
     */

    private static void attachment(@NotNull StringBuilder stringBuilder, int level, Attachment attachment) {
        header(stringBuilder, level, "`" + attachment.getPath() + "`");

        stringBuilder.append(
                "Please copy the contents of the above path into this report: files are too long to include in the " +
                        "URL when opening the browser.  You can get the exact contents of that path when the error " +
                        "occurred from the Attachments tab of the IDE Fatal Errors dialog that you had open before " +
                        "clicking the button to submit this issue."
        );
    }

    private static void attachments(@NotNull StringBuilder stringBuilder, int level, List<Attachment> attachmentList) {
        if (!attachmentList.isEmpty()) {
            header(stringBuilder, level, "Attachments");

            for (Attachment attachment : attachmentList) {
                attachment(stringBuilder, level + 1, attachment);
            }
        }
    }

    private static String body(ErrorBean errorBean) {
        StringBuilder stringBuilder = new StringBuilder();
        int level = 0;

        version(stringBuilder, errorBean.getPluginVersion());
        description(stringBuilder, level + 1, errorBean.getDescription());
        exception(stringBuilder, level + 1, errorBean);
        attachments(stringBuilder, level + 1, errorBean.getAttachments());

        return stringBuilder.toString();
    }

    private static void codeBlock(@NotNull StringBuilder stringBuilder, @NotNull String code) {
        codeFence(stringBuilder);
        stringBuilder.append(code);
        stringBuilder.append('\n');
        codeFence(stringBuilder);
    }

    private static void codeBlockSection(@NotNull StringBuilder stringBuilder,
                                         int level,
                                         @NotNull String name,
                                         @Nullable String code) {
        if (code != null && !code.isEmpty()) {
            header(stringBuilder, level, name);
            codeBlock(stringBuilder, code);
        }
    }

    private static void codeFence(@NotNull StringBuilder stringBuilder) {
        stringBuilder.append("```\n");
    }

    private static void description(@NotNull StringBuilder stringBuilder, int level, @Nullable String description) {
        textSection(stringBuilder, level, "Description", description);
    }

    private static void exception(@NotNull StringBuilder stringBuilder, int level, @NotNull ErrorBean errorBean) {
        header(stringBuilder, level, "Exception");
        message(stringBuilder, level + 1, errorBean.getMessage());
        stacktrace(stringBuilder, level + 1, errorBean.getStackTrace());
    }

    private static void header(@NotNull StringBuilder stringBuilder, int level, @NotNull String name) {
        stringBuilder.append("\n");

        for (int i = 0; i < level; i++) {
            stringBuilder.append('#');
        }

        stringBuilder.append(' ');
        stringBuilder.append(name);
        stringBuilder.append("\n\n");
    }


    private static void message(@NotNull StringBuilder stringBuilder, int level, @Nullable String message) {
        textSection(stringBuilder, level, "Message", message);
    }

    private static void stacktrace(@NotNull StringBuilder stringBuilder, int level, @Nullable String stacktrace) {
        codeBlockSection(stringBuilder, level, "Stacktrace", stacktrace);
    }

    private static void textSection(@NotNull StringBuilder stringBuilder,
                                    int level,
                                    @NotNull String name,
                                    @Nullable String text) {
        if (text != null && !text.isEmpty()) {
            header(stringBuilder, level, name);
            stringBuilder.append(text);
            stringBuilder.append("\n\n");
        }
    }

    private static void version(@NotNull StringBuilder stringBuilder, @Nullable String version) {
        if (version != null) {
            header(stringBuilder, 1, "Version");
            stringBuilder.append(version);
            stringBuilder.append("\n\n");
        }
    }

    /*
     * Fields
     */

    @NotNull
    public final String body;
    @NotNull
    public final String title;

    public Request(@NotNull ErrorBean errorBean) {
        this(TITLE, body(errorBean));
    }

    private Request(@NotNull String title, @NotNull String body) {
        this.body = body;
        this.title = title;
    }
}
