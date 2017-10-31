package org.elixir_lang.parser_definition;

public class UnmatchedExpressionParsingTestCase extends ParsingTestCase {
    public void testAtDotCallBlock() throws Exception{
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testAtQualifiedNoArgumentsCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testAtQualifiedNoParenthesesCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testAtQualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testAtUnqualifiedNoArgumentsBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testAtUnqualifiedNoParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtUnqualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly(false);
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

        assertParsedAndQuotedCorrectly(false);
    }

    public void testCaptureQualifiedNoParenthesesCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
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

        assertParsedAndQuotedCorrectly(false);
    }

    public void testExclamationPointDotCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testExclamationPointQualifiedNoArgumentsCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testExclamationPointQualifiedNoParenthesesCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testExclamationPointQualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testExclamationPointUnqualifiedNoArgumentsBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testExclamationPointUnqualifiedNoParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testExclamationPointUnqualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testInternalError() {
        assertParsedWithErrors();
    }

    public void testNotDotCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testNotQualifiedNoArgumentsCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testNotQualifiedNoParenthesesCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testNotUnqualifiedNoArgumentsBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testNotUnqualifiedNoParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testNotUnqualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testQualifiedNoArgumentsCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testQualifiedNoParenthesesCallBlock() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testQualifiedParenthesesCallBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnaryStabBody() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnqualifiedNoArgumentsAfterBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsAfterStabBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnqualifiedNoArgumentsBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsCatchBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsCatchStabBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnqualifiedNoArgumentsElseBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsElseStabBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnqualifiedNoArgumentsRepeatBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsRescueBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsRescueStabBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnqualifiedNoArgumentsStabAfterStabBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnqualifiedNoArgumentsStabCatchStabBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnqualifiedNoArgumentsStabElseStabBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnqualifiedNoArgumentsStabRescueStabBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnqualifiedNoArgumentsStabBlock() {
        assertParsedAndQuotedCorrectly(false);
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
