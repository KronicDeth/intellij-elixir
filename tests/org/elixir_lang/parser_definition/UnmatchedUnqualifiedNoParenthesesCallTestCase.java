package org.elixir_lang.parser_definition;

public class UnmatchedUnqualifiedNoParenthesesCallTestCase extends ParsingTestCase {
  public void testUnmatchedUnqualifiedNoParenthesesCall() {
    assertParsedAndQuotedCorrectly(false);
  }

  @Override
  protected String getTestDataPath() {
    return super.getTestDataPath() + "/unmatched_unqualified_no_parentheses_call_test_case";
  }

}
