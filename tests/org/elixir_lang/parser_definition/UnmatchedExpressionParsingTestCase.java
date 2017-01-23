package org.elixir_lang.parser_definition;

public class UnmatchedExpressionParsingTestCase extends ParsingTestCase {
    public void testAtDotCallBlock() throws Exception{
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testAtQualifiedNoArgumentsCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testAtQualifiedNoParenthesesCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testAtQualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtUnqualifiedNoArgumentsBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtUnqualifiedNoParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtUnqualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testBlockIdentifierKeywordKeys() {
        assertParsedAndQuotedCorrectly();
    }

    public void testBlockItemCommentStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureDotCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureQualifiedNoArgumentsCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureQualifiedNoParenthesesCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureQualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureUnqualifiedNoArgumentsBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureUnqualifiedNoParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureUnqualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDotCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testExclamationPointDotCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testExclamationPointQualifiedNoArgumentsCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testExclamationPointQualifiedNoParenthesesCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testExclamationPointQualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testExclamationPointUnqualifiedNoArgumentsBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testExclamationPointUnqualifiedNoParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testExclamationPointUnqualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testInternalError() {
        assertParsedWithErrors();
    }

    public void testNotDotCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testNotQualifiedNoArgumentsCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testNotQualifiedNoParenthesesCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testNotUnqualifiedNoArgumentsBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNotUnqualifiedNoParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNotUnqualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testQualifiedNoArgumentsCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testQualifiedNoParenthesesCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testQualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnaryStabBody() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsAfterBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsAfterStabBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsCatchBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsCatchStabBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsElseBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsElseStabBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsRepeatBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsRescueBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsRescueStabBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsStabAfterStabBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsStabCatchStabBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsStabElseStabBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsStabRescueStabBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsStabBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnquoteSplicingBlock() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/unmatched_expression_parsing_test_case";
    }
}
