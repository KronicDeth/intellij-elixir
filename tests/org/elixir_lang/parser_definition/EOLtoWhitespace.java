package org.elixir_lang.parser_definition;

public class EOLtoWhitespace extends ParsingTestCase {
   public void testCapture() {
      assertParsedAndQuotedCorrectly();
   }

   public void testInMatch() {
      assertParsedAndQuotedCorrectly();
   }

   public void testOpeningParentheses() {
      assertParsedAndQuotedCorrectly();
   }

   public void testSemicolon() {
      assertParsedAndQuotedCorrectly();
   }

   public void testWhen() {
      assertParsedAndQuotedCorrectly();
   }

   @Override
   protected String getTestDataPath() {
      return super.getTestDataPath() + "/eol_to_whitespace";
   }
}
