package org.elixir_lang.beam.chunk.beam_documentation

import com.ericsson.otp.erlang.*

/**
 * Renders `application/erlang+html` EEP-48 documentation terms to Markdown text.
 *
 * Erlang OTP (≤26) stores documentation as structured Erlang terms rather than plain Markdown.
 * The format is a list of element tuples: `{Tag :: atom(), Attrs :: [{atom(), binary()}], Children :: [child()]}`
 * where `child()` is either a binary/charlist string or another element tuple.
 *
 * See [EEP-48](https://www.erlang.org/eeps/eep-0048) and the `shell_docs` module for the specification.
 */
object ErlangHtmlRenderer {

    /**
     * Renders an Erlang doc term (the value side of the doc language map) to Markdown.
     * The term is typically an `OtpErlangList` of element tuples and/or strings.
     */
    fun render(term: OtpErlangObject): String? =
        when (term) {
            is OtpErlangList -> renderChildren(term.elements(), inPre = false).trimEnd()
            is OtpErlangBinary -> String(term.binaryValue(), Charsets.UTF_8)
            else -> null
        }

    private fun renderChildren(children: Array<OtpErlangObject>, inPre: Boolean): String =
        children.joinToString("") { renderNode(it, inPre) }

    private fun renderNode(node: OtpErlangObject, inPre: Boolean): String =
        when (node) {
            is OtpErlangBinary -> String(node.binaryValue(), Charsets.UTF_8)
            is OtpErlangList -> charlistOrChildren(node, inPre)
            is OtpErlangTuple -> renderElement(node, inPre)
            else -> ""
        }

    private fun charlistOrChildren(list: OtpErlangList, inPre: Boolean): String {
        return try {
            list.stringValue()
        } catch (_: Exception) {
            renderChildren(list.elements(), inPre)
        }
    }

    private fun renderElement(tuple: OtpErlangTuple, inPre: Boolean): String {
        if (tuple.arity() < 3) return ""

        val tag = (tuple.elementAt(0) as? OtpErlangAtom)?.atomValue() ?: return ""
        val isPreBlock = tag == "pre"
        val childText = when (val children = tuple.elementAt(2)) {
            is OtpErlangList -> renderChildren(children.elements(), inPre = inPre || isPreBlock)
            else -> renderNode(children, inPre = inPre || isPreBlock)
        }

        return when (tag) {
            "p" -> "$childText\n\n"
            "pre" -> "```\n$childText\n```\n\n"
            "div" -> "$childText\n\n"
            "br" -> "\n"
            "hr" -> "\n---\n\n"
            "h1" -> "# $childText\n\n"
            "h2" -> "## $childText\n\n"
            "h3" -> "### $childText\n\n"
            "h4" -> "#### $childText\n\n"
            "code" -> if (inPre) childText else "`$childText`"
            "em", "i" -> if (inPre) childText else "*$childText*"
            "strong", "b" -> if (inPre) childText else "**$childText**"
            "a" -> {
                val href = getAttr(tuple, "href")
                if (href != null) "[$childText]($href)" else childText
            }
            "ul" -> "$childText\n"
            "ol" -> "$childText\n"
            "li" -> "- $childText\n"
            "dl" -> "$childText\n"
            "dt" -> "**$childText** "
            "dd" -> "$childText\n\n"
            else -> childText
        }
    }

    private fun getAttr(tuple: OtpErlangTuple, name: String): String? {
        val attrs = tuple.elementAt(1) as? OtpErlangList ?: return null
        for (attr in attrs.elements()) {
            if (attr is OtpErlangTuple && attr.arity() >= 2) {
                val key = when (val k = attr.elementAt(0)) {
                    is OtpErlangAtom -> k.atomValue()
                    is OtpErlangBinary -> String(k.binaryValue(), Charsets.UTF_8)
                    else -> null
                }
                if (key == name) {
                    return when (val v = attr.elementAt(1)) {
                        is OtpErlangBinary -> String(v.binaryValue(), Charsets.UTF_8)
                        is OtpErlangList -> charlistOrChildren(v, inPre = false)
                        else -> null
                    }
                }
            }
        }
        return null
    }
}
