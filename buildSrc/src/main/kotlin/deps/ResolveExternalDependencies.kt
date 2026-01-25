package deps

// Cache seeding task for CI to pre-resolve external Gradle dependencies.
// Benefits:
// - Reduces load on Maven/JetBrains repos by avoiding repeated downloads in matrix jobs.
// - Improves CI reliability by warming the Gradle user home before tests/builds.
// - Keeps behavior deterministic by resolving only external module artifacts.

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider

abstract class ResolveExternalDependenciesTask : DefaultTask() {

    init {
        group = "help"
        description = "Resolves external dependencies for cache seeding."
    }

    @get:InputFiles
    abstract val externalArtifacts: ConfigurableFileCollection

    @get:Input
    abstract val configurationNames: ListProperty<String>

    @get:Internal
    abstract val projectPath: Property<String>

    @TaskAction
    fun resolve() {
        val configs = configurationNames.orNull.orEmpty()
        if (configs.isEmpty()) {
            logger.lifecycle("Resolving ${projectPath.get()}: no matching configurations.")
        } else {
            configs.forEach { config ->
                logger.lifecycle("Resolving $config")
            }
        }
        externalArtifacts.files.size
    }
}

fun Project.registerResolveExternalDependenciesTasksForAllProjects() {
    val rootTask = registerResolveExternalDependenciesTask()
    subprojects.forEach { subproject ->
        val subTask = subproject.registerResolveExternalDependenciesTask()
        rootTask.configure { dependsOn(subTask) }
    }
}

private fun Project.registerResolveExternalDependenciesTask(): TaskProvider<ResolveExternalDependenciesTask> {
    val taskProvider = if (tasks.names.contains("resolveExternalDependencies")) {
        tasks.named("resolveExternalDependencies", ResolveExternalDependenciesTask::class.java)
    } else {
        tasks.register("resolveExternalDependencies", ResolveExternalDependenciesTask::class.java)
    }

    configureResolveExternalDependenciesTask(taskProvider)

    return taskProvider
}

private fun Project.configureResolveExternalDependenciesTask(
    taskProvider: TaskProvider<ResolveExternalDependenciesTask>
) {
    val projectPathValue = path
    val projectPrefix = if (projectPathValue == ":") "" else projectPathValue

    taskProvider.configure {
        projectPath.set(projectPathValue)

        val configs = this@configureResolveExternalDependenciesTask.configurations
            .filter { it.isCanBeResolved }
            .filter { shouldResolveConfiguration(it.name) }
            .sortedBy { it.name }

        configurationNames.set(
            configs.map { config ->
                if (projectPrefix.isEmpty()) {
                    ":${config.name}"
                } else {
                    "${projectPrefix}:${config.name}"
                }
            }
        )
        externalArtifacts.from(
            configs.map { config ->
                config.incoming.artifactView {
                    componentFilter { it is ModuleComponentIdentifier }
                }.artifacts.artifactFiles
            }
        )
    }
}

private fun shouldResolveConfiguration(name: String): Boolean {
    val isRunIdeConfig = name.startsWith("intellijPlatform") && name.contains("_run")

    return !isRunIdeConfig && (
        name.endsWith("Classpath") ||
            name.startsWith("intellijPlatform") ||
            name.contains("kotlinCompiler", ignoreCase = true)
        )
}
