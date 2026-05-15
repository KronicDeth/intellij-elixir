package org.elixir_lang.beam

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Run-scoped log deduplication for bulk decompilation.
 */
object BulkDecompilationRunLogger {
    private val LOG = Logger.getInstance(BulkDecompilationRunLogger::class.java)
    private val ACTIVE = AtomicReference<RunState?>(null)

    fun begin(project: Project) {
        ACTIVE.set(RunState(project))
    }

    fun end(project: Project) {
        val runState = ACTIVE.getAndSet(null) ?: return

        if (runState.project != project) {
            LOG.info("Skipping bulk decompilation summary because project changed during run")
            return
        }

        val totalDecompiled = runState.totalDecompiledCount.get()
        val parseFirst = runState.parseFirstCount.get()
        val parseSuppressed = runState.parseSuppressedCount.get()
        val uniqueParseLines = runState.parseErrorLineCountByText.size
        val missingFirst = runState.missingFunctionFirstCount.get()
        val missingSuppressed = runState.missingFunctionSuppressedCount.get()

        LOG.info(
            "Bulk decompilation log summary: total files decompiled=$totalDecompiled, " +
                    "parse errors first=$parseFirst, parse errors suppressed=$parseSuppressed, " +
                    "unique parse error lines=$uniqueParseLines, missing-function errors first=$missingFirst, " +
                    "missing-function errors suppressed=$missingSuppressed"
        )

        if (uniqueParseLines > 0) {
            LOG.info(
                "Unique parse error lines (top $MAX_SAMPLE_SIZE):\n" +
                        sampleMultiline(runState.parseErrorLineCountByText)
            )
        }

        if (parseSuppressed > 0) {
            LOG.info("Suppressed repeated parse errors (sample): ${sample(runState.suppressedParseByFilePath)}")
        }

        if (missingSuppressed > 0) {
            LOG.info("Suppressed repeated missing-function errors (sample): ${sample(runState.suppressedMissingFunctionByMessage)}")
        }
    }

    /**
     * @return true when the caller should emit the detailed parse error log.
     */
    fun shouldLogParseError(project: Project, filePath: String): Boolean {
        val runState = ACTIVE.get()

        if (runState == null || runState.project != project) {
            return true
        }

        return if (runState.parseFirstByFilePath.add(filePath)) {
            runState.parseFirstCount.incrementAndGet()
            true
        } else {
            runState.parseSuppressedCount.incrementAndGet()
            runState.suppressedParseByFilePath.computeIfAbsent(filePath) { AtomicInteger(0) }.incrementAndGet()
            false
        }
    }

    fun recordParseErrorLine(project: Project, parseErrorLine: String?) {
        val runState = ACTIVE.get()

        if (runState == null || runState.project != project) {
            return
        }

        parseErrorLine
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.let { normalizedLine ->
                val boundedLine = normalizedLine.take(MAX_PARSE_LINE_LENGTH)
                runState.parseErrorLineCountByText.computeIfAbsent(boundedLine) { AtomicInteger(0) }.incrementAndGet()
            }
    }

    /**
     * @return true when the caller should emit the detailed missing-function error log.
     */
    fun shouldLogMissingFunctionError(project: Project, message: String): Boolean {
        val runState = ACTIVE.get()

        if (runState == null || runState.project != project) {
            return true
        }

        return if (runState.missingFunctionFirstByMessage.add(message)) {
            runState.missingFunctionFirstCount.incrementAndGet()
            true
        } else {
            runState.missingFunctionSuppressedCount.incrementAndGet()
            runState.suppressedMissingFunctionByMessage.computeIfAbsent(message) { AtomicInteger(0) }.incrementAndGet()
            false
        }
    }

    fun recordDecompilation(project: Project) {
        val runState = ACTIVE.get()
        if (runState != null && runState.project == project) {
            runState.totalDecompiledCount.incrementAndGet()
        }
    }

    private fun sample(counterByKey: ConcurrentHashMap<String, AtomicInteger>): String {
        return counterByKey.entries
            .sortedByDescending { it.value.get() }
            .take(MAX_SAMPLE_SIZE)
            .joinToString(" | ") { (key, count) -> "${count.get()}x $key" }
    }

    private fun sampleMultiline(counterByKey: ConcurrentHashMap<String, AtomicInteger>): String {
        return counterByKey.entries
            .sortedWith(compareByDescending<Map.Entry<String, AtomicInteger>> { it.value.get() }.thenBy { it.key })
            .take(MAX_SAMPLE_SIZE)
            .mapIndexed { index, (key, count) -> "  ${index + 1}. ${count.get()}x $key" }
            .joinToString("\n")
    }

    private class RunState(val project: Project) {
        val totalDecompiledCount = AtomicInteger(0)
        val parseFirstByFilePath = ConcurrentHashMap.newKeySet<String>()
        val parseFirstCount = AtomicInteger(0)
        val parseSuppressedCount = AtomicInteger(0)
        val suppressedParseByFilePath = ConcurrentHashMap<String, AtomicInteger>()
        val parseErrorLineCountByText = ConcurrentHashMap<String, AtomicInteger>()

        val missingFunctionFirstByMessage = ConcurrentHashMap.newKeySet<String>()
        val missingFunctionFirstCount = AtomicInteger(0)
        val missingFunctionSuppressedCount = AtomicInteger(0)
        val suppressedMissingFunctionByMessage = ConcurrentHashMap<String, AtomicInteger>()
    }

    private const val MAX_SAMPLE_SIZE = 10
    private const val MAX_PARSE_LINE_LENGTH = 160
}
