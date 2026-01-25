package quoter.tasks

import elixir.ElixirService
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.PathSensitive

/**
 * Task that fetches Mix dependencies for the Quoter project.
 * Uses ElixirService to get platform-specific mix executable and environment.
 */
abstract class GetQuoterDepsTask : Exec() {

    @get:ServiceReference("elixir")
    abstract val elixirService: Property<ElixirService>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val quoterDir: DirectoryProperty

    @get:OutputDirectory
    abstract val depsDir: DirectoryProperty

    init {
        // Configure task to cache outputs
        outputs.cacheIf { true }
    }

    override fun exec() {
        // Get the service and ensure Elixir is ready
        val service = elixirService.get()
        service.ensureReady()

        // Configure the command
        commandLine(service.getExecutable("mix"), "deps.get")
        environment(service.getMixEnvironment())

        // Execute
        super.exec()
    }
}
