package quoter.tasks

import quoter.QuoterAvailability
import quoter.QuoterService
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

/**
 * Task that starts the Quoter daemon via the QuoterService.
 * Uses @ServiceReference to properly integrate with configuration cache.
 */
abstract class StartQuoterTask : DefaultTask() {

    @get:ServiceReference("quoter")
    abstract val quoterService: Property<QuoterService>

    /**
     * Marker written by `releaseQuoter`. Read here rather than in [QuoterService], which is a shared
     * service with its own lifecycle, while this task is where the input already sits.
     */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val availabilityFile: RegularFileProperty

    @TaskAction
    fun start() {
        val availability = QuoterAvailability.readFrom(availabilityFile.get().asFile)

        // Nothing was built for this pair, so skip the start rather than spend the service's retries
        // discovering that. Tests that need the daemon fail with this reason; the rest run as usual.
        if (availability != null && !availability.available) {
            logger.lifecycle("Quoter daemon not started - ${availability.reason}")
            return
        }

        quoterService.get().ensureStarted()
    }
}
