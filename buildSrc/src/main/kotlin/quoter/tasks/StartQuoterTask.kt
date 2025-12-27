package quoter.tasks

import quoter.QuoterService
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.TaskAction

/**
 * Task that starts the Quoter daemon via the QuoterService.
 * Uses @ServiceReference to properly integrate with configuration cache.
 */
abstract class StartQuoterTask : DefaultTask() {

    @get:ServiceReference("quoter")
    abstract val quoterService: Property<QuoterService>

    @TaskAction
    fun start() {
        quoterService.get().ensureStarted()
    }
}
