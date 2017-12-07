package org.elixir_lang.parser_definition;

public class EOLtoWhitespace extends ParsingTestCase {
   public void testAnd() {
      assertParsedAndQuotedCorrectly();
   }

   public void testCapture() {
      assertParsedAndQuotedCorrectly();
   }

   public void testClosingBit() {
      assertParsedAndQuotedCorrectly();
   }

   public void testClosingBracket() {
      assertParsedAndQuotedCorrectly();
   }

   public void testComparison() {
      assertParsedAndQuotedCorrectly();
   }

   public void testClosingCurly() {
      assertParsedAndQuotedCorrectly();
   }

   public void testClosingParentheses() {
      assertParsedAndQuotedCorrectly();
   }

   public void testDo() {
      assertParsedAndQuotedCorrectly();
   }

   public void testInMatch() {
      assertParsedAndQuotedCorrectly();
   }

   public void testKeywordPairColon() {
      assertParsedAndQuotedCorrectly();
   }

   public void testMatch() {
      assertParsedAndQuotedCorrectly();
   }

   public void testOpeningBit() {
      assertParsedAndQuotedCorrectly();
   }

   public void testOpeningBracket() {
      assertParsedAndQuotedCorrectly();
   }

   public void testOpeningCurly() {
      assertParsedAndQuotedCorrectly();
   }

   public void testOpeningParentheses() {
      assertParsedAndQuotedCorrectly();
   }

   public void testOr() {
      assertParsedAndQuotedCorrectly();
   }

   public void testPipe() {
      assertParsedAndQuotedCorrectly();
   }

   public void testSemicolon() {
      assertParsedAndQuotedCorrectly();
   }

   public void testStab() {
      assertParsedAndQuotedCorrectly();
   }

   public void testWhen() {
      assertParsedAndQuotedCorrectly();
   }

   public void testType() {
      assertParsedAndQuotedCorrectly();
   }

   @Override
   protected String getTestDataPath() {
      return super.getTestDataPath() + "/eol_to_whitespace";
   }
}
