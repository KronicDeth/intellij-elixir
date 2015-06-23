package org.elixir_lang.parser_definition;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.CharsetToolkit;
import org.elixir_lang.ElixirLanguage;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

@RunWith(Parameterized.class)
public class ElixirLangElixirParsingTestCase extends ParsingTestCase {
    /*
     * Fields
     */

    private File elixirFile;

    /*
     * Constructors
     */
    public ElixirLangElixirParsingTestCase(File elixirFile) {
        this.elixirFile = elixirFile;
    }

    @Parameterized.Parameters(
            name = "\"#{0}\" parses and quotes correctly"
    )
    public static Collection<File[]> generateData() {
        String rootPath = System.getenv("ELIXIR_LANG_ELIXIR_PATH");

        assertNotNull("ELIXIR_LANG_ELIXIR_PATH environment variable not set", rootPath);

        File rootFile = new File(rootPath);

        Queue<File> treeQueue = new ArrayDeque<File>();
        treeQueue.add(rootFile);

        Queue<File[]> elixirFileDeque = new ArrayDeque<File[]>();

        while(!treeQueue.isEmpty()) {
            File treeFile = treeQueue.remove();

            // skip hidden files
            if (treeFile.getName().startsWith(".")) {
                continue;
            }

            if (treeFile.isDirectory()) {
                File[] childFiles = treeFile.listFiles();

                for (File childFile : childFiles) {
                    treeQueue.add(childFile);
                }
            } else if (FileUtilRt.extensionEquals(treeFile.toString(), "ex")) {
                elixirFileDeque.add(new File[]{treeFile});
            }
        }

        return elixirFileDeque;
    }

    @org.junit.Test
    public void parsedAndQuotedCorrectly() throws Exception {
        // this does not fire automatically
        super.setUp();

        // inlines part of com.intellij.testFramework.ParsingTestCase#doTest(boolean)
        try {
            String text = FileUtil.loadFile(elixirFile, CharsetToolkit.UTF8, true).trim();

            String nameWithoutExtension = FileUtilRt.getNameWithoutExtension(elixirFile.toString());
            myFile = createPsiFile(nameWithoutExtension, text);
            ensureParsed(myFile);
            toParseTreeText(myFile, skipSpaces(), includeRanges());
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        assertWithoutLocalError();
        assertQuotedCorrectly();
    }

    @Override
    @NotNull
    protected String getTestDataPath() {
        return System.getenv("ELIXIR_LANG_ELIXIR_PATH");
    }
}
