package org.elixir_lang.errorreport;

import com.intellij.errorreport.bean.ErrorBean;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;
import org.elixir_lang.github.issues.create.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URLEncoder;

import static org.elixir_lang.errorreport.Submitter.ISSUES_URL;

class Task extends com.intellij.openapi.progress.Task.Backgroundable {
    /*
     * CONSTANTS
     */

    private static final boolean CAN_BE_CANCELLED = true;
    private static final String TITLE = "Opening GitHub Issue";

    /*
     * Static Methods
     */

    private static void create(@NotNull ErrorBean errorBean) throws IOException {
        Request request = new Request(errorBean);
        BrowserUtil.open(
                ISSUES_URL + "/new?" +
                        "title=" + URLEncoder.encode(request.title, "UTF-8") +
                        "&" +
                        "body=" + URLEncoder.encode(request.body, "UTF-8")
        );
    }

    /*
     * Fields
     */

    @NotNull
    private final Consumer<Exception> errorCallback;
    @NotNull
    private final ErrorBean errorBean;
    @NotNull
    private final Consumer<Boolean> successCallback;

    /*
     * Constructors
     */

    Task(@Nullable Project project,
         @NotNull ErrorBean errorBean,
         @NotNull Consumer<Boolean> successCallback,
         @NotNull Consumer<Exception> errorCallback) {
        super(project, TITLE, CAN_BE_CANCELLED);

        this.errorCallback = errorCallback;
        this.errorBean = errorBean;
        this.successCallback = successCallback;
    }

    /*
     * Instance Methods
     */

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setIndeterminate(true);
        try {
            create(errorBean);
            successCallback.consume(true);
        } catch (Exception exception) {
            errorCallback.consume(exception);
        }
    }

}
