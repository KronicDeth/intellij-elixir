package org.elixir_lang.errorreport

import com.intellij.diagnostic.AbstractMessage
import com.intellij.diagnostic.ReportMessages
import com.intellij.ide.DataManager
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.util.Consumer
import java.awt.Component

/**
 * @see [{@code org.jf.smalidea.errorReporting.ErrorReporter}](https://github.com/JesusFreke/smali/blob/87d10dac2773cd35b7d5825d7957206e26c1727b/smalidea/src/main/java/org/jf/smalidea/errorReporting/ErrorReporter.java)
 */
class Submitter : ErrorReportSubmitter() {
    /**
     * @return an action text to be used in Error Reporter user interface, e.g. "Report to JetBrains".
     */
    override fun getReportActionText(): String = "Open issue against $REPOSITORY_URL"

    /**
     * This method is called whenever an exception in a plugin code had happened and a user decided to report a problem
     * to the plugin vendor.
     *
     * @param events          a non-empty sequence of error descriptors.
     * @param additionalInfo  additional information provided by a user.
     * @param parentComponent UI component to use as a parent in any UI activity from a submitter.
     * @param consumer        a callback to be called after sending is finished (or failed).
     * @return `true` if reporting was started, `false` if a report can't be sent at the moment.
     */
    override fun submit(events: Array<IdeaLoggingEvent>,
                        additionalInfo: String?,
                        parentComponent: Component,
                        consumer: Consumer<in SubmittedReportInfo>): Boolean {
        val project = project(parentComponent)
        val successCallback = successCallback(project, consumer)
        val errorCallback = errorCallback(project)
        val task = Task(project, additionalInfo, events, successCallback, errorCallback)
        run(task, project == null)
        return true
    }

    companion object {
        private const val ORGANIZATION = "KronicDeth"
        private const val REPOSITORY = "intellij-elixir"
        private const val REPOSITORY_URL = "https://github.com/$ORGANIZATION/$REPOSITORY"
        const val ISSUES_URL = "$REPOSITORY_URL/issues"


        private fun errorCallback(project: Project?): Consumer<Exception> {
            return Consumer { e: Exception ->
                val message = String.format(
                        """<html>
  There was an error while creating a GitHub issue: %s
  <br>
  Please consider <a href="$ISSUES_URL/new">manually creating an issue</a>
</html>""",
                        e.message
                )
                ReportMessages.GROUP.createNotification(
                        ReportMessages.getErrorReport(),
                        message,
                        NotificationType.ERROR,
                        NotificationListener.URL_OPENING_LISTENER
                ).setImportant(false).notify(project)
            }
        }

        private fun project(parentComponent: Component): Project? =
            DataManager
                    .getInstance()
                    .getDataContext(parentComponent)
                    .let { dataContext -> CommonDataKeys.PROJECT.getData(dataContext) }

        /**
         * Runs the task in the background if it can.
         *
         * @param task Task that will create an issue on Github
         * @param direct `true` to run directly with [Task.run]; `false` to run with
         * the [com.intellij.openapi.project.ProjectManager].
         */
        private fun run(task: Task, direct: Boolean) {
            if (direct) {
                task.run(EmptyProgressIndicator())
            } else {
                ProgressManager.getInstance().run(task)
            }
        }

        private fun successCallback(
                project: Project?,
                consumer: Consumer<in SubmittedReportInfo>
        ): Consumer<Boolean> {
            return Consumer {
                val url: String? = null
                val linkText: String? = null
                val reportInfo = SubmittedReportInfo(
                        url,
                        linkText,
                        SubmittedReportInfo.SubmissionStatus.NEW_ISSUE
                )
                consumer.consume(reportInfo)

                // pseudo-named-arguments
                val notificationListener: NotificationListener? = null
                ReportMessages.GROUP.createNotification(
                        ReportMessages.getErrorReport(),
                        "Submitted",
                        NotificationType.INFORMATION,
                        notificationListener
                ).setImportant(false).notify(project)
            }
        }
    }
}
