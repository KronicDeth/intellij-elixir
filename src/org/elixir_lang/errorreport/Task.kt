package org.elixir_lang.errorreport

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.diagnostic.Attachment
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.project.Project
import com.intellij.util.Consumer
import org.elixir_lang.github.issues.create.Request
import java.net.URLEncoder

internal class Task(project: Project?,
                    private val additionalInfo: String?,
                    private val throwableList: List<Throwable>,
                    private val attachmentList: List<Attachment>,
                    private val successCallback: Consumer<Boolean>,
                    private val errorCallback: Consumer<Exception>) : Backgroundable(project, TITLE, CAN_BE_CANCELLED) {
    override fun run(indicator: ProgressIndicator) {
        indicator.isIndeterminate = true
        try {
            create(additionalInfo, throwableList, attachmentList)
            successCallback.consume(true)
        } catch (exception: Exception) {
            errorCallback.consume(exception)
        }
    }

    companion object {
        private const val CAN_BE_CANCELLED = true
        private const val TITLE = "Opening GitHub Issue"

        private fun create(additionalInfo: String?, throwableList: List<Throwable>, attachmentList: List<Attachment>) {
            val request = Request(additionalInfo, throwableList, attachmentList)
            BrowserUtil.open(
                    Submitter.ISSUES_URL + "/new?" +
                            "title=" + URLEncoder.encode(request.title, "UTF-8") +
                            "&" +
                            "body=" + URLEncoder.encode(request.body, "UTF-8")
            )
        }
    }
}
