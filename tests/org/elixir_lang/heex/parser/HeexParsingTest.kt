package org.elixir_lang.heex.parser

/**
 * Multi-root parsing fixtures covering constructs beyond plain HTML: `<%= %>`, an `<% %>` block
 * spanning tags, `{}` in body/attribute/root position, local/remote components, a `<:slot>` tag,
 * and an HTML comment. See [testAttributeFollowingBraceAttribute] for the
 * `<.tag one={""} class=""/>` case - it parses cleanly here; see that test's doc comment for why
 * that matters for anyone about to change the outer-language insertion machinery.
 */
class HeexParsingTest : HeexParsingTestCase() {
    fun testEexExpression() {
        doTest(true)
    }

    fun testEexBlockSpanningTags() {
        doTest(true)
    }

    fun testBraceInBody() {
        doTest(true)
    }

    fun testBraceInAttribute() {
        doTest(true)
    }

    fun testBraceInRootPosition() {
        doTest(true)
    }

    fun testLocalComponent() {
        doTest(true)
    }

    fun testRemoteComponent() {
        doTest(true)
    }

    fun testSlot() {
        doTest(true)
    }

    fun testHtmlComment() {
        doTest(true)
    }

    /**
     * A `{}`-valued attribute immediately followed by another attribute. Without replacement text
     * for the `{""}` outer range, the HTML lexer's unquoted-attribute-value rule would run on
     * into `class=""`; [HeexParsingTestCase] registers the production patcher so that it doesn't.
     */
    fun testAttributeFollowingBraceAttribute() {
        doTest(true)
    }
}
