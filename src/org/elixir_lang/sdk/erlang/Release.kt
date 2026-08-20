package org.elixir_lang.sdk.erlang

/**
 * Represents an installed Erlang/OTP SDK version, derived from the `OTP_VERSION` file and
 * the `releases/` directory structure - no subprocess required.
 *
 * @param otpMajor  The major OTP release number, e.g. `"26"`.
 * @param otpVersion The full patch version string, e.g. `"26.2.5.6"`.
 */
data class Release(val otpMajor: String, val otpVersion: String) {
    override fun toString(): String = "Erlang/OTP $otpVersion"
}
