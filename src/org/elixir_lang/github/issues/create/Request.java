package org.elixir_lang.github.issues.create;

import com.intellij.errorreport.bean.ErrorBean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Request {
    /*
     * CONSTANTS
     */

    private static final String TITLE = "[auto-generated]";

    /*
     * Static Methods
     */

    private static String body(ErrorBean errorBean) {
        StringBuilder stringBuilder = new StringBuilder();

        version(stringBuilder, errorBean.getPluginVersion());
        description(stringBuilder, errorBean.getDescription());
        message(stringBuilder, errorBean.getMessage());
        stacktrace(stringBuilder, errorBean.getStackTrace());

        return stringBuilder.toString();
    }

    private static void codeBlock(@NotNull StringBuilder stringBuilder, @NotNull String code) {
        codeFence(stringBuilder);
        stringBuilder.append(code);
        codeFence(stringBuilder);
    }

    private static void codeBlockSection(@NotNull StringBuilder stringBuilder,
                                         @NotNull String name,
                                         @Nullable String code) {
        if (code != null && !code.isEmpty()) {
            header(stringBuilder, name);
            codeBlock(stringBuilder, code);
        }
    }

    private static void codeFence(@NotNull StringBuilder stringBuilder) {
        stringBuilder.append("```\n");
    }

    private static void description(@NotNull StringBuilder stringBuilder, @Nullable String description) {
        if (description != null && !description.isEmpty()) {
            header(stringBuilder, "Description");
            stringBuilder.append(description);
            stringBuilder.append("\n\n");
        }
    }

    private static void header(@NotNull StringBuilder stringBuilder, @NotNull String name) {
        stringBuilder.append("\n");
        stringBuilder.append("# ");
        stringBuilder.append(name);
        stringBuilder.append("\n\n");
    }


    private static void message(@NotNull StringBuilder stringBuilder, @Nullable String message) {
        codeBlockSection(stringBuilder, "Message", message);
    }

    private static void stacktrace(@NotNull StringBuilder stringBuilder, @Nullable String stacktrace) {
        codeBlockSection(stringBuilder, "Stacktrace", stacktrace);
    }

    private static void version(@NotNull StringBuilder stringBuilder, @Nullable String version) {
        if (version != null) {
            header(stringBuilder, "Version");
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
