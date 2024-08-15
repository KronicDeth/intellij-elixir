package org.elixir_lang.sdk.erlang

import java.util.regex.Pattern

class Release internal constructor(val otpRelease: String, private val ertsVersion: String) {
    override fun toString(): String {
        return "Erlang/OTP $otpRelease [erts-$ertsVersion]"
    }

    companion object {
        private val VERSION_PATTERN: Pattern = Pattern.compile("Erlang/OTP (\\S+) \\[erts-(\\S+)\\]")

        fun fromString(versionString: String?): Release? {
            var release: Release? = null

            if (versionString != null) {
                val matcher = VERSION_PATTERN.matcher(versionString)

                if (matcher.matches()) {
                    release = Release(matcher.group(1), matcher.group(2))
                }
            }

            return release
        }
    }
}
