package quoter.tasks

import quoter.QuoterAvailability
import quoter.QuoterService
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
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

    /**
     * The outcome of the *start*, as opposed to [availabilityFile], which records the outcome of the
     * *build*. Written every run, and authoritative for the test task: a quoter that built fine but
     * could not start is just as absent, from a test's point of view, as one that never compiled.
     */
    @get:OutputFile
    abstract val startedFile: RegularFileProperty

    /**
     * Opts back in to a hard failure here, matching `releaseQuoter`'s own flag: with it set, a quoter
     * that cannot start fails the build instead of marking itself unavailable. Both halves answer the
     * same question - "is a missing quoter tolerable?" - so one flag governs both.
     */
    @get:Input
    abstract val required: Property<Boolean>

    @TaskAction
    fun start() {
        val availability = QuoterAvailability.readFrom(availabilityFile.get().asFile)
        val startedFile = startedFile.get().asFile

        // Nothing was built for this pair, so skip the start rather than spend the service's retries
        // discovering that. Tests that need the daemon fail with this reason; the rest run as usual.
        if (availability != null && !availability.available) {
            logger.lifecycle("Quoter daemon not started - ${availability.reason}")
            availability.writeTo(startedFile)
            return
        }

        // A daemon that will not start is not a reason to fail the build. Only the tests that quote
        // through it need it; every other test in the suite is unaffected, and failing here runs none
        // of them - which is what used to happen when a second checkout held the distributed node
        // name, or when the release script exited 0 without starting. Record the reason and let the
        // test task decide, exactly as it already does for a quoter that would not compile.
        try {
            quoterService.get().ensureStarted()
            QuoterAvailability.AVAILABLE.writeTo(startedFile)
        } catch (exception: Exception) {
            val reason = "quoter daemon failed to start: ${exception.message ?: exception.toString()}"
            QuoterAvailability.unavailable(reason).writeTo(startedFile)

            if (required.getOrElse(false)) {
                throw exception
            }

            logger.warn("Quoter daemon not started - $reason")
            logger.warn(
                "Tests that quote through the daemon will fail with that reason; the rest of the suite " +
                    "still runs. Pass -PquoterRequired=true to fail the build here instead."
            )
        }
    }
}
