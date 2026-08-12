package org.elixir_lang.psi.quoting

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The thresholds are the whole design, and every other test exercises them only indirectly - a
 * misplaced boundary shows up as a quoting failure on one CI leg rather than as a wrong mapping.
 * So they are pinned here, one assertion per side of each boundary.
 *
 * Each expected value was confirmed against the reference implementation; see [QuotingDialect] for
 * the Elixir commit and release behind each one.
 */
class QuotingDialectTest {
    @Test
    fun `versions before 1_15 have no bracket or interpolation metadata`() {
        assertEquals(QuotingDialect.V1_13, QuotingDialect.of("1.13.4"))
        assertEquals(QuotingDialect.V1_13, QuotingDialect.of("1.14.5"))
        assertEquals(QuotingDialect.V1_13, QuotingDialect.of("1.14.99"))
    }

    @Test
    fun `from_brackets on a bracketed expression starts at 1_15_0`() {
        assertEquals(QuotingDialect.V1_13, QuotingDialect.of("1.14.5"))
        assertEquals(QuotingDialect.V1_15, QuotingDialect.of("1.15.0"))
        assertEquals(QuotingDialect.V1_15, QuotingDialect.of("1.15.8"))
    }

    @Test
    fun `from_interpolation starts at 1_16_0`() {
        assertEquals(QuotingDialect.V1_15, QuotingDialect.of("1.15.8"))
        assertEquals(QuotingDialect.V1_16_0, QuotingDialect.of("1.16.0"))
        assertEquals(QuotingDialect.V1_16_0, QuotingDialect.of("1.16.1"))
    }

    @Test
    fun `from_brackets on every bracket form starts at 1_16_2`() {
        assertEquals(QuotingDialect.V1_16_0, QuotingDialect.of("1.16.1"))
        assertEquals(QuotingDialect.V1_16_2, QuotingDialect.of("1.16.2"))
        assertEquals(QuotingDialect.V1_16_2, QuotingDialect.of("1.16.3"))
    }

    @Test
    fun `ellipsis becomes a nullary call at 1_17_0`() {
        assertEquals(QuotingDialect.V1_16_2, QuotingDialect.of("1.16.3"))
        assertEquals(QuotingDialect.V1_17, QuotingDialect.of("1.17.0"))
        assertEquals(QuotingDialect.V1_17, QuotingDialect.of("1.17.3"))
    }

    @Test
    fun `do-block and empty-file blocks gain line metadata at 1_20_0`() {
        assertEquals(QuotingDialect.V1_17, QuotingDialect.of("1.19.5"))
        assertEquals(QuotingDialect.V1_20, QuotingDialect.of("1.20.0"))
        assertEquals(QuotingDialect.V1_20, QuotingDialect.of("1.20.2"))
    }

    @Test
    fun `versions after the newest threshold resolve to it`() {
        assertEquals(QuotingDialect.V1_17, QuotingDialect.of("1.18.4"))
        assertEquals(QuotingDialect.V1_17, QuotingDialect.of("1.19.5"))
        assertEquals(QuotingDialect.V1_20, QuotingDialect.of("1.20.3"))
        assertEquals(QuotingDialect.V1_20, QuotingDialect.of("2.0.0"))
    }

    /** Minor is compared numerically, so 1.9 must not sort above 1.15 the way strings would. */
    @Test
    fun `version parts are compared as numbers`() {
        assertEquals(QuotingDialect.V1_13, QuotingDialect.of("1.9.4"))
        assertEquals(QuotingDialect.V1_13, QuotingDialect.of("1.2.6"))
    }

    /** A missing patch is 0, which is what puts a bare "1.16" below the 1.16.2 threshold. */
    @Test
    fun `an absent patch counts as zero`() {
        assertEquals(QuotingDialect.V1_16_0, QuotingDialect.of("1.16"))
        assertEquals(QuotingDialect.V1_15, QuotingDialect.of("1.15"))
        assertEquals(QuotingDialect.V1_13, QuotingDialect.of("1.14"))
    }

    /** mise reports Elixir versions with the OTP build tag attached. */
    @Test
    fun `a build tag is ignored`() {
        assertEquals(QuotingDialect.V1_13, QuotingDialect.of("1.13.4-otp-24"))
        assertEquals(QuotingDialect.V1_16_2, QuotingDialect.of("1.16.3-otp-26"))
        assertEquals(QuotingDialect.V1_17, QuotingDialect.of("1.19.5-otp-28"))
    }

