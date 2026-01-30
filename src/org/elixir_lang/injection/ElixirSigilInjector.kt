package org.elixir_lang.injection

import com.intellij.lang.Language
import com.intellij.lang.html.HTMLLanguage
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.ElixirInterpolation
import org.elixir_lang.psi.HeredocLine
import org.elixir_lang.psi.SigilHeredoc
import org.elixir_lang.psi.SigilLine
import org.intellij.lang.regexp.RegExpLanguage
import org.elixir_lang.eex.Language as EexLanguage

private val LOG = logger<ElixirSigilInjector>()

internal class ElixirSigilInjector : MultiHostInjector {
    // Per-line view of a sigil body used to compute injection ranges consistently for line and heredoc sigils.
    private data class InjectionLine(
        val hostRange: TextRange,
        val bodyElement: PsiElement?,
        val bodyStartInHost: Int
    )

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        when (context) {
            is SigilLine -> handleSigilLine(registrar, context)
            is SigilHeredoc -> handleSigilHeredoc(registrar, context)
            else -> return
        }
    }

    private fun handleSigilLine(registrar: MultiHostRegistrar, sigilLine: SigilLine) {
        if (!sigilLine.isValidHost) return
        val lang = languageForSigil(sigilLine.sigilName()) ?: return
        val ranges = sigilLineInjectionRanges(sigilLine, lang)
        if (ranges.isEmpty()) return

        registrar.startInjecting(lang)
        for (range in ranges) {
            registrar.addPlace(null, null, sigilLine, range)
        }
        registrar.doneInjecting()
    }

    private fun handleSigilHeredoc(registrar: MultiHostRegistrar, sigilHeredoc: SigilHeredoc) {
        if (!sigilHeredoc.isValidHost || sigilHeredoc.heredocLineList.isEmpty() || !sigilHeredoc.isValid) {
            if (LOG.isDebugEnabled) {
                LOG.debug(
                    "handleSigilHeredoc: returning early: " +
                            "isValidHost=${sigilHeredoc.isValidHost}, " +
                            "heredocLineList.isEmpty=${sigilHeredoc.heredocLineList.isEmpty()}, " +
                            "isValid=${sigilHeredoc.isValid}"
                )
            }
            return
        }

        val lang = languageForSigil(sigilHeredoc.sigilName()) ?: return
        val heredocLineList = sigilHeredoc.heredocLineList

        if (LOG.isDebugEnabled) {
            LOG.debug("handleSigilHeredoc: injecting ${lang.displayName} into ${heredocLineList.size} lines")
        }

        val ranges = sigilHeredocInjectionRanges(heredocLineList, lang)
        if (ranges.isEmpty()) {
            return
        }

        registrar.startInjecting(lang)
        for (range in ranges) {
            if (LOG.isDebugEnabled) {
                LOG.debug("handleSigilHeredoc: injecting range $range")
            }
            registrar.addPlace(null, null, sigilHeredoc, range)
        }
        if (LOG.isDebugEnabled) {
            LOG.debug("handleSigilHeredoc: done injecting into ${heredocLineList.size} lines")
        }
        registrar.doneInjecting()
    }

    private fun sigilHeredocInjectionRanges(
        heredocLineList: List<HeredocLine>,
        lang: Language
    ): List<TextRange> {
        val lines = heredocInjectionLines(heredocLineList)
        return injectionRangesForLines(lines, lang)
    }

    override fun elementsToInjectIn() = listOf(SigilHeredoc::class.java, SigilLine::class.java)

    private fun languageForSigil(sigilName: Char): Language? {
        LOG.debug("languageForSigil: sigilName='$sigilName'")

        return when (sigilName) {
            'H' -> HTMLLanguage.INSTANCE
            'L' -> EexLanguage.INSTANCE
            'r' -> RegExpLanguage.INSTANCE
            else -> null
        }
    }

    private fun interpolationRanges(body: PsiElement, bodyStartOffsetInHost: Int): List<TextRange> {
        val interpolations = PsiTreeUtil.findChildrenOfType(body, ElixirInterpolation::class.java)
        if (interpolations.isEmpty()) {
            return emptyList()
        }

        return interpolations
            .map { it.textRangeInParent.shiftRight(bodyStartOffsetInHost) }
            .sortedBy { it.startOffset }
    }

    private fun regexInjectionRangesExcludingInterpolations(
        baseRange: TextRange,
        body: PsiElement?,
        bodyStartOffsetInHost: Int
    ): List<TextRange> {
        if (body == null) return listOf(baseRange)

        // Interpolations like #{...} must be excluded from injected RegExpLanguage, or the parser will
        // treat `{` as a quantifier start and raise false "Number expected" warnings.
        val interpolations = interpolationRanges(body, bodyStartOffsetInHost)
        return buildInjectionRanges(baseRange, interpolations)
    }

    private fun sigilLineInjectionRanges(sigilLine: SigilLine, lang: Language): List<TextRange> {
        val lineBody = sigilLine.body ?: return emptyList()
        val hostRange = lineBody.textRangeInParent

        val lines = listOf(InjectionLine(hostRange, lineBody, hostRange.startOffset))
        return injectionRangesForLines(lines, lang)
    }

    private fun heredocInjectionLines(heredocLineList: List<HeredocLine>): List<InjectionLine> {
        val lines = mutableListOf<InjectionLine>()
        for (item in heredocLineList) {
            if (!item.isValid) {
                if (LOG.isDebugEnabled) {
                    LOG.debug("handleSigilHeredoc: skipping invalid heredocLine")
                }
                continue
            }

            val hostRange = item.textRangeInParent
            val lineBody = item.body
            // The body start is needed to shift interpolation ranges into the sigil host coordinate space.
            val bodyStartInHost = hostRange.startOffset + (lineBody?.textRangeInParent?.startOffset ?: 0)
            lines.add(InjectionLine(hostRange, lineBody, bodyStartInHost))
        }

        return lines
    }

    private fun injectionRangesForLines(lines: List<InjectionLine>, lang: Language): List<TextRange> {
        if (lines.isEmpty()) return emptyList()
        // Non-regex sigils inject the full body range; regex sigils skip interpolation ranges.
        if (lang != RegExpLanguage.INSTANCE) {
            return lines.map { it.hostRange }
        }

        val ranges = mutableListOf<TextRange>()
        for (line in lines) {
            ranges.addAll(
                regexInjectionRangesExcludingInterpolations(
                    line.hostRange,
                    line.bodyElement,
                    line.bodyStartInHost
                )
            )
        }
        return ranges
    }

    private fun buildInjectionRanges(baseRange: TextRange, excludedRanges: List<TextRange>): List<TextRange> {
        if (baseRange.length <= 0) return emptyList()
        if (excludedRanges.isEmpty()) return listOf(baseRange)

        val ranges = ArrayList<TextRange>()
        var currentStart = baseRange.startOffset

        for (excluded in excludedRanges) {
            val start = maxOf(baseRange.startOffset, excluded.startOffset)
            val end = minOf(baseRange.endOffset, excluded.endOffset)
            if (start > currentStart) {
                ranges.add(TextRange(currentStart, start))
            }
            if (end > currentStart) {
                currentStart = end
            }
        }

        if (currentStart < baseRange.endOffset) {
            ranges.add(TextRange(currentStart, baseRange.endOffset))
        }

        return ranges
    }

}
