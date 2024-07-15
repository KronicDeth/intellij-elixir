import com.intellij.testFramework.fixtures.BasePlatformTestCase

class CommentTogglingTest : BasePlatformTestCase() {
    fun testLineCommentToggle() {
        myFixture.configureByText("test.ex", """
            defmodule Test do
              def hello do
                IO.puts "Hello, World!"
              end
            end
        """.trimIndent())

        myFixture.performEditorAction("CommentByLineComment")

        myFixture.checkResult("""
            # defmodule Test do
            #   def hello do
            #     IO.puts "Hello, World!"
            #   end
            # end
        """.trimIndent())

        myFixture.performEditorAction("CommentByLineComment")

        myFixture.checkResult("""
            defmodule Test do
              def hello do
                IO.puts "Hello, World!"
              end
            end
        """.trimIndent())
    }

    fun testBlockCommentToggle() {
        myFixture.configureByText("test.ex", """
            defmodule Test do
              def hello do
                IO.puts "Hello, World!"
              end
            end
        """.trimIndent())

        myFixture.performEditorAction("CommentByBlockComment")

        myFixture.checkResult("""
            #[defmodule Test do
              def hello do
                IO.puts "Hello, World!"
              end
            end]#
        """.trimIndent())

        myFixture.performEditorAction("CommentByBlockComment")

        myFixture.checkResult("""
            defmodule Test do
              def hello do
                IO.puts "Hello, World!"
              end
            end
        """.trimIndent())
    }

    fun testCommentTogglePreservesIndentation() {
        myFixture.configureByText("test.ex", """
            defmodule Test do
              def hello do
                IO.puts "Hello, World!"
              end
            end
        """.trimIndent())

        myFixture.performEditorAction("CommentByLineComment")

        myFixture.checkResult("""
            # defmodule Test do
            #   def hello do
            #     IO.puts "Hello, World!"
            #   end
            # end
        """.trimIndent())
    }
}