    /** The SDK's own version string, used when the canonical version has not been detected yet. */
    @Test
    fun `a whole SDK version string resolves on its embedded version`() {
        assertEquals(QuotingDialect.V1_13, QuotingDialect.of("mise Elixir 1.13.4 (OTP 24)"))
        assertEquals(QuotingDialect.V1_17, QuotingDialect.of("Elixir 1.17.3 (OTP 27)"))
        assertEquals(
            QuotingDialect.V1_13,
            QuotingDialect.of("mise Elixir 1.13.4-otp-24 (Erlang 24.3.4.6)")
        )
    }

    @Test
    fun `a version-less string falls back`() {
        assertEquals(QuotingDialect.FALLBACK, QuotingDialect.of(null))
        assertEquals(QuotingDialect.FALLBACK, QuotingDialect.of(""))
        assertEquals(QuotingDialect.FALLBACK, QuotingDialect.of("Elixir at /opt/elixir"))
    }

    /**
     * The fallback is deliberately the newest dialect, not the oldest. Asserted against
     * `entries.last()` rather than a literal so adding a threshold moves it rather than breaking
     * here - but asserted at all, because flipping it to the oldest would silently change what
     * every module-less file quotes as.
     */
    @Test
    fun `the fallback is the newest dialect`() {
        assertEquals(QuotingDialect.entries.last(), QuotingDialect.FALLBACK)
    }

    @Test
    fun `each divergence is on from its own threshold and stays on`() {
        assertEquals(
            listOf(false, true, true, true, true, true),
            QuotingDialect.entries.map { it.emitsFromBracketsOnBracketedExpression }
        )
        assertEquals(
            listOf(false, false, true, true, true, true),
            QuotingDialect.entries.map { it.emitsFromInterpolation }
        )
        assertEquals(
            listOf(false, false, false, true, true, true),
            QuotingDialect.entries.map { it.emitsFromBracketsOnEveryBracketForm }
        )
        assertEquals(
            listOf(false, false, false, false, true, true),
            QuotingDialect.entries.map { it.quotesEllipsisAsNullaryCall }
        )
        assertEquals(
            listOf(false, false, false, false, true, true),
            QuotingDialect.entries.map { it.quotesAmbiguousDualOperatorAsCall }
        )
        assertEquals(
            listOf(false, false, false, false, false, true),
            QuotingDialect.entries.map { it.emitsLineMetadataOnBlock }
        )
    }

    /**
     * Shares [QuotingDialect.V1_17] with the ellipsis change, so the boundary is asserted on the
     * versions either side of it as well as across the constants - a threshold that silently moved to
     * 1.16.2 would still pass the list above by matching the ellipsis row.
     */
    @Test
    fun `the ambiguous dual operator call is on from 1_17_0`() {
        assertEquals(false, QuotingDialect.of("1.16.3").quotesAmbiguousDualOperatorAsCall)
        assertEquals(true, QuotingDialect.of("1.17.0").quotesAmbiguousDualOperatorAsCall)
        assertEquals(true, QuotingDialect.FALLBACK.quotesAmbiguousDualOperatorAsCall)
    }

    /**
     * The one predicate that reads downwards: 1.15.0 narrowed the wrapper rather than adding it, so
     * it is on for the oldest dialect and off from [QuotingDialect.V1_15]. Asserted across every
     * constant because getting the direction backwards is the easy mistake, and it would emit a
     * spurious `__block__` on every modern Elixir while looking like the other four predicates.
     */
    @Test
    fun `the unary block wrapper is on only below 1_15`() {
        assertEquals(
            listOf(true, false, false, false, false, false),
            QuotingDialect.entries.map { it.wrapsSolitaryUnaryNotInEveryBlock }
        )
        assertEquals(true, QuotingDialect.of("1.14.5").wrapsSolitaryUnaryNotInEveryBlock)
        assertEquals(false, QuotingDialect.of("1.15.0").wrapsSolitaryUnaryNotInEveryBlock)
        assertEquals(false, QuotingDialect.FALLBACK.wrapsSolitaryUnaryNotInEveryBlock)
    }
}
