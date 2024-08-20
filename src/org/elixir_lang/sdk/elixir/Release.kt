package org.elixir_lang.sdk.elixir

import org.jetbrains.annotations.Contract
import java.util.regex.Pattern

class Release private constructor(
    @JvmField val major: String,
    @JvmField val minor: String?,
    private val patch: String?,
    private val pre: String?,
    private val build: String?
) : Comparable<Release> {

    companion object {
        @JvmField val V_1_0_4 = Release("1", "0", "4", null, null)
        @JvmField val LATEST = Release("1", "6", "0", "dev", null)

        private val VERSION_PATTERN = Pattern.compile(
            "(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:\\-([\\d\\w\\.\\-]+))?(?:\\+([\\d\\w\\-]+))?"
        )

        @JvmStatic
        fun fromString(versionString: String?): Release? {
            val m = versionString?.let { VERSION_PATTERN.matcher(it) }
            return if (m != null && m.matches()) {
                Release(m.group(1), m.group(2), m.group(3), m.group(4), m.group(5))
            } else null
        }

        @Contract(pure = true)
        private fun compareMaybeFormattedDecimals(mine: String?, others: String?): Int {
            return when {
                mine == null && others == null -> 0
                mine == null -> -1
                others == null -> 1
                else -> try {
                    val myInt = mine.toInt()
                    val othersInt = others.toInt()
                    myInt.compareTo(othersInt)
                } catch (numberFormatException: NumberFormatException) {
                    mine.compareTo(others)
                }
            }
        }

        @Contract(pure = true)
        private fun comparePre(mine: String?, others: String?): Int {
            return when {
                mine == null && others == null -> 0
                mine == null -> 1
                others == null -> -1
                else -> mine.compareTo(others)
            }
        }
    }

    init {
        require(!(minor == null && patch != null)) { "patch MUST be null if minor is null" }
    }

    override fun compareTo(other: Release): Int {
        var comparison = compareMaybeFormattedDecimals(major, other.major)
        if (comparison == 0) {
            comparison = compareMaybeFormattedDecimals(minor, other.minor)
            if (comparison == 0) {
                comparison = compareMaybeFormattedDecimals(patch, other.patch)
                if (comparison == 0) {
                    comparison = comparePre(pre, other.pre)
                }
            }
        }
        return comparison
    }

    override fun toString(): String = "Elixir ${version()}"

    fun version(): String {
        return buildString {
            append(major)
            minor?.let { append('.').append(it) }
            patch?.let { append('.').append(it) }
            pre?.let { append('-').append(it) }
            build?.let { append('+').append(it) }
        }
    }
}