import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class ReleaseQuoterTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {

    @get:InputDirectory
    abstract val quoterUnzippedPath: DirectoryProperty

    @get:OutputDirectory
    abstract val releaseOutputDir: DirectoryProperty

    @TaskAction
    fun releaseQuoter() {
        execOperations.exec {
            it.workingDir(quoterUnzippedPath)
            it.commandLine(
                "mix",
                "do",
                "local.rebar",
                "--force,",
                "local.hex",
                "--force,",
                "deps.get,",
                "release",
                "--overwrite"
            )
//            it.standardOutput = System.out
//            it.errorOutput = System.err
        }
    }
}
