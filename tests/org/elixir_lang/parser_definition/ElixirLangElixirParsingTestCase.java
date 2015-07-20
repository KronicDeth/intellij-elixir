package org.elixir_lang.parser_definition;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.CharsetToolkit;
import org.elixir_lang.intellij_elixir.Quoter;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;

@RunWith(Parameterized.class)
public class ElixirLangElixirParsingTestCase extends ParsingTestCase {
    private enum Parse {
        ERROR("with local and remote error"),
        CORRECT("and quotes correctly");

        /*
         * Fields
         */

        private final String description;

        Parse(String description) {
            this.description = description;
        }

        public String toString() {
            return description;
        }
    }

    private static Map<String, Parse> parseByRelativePath;
    static {
        parseByRelativePath = new HashMap<String, Parse>();
        parseByRelativePath.put(
                "lib/elixir/test/elixir/fixtures/parallel_compiler/bat.ex",
                Parse.ERROR
        );
    }

    /*
     * Fields
     */

    private File elixirFile;
    private Parse parse;

    /*
     * Constructors
     */
    public ElixirLangElixirParsingTestCase(File elixirFile, Parse parse) {
        this.elixirFile = elixirFile;
        this.parse = parse;
    }

    @Parameterized.Parameters(
            name = "\"#{0}\" parses #{1}"
    )
    public static Collection<Object[]> generateData() {
        String rootPath = System.getenv("ELIXIR_LANG_ELIXIR_PATH");

        assertNotNull("ELIXIR_LANG_ELIXIR_PATH environment variable not set", rootPath);

        File rootFile = new File(rootPath);

        Queue<File> treeQueue = new ArrayDeque<File>();
        treeQueue.add(rootFile);

        Queue<Object[]> argumentQueue = new ArrayDeque<Object[]>();

        while(!treeQueue.isEmpty()) {
            File treeFile = treeQueue.remove();

            // skip hidden files
            if (treeFile.getName().startsWith(".")) {
                continue;
            }

            if (treeFile.isDirectory()) {
                File[] childFiles = treeFile.listFiles();

                assert childFiles != null;

                for (File childFile : childFiles) {
                    treeQueue.add(childFile);
                }
            } else if (FileUtilRt.extensionEquals(treeFile.toString(), "ex")) {
                Object[] arguments = new Object[] {
                        treeFile,
                        parse(rootFile, treeFile)
                };
                argumentQueue.add(arguments);
            }
        }

        return argumentQueue;
    }

    private static Parse parse(File rootFile, File descendantFile) {
        URI rootURI = rootFile.toURI();
        URI descendantURI = descendantFile.toURI();

        String relativePath = rootURI.relativize(descendantURI).getPath();

        Parse relativePathParse = parseByRelativePath.get(relativePath);

        if (relativePathParse == null) {
            relativePathParse = Parse.CORRECT;
        }

        return relativePathParse;
    }

    @org.junit.Test
    public void parse() throws Exception {
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

        switch (parse) {
            case CORRECT:
                assertWithoutLocalError();
                assertQuotedCorrectly();
                break;
            case ERROR:
                assertWithLocalError();
                Quoter.assertError(myFile);
        }
    }

    @Override
    @NotNull
    protected String getTestDataPath() {
        return System.getenv("ELIXIR_LANG_ELIXIR_PATH");
    }
}
