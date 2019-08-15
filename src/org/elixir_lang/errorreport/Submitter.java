package org.elixir_lang.errorreport;

import com.intellij.diagnostic.IdeErrorsDialog;
import com.intellij.diagnostic.LogMessageEx;
import com.intellij.diagnostic.ReportMessages;
import com.intellij.errorreport.bean.ErrorBean;
import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.idea.IdeaLogger;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Component;

/**
 * @see <a href="https://github.com/JesusFreke/smali/blob/87d10dac2773cd35b7d5825d7957206e26c1727b/smalidea/src/main/java/org/jf/smalidea/errorReporting/ErrorReporter.java">{@code org.jf.smalidea.errorReporting.ErrorReporter}</a>
 */
public class Submitter extends com.intellij.openapi.diagnostic.ErrorReportSubmitter {
    /*
     * CONSTANTS
     */

    private static final String ORGANIZATION = "KronicDeth";
    private static final String REPOSITORY = "intellij-elixir";
    private static final String REPOSITORY_URL = "https://github.com/" + ORGANIZATION + "/" + REPOSITORY;
    static final String ISSUES_URL = REPOSITORY_URL + "/issues";

    /*
     * Static Methods
     */

    @NotNull
    private static Consumer<Exception> errorCallback(@Nullable final Project project) {
        return e -> {
            String message = String.format(
                    "<html>\n" +
                            "  There was an error while creating a GitHub issue: %s\n" +
                            "  <br>\n" +
                            "  Please consider <a href=\"" + ISSUES_URL +
                            "/new\">manually creating an issue</a>\n" +
                            "</html>",
                    e.getMessage()
            );
            ReportMessages.GROUP.createNotification(
                    ReportMessages.ERROR_REPORT,
                    message,
                    NotificationType.ERROR,
                    NotificationListener.URL_OPENING_LISTENER
            ).setImportant(false).notify(project);
        };
    }

    @NotNull
    private static ErrorBean errorBean(@NotNull IdeaLoggingEvent[] events, String additionalInfo) {
        IdeaLoggingEvent event = events[0];
        ErrorBean bean = new ErrorBean(event.getThrowable(), IdeaLogger.ourLastActionId);

        bean.setDescription(additionalInfo);
        bean.setMessage(event.getMessage());

        Throwable throwable = event.getThrowable();
        if (throwable != null) {
            final PluginId pluginId = IdeErrorsDialog.findPluginId(throwable);
            if (pluginId != null) {
                final IdeaPluginDescriptor ideaPluginDescriptor = PluginManager.getPlugin(pluginId);
                if (ideaPluginDescriptor != null && !ideaPluginDescriptor.isBundled()) {
                    bean.setPluginName(ideaPluginDescriptor.getName());
                    bean.setPluginVersion(ideaPluginDescriptor.getVersion());
                }
            }
        }

        Object data = event.getData();

        if (data instanceof LogMessageEx) {
            bean.setAttachments(((LogMessageEx) data).getIncludedAttachments());
        }
        return bean;
    }

    @Nullable
    private static Project project(@NotNull Component parentComponent) {
        DataContext dataContext = DataManager.getInstance().getDataContext(parentComponent);
        return CommonDataKeys.PROJECT.getData(dataContext);
    }

    /**
     * Runs the task in the background if it can.
     *
     * @param task Task that will create an issue on Github
     * @param direct {@code true} to run directly with {@link Task#run(ProgressIndicator)}; {@code false} to run with
     *               the {@link com.intellij.openapi.project.ProjectManager}.
     */
    private static void run(Task task, boolean direct) {
        if (direct) {
            task.run(new EmptyProgressIndicator());
        } else {
            ProgressManager.getInstance().run(task);
        }
    }

    @NotNull
    private static Consumer<Boolean> successCallback(
            @Nullable final Project project,
            @NotNull final Consumer<SubmittedReportInfo> consumer
    ) {
        return new Consumer<Boolean>() {
            @Override
            public void consume(Boolean opened) {
                String url = null;
                String linkText = null;
                //noinspection ConstantConditions
                final SubmittedReportInfo reportInfo = new SubmittedReportInfo(
                        url,
                        linkText,
                        SubmittedReportInfo.SubmissionStatus.NEW_ISSUE
                );
                consumer.consume(reportInfo);

                // pseudo-named-arguments
                NotificationListener notificationListener = null;

                //noinspection ConstantConditions
                ReportMessages.GROUP.createNotification(
                        ReportMessages.ERROR_REPORT,
                        "Submitted",
                        NotificationType.INFORMATION,
                        notificationListener
                ).setImportant(false).notify(project);
            }
        };
    }

    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
     */

    /**
     * @return an action text to be used in Error Reporter user interface, e.g. "Report to JetBrains".
     */
    @Override
    public String getReportActionText() {
        return "Open issue against " + REPOSITORY_URL;
    }

    /**
     * This method is called whenever an exception in a plugin code had happened and a user decided to report a problem
     * to the plugin vendor.
     *
     * @param events          a non-empty sequence of error descriptors.
     * @param additionalInfo  additional information provided by a user.
     * @param parentComponent UI component to use as a parent in any UI activity from a submitter.
     * @param consumer        a callback to be called after sending is finished (or failed).
     * @return {@code true} if reporting was started, {@code false} if a report can't be sent at the moment.
     */
    @Override
    public boolean submit(@NotNull IdeaLoggingEvent[] events,
                          String additionalInfo,
                          @NotNull Component parentComponent,
                          @NotNull final Consumer<SubmittedReportInfo> consumer) {
        final Project project = project(parentComponent);
        ErrorBean errorBean = errorBean(events, additionalInfo);
        Consumer<Boolean> successCallback = successCallback(project, consumer);
        Consumer<Exception> errorCallback = errorCallback(project);

        Task task = new Task(project, errorBean, successCallback, errorCallback);
        run(task, project == null);

        return true;
    }
}
