package org.elixir_lang.jps.shared.mix

object MixToolingBootstrapSpec {
    @JvmField
    val TASKS: List<String> = listOf("local.hex", "local.rebar")

    @JvmField
    val ARGS: List<String> = listOf("--force", "--if-missing")

    @JvmStatic
    fun isDepsTask(task: String): Boolean =
        task == "deps" || (task.startsWith("deps.") && !task.startsWith("deps.compile"))

    @JvmStatic
    fun isBootstrapTask(task: String): Boolean =
        TASKS.contains(task)

    @JvmStatic
    fun shouldBootstrapForTask(task: String): Boolean =
        !isDepsTask(task) && !isBootstrapTask(task)

    @JvmStatic
    fun shouldBootstrapForTasks(tasks: List<String>): Boolean =
        tasks.none { isDepsTask(it) || isBootstrapTask(it) }
}
