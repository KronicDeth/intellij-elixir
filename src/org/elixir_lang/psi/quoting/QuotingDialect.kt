package org.elixir_lang.psi.quoting

/**
 * Which shape of quoted form to emit, as a threshold on the Elixir version that produced it.
 *
 * Elixir's quoted form changes between versions, so a single answer is wrong for every version but
 * one. Each constant marks a version at which a divergence appeared; the predicates below read as
 * "this dialect is at least that version", so a dialect answers every predicate for every earlier
 * constant too.
 *
 * All the divergences known so far are **additive** - a newer Elixir emits metadata, or a richer
 * node, where an older one emitted less - which is exactly what a monotonic threshold expresses.
 * Adding a version therefore costs one constant and one predicate. Should a future Elixir *remove*
 * something instead, a monotonic threshold can no longer express it and this becomes a hierarchy;
 * likewise if a divergence ever needs different *construction* rather than a different *decision*.
 *
 * Every threshold here was pinned against Elixir's own history and confirmed by quoting the
 * construct with the reference implementation on either side of the boundary - see each constant.
 */
enum class QuotingDialect {
    /**
     * Everything before Elixir 1.15.0: no bracket or interpolation metadata, and `...` quotes as a
     * variable. Named for the oldest version this plugin's CI covers, but it is the floor, not a
     * point - 1.14.5 resolves here too.
     */
    V1_13,

    /**
     * Elixir 1.15.0 added `from_brackets: true` to the `Access.get/2` metadata, but only for the
     * `bracket_expr -> access_expr bracket_arg` production - bracket access on an expression, as in
     * `[1, 2][0]`. The other four bracket forms did not get it until [V1_16_2].
     *
     * `elixir_parser.yrl`, `meta_with_from_brackets`, introduced by elixir-lang/elixir aa8e6d3fe
     * ("Add error message when piping into an expression ending in bracket-based access", #12359),
     * first released in v1.15.0.
     *
     * 1.15.0 also narrowed where a solitary `not` or `!` gets a `__block__` wrapper. That clause
     * lived in `build_block`, which every block position went through - a stab body, a file, an
     * interpolation - so `( -> ! one )` and `a not in b` were wrapped. 1.15.0 moved it into
     * `build_paren_stab`, leaving only a parenthesised single unary expression wrapped, and wrapped
     * with empty metadata rather than the parentheses' own. From elixir-lang/elixir 318681950
     * ("Apply rearrange ops only inside parens", #12296), also first released in v1.15.0.
     * `?rearrange_uop` is `Op == 'not' orelse Op == '!'`; the neighbouring `unquote_splicing`
     * clause was not touched and still wraps in every version.
     *
     * That one is read via [wrapsSolitaryUnaryNotInEveryBlock], the only predicate here true
     * *below* its threshold, because the behaviour was narrowed rather than added.
     */
    V1_15,

    /**
     * Elixir 1.16.0 added `from_interpolation: true` to the metadata of the `Kernel.to_string/1`
     * call that interpolation quotes to.
     *
     * elixir-lang/elixir 5225b33ba ("Add interpolation token metadata"), first released in v1.16.0.
     */
    V1_16_0,

    /**
     * Elixir 1.16.2 extended `from_brackets: true` to the remaining four bracket productions:
     * `bracket_expr -> dot_bracket_identifier` (`foo[:a]` and `Foo.bar[:a]`) and both
     * `bracket_at_expr` forms (`@foo[:a]` and `@1[:a]`).
     *
     * elixir-lang/elixir d8cc841ab ("Include from_brackets metadata in all cases", #13317),
     * first released in v1.16.2 (the same change reached master as eb1499ac2, released in v1.17.0).
     */
    V1_16_2,

    /**
     * Elixir 1.17.0 made `...` a nullary call - `{:..., meta, []}` - where earlier versions quoted
     * it as a variable, `{:..., meta, nil}`.
     *
     * `elixir_parser.yrl` gained `sub_matched_expr -> ellipsis_op : build_nullary_op('$1')` in
     * v1.17.0; v1.16.3 has no `ellipsis_op` production. Note this is **not** the 1.19 the code
     * comment on the plugin side claimed: quoting `...` gives `nil` on 1.16.3 and `[]` on 1.17.3.
     *
     * 1.17.0 also widened which ambiguous dual operators make the identifier before them a call.
     * `elixir_tokenizer.erl`'s `handle_space_sensitive_tokens` refused the `op_identifier`
     * conversion when the character after the sign was any of `( [ < { % + - / > :`; 1.17.0 shrank
     * that guard to `NotMarker =/= Sign, NotMarker =/= $/, NotMarker =/= $>`, so `one +(two)` went
     * from the operation `one + two` to the call `one(+two)`. From elixir-lang/elixir b8f069d08
     * ("Fix parsing of ambiguous operators followed by containers"), first released in v1.17.0.
     *
     * Read via [quotesAmbiguousDualOperatorAsCall].
     */
    V1_17,

    /**
     * Elixir 1.20.0 added `line` metadata to two `__block__` forms that previously carried none: a
     * `do:` block's value now carries the line of its own `do` token
     * (elixir-lang/elixir 90e1826c7), and a 0-byte file's implicit top-level block now carries
     * `line: 1` (elixir-lang/elixir 7da1b76b6). Both first released in v1.20.0-rc.0. Only `line` is
     * added, not `column` - the rest of each commit is gated behind `?columns()`/`?token_metadata()`,
     * which neither this plugin nor its reference quoter enables.
     *
     * Read via [emitsLineMetadataOnBlock].
     */
    V1_20;

