package org.elixir_lang.parser_definition;

public class EOLtoWhitespace extends ParsingTestCase {
   public void testAnd() {
      assertParsedAndQuotedCorrectly();
   }

   public void testArrow() {
      assertParsedAndQuotedCorrectly();
   }

   public void testAssociation() {
      assertParsedAndQuotedCorrectly();
   }

   public void testAt() {
      doTest(true);
      assertWithoutLocalError();
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

   public void testIn() {
      doTest(true);
      assertWithoutLocalError();
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

   public void testNot() {
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

   public void testRelational() {
      assertParsedAndQuotedCorrectly();
   }

   public void testSemicolon() {
      assertParsedAndQuotedCorrectly();
   }

   public void testStab() {
      assertParsedAndQuotedCorrectly();
   }

   public void testTwo() {
      doTest(true);
      assertWithoutLocalError();
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
