package org.elixir_lang.parser_definition;

public class EOLtoWhitespace extends ParsingTestCase {
   public void testOpeningParentheses() {
      assertParsedAndQuotedCorrectly();
   }

   @Override
   protected String getTestDataPath() {
      return super.getTestDataPath() + "/eol_to_whitespace";
   }
}
