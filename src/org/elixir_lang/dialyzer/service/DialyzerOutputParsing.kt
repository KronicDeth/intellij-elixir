package org.elixir_lang.dialyzer.service

import java.util.regex.Pattern

fun parseDialyzerOutput(out: Pair<String, String>) : List<DialyzerWarn> {
    val (stdout: String, stderr: String) = out
    return parseStdErr(stderr) + parseStdOut(stdout)
}

private val filePattern: Pattern = Pattern.compile("(?<name>[\\w/.]+):(?<line>[\\d]+).*$")
private fun parseStdErr(stderr: String): List<DialyzerWarn> {
    val separator = "________________________________________________________________________________"
    val entry = mutableListOf<String>()
    var start = false
    val entries = mutableListOf<List<String>>()
    for (line in stderr.lines()) {
        if (line == separator) {
            entries.add(entry.toList())
            entry.clear()
            start = false
        } else if (filePattern.toRegex().matches(line)) {
            start = true
        }
        if (start) {
            entry.add(line)
        }
    }
    return entries.map { e: List<String> ->
        val (file, line) = getFileAndLine(e[0])
        val message = e.drop(1).joinToString(System.lineSeparator())
        DialyzerWarn(file, line, message)
    }
}


// If there are compile errors, stderr is empty and compile errors are printed to stdout.
private val compileErrorPattern = Pattern.compile(
        "^\\*\\* \\((?<errType>[^)]+)\\) (?<file>[\\w/\\\\.]+):(?<line>\\d+): (?<msg>.*)$", Pattern.MULTILINE)
private val compileErrorPattern2 = Pattern.compile(
        "^== Compilation error in file (?<file>[\\w/\\\\.]+) ==\\s*\\*\\* \\((?<errType>[^)]+)\\) (?<msg>.*)",
        Pattern.MULTILINE + Pattern.DOTALL)

private fun parseStdOut(stdout: String): List<DialyzerWarn> {
    var result = listOf<DialyzerWarn>()
    if (stdout.contains("Compilation error")) {
        val matcher = compileErrorPattern.matcher(stdout)
        if (matcher.find()) {
            val errType = matcher.group("errType")
            val file = matcher.group("file")
            val line = matcher.group("line").toInt()
            val msg = matcher.group("msg")
            result = listOf(DialyzerWarn(file, line, "$errType: $msg"))
        }
    }
    if (result.isEmpty() && stdout.contains("Compilation error in file")) {
        val matcher = compileErrorPattern2.matcher(stdout)
        if (matcher.find()) {
            val file = matcher.group("file")
            val errType = matcher.group("errType")
            val msg = matcher.group("msg")
            val lineMatcher = Pattern.compile("\\Q$file\\E:(?<line>\\d+)").matcher(stdout)
            val line = if (lineMatcher.find()) {
                lineMatcher.group("line").toInt()
            }
            else {
                1
            }
            result = listOf(DialyzerWarn(file, line, "$errType: $msg"))
        }
    }
    return result
}

private fun getFileAndLine(firstLine: String): Pair<String, Int> {
    val matcher = filePattern.matcher(firstLine)
    matcher.find()
    return Pair(matcher.group("name"), matcher.group("line").toInt())
}