    /** `[1, 2][0]` and friends - the `bracket_expr -> access_expr bracket_arg` production. */
    val emitsFromBracketsOnBracketedExpression: Boolean get() = this >= V1_15

    /**
     * Whether a solitary `not`/`!` is wrapped in `__block__` in *every* block position, rather than
     * only inside parentheses. True below [V1_15] - see there for why this one reads downwards.
     */
    val wrapsSolitaryUnaryNotInEveryBlock: Boolean get() = this < V1_15

    /**
     * Whether `&` must be immediately followed by its digit for the two to be one capture argument.
     * `&1` always is; `& 1` is too below 1.15.0, but from 1.15.0 it is `&` applied to `1`, which
     * binds the rest of the expression - so `& & 1 + & 2` is `&((&1) + (&2))` up to 1.14.5 and
     * `&(&(1 + &2))` from 1.15.0.
     *
     * `elixir_parser.yrl`'s `access_expr -> capture_op_eol int` became
     * `access_expr -> capture_int int`, and `elixir_tokenizer.erl` emits `capture_int` only for an
     * adjacent digit; the `_eol` token it replaced permitted whitespace. From elixir-lang/elixir
     * 9fb3cf603 ("Fix ambiguity in &INT with brackets"), first released in v1.15.0.
     *
     * Read by the parser rather than the quoter, unlike everything else here: the divergence is in
     * how the tokens bind, which no reshape of the quoted form can express.
     */
    val requiresAdjacentCaptureArgument: Boolean get() = this >= V1_15

    /** The `Kernel.to_string/1` call that `"a#{b}c"` quotes to. */
    val emitsFromInterpolation: Boolean get() = this >= V1_16_0

    /** `foo[:a]`, `Foo.bar[:a]`, `@foo[:a]` and `@1[:a]` - every bracket form. */
    val emitsFromBracketsOnEveryBracketForm: Boolean get() = this >= V1_16_2

    /** `...` as `{:..., meta, []}` rather than `{:..., meta, nil}`. */
    val quotesEllipsisAsNullaryCall: Boolean get() = this >= V1_17

    /**
     * `one +(two)` as the call `one(+two)` rather than the operation `one + two` - a container, `%`
     * or the opposite sign after a spaced dual operator. `one +two` is a call in every version this
     * plugin supports and is not affected; `one ++two` and `one +/two` are operations in every
     * version, being the exclusions 1.17.0 kept.
     */
    val quotesAmbiguousDualOperatorAsCall: Boolean get() = this >= V1_17

    /**
     * Whether a `do:` block's `__block__` (and an empty file's implicit top-level `__block__`)
     * carries `line` metadata instead of `[]`.
     */
    val emitsLineMetadataOnBlock: Boolean get() = this >= V1_20

    companion object {
        /**
         * The dialect to assume when the Elixir version behind an element cannot be determined - no
         * module, no Elixir SDK, or an SDK whose version string carries no version.
         *
         * Deliberately the newest rather than the oldest: most users are on a recent Elixir, so a
         * wrong-but-modern quoted form is the least surprising default. It is also the direction
         * that ages well, since a new threshold added below shifts the fallback forward with it.
         */
        @JvmStatic
        val FALLBACK: QuotingDialect = entries.last()

        /** Leading `MAJOR.MINOR[.PATCH]`, wherever it sits in the string. */
        private val VERSION = Regex("""(\d+)\.(\d+)(?:\.(\d+))?""")

        /**
         * The dialect for an Elixir version, or [FALLBACK] when [version] carries no version number.
         *
         * [version] may be a bare canonical version (`"1.16.2"`, as
         * `ElixirVersionDetector.ELIXIR_VERSION_KEY` holds it), a mise-style version with a build
         * tag (`"1.13.4-otp-24"`), or a whole SDK version string
         * (`"mise Elixir 1.13.4 (OTP 24)"`). Anything after the version number is ignored, which
         * also means a pre-release resolves as its release - correct for these thresholds, since a
         * `1.16.2-rc` carries the 1.16.2 change.
         */
        @JvmStatic
        fun of(version: String?): QuotingDialect {
            val match = version?.let { VERSION.find(it) } ?: return FALLBACK
            val (major, minor, patch) = match.destructured
            val numbers = Triple(major.toInt(), minor.toInt(), patch.ifEmpty { "0" }.toInt())

            return when {
                numbers >= Triple(1, 20, 0) -> V1_20
                numbers >= Triple(1, 17, 0) -> V1_17
                numbers >= Triple(1, 16, 2) -> V1_16_2
                numbers >= Triple(1, 16, 0) -> V1_16_0
                numbers >= Triple(1, 15, 0) -> V1_15
                else -> V1_13
            }
        }

        private operator fun Triple<Int, Int, Int>.compareTo(other: Triple<Int, Int, Int>): Int =
            compareValuesBy(this, other, Triple<Int, Int, Int>::first, Triple<Int, Int, Int>::second, Triple<Int, Int, Int>::third)
    }
}
