package org.elixir_lang.find_usages.handler

import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.util.BuildNumber

object AlreadyResolved {
    private val OLD_START = BuildNumber("", 213)
    private val OLD_END = BuildNumber("", 213, 6461)
    private val NEW_START = BuildNumber("", 253) // 2025.3+

    val alreadyResolved by lazy {
        val build = ApplicationInfoEx.getInstance().build

        // Elements are already resolved in 2021.3.x (213.x) and 2025.3+ (253+)
        (OLD_START < build && build < OLD_END) || (build >= NEW_START)
    }
}
